package com.seyone22.expensetracker.utils

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class BiometricAuthActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    val data = Intent().putExtra("biometric_result", true)
                    setResult(RESULT_OK, data)
                    finish()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    setResult(RESULT_CANCELED)
                    Log.d("TAG", "parseResult: $errString")
                    finish()
                }

                override fun onAuthenticationFailed() {
                    setResult(RESULT_CANCELED)
                    finish()
                }
            })

        val promptInfo = BiometricHelper.getPromptInfo()

        biometricPrompt.authenticate(promptInfo)
    }
}
