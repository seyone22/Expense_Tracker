package com.example.expensetracker

import android.util.Log
import android.view.Window
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.ui.navigation.ExpenseNavHost

@Composable
fun ExpenseApp(
    navController: NavHostController = rememberNavController(),
    windowSizeClass : WindowWidthSizeClass
    )
{
    ExpenseNavHost(
        navController = navController,
        windowSizeClass = windowSizeClass
    )
}