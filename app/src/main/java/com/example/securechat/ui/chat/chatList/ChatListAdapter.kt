package com.example.securechat.ui.chat.chatList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.example.securechat.data.model.MessageGist
import com.example.securechat.databinding.HiddenLayoutBinding
import com.example.securechat.databinding.MyChatBinding
import com.example.securechat.databinding.OtherChatBinding
import com.example.securechat.utils.AppConstants
import com.example.securechat.utils.ChatSide

class ChatListAdapter(
    private val context: Context,
    private val messages: ArrayList<MessageGist> = ArrayList()
) : RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when (messages[position].side) {
            ChatSide.MY_CHAT -> AppConstants.MY_CHAT_CODE
            ChatSide.OTHER_CHAT -> AppConstants.OTHER_CHAT_CODE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        return when (viewType) {
            AppConstants.MY_CHAT_CODE -> {
                val binding = MyChatBinding.inflate(LayoutInflater.from(parent.context))
                MyChatVH(binding)
            }
            AppConstants.OTHER_CHAT_CODE -> {
                val binding = OtherChatBinding.inflate(LayoutInflater.from(parent.context))
                OtherChatVH(binding)
            }

            else -> {
                val binding = HiddenLayoutBinding.inflate(LayoutInflater.from(parent.context))
                HiddenVH(binding)
            }
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        holder.bind(messages[position], if (position != 0) messages[position-1].side else ChatSide.OTHER_CHAT)
    }

    fun updateMessageList(newList: List<MessageGist>) {
        messages.addAll(newList)
        notifyDataSetChanged()
    }

    fun addToFirst(newMessageGist: MessageGist) {
        messages.add(0, newMessageGist)
        notifyDataSetChanged()
    }

    fun getLastItem() = if (messages.isNotEmpty()) messages.last() else null

    fun getItemAt(position: Int) = if (messages.isNotEmpty() && position >= 0 && position < messages.size) messages[position] else null


    sealed class ChatListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(messageGist: MessageGist, lastMessageSide: ChatSide)
    }

    class HiddenVH(binding: HiddenLayoutBinding): ChatListViewHolder(binding.root) {
        override fun bind(messageGist: MessageGist, lastMessageSide: ChatSide) {

        }
    }
}