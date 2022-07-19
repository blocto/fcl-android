package com.portto.fcl.provider

import android.net.Uri
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.portto.fcl.lifecycle.LifecycleObserver
import com.portto.fcl.model.PollingResponse
import com.portto.fcl.model.Service
import com.portto.fcl.model.User
import com.portto.fcl.provider.Provider.ProviderInfo
import com.portto.fcl.webview.WebViewActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.*
import java.util.concurrent.TimeUnit

object Dapper : Provider {
    override var user: User? = null

    override val info: ProviderInfo
        get() = ProviderInfo(
            "Dapper",
            "Wallet created by Dapper Lab",
            "https://i.imgur.com/L1dgOKn.png"
        )

    override suspend fun authn(): User {
        val pollingResponse = runBlocking { execService("https://dapper-http-post.vercel.app/api/authn") }
        val updates = pollingResponse.updates ?: throw Error()
        pollingResponse.openAuthenticationWebView()

        var authnResponse: PollingResponse? = null
        repeatWhen(predicate = { (authnResponse == null || authnResponse?.status == "PENDING") }) {
            delay(1000)
            authnResponse = polling(updates)
        }

        return User(address = authnResponse?.data?.address.orEmpty())
    }

    private fun PollingResponse.openAuthenticationWebView() {
        local ?: throw Error()
        val url = local.endpoint ?: throw Error()
        val params = local.params ?: throw Error()

        val uri = Uri.parse(url).buildUpon().apply {
            params.forEach { appendQueryParameter(it.key, it.value) }
        }.build()
        LifecycleObserver.context()?.apply {
            WebViewActivity.launchUrl(this, uri.toString())
        }
    }

    private suspend fun polling(service: Service): PollingResponse? {
        val url = service.endpoint ?: throw Error()
        val response = retrofitAuthApi().executeGet(url, service.params)
        when (response.status) {
            "APPROVED" -> {
                println(response)
                WebViewActivity.close()
            }
            "DECLINED" -> {
                println(response)
                throw Error()
            }
            else -> return response
        }
        return response
    }

    private suspend fun execService(url: String): PollingResponse {
        return retrofitAuthApi().executePost(url)
    }

    private suspend fun repeatWhen(
        predicate: suspend () -> Boolean,
        block: suspend () -> Unit,
    ) {
        while (predicate()) {
            block.invoke()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = false
        isLenient = true
        useArrayPolymorphism = true
        coerceInputValues = true
        explicitNulls = false
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun retrofitAuthApi(url: String? = null): RetrofitAuthApi {
        val client = okHttpClient()

        return Retrofit.Builder()
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(url ?: "https://google.com")
            .build()
            .create(RetrofitAuthApi::class.java)
    }

    private fun okHttpClient(): OkHttpClient {
        val client = OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })

            callTimeout(20, TimeUnit.SECONDS)
            connectTimeout(20, TimeUnit.SECONDS)
            readTimeout(20, TimeUnit.SECONDS)
            writeTimeout(20, TimeUnit.SECONDS)

        }.build()
        return client
    }

    private interface RetrofitAuthApi {

        @POST
        suspend fun executePost(@Url url: String, @QueryMap params: Map<String, String>? = mapOf(), @Body data: Any): PollingResponse

        @POST
        suspend fun executePost(@Url url: String, @QueryMap params: Map<String, String>? = mapOf()): PollingResponse

        @GET
        suspend fun executeGet(@Url url: String, @QueryMap params: Map<String, String>? = mapOf()): PollingResponse
    }

    override suspend fun authz() {
        TODO("Not yet implemented")
    }
}
