package com.example.securechat.utils

import android.content.Context
import com.example.securechat.MainActivity
import com.example.securechat.ui.login.LoginActivity

object ActivityLauncher {

    fun launchLogin(context: Context) {
        LoginActivity().open(context)
    }

    fun launchMain(context: Context) {
        MainActivity().open(context)
    }
}