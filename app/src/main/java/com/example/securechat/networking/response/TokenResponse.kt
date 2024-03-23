package com.example.securechat.networking.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TokenResponse(
    @field:SerializedName("status")
    @Expose
    val status: Boolean,

    @field:SerializedName("token")
    @Expose
    val token: String
)