package com.seyone22.expensetracker

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
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
                finish()
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Request runtime permission for notifications on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS
            if (checkSelfPermission(permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(permission), 101)
            }
        }

        sharedViewModel =
            ViewModelProvider(this, AppViewModelProvider.Factory).get(SharedViewModel::class.java)

        val cryptoManager = CryptoManager()
        screenLockManager = ScreenLockManager(this, cryptoManager, biometricAuthLauncher)

        val container = (application as ExpenseApplication).container
        transactionStartupManager = TransactionStartupManager(
            container.billsDepositsRepository,
            container.transactionsRepository
        )

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
                        val isAppLocked by screenLockManager.isAppLocked.collectAsState()
                        val isScreenLockEnabled = remember(isAppLocked) { screenLockManager.isScreenLockEnabled() }

                        if (isScreenLockEnabled && isAppLocked) {
                            LockScreen(
                                onUnlockClick = { screenLockManager.triggerUnlock() }
                            )
                        } else {
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
        lifecycleScope.launch {
            transactionStartupManager.processPastDueTransactions()
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

@Composable
fun LockScreen(
    onUnlockClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = "Lock Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Expense Tracker Locked",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Please authenticate to view your personal finance data.",
                style = MaterialTheme.typography.bodyMedium,
                color = androidx.compose.ui.graphics.Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onUnlockClick,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            ) {
                Text(text = "Unlock with Biometrics")
            }
        }
    }
}
