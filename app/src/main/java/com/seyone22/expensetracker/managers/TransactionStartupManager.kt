package com.seyone22.expensetracker.managers

import android.util.Log
import com.seyone22.expensetracker.data.constants.RecurrenceType
import com.seyone22.expensetracker.data.model.Transaction
import com.seyone22.expensetracker.data.model.toBillsDeposit
import com.seyone22.expensetracker.data.repository.billsDeposit.BillsDepositsRepository
import com.seyone22.expensetracker.data.repository.transaction.TransactionsRepository
import com.seyone22.expensetracker.utils.RepeatsFieldHelper
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate

class TransactionStartupManager(
    private val billsDepositsRepository: BillsDepositsRepository,
    private val transactionsRepository: TransactionsRepository
) {
    /**
     * Call this function to process any past due transactions.
     */
    suspend fun processPastDueTransactions() {
        // Query for transactions that are past due (due date <= today)
        val pastDueTransactions = billsDepositsRepository.getPastDueBillsDeposits().firstOrNull()

        if (pastDueTransactions.isNullOrEmpty()) return

        pastDueTransactions.forEach { billsDeposit ->
            val (autoExecute, autoSilent, recurrenceType) = RepeatsFieldHelper.decode(
                billsDeposit.REPEATS ?: 0
            )

            if (autoExecute) {
                // Map details from BillsDeposit to a new Transaction
                val newTransaction = Transaction(
                    transId = 0,
                    accountId = billsDeposit.ACCOUNTID,
                    toAccountId = billsDeposit.TOACCOUNTID ?: -1,
                    payeeId = billsDeposit.PAYEEID,
                    transCode = billsDeposit.TRANSCODE,
                    transAmount = billsDeposit.TRANSAMOUNT,
                    status = "R", // Default to Reconciled
                    transactionNumber = billsDeposit.TRANSACTIONNUMBER ?: "",
                    notes = billsDeposit.NOTES ?: "",
                    categoryId = billsDeposit.CATEGID ?: -1,
                    transDate = billsDeposit.NEXTOCCURRENCEDATE ?: LocalDate.now().toString(),
                    followUpId = billsDeposit.FOLLOWUPID ?: 0,
                    toTransAmount = billsDeposit.TOTRANSAMOUNT ?: billsDeposit.TRANSAMOUNT,
                    color = billsDeposit.COLOR
                )
                transactionsRepository.insertTransaction(newTransaction)
                Log.d("RecurrenceEngine", "Automatically executed transaction for scheduled entry BDID: ${billsDeposit.BDID}")
            }

            // Decrement remaining occurrences and compute next date
            val remainingOccurrences = ((billsDeposit.NUMOCCURRENCES ?: 1) - 1)
            val nextDate = RepeatsFieldHelper.calculateNextOccurrenceDate(billsDeposit.NEXTOCCURRENCEDATE, billsDeposit.REPEATS)

            billsDepositsRepository.updateBillsDeposit(
                billsDeposit.copy(
                    NUMOCCURRENCES = remainingOccurrences,
                    NEXTOCCURRENCEDATE = nextDate
                ).toBillsDeposit()
            )
        }
    }
}
