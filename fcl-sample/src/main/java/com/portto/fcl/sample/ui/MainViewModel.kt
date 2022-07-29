package com.portto.fcl.sample.ui

import androidx.lifecycle.*
import com.portto.fcl.Fcl
import com.portto.fcl.config.ConfigOption
import com.portto.fcl.model.CompositeSignature
import com.portto.fcl.model.Result
import com.portto.fcl.model.authn.AccountProofResolvedData
import com.portto.fcl.provider.Provider
import com.portto.fcl.sample.util.FLOW_APP_IDENTIFIER
import com.portto.fcl.sample.util.FLOW_NONCE
import com.portto.fcl.utils.AppUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    // authn - account address
    private val _address = MutableLiveData<String?>(null)
    val address: LiveData<String?> get() = _address

    // authn - signatures
    private val _accountProofSignatures = MutableLiveData<List<CompositeSignature>?>(null)
    val accountProofSignatures: LiveData<List<CompositeSignature>?> get() = _accountProofSignatures

    // user_signature - input message
    val userMessage = MutableLiveData("Hello")

    // user_signature - signatures
    private val _userSignatures = MutableStateFlow<List<CompositeSignature>?>(null)
    val userSignatures get() = _userSignatures.asStateFlow()

    // Message to be shown as snackbar
    private val _message = MutableStateFlow<String?>(null)
    val message get() = _message.asStateFlow()

    fun connect(walletProvider: Provider, withAccountPoof: Boolean) = viewModelScope.launch {
        Fcl.config.put(ConfigOption.SelectedWalletProvider(walletProvider))
        val accountProofResolvedData =
            if (withAccountPoof) AccountProofResolvedData(FLOW_APP_IDENTIFIER, FLOW_NONCE)
            else null
        when (val result = Fcl.authenticate(accountProofResolvedData)) {
            is Result.Success -> {
                _address.value = result.value
                _accountProofSignatures.value = Fcl.currentUser?.accountProofData?.signatures
            }
            is Result.Failure -> _message.value = result.throwable.message
        }
    }

    fun disconnect() {
        Fcl.unauthenticate()
        _address.value = null
        _accountProofSignatures.value = null
        _userSignatures.value = null
        resetMessage()
    }

    fun signUserMessage(message: String) {
        viewModelScope.launch {
            when (val result = Fcl.signUserMessage(message)) {
                is Result.Success -> _userSignatures.value = result.value
                is Result.Failure -> _message.value = result.throwable.message
            }
        }
    }

    fun verifySignature(isAccountProof: Boolean) {
        viewModelScope.launch {
            try {
                val isValid = if (isAccountProof) {
                    val accountProofData = Fcl.currentUser?.accountProofData
                        ?: throw Exception("No Account Proof Data")

                    AppUtils.verifyAccountProof(
                        appIdentifier = FLOW_APP_IDENTIFIER,
                        accountProofData = accountProofData
                    )
                } else {
                    val userMessage = userMessage.value
                        ?: throw Exception("Invalid message: ${userMessage.value}")

                    val userSignatures = userSignatures.value
                        ?: throw Exception("Signature is not provided")

                    AppUtils.verifyUserSignatures(
                        message = userMessage,
                        signatures = userSignatures
                    )
                }
                val type = if (isAccountProof) "Account Proof Data" else "User Signature Data"
                val validString = if (isValid) "valid" else "invalid"
                _message.value = "$type is $validString"
            } catch (exception: Exception) {
                _message.value = exception.message
            }
        }
    }

    fun resetMessage() {
        _message.value = null
    }
}