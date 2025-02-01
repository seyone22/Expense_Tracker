package com.seyone22.expensetracker

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.seyone22.expensetracker.data.model.BillsDeposits
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.Payee
import com.seyone22.expensetracker.data.model.Transaction
import com.seyone22.expensetracker.ui.navigation.ExpenseNavHost
import com.seyone22.expensetracker.utils.BiometricPromptActivityResultContract
import com.seyone22.expensetracker.utils.ScreenLockManager

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ExpenseApp(
    navController: NavHostController = rememberNavController(),
    windowSizeClass: WindowWidthSizeClass,
    onToggleDarkTheme: (Int) -> Unit,
    screenLockManager: ScreenLockManager
) {
    val isLocked by screenLockManager.isLocked.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val biometricLauncher =
        rememberLauncherForActivityResult(contract = BiometricPromptActivityResultContract()) { isSuccess ->
            if (isSuccess) {
                //screenLockManager.saveScreenLockPreference(false) // Unlock app
            } else {
                //Log.e("BiometricAuth", "Biometric authentication failed!")
            }
        }

    // Enforce lock when returning
    LaunchedEffect(isLocked) {
        if (isLocked) {
            biometricLauncher.launch(Unit)
        }
    }

    ExpenseNavHost(
        navController = navController,
        windowSizeClass = windowSizeClass,
        onToggleDarkTheme = { onToggleDarkTheme(it) },
        snackbarHostState = snackbarHostState,
    )
}


data class SelectedObjects(
    val transaction: Transaction = Transaction(),
    val billsDeposits: BillsDeposits = BillsDeposits(),
    val payee: Payee = Payee(),
    val category: Category = Category(),
    val currency: CurrencyFormat = CurrencyFormat()
)