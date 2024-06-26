package com.example.securechat.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import com.example.securechat.ui.main.HomeActivity
import com.example.securechat.databinding.ActivityLoginBinding

import com.example.securechat.R
import com.example.securechat.RoutingActivity
import com.example.securechat.data.model.LoggedInUser
import com.example.securechat.utils.UserInfo

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    fun open(context: Context) {
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading
        val changeTv = binding.changeTv
        val name = binding.name!!

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())[LoginViewModel::class.java]

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
            if (login.text.toString().equals(getString(R.string.action_sign_up_short), ignoreCase = true)) {
                if (loginState.nameError != null) {
                    name.error = getString(loginState.nameError)
                }
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity) {
            val loginResult = it ?: return@observe

            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                UserInfo(this@LoginActivity).userId = loginResult.success.userId
                UserInfo(this@LoginActivity).displayName = loginResult.success.displayName
                callTokenGeneration(loginResult.success)
                setResult(Activity.RESULT_OK)
            }
        }

        loginViewModel.tokenResult.observe(this@LoginActivity) {
            UserInfo(this).accessToken = it
            updateUiWithUser()
        }

        loginViewModel.authenticationType.observe(this@LoginActivity) {
            when (it) {
                AuthenticationState.LOGIN -> {
                    login.text = resources.getText(R.string.action_sign_in_short)
                    changeTv?.text = resources.getText(R.string.register_now)
                    name.visibility = View.GONE
                }
                AuthenticationState.REGISTER -> {
                    login.text = resources.getText(R.string.action_sign_up_short)
                    changeTv?.text = resources.getText(R.string.login_now)
                    name.visibility = View.VISIBLE
                }
                null -> TODO("Handle null scenario for authentication state")
            }
        }

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString(),
                name.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString(),
                    name.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.authenticate(this@LoginActivity, username.text.toString(), password.text.toString(), name.text?.toString())
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.authenticate(this@LoginActivity, username.text.toString(), password.text.toString(), name.text?.toString())
            }
        }

        changeTv?.setOnClickListener {
            loginViewModel.authenticationTypeChange()
        }

    }

    private fun callTokenGeneration(success: LoggedInUser) {
        loginViewModel.handleTokenGeneration(this, success.userId);
    }

    private fun updateUiWithUser() {
        binding.loading.visibility = View.GONE
        val welcome = getString(R.string.welcome)
        startActivity(Intent(this, RoutingActivity::class.java))
        finish()
        Toast.makeText(
            applicationContext,
            "$welcome",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}