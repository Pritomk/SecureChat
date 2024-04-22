package com.example.securechat.data

import android.content.Context
import android.graphics.Bitmap
import com.example.securechat.data.model.ChannelGist
import java.util.concurrent.CompletableFuture

class HomeRepository(
    private val dataSource: HomeDataSource
) {
    fun getGeneratedQrCode(uid: String, context: Context): CompletableFuture<Result<Bitmap>> {
        return dataSource.generateQrCode(uid, context)
    }

    fun createChannel(context: Context, myUid: String, newUserUid: String, publicKey: String, privateKey: String): CompletableFuture<Result<ChannelGist>> {
        return dataSource.createChannel(context,myUid, newUserUid, publicKey, privateKey)
    }

    fun getExistingChannels(myUid: String, pageIndex: Int): CompletableFuture<Result<List<ChannelGist>>> {
        return dataSource.getChannels(myUid, pageIndex)
    }
}