package com.portto.fcl.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.portto.fcl.network.service.DiscoveryService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object FclApi {
    private val okHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)) // add logger
        connectTimeout(30, TimeUnit.SECONDS)
        readTimeout(30, TimeUnit.SECONDS)
        writeTimeout(30, TimeUnit.SECONDS)
    }.build()


    @OptIn(ExperimentalSerializationApi::class)
    private fun retrofit(url: String): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .addConverterFactory(Json.asConverterFactory(contentType))
            .baseUrl(url)
            .client(okHttpClient)
            .build()
    }

    // Wallet discovery
    val discoveryService: DiscoveryService by lazy {
        retrofit(DiscoveryService.BASE_URL).create(DiscoveryService::class.java)
    }
}