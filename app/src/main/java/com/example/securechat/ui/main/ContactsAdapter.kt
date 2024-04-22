package com.example.securechat.ui.main

import android.content.Context
import android.database.Observable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.securechat.R
import com.example.securechat.data.model.ChannelGist
import com.example.securechat.databinding.ContactItemBinding
import com.example.securechat.listeners.ContactClicked
import com.example.securechat.utils.ViewAnimations

class ContactsAdapter(
    private val context: Context,
    private val listener: ContactClicked
) : RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>() {

    private val contacts = ArrayList<ChannelGist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val binding = ContactItemBinding.inflate(LayoutInflater.from(parent.context))
        return ContactsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val singleChannelGist = contacts[position]
        holder.bind(context, singleChannelGist, listener)
        holder.itemView.setOnClickListener {
            listener.contactClicked(singleChannelGist)
        }
    }

    fun updateData(newData: List<ChannelGist>?) {
        newData?.let {
            contacts.clear()
            contacts.addAll(newData)

            notifyDataSetChanged()
        }
    }

    fun addToInitial(newChannel: ChannelGist?) {
        newChannel?.let {
            contacts.add(0, newChannel)
            notifyDataSetChanged()
        }
    }

    inner class ContactsViewHolder(private val binding: ContactItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, channelGist: ChannelGist, listener: ContactClicked) {
            Glide.with(context).load("https://xsgames.co/randomusers/avatar.php?g=female").into(binding.profilePic)
            binding.personName.text = channelGist.name
            if (channelGist.isLocked) {
                binding.lockBtn.setImageResource(R.drawable.ic_locked)
            } else {
                binding.lockBtn.setImageResource(R.drawable.ic_unlocked)
            }
            binding.lockBtn.setOnClickListener {
                listener.lockedBtnClicked(channelGist, binding.lockBtn)
            }
        }
    }
}