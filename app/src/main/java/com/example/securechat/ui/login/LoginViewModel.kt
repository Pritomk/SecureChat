package com.example.securechat.ui.login

import android.app.Activity
import android.app.Application
import android.content.Context
import android.media.session.MediaSession.Token
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.example.securechat.data.LoginRepository
import com.example.securechat.data.Result

import com.example.securechat.R
import com.example.securechat.data.model.LoggedInUser
import com.example.securechat.utils.UserInfo
import java.util.concurrent.CompletableFuture

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _tokenResult = MutableLiveData<String>()
    val tokenResult: LiveData<String> = _tokenResult

    private val _authenticationType = MutableLiveData(
        AuthenticationState.LOGIN
    )
    val authenticationType = _authenticationType;

    fun authenticate(context: Context, username: String, password: String, name: String?) {
        // can be launched in a separate asynchronous job
        var result: CompletableFuture<Result<LoggedInUser>>
        authenticationType.value?.let {
            result = when(it) {
                AuthenticationState.LOGIN -> {
                    loginRepository.login(username, password)
                }

                AuthenticationState.REGISTER -> {
                    loginRepository.register(username, password, name!!)
                }
            }

            result.thenApply {res ->
                if (res is Result.Error) {
                    _loginResult.value = LoginResult(error = R.string.login_failed)
                } else if (res is Result.Success) {
                    _loginResult.value =
                        LoginResult(
                            success = res.data
                        )
                }
            }
        }
    }

    fun handleTokenGeneration(activity: LoginActivity , uid: String) {
        loginRepository.callTokenGeneration(uid).thenApply { tokenResult ->
            if (tokenResult is Result.Success) {
                activity.runOnUiThread {
                    _tokenResult.value = tokenResult.data
                }
            }
        }
    }

    fun loginDataChanged(username: String, password: String, name: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    fun authenticationTypeChange() {
        authenticationType.value?.let {
            when (it) {
                AuthenticationState.LOGIN -> _authenticationType.value = AuthenticationState.REGISTER
                AuthenticationState.REGISTER -> _authenticationType.value = AuthenticationState.LOGIN
            }
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}