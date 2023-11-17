package com.example.expensetracker

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.expensetracker.ui.navigation.NavigationDestination

object EntityDestination : NavigationDestination {
    override val route = "Entities"
    override val titleRes = R.string.app_name
}
@Composable
fun EntityScreen() {
    Text("Entities")
}
