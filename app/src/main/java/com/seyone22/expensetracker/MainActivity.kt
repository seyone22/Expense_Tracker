package com.seyone22.expensetracker

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
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
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.screen.settings.SettingsViewModel
import com.seyone22.expensetracker.ui.theme.DarkTheme
import com.seyone22.expensetracker.ui.theme.ExpenseTrackerTheme
import com.seyone22.expensetracker.ui.theme.LocalTheme
import com.seyone22.expensetracker.utils.BiometricPromptActivityResultContract
import com.seyone22.expensetracker.utils.CryptoManager
import com.seyone22.expensetracker.utils.ScreenLockManager
import com.seyone22.expensetracker.utils.TransactionStartupManager
import kotlinx.coroutines.flow.collectLatest

@ExperimentalMaterial3WindowSizeClassApi
class MainActivity : ComponentActivity() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var cryptoManager: CryptoManager // Assuming you have a CryptoManager instance
    private lateinit var screenLockManager: ScreenLockManager
    private lateinit var transactionStartupManager: TransactionStartupManager


    // Create an ActivityResultLauncher to launch biometric authentication
    private val biometricAuthLauncher =
        registerForActivityResult(BiometricPromptActivityResultContract()) { success ->
            if (success) {
                screenLockManager.toggleLockState()
            } else {
                screenLockManager.triggerLock()
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        cryptoManager = CryptoManager()
        screenLockManager = ScreenLockManager(context = this, cryptoManager, biometricAuthLauncher)
        sharedViewModel = ViewModelProvider(this, AppViewModelProvider.Factory)
            .get(SharedViewModel::class.java)

        applySecureScreenSetting(this, sharedViewModel)

        // Observe app lifecycle globally
        ProcessLifecycleOwner.get().lifecycle.addObserver(screenLockManager)

        // Check for past due transactions
        transactionStartupManager = TransactionStartupManager(sharedViewModel)

        setContent {
            val windowSize = calculateWindowSizeClass(this)
            val viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)

            val darkThemeState = remember { mutableStateOf(DarkTheme()) }
            var requireUnlock by remember { mutableStateOf(false) }


            LaunchedEffect(Unit) {
                val currentTheme = viewModel.getCurrentTheme()
                darkThemeState.value = currentTheme // assuming getCurrentTheme returns DarkTheme

                sharedViewModel.isSecureScreenEnabled.collectLatest { enabled ->
                    if (enabled) {
                        window.setFlags(
                            WindowManager.LayoutParams.FLAG_SECURE,
                            WindowManager.LayoutParams.FLAG_SECURE
                        )
                    } else {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                    }
                }
            }

            CompositionLocalProvider(LocalTheme provides darkThemeState.value) {
                ExpenseTrackerTheme(
                    darkTheme = darkThemeState.value.isDark,
                    midnight = darkThemeState.value.isMidnight
                ) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        ExpenseApp(windowSizeClass = windowSize.widthSizeClass,
                            onToggleDarkTheme = { option ->
                                darkThemeState.value = when (option) {
                                    1 -> darkThemeState.value.copy(isDark = true)
                                    0 -> darkThemeState.value.copy(isDark = false)
                                    2 -> darkThemeState.value.copy(isDark = isSystemInDarkTheme())
                                    else -> darkThemeState.value
                                }
                            })
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (screenLockManager.isScreenLockEnabled()) {
            screenLockManager.triggerLock()
        }
    }


    // Trigger biometric unlock when the app is resumed and locked
    override fun onResume() {
        super.onResume()
        if (screenLockManager.isScreenLockEnabled() && screenLockManager.isAppLocked.value) {
            screenLockManager.triggerUnlock()
        }
    }

    private fun isSystemInDarkTheme(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    private fun applySecureScreenSetting(context: Context, sharedViewModel: SharedViewModel) {
        if (sharedViewModel.getSecureScreenSetting(context)) {
            // Enable secure screen (disable screenshots, hide app preview)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE
            )
        } else {
            // Disable secure screen (allow screenshots, show app preview)
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

}

