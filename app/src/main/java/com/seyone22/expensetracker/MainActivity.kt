package com.seyone22.expensetracker

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.screen.settings.SettingsViewModel
import com.seyone22.expensetracker.ui.theme.DarkTheme
import com.seyone22.expensetracker.ui.theme.ExpenseTrackerTheme
import com.seyone22.expensetracker.ui.theme.LocalTheme
import com.seyone22.expensetracker.utils.CryptoManager
import com.seyone22.expensetracker.utils.ScreenLockManager

@ExperimentalMaterial3WindowSizeClassApi
class MainActivity : ComponentActivity() {
    private lateinit var cryptoManager: CryptoManager // Assuming you have a CryptoManager instance
    private lateinit var screenLockManager: ScreenLockManager


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        cryptoManager = CryptoManager()
        screenLockManager = ScreenLockManager(application, context = this, cryptoManager)

        // Observe app lifecycle globally
        ProcessLifecycleOwner.get().lifecycle.addObserver(screenLockManager)

        // Fetch the screen lock preference on app load
        val isScreenLockEnabled = screenLockManager.isScreenLockEnabled()
        if (isScreenLockEnabled) {
            // Lock the app if the preference indicates screen lock is enabled
            screenLockManager.triggerLock()
        } else {
            // Unlock the app if screen lock is disabled
            screenLockManager.triggerUnlock()
        }

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
                            screenLockManager = screenLockManager,
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


    // Trigger biometric unlock when the app is resumed and locked
    override fun onResume() {
        super.onResume()
        if (screenLockManager.isLocked.value) {
            // Trigger biometric authentication to unlock the app
        }
    }

    private fun isSystemInDarkTheme(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }
}

