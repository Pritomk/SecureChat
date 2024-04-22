package com.example.securechat.data

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.securechat.data.model.ChannelGist
import com.example.securechat.utils.ChatService
import com.example.securechat.utils.UserInfo
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import io.getstream.chat.android.client.api.models.QueryChannelsRequest
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Filters
import io.getstream.chat.android.models.querysort.QuerySortByField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture

class HomeDataSource(
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    private val TAG = "com.example.securechat.data.HomeDataSource"

    fun generateQrCode(uid: String, context: Context): CompletableFuture<Result<Bitmap>> {
        val future = CompletableFuture<Result<Bitmap>>()
        coroutineScope.launch {
            val mWriter = MultiFormatWriter()
            try {
                val myPrivateKey = UserInfo(context).privateKey
                val myPublicKey = UserInfo(context).publicKey
                //BitMatrix class to encode entered text and set Width & Height
                val jsonObject = JsonObject()
                jsonObject.addProperty("uid", uid)
                jsonObject.addProperty("publicKey", myPublicKey)
                jsonObject.addProperty("privateKey", myPrivateKey)
                val mMatrix =
                    mWriter.encode(Gson().toJson(jsonObject), BarcodeFormat.QR_CODE, 300, 300)
                val mEncoder = BarcodeEncoder()
                val mBitmap = mEncoder.createBitmap(mMatrix) //creating bitmap of code
                future.complete(Result.Success(data = mBitmap))
            } catch (e: WriterException) {
                future.complete(Result.Error(exception = e))
            }
        }
        return future
    }

    fun createChannel(
        context: Context,
        myUid: String,
        newUserUid: String,
        publicKey: String,
        privateKey: String
    ): CompletableFuture<Result<ChannelGist>> {
        val future = CompletableFuture<Result<ChannelGist>>()
        coroutineScope.launch {
            val chatClient = ChatService.getChatClient()

            val myPrivateKey = UserInfo(context).privateKey!!
            val myPublicKey = UserInfo(context).publicKey!!

            val publicKeys = mapOf(
                myUid + "_pub" to myPublicKey,
                newUserUid + "_pub" to publicKey,
                myUid + "_private" to myPrivateKey,
                newUserUid + "_private" to privateKey
            )

            val channel =
                chatClient.createChannel("messaging", "", listOf(myUid, newUserUid), publicKeys)
            channel.enqueue {
                if (it.isFailure) {
                    Log.e(TAG, "Custom error" + Gson().toJson(it.errorOrNull()))
                    future.complete(Result.Error(Exception(it.errorOrNull()?.message)))
                }
                if (it.isSuccess) {
                    Log.d(TAG, "Success" + Gson().toJson(it.getOrNull()))
                    it.getOrNull()?.let { channel ->
                        getChannelGistFromChannel(channel, myUid)?.let { gist ->
                            future.complete(Result.Success(gist))
                        }
                    }
                }
            }

        }
        return future
    }

    fun getChannels(myUid: String, pageIndex: Int): CompletableFuture<Result<List<ChannelGist>>> {
        val future = CompletableFuture<Result<List<ChannelGist>>>()
        coroutineScope.launch {
            val request = QueryChannelsRequest(
                filter = Filters.and(
                    Filters.eq("type", "messaging"),
                    Filters.`in`("members", myUid),
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
                        val channelGists = channels.getOnlyChannelGist(myUid)
                        future.complete(Result.Success(channelGists))
                    }
                    val channelSTr = Gson().toJson(channels)
                    for (i in 0..channelSTr.length step 1000) {
                        Log.d(
                            TAG,
                            channelSTr.substring(
                                i,
                                if (i + 1000 < channelSTr.length) i + 1000 else channelSTr.length
                            )
                        )
                    }
                } else {
                    future.complete(Result.Error(Exception(result.errorOrNull()?.message)))
                }
            }
        }
        return future
    }

    private fun List<Channel>.getOnlyChannelGist(myUid: String): List<ChannelGist> {
        val channelGists = ArrayList<ChannelGist>()
        this.forEach {
            getChannelGistFromChannel(it, myUid)?.let { gist ->
                channelGists.add(gist)
            }
        }
        return channelGists
    }

    private fun getChannelGistFromChannel(channel: Channel, myUid: String): ChannelGist? {
        val other = channel.members.singleOrNull {
            it.user.id != myUid
        }
        return if (other != null) {
            val name = other.user.name
            val userId = other.user.id
            val channelId = channel.id
            ChannelGist(channelId, userId, name)
        } else {
            val name = channel.createdBy.name
            val userId = channel.createdBy.id
            val channelId = channel.createdBy.id
            ChannelGist(channelId, userId, name)
        }
    }
}