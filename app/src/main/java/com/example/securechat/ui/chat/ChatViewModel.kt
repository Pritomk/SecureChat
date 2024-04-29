package com.example.securechat.ui.chat

import android.net.Uri
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
import com.example.securechat.data.model.MessageGist
import com.example.securechat.utils.AppCommonMethods
import com.example.securechat.utils.ChatSide
import io.getstream.chat.android.client.events.NewMessageEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.reflect.KFunction1

class ChatViewModel(
    private val runOnUiThread: KFunction1<Runnable, Unit>,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _chatTextForm = MutableLiveData<ChatTextState>()
    val chatTextForm: LiveData<ChatTextState> = _chatTextForm

    private val _channelGistData = MutableLiveData<ChannelGist>()
    val channelGistData: LiveData<ChannelGist> = _channelGistData

    private val _messages = MutableLiveData<List<MessageGist>>()
    val messages: LiveData<List<MessageGist>> = _messages

    private val _newMessage = MutableLiveData<MessageGist>()
    val newMessage: LiveData<MessageGist> = _newMessage

    private val _replyMessageId = MutableStateFlow(MessageGist(null, ChatSide.MY_CHAT))
    val replyMessage = _replyMessageId.asStateFlow()

    private val _rsaKeys = MutableStateFlow(HashMap<String, String>())
    val rsaKeys = _rsaKeys.asStateFlow()

    private val _otherUserId = MutableStateFlow("")
    val otherUserId = _otherUserId.asStateFlow()

    companion object {
        fun provideViewModelFactory(runOnUiThread: KFunction1<Runnable, Unit>, channelId: String) =
            viewModelFactory {
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

    fun listenEvents(lifeCycleOwner: LifecycleOwner, myUid: String) {
        chatRepository.listenEvent().observe(lifeCycleOwner) { event ->
            when (event) {
                is NewMessageEvent -> {
                    getMessage(myUid, event) {
                        runOnUiThread {
                            _newMessage.value = it
                        }
                    }
                }

                else -> {

                }
            }
        }
    }

    fun updateOtherUserId(otherUserId: String) {
        _otherUserId.value = otherUserId
    }

    private fun getMessage(
        myUid: String,
        event: NewMessageEvent,
        fetchedMessageGist: (MessageGist) -> Unit
    ) {
        val message = event.message
        val side = AppCommonMethods.checkSide(message.user, myUid)
        val decryptedMessage =
            AppCommonMethods.decryptMsg(side, message, rsaKeys.value, otherUserId.value, myUid)
        if (message.replyMessageId != null) {
            chatRepository.fetchSingleMessage(
                myUid,
                message.replyMessageId!!,
                rsaKeys.value,
                otherUserId.value
            ) {
                fetchedMessageGist(
                    MessageGist(
                        id = message.id,
                        side = side,
                        text = decryptedMessage,
                        createdAt = message.createdAt,
                        attachments = message.attachments,
                        replyMessage = it
                    )
                )
            }

        } else {
            fetchedMessageGist(
                MessageGist(
                    id = message.id,
                    side = side,
                    text = decryptedMessage,
                    createdAt = message.createdAt,
                    attachments = message.attachments
                )
            )
        }
    }


    fun sendingTextChange(sendingText: String?) {
        _chatTextForm.value = ChatTextState(sendingText)
    }

    fun sendText(sendingText: String) {
        if (replyMessage.value.id != null) {
            rsaKeys.value[otherUserId.value + "_pub"]?.let {
                chatRepository.sendTextMsg(
                    sendingText,
                    replyMessage.value.id,
                    it
                )
            }
        } else {
            rsaKeys.value[otherUserId.value + "_pub"]?.let {
                chatRepository.sendTextMsg(
                    sendingText,
                    null,
                    it
                )
            }
        }
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

    fun getAllMessages(lastMsgId: String?, myUid: String) {
        chatRepository.getAllMessages(lastMsgId, myUid, rsaKeys.value, otherUserId.value).thenAccept { result ->
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

    fun updateReplyMessage(messageGist: MessageGist) {
        _replyMessageId.value = messageGist
    }

    fun watchChannel(myUid: String) {
        chatRepository.watchChannel().thenAccept { result ->
            when (result) {
                is Result.Error -> TODO()
                is Result.Success -> {
                    runOnUiThread {
                        _rsaKeys.value = result.data as HashMap<String, String>
                        getAllMessages(null, myUid)
                    }
                }
            }

        }
    }


    fun reliabilityCheck(position: Int, callback: (String)->Unit) {
        _messages.value?.get(position).let {
            chatRepository.reliabilityCheck(it?.text!!).thenAccept { result ->
                when (result) {
                    is Result.Error -> TODO()
                    is Result.Success -> {
                        runOnUiThread {
                            _messages.value?.get(position)?.isFake = result.data.claims.isNullOrEmpty()
                            if (result.data.claims.isNullOrEmpty()) {
                                callback("This message does not containing news fact")
                            }
                        }
                    }
                }
            }
        }
    }

}