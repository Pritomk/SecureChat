package com.example.securechat.utils

import android.content.Context
import com.example.securechat.ApplicationMain
import com.google.gson.Gson

class UserInfo(context: Context): SharedPreferencesHelper(context) {

    private val ACCESS_TOKEN = "access_token"
    private val USER_ID = "user_id"
    private val DISPLAY_NAME = "display_name"
    private val CHAT_USER = "chat_user"
    var accessToken: String?
        set(value) {
            if (value != null) {
                setString(ACCESS_TOKEN, value)
            }
        }
        get() = getString(ACCESS_TOKEN, "")

    var userId: String?
        set(value) {
            if (value != null) {
                setString(USER_ID, value)
            }
        }
        get() = getString(USER_ID, "")

    var displayName: String?
        set(value) {
            if (value != null) {
                setString(DISPLAY_NAME, value)
            }
        }
        get() = getString(DISPLAY_NAME, "")

    var chatUserDetails: io.getstream.chat.android.models.User?
        set(value) {
            if (value != null) {
                setString(CHAT_USER, Gson().toJson(value))
            }
        }
        get() = Gson().fromJson(getString(CHAT_USER, ""), io.getstream.chat.android.models.User::class.java)
}