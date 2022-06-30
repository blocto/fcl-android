package com.portto.fcl.sample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portto.fcl.model.discovery.Service
import com.portto.fcl.sample.repository.FclRepository
import kotlinx.coroutines.launch


class MainViewModel(private val repository: FclRepository) : ViewModel() {
    private val _isLoadingWalletProviders = MutableLiveData(false)
    val isLoadingWalletProviders: LiveData<Boolean> get() = _isLoadingWalletProviders

    private val _fclWallets = MutableLiveData<List<Service>>()
    val fclWallets: LiveData<List<Service>> get() = _fclWallets

    fun getWalletProviders() {
        viewModelScope.launch {
            _isLoadingWalletProviders.value = true
            _fclWallets.value = repository.getSupportedWallets()
            _isLoadingWalletProviders.value = false
        }
    }
}