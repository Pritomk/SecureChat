package com.example.securechat.utils

import android.content.Context
import com.example.securechat.RoutingActivity
import com.example.securechat.data.model.ChannelGist
import com.example.securechat.ui.chat.ChatActivity
import com.example.securechat.ui.main.HomeActivity
import com.example.securechat.ui.login.LoginActivity

object ActivityLauncher {

    fun launchLogin(context: Context) {
        LoginActivity().open(context)
    }

    fun launchHome(context: Context) {
        HomeActivity().open(context)
    }

    fun launchRoute(context: Context) {
        RoutingActivity().open(context)
    }

    fun launchChat(context: Context, channelGist: ChannelGist) {
        ChatActivity().open(context, channelGist)
    }
}