package com.example.securechat.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.securechat.data.model.MessageGist
import com.example.securechat.utils.AppCommonMethods
import com.example.securechat.utils.ChatService
import com.example.securechat.utils.crypto.CryptoManager
import io.getstream.chat.android.client.api.models.Pagination
import io.getstream.chat.android.client.api.models.QueryChannelRequest
import io.getstream.chat.android.client.events.ChatEvent
import io.getstream.chat.android.models.Message
import io.getstream.result.call.enqueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Base64
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

    fun watchChannel(): CompletableFuture<Result<Map<String, String>>> {
        val future = CompletableFuture<Result<Map<String, String>>>()

        coroutineScope.launch {
            channelClient?.watch()?.enqueue {
                if (it.isSuccess) {
                    future.complete(Result.Success(it.getOrNull()?.extraData as Map<String, String>))
                }
                if (it.isFailure) {
                    future.complete(Result.Error(IOException(it.errorOrNull()?.message)))
                }
            }
        }
        return future
    }

    fun sendTextMsg(
        text: String,
        isRetry: Boolean,
        replyMsgId: String?,
        pubKey: String
    ): CompletableFuture<Result<Message>> {
        val future = CompletableFuture<Result<Message>>()
        coroutineScope.launch {
            CryptoManager.encrypt(
                text,
                pubKey
            ) { encryptedData, encryptedAesKey ->
                val message: Message = if (replyMsgId != null) {
                    Message(
                        text = encryptedData,
                        replyMessageId = replyMsgId,
                        extraData = mapOf(
                            "aesKey" to encryptedAesKey
                        )
                    )
                } else {
                    Message(
                        text = encryptedData,
                        extraData = mapOf(
                            "aesKey" to encryptedAesKey
                        )
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

        }
        return future
    }

    fun getAllMessage(
        pageSize: Int,
        lastMsgId: String?,
        myUid: String,
        rsaKeys: Map<String, String>,
        otherUserId: String
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
                        future.complete(Result.Success(it.reversed().getMessageGist(myUid, rsaKeys, otherUserId)))
                    }
                } else {
                    future.complete(Result.Error(IOException(result.errorOrNull()?.message)))
                }
            }
        }

        return future
    }

    private fun List<Message>.getMessageGist(
        myUid: String,
        rsaKeys: Map<String, String>,
        otherUserId: String,
    ): List<MessageGist> {
        val messageList = ArrayList<MessageGist>()
        this.forEach { message ->

            val side = AppCommonMethods.checkSide(message.user, myUid)
            val decryptedText =
                AppCommonMethods.decryptMsg(side, message, rsaKeys, otherUserId, myUid)

            if (message.replyMessageId != null) {
                val matchedMsg = this.singleOrNull {
                    it.id == message.replyMessageId
                }
                if (matchedMsg != null) {
                    messageList.add(
                        MessageGist(
                            id = message.id,
                            side = side,
                            text = decryptedText,
                            createdAt = message.createdAt,
                            attachments = message.attachments,
                            replyMessage = AppCommonMethods.convertToMessageGist(matchedMsg, myUid)
                        )
                    )
                } else {
                    fetchSingleMessage(myUid, message.replyMessageId!!, rsaKeys, otherUserId) {
                        messageList.add(
                            MessageGist(
                                id = message.id,
                                side = side,
                                text = decryptedText,
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
                        side = side,
                        text = decryptedText,
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
        rsaKeys: Map<String, String>,
        otherUserId: String,
        fetchedMessage: (MessageGist) -> Unit
    ) {
        channelClient?.getMessage(id)?.enqueue { result ->
            if (result.isSuccess) {
                val message = result.getOrNull()
                val side = AppCommonMethods.checkSide(message!!.user, myUid)
                val decryptedText =
                    AppCommonMethods.decryptMsg(side, message, rsaKeys, otherUserId, myUid)
                message.let {
                    val messageGist = MessageGist(
                        id = it.id,
                        side = side,
                        text = decryptedText,
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