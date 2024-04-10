package com.example.securechat.ui.chat

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.securechat.data.ChatDataSource
import com.example.securechat.data.ChatRepository
import com.example.securechat.data.Result
import com.example.securechat.data.model.ChannelGist
import com.example.securechat.utils.ChatService
import io.getstream.chat.android.client.channel.ChannelClient
import io.getstream.chat.android.client.events.ChatEvent
import io.getstream.chat.android.client.events.NewMessageEvent
import io.getstream.chat.android.client.utils.observable.Disposable
import io.getstream.chat.android.models.Message
import kotlin.reflect.KFunction1

class ChatViewModel(
    private val runOnUiThread: KFunction1<Runnable, Unit>,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _chatTextForm = MutableLiveData<ChatTextState>()
    val chatTextForm: LiveData<ChatTextState> = _chatTextForm

    private val _channelGistData = MutableLiveData<ChannelGist>()
    val channelGistData: LiveData<ChannelGist> = _channelGistData

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    private val _newMessage = MutableLiveData<Message>()
    val newMessage: LiveData<Message> = _newMessage

    companion object {
        fun provideViewModelFactory(runOnUiThread: KFunction1<Runnable, Unit>, channelId: String) = viewModelFactory {
            initializer {
                ChatViewModel(
                    runOnUiThread = runOnUiThread,
                    chatRepository =
                    ChatRepository(
                        chatDataSource =
                        ChatDataSource(
                            channelId = channelId
                        )
                    )
                )
            }
        }
    }

    fun listenEvents(lifeCycleOwner: LifecycleOwner) {
        chatRepository.listenEvent().observe(lifeCycleOwner) {event ->
            when (event) {
                is NewMessageEvent -> {
                    runOnUiThread {
                        _newMessage.value = event.message
                    }
                }
                else -> {

                }
            }
        }
    }


    fun sendingTextChange(sendingText: String?) {
        _chatTextForm.value = ChatTextState(sendingText)
    }

    fun sendText(sendingText: String) {
        chatRepository.sendTextMsg(sendingText)
    }

    fun sendImage(sendingImage: Uri?) {
        if (sendingImage == null) {
            return
        }

    }

    fun updateChannelGistData(channelGist: ChannelGist?) {
        channelGist?.let {
            _channelGistData.value = it
        }
    }

    fun getAllMessages(lastMsgId: String?) {
        chatRepository.getAllMessages(lastMsgId).thenAccept {result ->
            when (result) {
                is Result.Error -> TODO()
                is Result.Success -> {
                    runOnUiThread {
                        _messages.value = result.data
                    }
                }
            }
        }
    }

}