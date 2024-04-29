package com.example.securechat.ui.chat.chatList

import android.view.ContextMenu
import android.view.View
import com.bumptech.glide.Glide
import com.example.securechat.data.model.MessageGist
import com.example.securechat.databinding.MyChatBinding
import com.example.securechat.utils.AppCommonMethods
import com.example.securechat.utils.ChatSide

class MyChatVH(private val binding: MyChatBinding) : ChatListAdapter.ChatListViewHolder(binding.root) {
    override fun bind(messageGist: MessageGist, lastMessageSide: ChatSide) {
        if (messageGist.text.isNullOrBlank()) {
            binding.chatText.visibility = View.GONE
        } else {
            binding.chatText.visibility = View.VISIBLE
            binding.chatText.text = messageGist.text
        }
        binding.chatTime.text = AppCommonMethods.formatTimeFromMillis(messageGist.createdAt?.time, "HH:mm a")
        when (lastMessageSide) {
            ChatSide.MY_CHAT -> {
                AppCommonMethods.setMargins(binding.parentCl, 0, 4, 0, 4)
            }
            ChatSide.OTHER_CHAT -> {
                AppCommonMethods.setMargins(binding.parentCl, 0, 8, 0, 4)
            }
        }
        if (!messageGist.attachments.isNullOrEmpty()) {
            Glide.with(binding.attachedImg.context).load(messageGist.attachments[0].imageUrl).into(binding.attachedImg)
            binding.attachedImg.visibility = View.VISIBLE
        } else {
            binding.attachedImg.visibility = View.GONE
        }
        if (messageGist.replyMessage != null) {
            binding.replyText.text = messageGist.replyMessage.text
            binding.replyText.visibility = View.VISIBLE
        } else {
            binding.replyText.visibility = View.GONE
        }
        if (messageGist.isFake == true) {
            binding.infoTag.visibility = View.VISIBLE
        } else {
            binding.infoTag.visibility = View.GONE
        }
        binding.root.setOnCreateContextMenuListener(this)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menu?.add(bindingAdapterPosition, 0, 0, "Reliability Check")
    }

}