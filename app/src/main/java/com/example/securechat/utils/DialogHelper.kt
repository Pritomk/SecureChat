package com.example.securechat.utils

import android.app.AlertDialog
import android.content.Context

object DialogHelper {
    fun simpleDialog(
        context: Context,
        msg: String,
        positiveCallback: ()->Unit) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder
            .setTitle(msg)
            .setPositiveButton("Retry") { _, _ ->
                positiveCallback()
            }.create()
        dialog.show()
    }
}