package com.example.securechat.ui.chat

import io.getstream.chat.android.ui.feature.messages.MessageListFragment
import io.getstream.chat.android.ui.feature.messages.composer.MessageComposerView
import io.getstream.chat.android.ui.feature.messages.header.MessageListHeaderView
import io.getstream.chat.android.ui.feature.messages.list.MessageListView

class CustomMessageListFragment : MessageListFragment() {

    override fun setupMessageListHeader(messageListHeaderView: MessageListHeaderView) {
        super.setupMessageListHeader(messageListHeaderView)
        // Customize message list header view

        // For example, set a custom listener for the back button
        messageListHeaderView.setBackButtonClickListener {

        }
    }

    override fun setupMessageList(messageListView: MessageListView) {
        super.setupMessageList(messageListView)

    }

    override fun setupMessageComposer(messageComposerView: MessageComposerView) {
        super.setupMessageComposer(messageComposerView)
        // Customize message composer view
    }
}