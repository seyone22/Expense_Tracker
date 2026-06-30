package com.seyone22.expensetracker.ui.screen.onboarding.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.Metadata
import com.seyone22.expensetracker.ui.screen.onboarding.OnboardingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingForm(
    modifier: Modifier,
    viewModel: OnboardingViewModel,
    setButtonState: (Boolean) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    var username by remember { mutableStateOf("") }
    val currencyList by viewModel.currencyList.collectAsState()
    var currentCurrency by remember { mutableStateOf(CurrencyFormat()) }
    var baseCurrencyExpanded by remember { mutableStateOf(false) }

    // Enable/disable the "Begin" button based on valid input
    LaunchedEffect(username, currentCurrency) {
        setButtonState(username.trim().isNotEmpty() && currentCurrency.currencyId != 0)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .focusGroup()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Hello!",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        
        Text(
            text = "What do they call you?",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Username field
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = username,
                    onValueChange = {
                        username = it
                        viewModel.updateUiState(
                            viewModel.metadataUiState.metadataDetails.copy(
                                usernameMetadata = Metadata(6, "USERNAME", username)
                            )
                        )
                    },
                    label = { Text("Your Username") },
                    placeholder = { Text("Enter name for transaction labels") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
                )

                // Currency selector
                var currencyFilter by remember { mutableStateOf("") }
                val filteredCurrencies = currencyList.currenciesList.filter {
                    it.currencyName.contains(currencyFilter, true)
                }

                ExposedDropdownMenuBox(
                    expanded = baseCurrencyExpanded,
                    onExpandedChange = { baseCurrencyExpanded = !baseCurrencyExpanded }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                            .fillMaxWidth(),
                        value = currencyFilter,
                        onValueChange = { v -> currencyFilter = v },
                        label = { Text("Base Currency") },
                        placeholder = { Text("Search and select currency") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Paid,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                        }),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = baseCurrencyExpanded) },
                    )

                    ExposedDropdownMenu(
                        expanded = baseCurrencyExpanded,
                        onDismissRequest = { baseCurrencyExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        if (filteredCurrencies.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No currencies found", style = MaterialTheme.typography.bodyMedium) },
                                onClick = {},
                                enabled = false
                            )
                        } else {
                            filteredCurrencies.forEach { currency ->
                                DropdownMenuItem(
                                    text = { 
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(currency.currencyName, fontWeight = FontWeight.Medium)
                                            Text(currency.currency_symbol, color = MaterialTheme.colorScheme.primary)
                                        }
                                    },
                                    onClick = {
                                        currencyFilter = currency.currencyName
                                        currentCurrency = currency
                                        viewModel.updateUiState(
                                            viewModel.metadataUiState.metadataDetails.copy(
                                                baseCurrencyMetadata = Metadata(
                                                    5, "BASECURRENCYID", currentCurrency.currencyId.toString()
                                                )
                                            )
                                        )
                                        baseCurrencyExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
