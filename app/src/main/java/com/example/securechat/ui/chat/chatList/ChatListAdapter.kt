package com.example.securechat.ui.chat.chatList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.securechat.databinding.HiddenLayoutBinding
import com.example.securechat.databinding.MyChatBinding
import com.example.securechat.databinding.OtherChatBinding
import com.example.securechat.utils.AppConstants
import com.example.securechat.utils.CommonMethods
import io.getstream.chat.android.models.Message

class ChatListAdapter(
    private val context: Context,
    private val messages: ArrayList<Message> = ArrayList()
) : RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (CommonMethods(context).checkFromUser(messages[position]) == true)
            AppConstants.MY_CHAT_CODE
        else
            AppConstants.OTHER_CHAT_CODE
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
        holder.bind(messages[position])
    }

    fun updateMessageList(newList: List<Message>) {
        messages.addAll(newList)
        notifyDataSetChanged()
    }

    fun addToFirst(newMessage: Message) {
        messages.add(0, newMessage)
        notifyDataSetChanged()
    }

    fun getLastItem() = if (messages.isNotEmpty()) messages.first() else null

    sealed class ChatListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(message: Message)
    }

    class HiddenVH(binding: HiddenLayoutBinding): ChatListViewHolder(binding.root) {
        override fun bind(message: Message) {

        }
    }
}