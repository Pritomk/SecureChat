package com.example.securechat.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import com.example.securechat.data.model.ChannelGist
import io.getstream.chat.android.models.Channel

class CommonMethods(val context: Context) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val isConnected: Boolean
        get() = connectivityManager.activeNetworkInfo?.isConnected ?: false

    fun removePreferences() {
        UserInfo(context).userId = ""
        UserInfo(context).accessToken = ""
        UserInfo(context).displayName = ""
        UserInfo(context).chatUserDetails = null
    }
}