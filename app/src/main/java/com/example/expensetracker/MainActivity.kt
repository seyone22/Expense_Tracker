package com.example.expensetracker

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.House
import androidx.compose.material.icons.outlined.TextSnippet
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme

data class ActivityIconPair(val activity: String, val icon: ImageVector)
val activitiesAndIcons = listOf(
    ActivityIconPair("Accounts", Icons.Outlined.AccountBalanceWallet),
    ActivityIconPair("Entities", Icons.Outlined.AccountBalance),
    ActivityIconPair("Budgets", Icons.Outlined.Balance),
    ActivityIconPair("Assets", Icons.Outlined.House),
    ActivityIconPair("Reports", Icons.Outlined.TextSnippet),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpenseTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExpenseApp()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LayoutPreview() {
    ExpenseTrackerTheme {
        ExpenseApp()
    }
}