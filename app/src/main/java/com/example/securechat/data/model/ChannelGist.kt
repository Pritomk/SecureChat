package com.example.securechat.data.model

import android.net.Uri

data class ChannelGist(
    val channelId: String,
    val uid: String,
    val name: String,
    val displayPic: String = "https://xsgames.co/randomusers/avatar.php?g=female",
)
