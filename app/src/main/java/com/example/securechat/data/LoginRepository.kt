package com.example.securechat.data

import com.example.securechat.data.model.LoggedInUser
import java.util.concurrent.CompletableFuture

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    fun login(username: String, password: String): CompletableFuture<Result<LoggedInUser>> {
        // handle login
        val result = dataSource.login(username, password)

        result.thenApply {
            if (it is Result.Success) {
                setLoggedInUser(it.data)
            }
        }

        return result
    }

    fun register(username: String, password: String, name: String): CompletableFuture<Result<LoggedInUser>> {
        // handle login
        val result = dataSource.register(username, password, name)

        result.thenApply {
            if (it is Result.Success) {
                setLoggedInUser(it.data)
            }
        }

        return result
    }

    fun callTokenGeneration(uid: String): CompletableFuture<Result<String>> = dataSource.getToken(uid)

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}