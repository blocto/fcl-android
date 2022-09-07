package com.portto.fcl.network

import com.portto.fcl.model.PollingResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap
import retrofit2.http.Url

internal interface AuthService {

    @POST
    suspend fun executePost(
        @Url url: String,
        @QueryMap params: Map<String, String>? = mapOf(),
        @Body data: JsonObject
    ): PollingResponse

    @POST
    suspend fun executePost(
        @Url url: String,
        @QueryMap params: Map<String, String>? = mapOf()
    ): PollingResponse

    @GET
    suspend fun executeGet(
        @Url url: String,
        @QueryMap params: Map<String, String>? = mapOf()
    ): PollingResponse
}
