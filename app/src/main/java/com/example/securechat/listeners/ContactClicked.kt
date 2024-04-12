package com.example.securechat.listeners

import android.widget.ImageView
import com.example.securechat.data.model.ChannelGist

interface ContactClicked {
    fun contactClicked(channelGist: ChannelGist)

    fun lockedBtnClicked(channelGist: ChannelGist, imageView: ImageView)
}