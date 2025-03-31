package com.seyone22.expensetracker

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
import com.seyone22.expensetracker.managers.CryptoManager
import com.seyone22.expensetracker.managers.ScreenLockManager
import com.seyone22.expensetracker.managers.TransactionStartupManager
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.screen.settings.SettingsViewModel
import com.seyone22.expensetracker.ui.theme.DarkTheme
import com.seyone22.expensetracker.ui.theme.ExpenseTrackerTheme
import com.seyone22.expensetracker.ui.theme.LocalTheme
import com.seyone22.expensetracker.utils.BiometricPromptActivityResultContract
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@ExperimentalMaterial3WindowSizeClassApi
class MainActivity : ComponentActivity() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var screenLockManager: ScreenLockManager
    private lateinit var transactionStartupManager: TransactionStartupManager

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

        sharedViewModel =
            ViewModelProvider(this, AppViewModelProvider.Factory).get(SharedViewModel::class.java)

        val cryptoManager = CryptoManager()
        screenLockManager = ScreenLockManager(this, cryptoManager, biometricAuthLauncher)

        // Observe app lifecycle globally
        ProcessLifecycleOwner.get().lifecycle.addObserver(screenLockManager)

        setContent {
            val windowSize = calculateWindowSizeClass(this)
            val settingsViewModel: SettingsViewModel =
                viewModel(factory = AppViewModelProvider.Factory)

            // Use lazy initialization to prevent UI lag
            var darkTheme by remember { mutableStateOf(DarkTheme()) }
            var isSecureScreenEnabled by remember { mutableStateOf(false) }

            // Start UI immediately, then update theme/security settings asynchronously
            LaunchedEffect(Unit) {
                launch {
                    darkTheme = settingsViewModel.getCurrentTheme()
                }
                launch {
                    sharedViewModel.isSecureScreenEnabled.collectLatest { enabled ->
                        isSecureScreenEnabled = enabled
                        toggleSecureScreen(enabled)
                    }
                }
            }

            CompositionLocalProvider(LocalTheme provides darkTheme) {
                ExpenseTrackerTheme(
                    darkTheme = darkTheme.isDark, midnight = darkTheme.isMidnight
                ) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        ExpenseApp(windowSizeClass = windowSize.widthSizeClass,
                            onToggleDarkTheme = { option ->
                                darkTheme = when (option) {
                                    1 -> DarkTheme(isDark = true)
                                    0 -> DarkTheme(isDark = false)
                                    else -> DarkTheme(isDark = isSystemInDarkTheme())
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

    override fun onResume() {
        super.onResume()
        if (screenLockManager.isScreenLockEnabled() && screenLockManager.isAppLocked.value) {
            screenLockManager.triggerUnlock()
        }
    }

    private fun toggleSecureScreen(enabled: Boolean) {
        if (enabled) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE
            )
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    private fun isSystemInDarkTheme(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }
}
