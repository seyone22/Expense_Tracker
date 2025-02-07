package com.seyone22.expensetracker.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

fun getCurrentWeekNumber(): Int {
    val currentDate = LocalDate.now()
    return currentDate.get(
        WeekFields.of(Locale.getDefault()).weekOfYear()
    )  // Uses locale-based week calculation
}

fun getStartOfCurrentWeek(): String {
    return LocalDate.now()
        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))  // Start of the week (Monday)
        .toString()  // Convert to "YYYY-MM-DD"
}

fun getEndOfCurrentWeek(): String {
    return LocalDate.now()
        .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))  // End of the week (Sunday)
        .toString()  // Convert to "YYYY-MM-DD"
}