package com.example.securechat.networking

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object NetworkProvider {
    private var OLD_BASE_URL: String? = null
    private var retrofit: Retrofit? = null
    fun getRetrofitBuilder(BASE_URL: String) = run {
        this.OLD_BASE_URL = BASE_URL
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit
    }

    private fun getOkHttpClient(): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.readTimeout(120, TimeUnit.SECONDS)
        httpClient.connectTimeout(120, TimeUnit.SECONDS)
        httpClient.writeTimeout(120, TimeUnit.SECONDS)

        httpClient.addInterceptor(LoggingInterceptor())
        return httpClient.build()
    }

    class LoggingInterceptor: Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            return chain.proceed(request)
        }

    }
}