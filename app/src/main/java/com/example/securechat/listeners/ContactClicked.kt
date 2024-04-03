package com.example.securechat.listeners

import com.example.securechat.data.model.ChannelGist

interface ContactClicked {
    fun contactClicked(channelGist: ChannelGist)
}