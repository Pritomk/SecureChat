package com.example.securechat.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.google.gson.Gson
import java.io.ByteArrayOutputStream


class UserInfo(context: Context): SharedPreferencesHelper(context) {

    private val ACCESS_TOKEN = "access_token"
    private val USER_ID = "user_id"
    private val DISPLAY_NAME = "display_name"
    private val CHAT_USER = "chat_user"
    private val MY_QR = "my_qr"
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

    var myQrCode: Bitmap?
        set(value) {
            val baos = ByteArrayOutputStream()
            value!!.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val byteArray = baos.toByteArray()
            val bitmapAsString: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
            setString(MY_QR, bitmapAsString)
        }
        get() {
            val bitmapAsString: String = getString(MY_QR, "") ?: ""

            // Convert the Base64-encoded string back to a Bitmap
            val byteArray = Base64.decode(bitmapAsString, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }
}