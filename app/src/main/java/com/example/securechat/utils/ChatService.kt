package com.example.securechat.utils

import android.content.Context
import com.example.securechat.ApplicationMain
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory

object ChatService {

    private val offlinePluginFactory by lazy {
        StreamOfflinePluginFactory(appContext = ApplicationMain.getInstance().applicationContext)
    }
    private val statePluginFactory by lazy {
        StreamStatePluginFactory(
            config = StatePluginConfig(
                backgroundSyncEnabled = true,
                userPresence = true,
            ),
            appContext = ApplicationMain.getInstance().applicationContext,
        )
    }
    private lateinit var chatClient: ChatClient
    fun getChatClient(): ChatClient {
        if (!::chatClient.isInitialized) {
            chatClient = ChatClient.Builder(AppConstants.API_KEY, ApplicationMain.getInstance().applicationContext)
                .withPlugins(offlinePluginFactory, statePluginFactory)
                .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
                .build()
        }
        return chatClient
    }
}