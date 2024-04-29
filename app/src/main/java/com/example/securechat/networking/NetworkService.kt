package com.example.securechat.networking

import com.example.securechat.networking.response.ClaimResponse
import com.example.securechat.networking.response.TokenResponse
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface NetworkService {
    @POST("/getToken")
    @Headers("Content-Type: application/json")
    suspend fun getToken(@Body jsonObject: JsonObject): TokenResponse

    @GET("v1alpha1/claims:search")
    suspend fun reliabilityCheck(@Query("query") query: String, @Query("key") key: String): ClaimResponse
}