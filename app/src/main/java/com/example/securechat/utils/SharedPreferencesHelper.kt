package com.example.securechat.utils

import android.content.Context
import android.content.SharedPreferences

open class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("my_app", Context.MODE_PRIVATE)

    // Function to save a value to SharedPreferences
    fun setString(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    // Function to get a value from SharedPreferences
    fun getString(key: String, defaultValue: String): String? {
        return sharedPreferences.getString(key, defaultValue)
    }
}