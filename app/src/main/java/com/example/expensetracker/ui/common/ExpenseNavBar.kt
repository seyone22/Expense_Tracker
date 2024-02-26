package com.example.expensetracker.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.automirrored.outlined.TextSnippet
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.CompareArrows
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import com.example.expensetracker.R
import com.example.expensetracker.ui.utils.ExpenseNavigationType

data class ActivityIconPair(
    val activity: String,
    val icon: ImageVector
)

@Composable
fun ExpenseNavBar(
    selectedActivity : Int,
    navigateToScreen : (screen: String) -> Unit,
    type : ExpenseNavigationType = ExpenseNavigationType.BOTTOM_NAVIGATION

) {
    val activitiesAndIcons = listOf(
        ActivityIconPair(stringArrayResource(id = R.array.activities)[0], Icons.Outlined.AccountBalanceWallet),
        ActivityIconPair(stringArrayResource(id = R.array.activities)[1], Icons.Outlined.AccountBalance),
        ActivityIconPair(stringArrayResource(id = R.array.activities)[2], Icons.Outlined.Balance),
        ActivityIconPair(stringArrayResource(id = R.array.activities)[3], Icons.AutoMirrored.Outlined.CompareArrows),
        ActivityIconPair(stringArrayResource(id = R.array.activities)[4], Icons.AutoMirrored.Outlined.TextSnippet),
    )

    if(type == ExpenseNavigationType.BOTTOM_NAVIGATION) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            activitiesAndIcons.forEachIndexed { index, pair ->
                NavigationBarItem(
                    icon = { Icon(pair.icon, contentDescription = pair.activity) },
                    label = { Text(pair.activity) },
                    selected = selectedActivity == index,
                    onClick = { navigateToScreen(pair.activity) }
                )
            }
        }
    } else {
        NavigationRail(
        ) {
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    activitiesAndIcons.forEachIndexed {index, pair ->
                        NavigationRailItem(
                            selected = selectedActivity == index,
                            label = {Text(pair.activity)},
                            onClick = { navigateToScreen(pair.activity) },
                            icon = { Icon(pair.icon, contentDescription = pair.activity) },
                            alwaysShowLabel = false)
                    }
                }
            }
        }
    }
}