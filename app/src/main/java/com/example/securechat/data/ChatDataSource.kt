package com.example.securechat.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.securechat.data.model.MessageGist
import com.example.securechat.utils.AppCommonMethods
import com.example.securechat.utils.ChatService
import io.getstream.chat.android.client.api.models.Pagination
import io.getstream.chat.android.client.api.models.QueryChannelRequest
import io.getstream.chat.android.client.events.ChatEvent
import io.getstream.chat.android.models.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.CompletableFuture

class ChatDataSource(
    private val channelId: String?,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    private val channelClient by lazy {
        channelId?.let {
            ChatService.getChannelClient(it)
        }
    }

    fun listenEvents(): LiveData<ChatEvent> {

        val newEvent = MutableLiveData<ChatEvent>()

        coroutineScope.launch {
            channelClient?.subscribe { event: ChatEvent ->
                newEvent.value = event
            }
        }
        return newEvent
    }

    fun sendTextMsg(
        text: String,
        isRetry: Boolean,
        replyMsgId: String?
    ): CompletableFuture<Result<Message>> {
        val future = CompletableFuture<Result<Message>>()
        coroutineScope.launch {
            val message: Message = if (replyMsgId != null) {
                Message(
                    text = text,
                    replyMessageId = replyMsgId
                )
            } else {
                Message(
                    text = text
                )
            }
            channelClient?.sendMessage(message, isRetry)?.enqueue {
                if (it.isFailure) {
                    Log.d("pritom", "error ${it.errorOrNull()?.message}")
                    future.complete(Result.Error(IOException(it.errorOrNull()?.message)))
                }
                if (it.isSuccess) {
                    Log.d("pritom", "${it.getOrNull()}")
                    it.getOrNull()?.let { message ->
                        future.complete(Result.Success(message))
                    }
                }
            }
        }
        return future
    }

    fun getAllMessage(
        pageSize: Int,
        lastMsgId: String?,
        myUid: String
    ): CompletableFuture<Result<List<MessageGist>>> {
        val future = CompletableFuture<Result<List<MessageGist>>>()

        coroutineScope.launch {
            val request: QueryChannelRequest = if (lastMsgId == null) {
                QueryChannelRequest()
                    .withMessages(pageSize)
            } else {
                QueryChannelRequest()
                    .withMessages(Pagination.LESS_THAN, lastMsgId, pageSize)
            }
            channelClient?.query(request)?.enqueue { result ->
                if (result.isSuccess) {
                    val messages = result.getOrNull()?.messages
                    messages?.let {
                        future.complete(Result.Success(it.reversed().getMessageGist(myUid)))
                    }
                } else {
                    future.complete(Result.Error(IOException(result.errorOrNull()?.message)))
                }
            }
        }

        return future
    }

    private fun List<Message>.getMessageGist(myUid: String): List<MessageGist> {
        val messageList = ArrayList<MessageGist>()
        this.forEach { message ->
            if (message.replyMessageId != null) {
                val matchedMsg = this.singleOrNull {
                    it.id == message.replyMessageId
                }
                if (matchedMsg != null) {
                    messageList.add(
                        MessageGist(
                            id = message.id,
                            side = AppCommonMethods.checkSide(message.user, myUid),
                            text = message.text,
                            createdAt = message.createdAt,
                            attachments = message.attachments,
                            replyMessage = AppCommonMethods.convertToMessageGist(matchedMsg, myUid)
                        )
                    )
                } else {
                    fetchSingleMessage(myUid, message.replyMessageId!!) {
                        messageList.add(
                            MessageGist(
                                id = message.id,
                                side = AppCommonMethods.checkSide(message.user, myUid),
                                text = message.text,
                                createdAt = message.createdAt,
                                attachments = message.attachments,
                                replyMessage = it
                            )
                        )
                    }
                }
            } else {
                messageList.add(
                    MessageGist(
                        id = message.id,
                        side = AppCommonMethods.checkSide(message.user, myUid),
                        text = message.text,
                        createdAt = message.createdAt,
                        attachments = message.attachments
                    )
                )
            }
        }
        return messageList
    }

    fun fetchSingleMessage(
        myUid: String,
        id: String,
        fetchedMessage: (MessageGist) -> Unit
    ) {
        channelClient?.getMessage(id)?.enqueue { result ->
            if (result.isSuccess) {
                val message = result.getOrNull()
                message?.let {
                    val messageGist = MessageGist(
                        id = it.id,
                        side = AppCommonMethods.checkSide(it.user, myUid),
                        text = it.text,
                        createdAt = it.createdAt,
                        attachments = it.attachments,
                        replyMessage = null
                    )
                    fetchedMessage(messageGist)
                }
            } else {

            }
        }
    }

}