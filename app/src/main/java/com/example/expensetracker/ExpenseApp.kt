package com.example.expensetracker

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.ui.navigation.ExpenseNavHost

@Composable
fun ExpenseApp( navController: NavHostController = rememberNavController(),)
{
    ExpenseNavHost(
        navController = navController,
    )
}