package com.example.securechat.networking.response

import com.google.gson.annotations.SerializedName


data class ClaimResponse(
    @field:SerializedName("claims")
    val claims: List<Claim>,
    @field:SerializedName("nextPageToken")
    val nextPageToken: String
)

data class Claim(
    @field:SerializedName("text")
    val text: String,
    @field:SerializedName("claimReview")
    val claimReview: List<ClaimReview>
)

data class ClaimReview(
    @field:SerializedName("publisher")
    val publisher: Publisher,
    @field:SerializedName("url")
    val url: String,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("reviewDate")
    val reviewDate: String,
    @field:SerializedName("textualRating")
    val textualRating: String,
    @field:SerializedName("languageCode")
    val languageCode: String
)

data class Publisher(
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("site")
    val site: String
)
