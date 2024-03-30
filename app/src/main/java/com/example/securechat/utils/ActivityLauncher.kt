package com.example.securechat.utils

import android.content.Context
import com.example.securechat.ui.main.HomeActivity
import com.example.securechat.ui.login.LoginActivity

object ActivityLauncher {

    fun launchLogin(context: Context) {
        LoginActivity().open(context)
    }

    fun launchMain(context: Context) {
        HomeActivity().open(context)
    }
}