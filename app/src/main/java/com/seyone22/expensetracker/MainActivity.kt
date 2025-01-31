package com.seyone22.expensetracker

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.screen.settings.SettingsViewModel
import com.seyone22.expensetracker.ui.theme.DarkTheme
import com.seyone22.expensetracker.ui.theme.ExpenseTrackerTheme
import com.seyone22.expensetracker.ui.theme.LocalTheme

@ExperimentalMaterial3WindowSizeClassApi
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val windowSize = calculateWindowSizeClass(this)
            val viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)

            val darkThemeState = remember { mutableStateOf(DarkTheme()) }
            var requireUnlock by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                val currentTheme = viewModel.getCurrentTheme()
                darkThemeState.value = currentTheme // assuming getCurrentTheme returns DarkTheme
                requireUnlock = viewModel.getRequireUnlock()
            }

            CompositionLocalProvider(LocalTheme provides darkThemeState.value) {
                ExpenseTrackerTheme(
                    darkTheme = darkThemeState.value.isDark,
                    midnight = darkThemeState.value.isMidnight
                ) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        ExpenseApp(
                            windowSizeClass = windowSize.widthSizeClass,
                            onToggleDarkTheme = { option ->
                                darkThemeState.value = when (option) {
                                    1 -> darkThemeState.value.copy(isDark = true)
                                    0 -> darkThemeState.value.copy(isDark = false)
                                    2 -> darkThemeState.value.copy(isDark = isSystemInDarkTheme())
                                    else -> darkThemeState.value
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun isSystemInDarkTheme(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }
}

enum class AppTheme {
    MODE_DAY, MODE_NIGHT, MODE_AUTO, MODE_MIDNIGHT;

    companion object {
        fun fromOrdinal(ordinal: Int) = entries[ordinal]
    }
}

/**
 * Checks whether the device is capable of biometric authentication.
 *
 * @param context The context to retrieve biometric capability information.
 * @return A constant indicating the biometric capability status. Possible values:
 *         [BiometricManager.BIOMETRIC_SUCCESS] - The device supports biometric authentication.
 *         [BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE] - The device does not have biometric hardware.
 *         [BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE] - Biometric hardware is currently unavailable.
 *         [BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED] - The device does not support the required features.
 */
private fun hasBiometricCapability(context: Context): Int {
    return BiometricManager.from(context)
        .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL or BiometricManager.Authenticators.BIOMETRIC_WEAK)
}

/**
 * Checks whether the device is ready for biometric authentication.
 *
 * @param context The context to check biometric readiness.
 * @return `true` if the device is ready for biometric authentication, otherwise `false`.
 */
fun isBiometricReady(context: Context): Boolean =
    hasBiometricCapability(context) == BiometricManager.BIOMETRIC_SUCCESS


private fun showBiometricPrompt(
    proceedToMainActivity: () -> Unit, context: Context, fragmentActivity: FragmentActivity
) {
    val biometricPrompt = BiometricPrompt(fragmentActivity,
        ContextCompat.getMainExecutor(context),
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                // Biometric authentication succeeded, proceed to main activity
                proceedToMainActivity()
            }

        })

    val promptInfo = BiometricPrompt.PromptInfo.Builder().setTitle("Unlock")
        .setDescription("Use biometric to unlock").setNegativeButtonText("Cancel").build()

    biometricPrompt.authenticate(promptInfo)
}