package com.example.securechat.data

import androidx.lifecycle.LiveData
import com.example.securechat.data.model.MessageGist
import io.getstream.chat.android.client.events.ChatEvent
import io.getstream.chat.android.models.Message
import java.util.concurrent.CompletableFuture

class ChatRepository(
    val chatDataSource: ChatDataSource
) {

    fun watchChannel(): CompletableFuture<Result<Map<String, String>>> {
        return chatDataSource.watchChannel()
    }

    fun sendTextMsg(
        text: String,
        replyMsgId: String?,
        pubKey: String
    ): CompletableFuture<Result<Message>> {
        return chatDataSource.sendTextMsg(text, false, replyMsgId, pubKey)
    }

    fun getAllMessages(
        lastMsgId: String?,
        myUid: String,
        rsaKeys: Map<String, String>,
        otherUserId: String
        ): CompletableFuture<Result<List<MessageGist>>> {
        return chatDataSource.getAllMessage(30, lastMsgId, myUid, rsaKeys, otherUserId)
    }

    fun listenEvent(): LiveData<ChatEvent> {
        return chatDataSource.listenEvents()
    }

    fun fetchSingleMessage(
        myUid: String,
        id: String,
        rsaKeys: Map<String, String>,
        otherUserId: String,
        fetchedMessage: (MessageGist) -> Unit
    ) {
        chatDataSource.fetchSingleMessage(myUid, id, rsaKeys, otherUserId, fetchedMessage)
    }
}