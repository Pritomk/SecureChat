package com.example.securechat.ui.main

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.securechat.data.HomeDataSource
import com.example.securechat.data.HomeRepository
import com.example.securechat.data.Result
import io.getstream.chat.android.models.Channel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.reflect.KFunction1

class HomeViewModel(
    private val homeRepository: HomeRepository,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    private val runOnUiThread: KFunction1<Runnable, Unit>
): ViewModel() {

    private val _qrCode = MutableLiveData<Bitmap>()
    val qrCode: LiveData<Bitmap> = _qrCode

    private val _channels = MutableLiveData<List<Channel>>()
    val channels: LiveData<List<Channel>> = _channels

    private val _newChannel = MutableLiveData<Channel>()
    val newChannel: LiveData<Channel> = _newChannel
    companion object {
        fun provideViewModelFactory(runOnUiThread: KFunction1<Runnable, Unit>) = viewModelFactory {
            initializer {
                HomeViewModel(
                    homeRepository = HomeRepository(
                        dataSource = HomeDataSource()
                    ),
                    runOnUiThread = runOnUiThread
                )
            }
        }
    }

    fun generateQrCode(uid: String) {
        homeRepository.getGeneratedQrCode(uid).thenAccept {
            when (it) {
                is Result.Success -> {
                    runOnUiThread {
                        _qrCode.value = it.data
                    }
                }
                is Result.Error -> {
                    TODO()
                }
            }
        }
    }

    fun createChannel(myUid: String, newUserUid: String) {
        homeRepository.createChannel(myUid, newUserUid).thenAccept { result ->
            when (result) {
                is Result.Error -> TODO()
                is Result.Success -> {
                    _newChannel.value = result.data
                }
            }
        }
    }

    fun getExistingChannels(uid: String, pageIndex: Int) {
        homeRepository.getExistingChannels(uid, pageIndex).thenAccept { result ->
            when (result) {
                is Result.Error -> TODO()
                is Result.Success -> {
                    _channels.value = result.data
                }
            }
        }
    }

}