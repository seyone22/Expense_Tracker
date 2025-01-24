package com.example.expensetracker.utils

import java.time.LocalDate
import java.time.temporal.IsoFields

fun getCurrentWeekNumber(): Int {
    val currentDate = LocalDate.now()  // Get the current date
    return currentDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)  // Get the current week number
}