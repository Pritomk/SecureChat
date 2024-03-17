package com.example.securechat

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.securechat.ui.login.LoginActivity
import com.example.securechat.utils.ActivityLauncher
import com.example.securechat.utils.UserInfo
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RoutingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            true
        }

        splashScreen.setOnExitAnimationListener { splashScreenView ->

            val slideUp = ObjectAnimator.ofFloat(
                splashScreenView.view,
                View.ROTATION_X,
                0f,
                -splashScreenView.view.height.toFloat()
            )

            slideUp.interpolator = AnticipateInterpolator()
            slideUp.duration = 5000
            // Call SplashScreenView.remove at the end of your custom animation.
            slideUp.doOnEnd { splashScreenView.remove() }

            // Run your animation.
            slideUp.start()

        }


        if (UserInfo(this).accessToken.isNullOrEmpty()) {
            ActivityLauncher.launchLogin(this@RoutingActivity)
        } else {
            ActivityLauncher.launchMain(this@RoutingActivity)
        }
    }
}