package com.seyone22.expensetracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.seyone22.expensetracker.ui.navigation.ExpenseNavHost

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ExpenseApp(
    navController: NavHostController = rememberNavController(),
    windowSizeClass: WindowWidthSizeClass,
    onToggleDarkTheme: (Int) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    ExpenseNavHost(
        navController = navController,
        windowSizeClass = windowSizeClass,
        onToggleDarkTheme = { onToggleDarkTheme(it) },
        snackbarHostState = snackbarHostState,
    )
}