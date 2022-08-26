package com.portto.fcl.network

import com.portto.fcl.model.PollingResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.http.*

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