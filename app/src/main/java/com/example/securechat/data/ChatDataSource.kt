package com.example.securechat.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.securechat.utils.ChatService
import io.getstream.chat.android.client.api.models.Pagination
import io.getstream.chat.android.client.api.models.QueryChannelRequest
import io.getstream.chat.android.client.channel.state.ChannelState
import io.getstream.chat.android.client.events.ChatEvent
import io.getstream.chat.android.client.events.NewMessageEvent
import io.getstream.chat.android.client.utils.observable.Disposable
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.state.extensions.watchChannelAsState
import io.getstream.result.call.enqueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import java.util.Collections
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

    fun sendTextMsg(text: String, isRetry: Boolean): CompletableFuture<Result<Message>> {
        val future = CompletableFuture<Result<Message>>()
        coroutineScope.launch {
            val message = Message(
                text = text
            )
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

    fun getAllMessage(pageSize: Int, lastMsgId: String?): CompletableFuture<Result<List<Message>>> {
        val future = CompletableFuture<Result<List<Message>>>()

        coroutineScope.launch {
            val request: QueryChannelRequest = if (lastMsgId == null) {
                QueryChannelRequest()
                    .withMessages(pageSize)
            } else {
                QueryChannelRequest()
                    .withMessages(Pagination.GREATER_THAN, lastMsgId, pageSize)
            }
            channelClient?.query(request)?.enqueue { result ->
                if (result.isSuccess) {
                    val messages = result.getOrNull()?.messages
                    messages?.let {
                        future.complete(Result.Success(it.reversed()))
                    }
                } else {
                    future.complete(Result.Error(IOException(result.errorOrNull()?.message)))
                }
            }
        }

        return future
    }

}