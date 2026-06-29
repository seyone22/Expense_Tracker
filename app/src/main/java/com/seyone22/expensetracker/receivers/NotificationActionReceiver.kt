package com.seyone22.expensetracker.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.seyone22.expensetracker.ExpenseApplication
import com.seyone22.expensetracker.data.model.Transaction
import com.seyone22.expensetracker.data.model.toBillsDeposit
import com.seyone22.expensetracker.utils.RepeatsFieldHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val bdid = intent.getIntExtra("EXTRA_BD_ID", -1)
        if (bdid == -1) return

        Log.d("NotificationActionReceiver", "Received action $action for bdid $bdid")

        val pendingResult = goAsync()
        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {
            try {
                val container = (context.applicationContext as ExpenseApplication).container
                val billsDepositsRepository = container.billsDepositsRepository
                val transactionsRepository = container.transactionsRepository

                val allReminders = billsDepositsRepository.getAllTransactionsStream().firstOrNull() ?: emptyList()
                val scheduledItemDetails = allReminders.firstOrNull { it.BDID == bdid }
                if (scheduledItemDetails != null) {
                    val scheduledItem = scheduledItemDetails.toBillsDeposit()

                    if (action == "com.seyone22.expensetracker.ACTION_PAY_NOW") {
                        val transaction = Transaction(
                            accountId = scheduledItem.ACCOUNTID,
                            toAccountId = scheduledItem.TOACCOUNTID ?: -1,
                            payeeId = scheduledItem.PAYEEID,
                            transCode = scheduledItem.TRANSCODE,
                            transAmount = scheduledItem.TRANSAMOUNT,
                            status = "Reconciled",
                            transactionNumber = scheduledItem.TRANSACTIONNUMBER ?: "",
                            notes = scheduledItem.NOTES ?: "",
                            categoryId = scheduledItem.CATEGID ?: -1,
                            transDate = java.time.LocalDate.now().toString(),
                            followUpId = scheduledItem.FOLLOWUPID ?: 0,
                            toTransAmount = scheduledItem.TOTRANSAMOUNT ?: scheduledItem.TRANSAMOUNT,
                            color = scheduledItem.COLOR
                        )
                        transactionsRepository.insertTransaction(transaction)
                    }

                    val nextDate = RepeatsFieldHelper.calculateNextOccurrenceDate(
                        scheduledItem.NEXTOCCURRENCEDATE,
                        scheduledItem.REPEATS ?: 0
                    )
                    val remainingOccurrences = (scheduledItem.NUMOCCURRENCES ?: 0) - 1

                    val updatedItem = scheduledItem.copy(
                        NEXTOCCURRENCEDATE = nextDate,
                        NUMOCCURRENCES = if (remainingOccurrences < 0) 0 else remainingOccurrences
                    )
                    billsDepositsRepository.updateBillsDeposit(updatedItem)

                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(bdid)
                    notificationManager.cancel(bdid + 10000)
                }
            } catch (e: Exception) {
                Log.e("NotificationActionReceiver", "Failed to process notification action", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
