package com.example.securechat

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.securechat.utils.ActivityLauncher
import com.example.securechat.utils.AppConstants
import com.example.securechat.utils.CommonMethods
import com.example.securechat.utils.DialogHelper
import com.example.securechat.utils.UserInfo
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.User
import io.getstream.result.Result

class RoutingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { true }

        checkNetwork(CommonMethods(this@RoutingActivity).isConnected)
    }

    private fun checkNetwork(isConnected: Boolean) {
        if (isConnected) {
            routeToPage()
        } else {
            DialogHelper.simpleDialog(this@RoutingActivity, resources.getString(R.string.no_internet_msg)) {
                checkNetwork(CommonMethods(this@RoutingActivity).isConnected)
            }
        }
    }

    private fun routeToPage() {
        if (UserInfo(this).accessToken.isNullOrEmpty()) {
            ActivityLauncher.launchLogin(this@RoutingActivity)
        } else {
            initializeUse();
        }
    }

    private fun initializeUse() {
        val userId = UserInfo(this@RoutingActivity).userId ?: ""
        val userName = UserInfo(this@RoutingActivity).displayName ?: ""
        val accessToken = UserInfo(this@RoutingActivity).accessToken ?: ""

        if (userId.isEmpty() || accessToken.isEmpty() || userName.isEmpty()) {
            sendToLogin()
        }
        val userExtraData: Map<String, Any> = mutableMapOf(
            "name" to userName
        )
        val user = User(
            id = userId,
            extraData = userExtraData
        )

        val client = ChatClient.Builder(AppConstants.API_KEY, this@RoutingActivity).build()

        client.connectUser(user, accessToken).enqueue { result ->
            when (result) {
                is Result.Failure -> {
                    sendToLogin()
                }

                is Result.Success -> {
                    UserInfo(this@RoutingActivity).chatUserDetails = result.value.user
                    ActivityLauncher.launchMain(this@RoutingActivity)
                    finish()
                }
            }
        }
    }

    private fun sendToLogin() {
        CommonMethods(this@RoutingActivity).removePreferences()
        ActivityLauncher.launchLogin(this@RoutingActivity)
    }
}