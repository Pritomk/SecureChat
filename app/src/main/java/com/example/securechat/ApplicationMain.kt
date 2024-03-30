package com.example.securechat

import android.app.Application
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.securechat.utils.ChuckerCrashHandler
import com.hunter.library.okhttp.OkHttpHooker

class ApplicationMain: Application() {
    companion object {
        private var mInstance: ApplicationMain? = null
        fun getInstance(): ApplicationMain {
            return mInstance ?: ApplicationMain()
        }
    }
    override fun onCreate() {
        super.onCreate()

        mInstance = this

        if (OkHttpHooker.globalInterceptors.isEmpty()) {
            OkHttpHooker.installInterceptor(ChuckerInterceptor(this))
        }

        val systemHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(ChuckerCrashHandler(systemHandler, this))

    }
}