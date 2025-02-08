package com.seyone22.expensetracker.ui.common

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.icu.text.DecimalFormat
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.RepeatFrequency
import com.seyone22.expensetracker.ui.screen.operations.transaction.BillsDepositsDetails
import com.seyone22.expensetracker.workers.RecurringTransactionWorker
import java.text.SimpleDateFormat
import java.time.Month
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit


const val TAG = "TESTING"

@Composable
fun FormattedCurrency(
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleMedium,
    value: Double,
    currency: CurrencyFormat,
    type: TransactionType = TransactionType.NEUTRAL,
    defaultColor: Color = MaterialTheme.colorScheme.onBackground
) {
    val formattedValue = DecimalFormat("#,###.00").format(value) // Add comma separators
    val textColor = if ((type == TransactionType.DEBIT) or (value < 0)) {
        MaterialTheme.colorScheme.error
    } else {
        defaultColor
    }

    val displayText = if (currency.pfx_symbol.isNotEmpty()) {
        "${currency.pfx_symbol} $formattedValue"
    } else {
        "$formattedValue${currency.sfx_symbol}"
    }

    Text(
        style = style,
        text = displayText,
        color = textColor,
        modifier = modifier
    )
}


fun removeTrPrefix(input: String): String {
    val prefix = "_tr_"

    return if (input.startsWith(prefix)) {
        input.removePrefix(prefix)
    } else {
        input
    }
}

fun getAbbreviatedMonthName(monthValue: Int, locale: Locale = Locale.getDefault()): String {
    val month = Month.of(monthValue)
    return month.getDisplayName(TextStyle.SHORT, locale)
}

enum class TransactionType {
    DEBIT,
    CREDIT,
    NEUTRAL
}


enum class EntryFields {
    STATUS,
    TYPE,
    ACCOUNT,
    PAYEE,
    CATEGORY,
    TRANSACTION
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun askNotificationPermissions(context: Context) {
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // Permission not granted, request it from the user
        ActivityCompat.requestPermissions(
            context as Activity, // Assuming the context is an activity
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            2
        )
    }
}


fun scheduleWorkByDayCount(context: Context, recurrenceDetails: BillsDepositsDetails) {
    val dayCount =
        RepeatFrequency.valueOf(recurrenceDetails.REPEATS.uppercase(Locale.ROOT)).dayCount

    // Parse the next occurrence date from the string
    val nextOccurrenceDate = Calendar.getInstance().apply {
        time = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).parse(recurrenceDetails.NEXTOCCURRENCEDATE)!!
    }

    // Calculate the initial delay until the next occurrence date
    val currentDate = Calendar.getInstance()
    val initialDelay = nextOccurrenceDate.timeInMillis - currentDate.timeInMillis

    // Create a periodic request to run every period
    val periodicWorkRequest = PeriodicWorkRequestBuilder<RecurringTransactionWorker>(
        repeatInterval = dayCount.toLong(), // repeatInterval is in days
        repeatIntervalTimeUnit = TimeUnit.DAYS
    )
        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
        .build()

    // Enqueue the work request
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        recurrenceDetails.BDID.toString(),
        ExistingPeriodicWorkPolicy.UPDATE,
        periodicWorkRequest
    )
}

fun scheduleWorkByMonthCount(context: Context, recurrenceDetails: BillsDepositsDetails) {
    val monthCount =
        RepeatFrequency.valueOf(recurrenceDetails.REPEATS.uppercase(Locale.ROOT)).dayCount

    // Parse the next occurrence date from the string
    val nextOccurrenceDate = Calendar.getInstance().apply {
        time = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).parse(recurrenceDetails.NEXTOCCURRENCEDATE)!!
    }

    // Calculate the initial delay until the next occurrence date
    val currentDate = Calendar.getInstance()
    val initialDelay = nextOccurrenceDate.timeInMillis - currentDate.timeInMillis

    // Calculate the repeat interval based on the month count
    val repeatInterval = TimeUnit.DAYS.toMillis(30L * monthCount)

    // Create a periodic work request with initial delay and repeat interval
    val workRequest = PeriodicWorkRequestBuilder<RecurringTransactionWorker>(
        repeatInterval,
        TimeUnit.MILLISECONDS
    )
        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
        .build()

    // Enqueue the work request
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        recurrenceDetails.BDID.toString(),
        ExistingPeriodicWorkPolicy.UPDATE,
        workRequest
    )
}

fun calculateNextOccurrenceDate(currentDate: Calendar, monthCount: Int): Calendar {
    val nextOccurrenceDate = currentDate.clone() as Calendar
    nextOccurrenceDate.add(Calendar.MONTH, monthCount)
    return nextOccurrenceDate
}

fun calculateDaysUntilEndOfMonth(calendar: Calendar): Int {
    val currentMonth = calendar.get(Calendar.MONTH)
    val nextMonth = (currentMonth + 1) % 12 // Increment month, wrap around to January if December
    val currentYear = calendar.get(Calendar.YEAR)

    val daysInCurrentMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val daysInNextMonth = Calendar.getInstance().apply {
        set(Calendar.YEAR, currentYear)
        set(Calendar.MONTH, nextMonth)
    }.getActualMaximum(Calendar.DAY_OF_MONTH)

    return daysInCurrentMonth - calendar.get(Calendar.DAY_OF_MONTH) + daysInNextMonth
}