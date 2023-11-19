package com.example.expensetracker.ui.common

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.expensetracker.activitiesAndIcons

@Composable
fun ExpenseNavBar(
    selectedActivity : Int,
    navigateToScreen : (screen: String) -> Unit,
) {
    NavigationBar {
        activitiesAndIcons.forEachIndexed { index, pair ->
            NavigationBarItem(
                icon = { Icon(pair.icon, contentDescription = pair.activity) },
                label = { Text(pair.activity) },
                selected = selectedActivity == index,
                onClick = { navigateToScreen(pair.activity) }
            )
        }
    }
}