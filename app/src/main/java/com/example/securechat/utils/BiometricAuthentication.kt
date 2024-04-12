package com.example.securechat.utils

import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object BiometricAuthentication {

    fun executeBiometricAuthentication(
        context: FragmentActivity,
        text: String,
        authPass: () -> Unit,
        authFailed: () -> Unit
    ) {
        val biometricPrompt = getBiometricPrompt(context, authPass, authFailed)
        val promptInfo = getBiometricInfo(text)
        biometricPrompt.authenticate(promptInfo)
    }

    private fun getBiometricPrompt(
        context: FragmentActivity,
        authPass: () -> Unit,
        authFailed: () -> Unit
    ): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(context, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        context,
                        "Authentication error: $errString", Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(
                        context,
                        "Authentication succeeded!", Toast.LENGTH_SHORT
                    ).show()
                    authPass()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        context, "Authentication failed",
                        Toast.LENGTH_SHORT
                    ).show()
                    authFailed()
                }
            })
        return biometricPrompt
    }

    private fun getBiometricInfo(text: String): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric authentication")
            .setSubtitle(text)
            .setNegativeButtonText("Only fingerprints")
            .build()

    }
}