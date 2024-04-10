package com.example.securechat.data

import android.net.Uri
import android.util.Log
import com.example.securechat.data.model.LoggedInUser
import com.example.securechat.networking.NetworkProvider
import com.example.securechat.networking.NetworkService
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.CompletableFuture

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    private val coroutineScope by lazy {
        CoroutineScope(Dispatchers.IO)
    }

    private val networkService by lazy {
        NetworkProvider.getRetrofitBuilder("https://secure-chat-ccz5.onrender.com")!!.create(NetworkService::class.java)
    }
    fun login(username: String, password: String): CompletableFuture<Result<LoggedInUser>> {
        val future = CompletableFuture<Result<LoggedInUser>>()
        try {
            coroutineScope.launch {
                Firebase.auth.signInWithEmailAndPassword(username, password).addOnSuccessListener {
                    it?.user?.let {user ->
                        user.displayName?.let { name ->
                            val loggedInUser = LoggedInUser(user.uid, name)
                            future.complete(Result.Success(loggedInUser))
                        }
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

    fun register(username: String, password: String, name: String): CompletableFuture<Result<LoggedInUser>> {
        val future = CompletableFuture<Result<LoggedInUser>>()
        try {
            coroutineScope.launch {
                Firebase.auth.createUserWithEmailAndPassword(username, password).addOnSuccessListener {
                    it?.user?.let {user ->
                        setUserName(name, user, future)
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

    private fun setUserName(name: String, user: FirebaseUser, future: CompletableFuture<Result<LoggedInUser>>) {
        val loggedInUser = LoggedInUser(
            userId = user.uid,
            displayName = name
        )
        val profileUpdate = UserProfileChangeRequest.Builder()
            .setPhotoUri(Uri.parse("https://random.imagecdn.app/60/60"))
            .setDisplayName(name)
            .build()
        user.updateProfile(profileUpdate).addOnSuccessListener {
            future.complete(Result.Success(loggedInUser))
        }.addOnFailureListener {
            future.complete(Result.Error(IOException("Update profile error", it)))
        }
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