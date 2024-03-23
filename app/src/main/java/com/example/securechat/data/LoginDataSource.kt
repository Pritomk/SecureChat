package com.example.securechat.data

import android.util.Log
import com.example.securechat.data.model.LoggedInUser
import com.example.securechat.networking.NetworkProvider
import com.example.securechat.networking.NetworkService
import com.example.securechat.networking.response.TokenResponse
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonObject
import io.getstream.chat.android.ui.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import java.io.IOException
import java.util.concurrent.CompletableFuture
import retrofit2.Callback
import retrofit2.Response

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    private val coroutineScope by lazy {
        CoroutineScope(Dispatchers.IO)
    }

    private val networkService by lazy {
        NetworkProvider.getRetrofitBuilder("https://encrypted-chat-server.onrender.com")!!.create(NetworkService::class.java)
    }
    fun login(username: String, password: String): CompletableFuture<Result<LoggedInUser>> {
        val future = CompletableFuture<Result<LoggedInUser>>()
        try {
            coroutineScope.launch {
                Firebase.auth.signInWithEmailAndPassword(username, password).addOnSuccessListener {
                    it?.user?.let {user ->
                        val loggedInUser = LoggedInUser(user.uid, "Default User")
                        future.complete(Result.Success(loggedInUser))
                    }
                }.addOnFailureListener {
                    future.complete(Result.Error(IOException("Error logging in", it)))
                }
            }
        } catch (e: Throwable) {
            future.complete(Result.Error(IOException("Error logging in", e)))
        }
        return future
    }

    fun register(username: String, password: String): CompletableFuture<Result<LoggedInUser>> {
        val future = CompletableFuture<Result<LoggedInUser>>()
        try {
            coroutineScope.launch {
                Firebase.auth.createUserWithEmailAndPassword(username, password).addOnSuccessListener {
                    it?.user?.let {user ->
                        val loggedInUser = LoggedInUser(user.uid, "Default User")
                        future.complete(Result.Success(loggedInUser))
                    }
                }.addOnFailureListener {
                    future.complete(Result.Error(IOException("Error logging in", it)))
                }
            }
        } catch (e: Throwable) {
            future.complete(Result.Error(IOException("Error logging in", e)))
        }
        return future
    }

    fun getToken(uid: String): CompletableFuture<Result<String>> {
        val future = CompletableFuture<Result<String>>()
        try {
            coroutineScope.launch {
                val jsonObject = JsonObject()
                jsonObject.addProperty("uid", uid)

                val result = kotlin.runCatching {
                    networkService.getToken(jsonObject)
                }

                if (result.isFailure) {
                    future.complete(Result.Error(IOException("Error in token generation ", result.exceptionOrNull())))
                    return@launch
                }

                val response = result.getOrNull()
                if (response == null) {
                    future.complete(Result.Error(IOException("Error in token generation ", result.exceptionOrNull())))
                    return@launch
                }
                future.complete(Result.Success(response.token))
            }
        } catch(e: Exception) {
            Log.e("pritom", "$e")
        }
        return future
    }

    fun logout() {
        Firebase.auth.signOut()
    }
}