package com.example.expensetracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.expensetracker.model.Account
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme

enum class AccountTypes(val displayName: String) {
    CASH("Cash"),
    CHECKING("Checking"),
    CREDIT_CARD("Credit Card"),
    LOAN("Loan"),
    TERM("Term"),
    INVESTMENT("Investment"),
    ASSET("Asset"),
    SHARES("Shares")
}

@Composable
fun AccountScreen() {
    Column {
        Text("Current Month Summary")

        Text("Your Accounts")
        enumValues<AccountTypes>().forEach { accountType ->
            val displayName: String = accountType.displayName
            AccountList(
                category = displayName,
                accountList = listOf(),
            )
        }

    }
}

@Composable
fun AccountList(category: String, accountList : List<Account>, modifier: Modifier = Modifier) {
    Text(text = category)
    Column(modifier = modifier) {
        accountList.forEach { account ->
            AccountCard(
                account = account,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun AccountCard(account: Account, modifier: Modifier = Modifier) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,

            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalanceWallet,
                    contentDescription = "Description",
                    Modifier.size(36.dp, 36.dp)
                )
            }
            Column(
                Modifier.weight(3f)
            ) {
                Text(
                    text = account.accountName,
                )
                Text(
                    text = "Subtitle",
                )
            }
            Column(
                Modifier.weight(2f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Rs. 83.33"
                )
                Text(
                    text = "Rs. 80.22"
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AccountScreenPreview() {
    ExpenseTrackerTheme {
        AccountScreen()
    }
}
