package com.example.securechat.utils

import android.view.View
import android.view.ViewGroup
import com.example.securechat.ApplicationMain
import com.example.securechat.data.model.MessageGist
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.models.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

object AppCommonMethods {
    fun formatTimeFromMillis(timeInMillis: Long?, format: String): String? {
        val formatter = SimpleDateFormat(format)
        formatter.timeZone = TimeZone.getDefault()
        return timeInMillis?.let { Date(it) }?.let { formatter.format(it) }
    }

    fun checkSide(user: User, myUid: String): ChatSide = if (user.id == myUid) ChatSide.MY_CHAT else ChatSide.OTHER_CHAT

    fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is ViewGroup.MarginLayoutParams) {
            val p = view.layoutParams as ViewGroup.MarginLayoutParams
            val density = ApplicationMain.getInstance().resources.displayMetrics.density
            p.setMargins((left*density).toInt(), (top*density).toInt(), (right*density).toInt(), (bottom*density).toInt())
            view.layoutParams = p
        }
    }

    fun convertToMessageGist(message: Message, myUid: String): MessageGist {
        return MessageGist(
            id = message.id,
            side = checkSide(message.user, myUid),
            text = message.text,
            createdAt = message.createdAt,
            attachments = message.attachments,
            replyMessage = null
        )
    }

}