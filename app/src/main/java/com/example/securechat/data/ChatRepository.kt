package com.example.securechat.data

import androidx.lifecycle.LiveData
import io.getstream.chat.android.client.events.ChatEvent
import io.getstream.chat.android.models.Message
import java.util.concurrent.CompletableFuture

class ChatRepository(
    val chatDataSource: ChatDataSource
) {

    fun sendTextMsg(text: String): CompletableFuture<Result<Message>> {
        return chatDataSource.sendTextMsg(text, false)
    }

    fun getAllMessages(lastMsgId: String?): CompletableFuture<Result<List<Message>>> {
        return chatDataSource.getAllMessage(20, lastMsgId)
    }

    fun listenEvent() : LiveData<ChatEvent> {
        return chatDataSource.listenEvents()
    }
}