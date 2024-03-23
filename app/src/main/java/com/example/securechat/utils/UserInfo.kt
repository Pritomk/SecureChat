package com.example.securechat.utils

import android.content.Context
import com.example.securechat.ApplicationMain

class UserInfo(context: Context): SharedPreferencesHelper(context) {

    private val ACCESS_TOKEN = "access_token"
    var accessToken: String?
        set(value) {
            if (value != null) {
                setString(ACCESS_TOKEN, value)
            }
        }
        get() = getString(ACCESS_TOKEN, "")
}