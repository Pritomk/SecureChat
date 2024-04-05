package com.example.securechat.ui.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.securechat.R
import com.example.securechat.data.model.ChannelGist
import com.example.securechat.databinding.ActivityChatBinding
import com.example.securechat.utils.AppConstants
import io.getstream.chat.android.client.events.MemberAddedEvent
import io.getstream.chat.android.client.events.NewMessageEvent
import io.getstream.chat.android.state.utils.Event

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    fun open(context: Context, channelGist: ChannelGist) {
        val intent = Intent(context, ChatActivity::class.java)
        intent.putExtra(AppConstants.CHANNEL_GIST_NAME, channelGist)
        context.startActivity(intent)
    }
    fun getIntentData(): ChannelGist? {
        return intent?.getParcelableExtra(AppConstants.CHANNEL_GIST_NAME)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}