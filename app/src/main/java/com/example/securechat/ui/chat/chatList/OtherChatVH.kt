package com.example.securechat.ui.chat.chatList

import android.view.View
import com.bumptech.glide.Glide
import com.example.securechat.data.model.MessageGist
import com.example.securechat.databinding.OtherChatBinding
import com.example.securechat.utils.AppCommonMethods
import com.example.securechat.utils.ChatSide

class OtherChatVH(private val binding: OtherChatBinding): ChatListAdapter.ChatListViewHolder(binding.root) {
    override fun bind(message: Message) {
        binding.chatText.text = message.text
        binding.chatTime.text = AppCommonMethods.formatTimeFromMillis(message.createdAt?.time, "HH:mm a")
        if (messageGist.text.isNullOrBlank()) {
            binding.chatText.visibility = View.GONE
        } else {
            binding.chatText.visibility = View.VISIBLE
            binding.chatText.text = messageGist.text
        }
        if (!messageGist.attachments.isNullOrEmpty()) {
            Glide.with(binding.attachedImg.context).load(messageGist.attachments[0].imageUrl).into(binding.attachedImg)
            binding.attachedImg.visibility = View.VISIBLE
        }
    }

}