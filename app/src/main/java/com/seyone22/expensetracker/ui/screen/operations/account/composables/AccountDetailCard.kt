package com.seyone22.expensetracker.ui.screen.operations.account.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.FormattedCurrency
import com.seyone22.expensetracker.ui.screen.operations.account.AccountDetailUiState

@Composable
fun AccountDetailCard(
    modifier: Modifier,
    accountDetailUiState: AccountDetailUiState,
) {
    val account = accountDetailUiState.account

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Account Information",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            // Render key metadata items
            val items = mutableListOf<MetadataRowItem>()

            if (!account.accountNum.isNullOrBlank()) {
                items.add(MetadataRowItem("Account Number", account.accountNum, Icons.Default.Tag))
            }
            if (!account.heldAt.isNullOrBlank()) {
                items.add(MetadataRowItem("Institution", account.heldAt, Icons.Default.AccountBalance))
            }
            if (account.creditLimit != null && account.creditLimit > 0.0) {
                items.add(MetadataRowItem("Credit Limit", account.creditLimit.toString(), Icons.Default.CreditCard, isCurrency = true))
            }
            if (account.minimumBalance != null && account.minimumBalance > 0.0) {
                items.add(MetadataRowItem("Minimum Balance", account.minimumBalance.toString(), Icons.Default.VerticalAlignBottom, isCurrency = true))
            }
            if (account.interestRate != null && account.interestRate > 0.0) {
                items.add(MetadataRowItem("Interest Rate", "${account.interestRate}%", Icons.Default.Percent))
            }
            if (!account.notes.isNullOrBlank()) {
                items.add(MetadataRowItem("Notes", account.notes, Icons.AutoMirrored.Filled.Notes))
            }

            if (items.isEmpty()) {
                Text(
                    text = "No additional details available for this account.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                items.forEachIndexed { index, item ->
                    if (index > 0) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            thickness = 1.dp
                        )
                    }
                    MetadataRow(item = item, accountDetailUiState = accountDetailUiState)
                }
            }
        }
    }
}

data class MetadataRowItem(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val isCurrency: Boolean = false
)

@Composable
fun MetadataRow(
    item: MetadataRowItem,
    accountDetailUiState: AccountDetailUiState
) {
    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)
    var currencyData by remember { mutableStateOf(CurrencyFormat()) }

    LaunchedEffect(Unit, accountDetailUiState.account.currencyId) {
        currencyData = sharedViewModel.getCurrencyById(accountDetailUiState.account.currencyId)
            ?: CurrencyFormat()
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        if (item.isCurrency) {
            val amount = item.value.toDoubleOrNull() ?: 0.0
            FormattedCurrency(
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                value = amount,
                currency = currencyData
            )
        } else {
            Text(
                text = item.value,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }
    }
}