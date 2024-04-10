package com.example.securechat.ui.chat.chatList

import androidx.recyclerview.widget.RecyclerView
import com.example.securechat.databinding.OtherChatBinding
import com.example.securechat.utils.AppCommonMethods
import io.getstream.chat.android.models.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class OtherChatVH(private val binding: OtherChatBinding): ChatListAdapter.ChatListViewHolder(binding.root) {
    override fun bind(message: Message) {
        binding.chatText.text = message.text
        binding.chatTime.text = AppCommonMethods.formatTimeFromMillis(message.createdAt?.time, "HH:mm a")
    }

}