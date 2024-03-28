package com.example.securechat

import android.app.Application
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.securechat.utils.ChuckerCrashHandler
import com.hunter.library.okhttp.OkHttpHooker

class ApplicationMain: Application() {
    override fun onCreate() {
        super.onCreate()

        if (OkHttpHooker.globalInterceptors.isEmpty()) {
            OkHttpHooker.installInterceptor(ChuckerInterceptor(this))
        }

        val systemHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(ChuckerCrashHandler(systemHandler, this))

    }
}