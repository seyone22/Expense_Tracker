import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.CurrencyFormat
import com.example.expensetracker.data.model.Metadata
import com.example.expensetracker.ui.screen.onboarding.OnboardingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingForm(
    modifier: Modifier,
    viewModel: OnboardingViewModel,
    setButtonState: (Boolean) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    var username by remember { mutableStateOf("") }
    val currencyList by viewModel.currencyList.collectAsState()
    var currentCurrency by remember { mutableStateOf(CurrencyFormat()) }
    var baseCurrencyExpanded by remember { mutableStateOf(false) }

    // Initially set the button state to false
    LaunchedEffect(username, currentCurrency) {
        // Enable button when both fields are filled
        setButtonState(username.isNotEmpty() && currentCurrency.currencyId != 0)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .focusGroup()
            .padding(16.dp)
    ) {
        Text(
            text = "Hello!",
            style = MaterialTheme.typography.displayMedium,
        )
        Text(
            text = "What do they call you?",
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text = "Your name (For labelling transactions)",
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
            value = username,
            onValueChange = {
                username = it
                viewModel.updateUiState(
                    viewModel.metadataUiState.metadataDetails.copy(
                        usernameMetadata = Metadata(6, "USERNAME", username)
                    )
                )
            },
            label = { Text("Username") },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Choose your currency",
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleMedium
        )

        var currencyFilter by remember { mutableStateOf("") }
        val filteredCurrencies = currencyList.currenciesList.filter {
            it.currencyName.contains(currencyFilter, true)
        }

        ExposedDropdownMenuBox(expanded = baseCurrencyExpanded,
            onExpandedChange = { baseCurrencyExpanded = !baseCurrencyExpanded }) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clickable(enabled = true) { baseCurrencyExpanded = true }
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                    .fillMaxWidth(),
                value = currencyFilter,
                onValueChange = { v -> currencyFilter = v },
                label = { Text("Base Currency") },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.moveFocus(FocusDirection.Next)
                }),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = baseCurrencyExpanded) },
            )

            ExposedDropdownMenu(
                expanded = baseCurrencyExpanded,
                onDismissRequest = { baseCurrencyExpanded = false },
            ) {
                filteredCurrencies.forEach { currency ->
                    DropdownMenuItem(text = { Text(currency.currencyName) }, onClick = {
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
                    })
                }
            }
        }
    }
}
