package com.portto.fcl.sample.ui

import androidx.lifecycle.*
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowArgument
import com.nftco.flow.sdk.cadence.JsonCadenceBuilder
import com.portto.fcl.Fcl
import com.portto.fcl.config.Config
import com.portto.fcl.model.CompositeSignature
import com.portto.fcl.model.Result
import com.portto.fcl.model.authn.AccountProofResolvedData
import com.portto.fcl.provider.Provider
import com.portto.fcl.sample.util.FLOW_APP_IDENTIFIER
import com.portto.fcl.sample.util.FLOW_NONCE
import com.portto.fcl.sample.util.getMutateSampleScript
import com.portto.fcl.sample.util.getQuerySampleScript
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    // current network
    private val _isCurrentMainnet = MutableLiveData(false)
    val isCurrentMainnet: LiveData<Boolean> get() = _isCurrentMainnet

    val queryScript: LiveData<String> = Transformations.map(isCurrentMainnet) {
        getQuerySampleScript(isMainnet = it)
    }

    val mutateScript: LiveData<String> = Transformations.map(isCurrentMainnet) {
        getMutateSampleScript(isMainnet = it)
    }

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

    // query
    private val _queryResult = MutableLiveData<String?>(null)
    val queryResult: LiveData<String?> get() = _queryResult

    // transaction - input value
    val txInputValue = MutableLiveData("123")

    // transaction - result
    private val _transactionId = MutableLiveData<String?>(null)
    val transactionId: LiveData<String?> get() = _transactionId

    // Message to be shown as snackbar
    private val _message = MutableStateFlow<String?>(null)
    val message get() = _message.asStateFlow()

    fun connect(walletProvider: Provider, withAccountPoof: Boolean) = viewModelScope.launch {
        Fcl.config.put(Config.Option.SelectedWalletProvider(walletProvider))
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

    fun setCurrentNetwork(isMainnet: Boolean) {
        _isCurrentMainnet.value = isMainnet
    }

    fun disconnect() {
        Fcl.unauthenticate()
        _address.value = null
        _accountProofSignatures.value = null
        _userSignatures.value = null
        _queryResult.value = null
        _transactionId.value = null
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

                    when (val result =
                        Fcl.verifyAccountProof(FLOW_APP_IDENTIFIER, accountProofData)) {
                        is Result.Success -> result.value
                        is Result.Failure -> throw result.throwable
                    }
                } else {
                    val userMessage = userMessage.value
                        ?: throw Exception("Invalid message: ${userMessage.value}")

                    val userSignatures = userSignatures.value
                        ?: throw Exception("Signature is not provided")

                    when (val result = Fcl.verifyUserSignatures(userMessage, userSignatures)) {
                        is Result.Success -> result.value
                        is Result.Failure -> throw result.throwable
                    }
                }
                val type = if (isAccountProof) "Account Proof Data" else "User Signature Data"
                val validString = if (isValid) "valid" else "invalid"
                _message.value = "$type is $validString"
            } catch (exception: Exception) {
                _message.value = exception.message
            }
        }
    }

    fun sendQuery(script: String) {
        viewModelScope.launch {
            when (val result = Fcl.query(script)) {
                is Result.Success -> _queryResult.value = result.value.toString()
                is Result.Failure -> _queryResult.value = result.throwable.message
            }
        }
    }

    fun sendTransaction(script: String, userAddress: String) {
        viewModelScope.launch {
            val value = txInputValue.value ?: return@launch
            val args = listOf(FlowArgument(JsonCadenceBuilder().ufix64(value)))
            when (val result = Fcl.mutate(
                cadence = script, arguments = args, limit = 300u,
                authorizers = listOf(FlowAddress(userAddress))
            )) {
                is Result.Success -> _transactionId.value = result.value
                is Result.Failure -> _message.value = result.throwable.message
            }
        }
    }

    fun resetMessage() {
        _message.value = null
    }
}