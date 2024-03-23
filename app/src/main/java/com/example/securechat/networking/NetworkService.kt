package com.example.securechat.networking

import com.example.securechat.networking.response.TokenResponse
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface NetworkService {
    @POST("/api/chats")
    @Headers("Content-Type: application/json")
    suspend fun getToken(@Body jsonObject: JsonObject): TokenResponse
}