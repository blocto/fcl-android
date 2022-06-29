package com.portto.fcl.network.service

import com.portto.fcl.model.discovery.Service
import retrofit2.http.POST

/**
 */
interface DiscoveryService {
    @POST("authn")
    suspend fun getWalletProviders(): List<Service>

    companion object {
        const val BASE_URL = "https://fcl-discovery.onflow.org/api/testnet/"
    }
}