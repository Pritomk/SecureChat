package com.example.securechat.data

import android.graphics.Bitmap
import com.example.securechat.data.model.ChannelGist
import io.getstream.chat.android.models.Channel
import java.util.concurrent.CompletableFuture

class HomeRepository(
    private val dataSource: HomeDataSource
) {
    fun getGeneratedQrCode(uid: String): CompletableFuture<Result<Bitmap>> {
        return dataSource.generateQrCode(uid)
    }

    fun createChannel(myUid: String, newUserUid: String): CompletableFuture<Result<ChannelGist>> {
        return dataSource.createChannel(myUid, newUserUid)
    }

    fun getExistingChannels(myUid: String, pageIndex: Int): CompletableFuture<Result<List<ChannelGist>>> {
        return dataSource.getChannels(myUid, pageIndex)
    }
}