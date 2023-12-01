package com.example.expensetracker.ui.screen.settings

import androidx.lifecycle.ViewModel
import com.example.expensetracker.data.metadata.MetadataRepository
import com.example.expensetracker.model.Metadata
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * ViewModel to retrieve all items in the Room database.
 */
class SettingsViewModel(
    private val metadataRepository: MetadataRepository,

) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    // Flow for username
    private val usernameFlow: Flow<Metadata?> = metadataRepository.getMetadataByNameStream("USERNAME")

    // Flow for baseCurrency
    private val baseCurrencyIdFlow: Flow<Metadata?> = metadataRepository.getMetadataByNameStream("BASECURRENCYID")

    // Combine the flows and calculate the totals
    val metadataList: Flow<List<Metadata?>> =
        combine(usernameFlow, baseCurrencyIdFlow) { username, basecurrencyid ->
            listOf(username, basecurrencyid)
        }
}

// TODO : Recurring Transactions
// TODO: Reports, Transaction Reports PRIORITY
// TODO: Budget setup, Budgets
// TODO: Stock Portfolio
// TODO: Assets
// TODO: Import/Export databases, transactions -> as format mmdb, csv, etc...
// TODO: Handle Attachments

// TODO : Multiple databases / switching databases

/* Settings stuff
    user name
    language
    date format
    base currency
    currency format
    currency history
    financial year start day
    financial year start month
    use original date when pasting transactions
    use original date when duplicating transactions

    view budgets as financial yars
    view budgets with transfer transactions
    view budget category report with summaries
    override yearly budget with munthly budget
    subtract monthly budgets from yearly budget in reporting
    budget offset days
    startday of month for repoirting
    ignore future transactions

    Defaults for new transaction dialog
    backup options
    deleted transactions retainment
    csv delimiter
*/

// NEW FEATURES
// TODO: automatic Interest handling for accounts