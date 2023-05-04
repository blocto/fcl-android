package com.portto.fcl.network

import com.portto.fcl.model.PollingResponse
import com.portto.fcl.utils.HEADER_REQUEST_SOURCE
import com.portto.fcl.utils.SDK_SOURCE
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.QueryMap
import retrofit2.http.Url

internal interface AuthService {

    @Headers("$HEADER_REQUEST_SOURCE: $SDK_SOURCE")
    @POST
    suspend fun executePost(
        @Url url: String,
        @HeaderMap headers: Map<String, String>? = mapOf(),
        @QueryMap params: Map<String, String>? = mapOf(),
        @Body data: JsonObject
    ): PollingResponse

    @Headers("$HEADER_REQUEST_SOURCE: $SDK_SOURCE")
    @POST
    suspend fun executePost(
        @Url url: String,
        @HeaderMap headers: Map<String, String>? = mapOf(),
        @QueryMap params: Map<String, String>? = mapOf()
    ): PollingResponse

    @GET
    suspend fun executeGet(
        @Url url: String,
        @QueryMap params: Map<String, String>? = mapOf()
    ): PollingResponse
}
