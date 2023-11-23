package com.example.expensetracker.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TextSnippet
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.House
import androidx.compose.material.icons.outlined.TextSnippet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.example.expensetracker.R

data class ActivityIconPair(
    val activity: String,
    val icon: ImageVector
)

@Composable
fun ExpenseNavBar(
    selectedActivity : Int,
    navigateToScreen : (screen: String) -> Unit,

) {
    val activitiesAndIcons = listOf(
        ActivityIconPair(stringArrayResource(id = R.array.activities)[0], Icons.Outlined.AccountBalanceWallet),
        ActivityIconPair(stringArrayResource(id = R.array.activities)[1], Icons.Outlined.AccountBalance),
        ActivityIconPair(stringArrayResource(id = R.array.activities)[2], Icons.Outlined.Balance),
        ActivityIconPair(stringArrayResource(id = R.array.activities)[3], Icons.Outlined.House),
        ActivityIconPair(stringArrayResource(id = R.array.activities)[4], Icons.AutoMirrored.Outlined.TextSnippet),
    )

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
}