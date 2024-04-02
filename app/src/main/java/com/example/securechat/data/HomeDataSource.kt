package com.example.securechat.data

import android.graphics.Bitmap
import android.util.Log
import com.example.securechat.utils.ChatService
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import io.getstream.chat.android.client.api.models.QueryChannelsRequest
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Filters
import io.getstream.chat.android.models.Member
import io.getstream.chat.android.models.querysort.QuerySortByField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.concurrent.CompletableFuture

class HomeDataSource(
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    private val TAG = "com.example.securechat.data.HomeDataSource"

    fun generateQrCode(uid: String): CompletableFuture<Result<Bitmap>> {
        val future = CompletableFuture<Result<Bitmap>>()
        coroutineScope.launch {
            val mWriter = MultiFormatWriter()
            try {
                //BitMatrix class to encode entered text and set Width & Height
                val jsonObject = JsonObject()
                jsonObject.addProperty("uid", uid)
                val mMatrix = mWriter.encode(Gson().toJson(jsonObject), BarcodeFormat.QR_CODE, 300, 300)
                val mEncoder = BarcodeEncoder()
                val mBitmap = mEncoder.createBitmap(mMatrix) //creating bitmap of code
                future.complete(Result.Success(data = mBitmap))
            } catch (e: WriterException) {
                future.complete(Result.Error(exception = e))
            }
        }
        return future
    }

    fun createChannel(myUid: String, newUserUid: String): CompletableFuture<Result<Channel>> {
        val future = CompletableFuture<Result<Channel>>()
        coroutineScope.launch {
            val chatClient = ChatService.getChatClient()

            val channel = chatClient.createChannel("messaging", "", listOf(myUid, newUserUid), emptyMap())
            channel.enqueue {
                if (it.isFailure) {
                    Log.e(TAG, "Custom error" + Gson().toJson(it.errorOrNull()))
                    future.complete(Result.Error(Exception(it.errorOrNull()?.message)))
                }
                if (it.isSuccess) {
                    Log.d(TAG, "Success" + Gson().toJson(it.getOrNull()))
                    it.getOrNull()?.let {channel ->
                        future.complete(Result.Success(channel))
                    }
                }
            }

        }
        return future
    }

    fun getChannels(myUid: String, pageIndex: Int): CompletableFuture<Result<List<Channel>>> {
        val future = CompletableFuture<Result<List<Channel>>>()
        coroutineScope.launch {
            val request = QueryChannelsRequest(
                filter = Filters.and(
                    Filters.eq("type", "messaging"),
                    Filters.`in`("members", listOf(myUid)),
                ),
                offset = 0,
                limit = 10,
                querySort = QuerySortByField.descByName("has_unread")
            ).apply {
                watch = true
                state = true
            }
            val chatClient = ChatService.getChatClient()
            chatClient.queryChannels(request).enqueue { result ->
                if (result.isSuccess) {
                    val channels: List<Channel>? = result.getOrNull()
                    channels?.let {
                        future.complete(Result.Success(channels))
                    }
                    Log.d(TAG, "$channels")
                } else {
                    future.complete(Result.Error(Exception(result.errorOrNull()?.message)))
                }
            }
        }
        return future
    }

}