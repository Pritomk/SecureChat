package com.example.securechat.data

import androidx.lifecycle.LiveData
import com.example.securechat.data.model.MessageGist
import io.getstream.chat.android.client.events.ChatEvent
import io.getstream.chat.android.models.Message
import java.util.concurrent.CompletableFuture

class ChatRepository(
    val chatDataSource: ChatDataSource
) {

    fun sendTextMsg(text: String): CompletableFuture<Result<Message>> {
        return chatDataSource.sendTextMsg(text, false)
    }

    fun getAllMessages(lastMsgId: String?, myUid: String): CompletableFuture<Result<List<MessageGist>>> {
        return chatDataSource.getAllMessage(30, lastMsgId, myUid)
    }

    fun listenEvent() : LiveData<ChatEvent> {
        return chatDataSource.listenEvents()
    }
}