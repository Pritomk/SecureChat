package com.example.securechat.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val userId: String,
    val displayName: String,
    var token: String = "",
    var profilePic: String = "https://random.imagecdn.app/60/60"
)