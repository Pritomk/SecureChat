package com.example.securechat.data

import android.graphics.Bitmap
import io.getstream.chat.android.models.Channel
import java.util.concurrent.CompletableFuture

class HomeRepository(
    private val dataSource: HomeDataSource
) {
    fun getGeneratedQrCode(uid: String): CompletableFuture<Result<Bitmap>> {
        return dataSource.generateQrCode(uid)
    }

    fun createChannel(myUid: String, newUserUid: String): CompletableFuture<Result<Channel>> {
        return dataSource.createChannel(myUid, newUserUid)
    }

    fun getExistingChannels(myUid: String, pageIndex: Int): CompletableFuture<Result<List<Channel>>> {
        return dataSource.getChannels(myUid, pageIndex)
    }
}