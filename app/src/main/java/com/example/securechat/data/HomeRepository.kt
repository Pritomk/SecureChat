package com.example.securechat.data

import android.graphics.Bitmap
import java.util.concurrent.CompletableFuture

class HomeRepository(
    private val dataSource: HomeDataSource
) {
    fun getGeneratedQrCode(uid: String): CompletableFuture<Result<Bitmap>> {
        return dataSource.generateQrCode(uid)
    }
}