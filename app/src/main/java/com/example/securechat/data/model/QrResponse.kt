package com.example.securechat.data.model

import com.google.gson.annotations.SerializedName

data class QrResponse(
    @field:SerializedName("uid")
    val uid: String? = null,

    @field:SerializedName("publicKey")
    val publicKey: String? = null,

    @field:SerializedName("privateKey")
    val privateKey: String? = null
)
