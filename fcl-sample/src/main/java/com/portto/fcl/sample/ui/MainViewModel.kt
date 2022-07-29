package com.portto.fcl.sample.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portto.fcl.Fcl
import com.portto.fcl.config.ConfigOption
import com.portto.fcl.model.Result
import com.portto.fcl.model.authn.AccountProofResolvedData
import com.portto.fcl.provider.Provider
import com.portto.fcl.sample.util.FLOW_APP_IDENTIFIER
import com.portto.fcl.sample.util.FLOW_NONCE
import com.portto.sdk.wallet.flow.CompositeSignature
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _address = MutableLiveData<String?>(null)
    val address: LiveData<String?> get() = _address

    private val _accountProofSignatures = MutableLiveData<List<CompositeSignature>?>(null)
    val accountProofSignatures: LiveData<List<CompositeSignature>?> get() = _accountProofSignatures

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun connect(walletProvider: Provider, withAccountPoof: Boolean) = viewModelScope.launch {
        Fcl.config.put(ConfigOption.SelectedWalletProvider(walletProvider))

        when (val result =
            Fcl.authenticate(
                if (withAccountPoof) AccountProofResolvedData(FLOW_APP_IDENTIFIER, FLOW_NONCE)
                else null
            )) {
            is Result.Success -> {
                _address.value = result.value
                _accountProofSignatures.value = Fcl.currentUser?.accountProofSignatures
            }
            is Result.Failure -> _errorMessage.value = result.throwable.message
        }
    }

    fun disconnect() {
        Fcl.unauthenticate()
        _address.value = null
        _accountProofSignatures.value = null
    }

}