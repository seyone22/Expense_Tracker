package com.seyone22.expensetracker.managers

import com.seyone22.expensetracker.data.model.Transaction
import com.seyone22.expensetracker.data.model.toBillsDeposit
import com.seyone22.expensetracker.data.repository.billsDeposit.BillsDepositsRepository
import com.seyone22.expensetracker.data.repository.transaction.TransactionsRepository
import com.seyone22.expensetracker.utils.RepeatsFieldHelper
import kotlinx.coroutines.flow.firstOrNull

class TransactionStartupManager(
    private val billsDepositsRepository: BillsDepositsRepository,
    private val transactionsRepository: TransactionsRepository
) {
    /**
     * Call this function from onResume (or onCreate) to process any past due transactions.
     */
    suspend fun processPastDueTransactions() {
        // Query for transactions that are past due (due date <= today)
        val pastDueTransactions = billsDepositsRepository.getPastDueBillsDeposits().firstOrNull()

        if (pastDueTransactions.isNullOrEmpty()) return

        pastDueTransactions.forEach { billsDeposit ->
            // Decode the REPEATS field
            val (autoExecute, autoSilent, period) = RepeatsFieldHelper.decode(
                billsDeposit.REPEATS ?: 0
            )

            when {
                // 1. Auto Execute is enabled: execute immediately
                autoExecute -> {
                    transactionsRepository.insertTransaction(Transaction())/*                    scaffoldState.snackbarHostState.showSnackbar(
                                            message = "Automatically executed scheduled transaction: ${transaction.details}",
                                            duration = SnackbarDuration.Short
                                        )*/
                }
                // 2. Manual confirmation required:
                // Here, we show a persistent snackbar with an action to review/execute.
                // (Assuming that autoExecute is false but the transaction is still pending.)
                !autoSilent -> {/*                    val result = scaffoldState.snackbarHostState.showSnackbar(
                                            message = "You have a pending scheduled transaction: ${transaction.details}",
                                            actionLabel = "Review Now",
                                            duration = SnackbarDuration.Indefinite
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            // User tapped the action, so process the transaction (or open a details screen)
                                            transactionRepository.executeTransaction(transaction)
                                        }*/
                }
                // 3. Otherwise, just notify the user of pending transactions.
                else -> {/*                    scaffoldState.snackbarHostState.showSnackbar(
                                            message = "There are pending scheduled transactions to review.",
                                            duration = SnackbarDuration.Short
                                        )*/
                }
            }
            billsDepositsRepository.insertBillsDeposit(
                billsDeposit.copy(
                    NUMOCCURRENCES = ((billsDeposit.NUMOCCURRENCES ?: 1) - 1)
                ).toBillsDeposit()
            )
        }
    }
}
