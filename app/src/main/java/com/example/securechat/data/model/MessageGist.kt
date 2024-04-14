package com.example.securechat.data.model

import com.example.securechat.utils.ChatSide
import io.getstream.chat.android.models.Attachment
import java.util.Date

data class MessageGist(
    val id: String,
    val side: ChatSide,
    val text: String? = null,
    val createdAt: Date? = null,
    val attachments: List<Attachment>? = null
)
