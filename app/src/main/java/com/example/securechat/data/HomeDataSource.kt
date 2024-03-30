package com.example.securechat.data

import android.graphics.Bitmap
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture

class HomeDataSource(
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

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

}