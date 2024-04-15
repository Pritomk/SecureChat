package com.example.securechat.listeners

import com.example.securechat.data.model.MessageGist

interface SwipeListener {

    fun onSwipeLeft(messageGist: MessageGist?)

    fun onSwipeRight(messageGist: MessageGist?)
}