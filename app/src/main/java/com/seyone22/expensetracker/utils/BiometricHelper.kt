package com.seyone22.expensetracker.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt

object BiometricHelper {
    // Check if biometric authentication is available on the device
    fun isBiometricAvailable(context: Activity?): Boolean {
        if (context == null) return false
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> {
                Log.e("TAG", "Biometric authentication not available")
                false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun checkBiometricStatus(context: Context?) {
        if (context == null) return

        val biometricManager = BiometricManager.from(context)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> Log.d(
                "MY_APP_TAG", "App can authenticate using biometrics."
            )

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> Log.e(
                "MY_APP_TAG", "No biometric features available on this device."
            )

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> Log.e(
                "MY_APP_TAG", "Biometric features are currently unavailable."
            )

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                TODO()
            }

            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                TODO()
            }

            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                TODO()
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Prompts the user to create credentials that your app accepts.
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BIOMETRIC_STRONG
                    )
                }
                context.startActivity(enrollIntent)
            }
        }
    }

    // Create BiometricPrompt.PromptInfo with customized display text
    fun getPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock with Biometrics")
            .setSubtitle("Use your fingerprint or face recognition")
            .setDescription("To proceed, authenticate using your registered biometric credentials.")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()
    }
}