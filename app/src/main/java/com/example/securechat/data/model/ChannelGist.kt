package com.example.securechat.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChannelGist(
    val channelId: String,
    val uid: String,
    val name: String,
    val displayPic: String = "https://xsgames.co/randomusers/avatar.php?g=female",
): Parcelable
