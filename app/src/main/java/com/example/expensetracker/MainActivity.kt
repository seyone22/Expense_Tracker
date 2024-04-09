package com.example.expensetracker

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
import androidx.compose.ui.Modifier
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme

@ExperimentalMaterial3WindowSizeClassApi
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(
        savedInstanceState: Bundle?)
    {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val windowSize = calculateWindowSizeClass(this)
            ExpenseTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface( modifier = Modifier.fillMaxSize() )
                {
                    ExpenseApp(
                        windowSizeClass = windowSize.widthSizeClass,
                    )
                }
            }
        }
    }
}