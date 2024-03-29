package com.portto.fcl.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.portto.fcl.Fcl
import com.portto.fcl.provider.blocto.Blocto
import com.portto.fcl.utils.json
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

internal object FclClient {

    private const val HEADER_CONTENT_TYPE_JSON = "application/json"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val builder = chain.request().newBuilder()
            val headers = when (Fcl.config.selectedWalletProvider) {
                is Blocto -> mapOf("Blocto-Application-Identifier" to Blocto.bloctoAppIdentifier)
                else -> null
            }
            headers?.forEach { builder.addHeader(it.key, it.value) }
            chain.proceed(builder.build())
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .callTimeout(20, TimeUnit.SECONDS)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    @OptIn(ExperimentalSerializationApi::class)
    private fun retrofit(url: String? = null): Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory(HEADER_CONTENT_TYPE_JSON.toMediaType()))
        .client(okHttpClient)
        .baseUrl(url ?: "https://localhost")
        .build()

    val authService: AuthService by lazy {
        retrofit().create(AuthService::class.java)
    }
}
