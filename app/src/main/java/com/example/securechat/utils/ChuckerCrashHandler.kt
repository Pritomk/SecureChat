package com.example.securechat.utils

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.RetentionManager

class ChuckerCrashHandler(
    private val defaultHandler: Thread.UncaughtExceptionHandler? = null,
    applicationContext: Context
): Thread.UncaughtExceptionHandler {
    private val chuckerCollector: ChuckerCollector by lazy {
        ChuckerCollector(
            context = applicationContext,
            showNotification = true,
            retentionPeriod = RetentionManager.Period.FOREVER
        )
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        chuckerCollector.onError("error", e)
        defaultHandler?.uncaughtException(t, e)
    }


}