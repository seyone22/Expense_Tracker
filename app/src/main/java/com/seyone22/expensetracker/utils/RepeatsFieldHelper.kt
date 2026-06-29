package com.seyone22.expensetracker.utils

import com.seyone22.expensetracker.data.constants.RecurrenceType

object RepeatsFieldHelper {
    const val BD_REPEATS_MULTIPLEX_BASE = 100

    /**
     * Encodes recurrence type and other flags (e.g., autoExecute, autoSilent) into a single integer.
     */
    fun encode(
        autoExecute: Boolean,
        autoSilent: Boolean,
        recurrenceType: RecurrenceType,
        n: Int
    ): Int {
        var value = recurrenceType.code // Map recurrence type to an integer
        if (autoExecute) {
            value += BD_REPEATS_MULTIPLEX_BASE
        }
        if (autoSilent) {
            value += BD_REPEATS_MULTIPLEX_BASE
        }
        return value
    }

    /**
     * Decodes the multiplexed value into a Triple:
     * - autoExecute flag (true if in 100s or 200s)
     * - autoSilent flag (true if in the 200s)
     * - RecurrenceType (Mapped using the enum)
     */
    fun decode(value: Int): Triple<Boolean, Boolean, RecurrenceType> {
        val autoExecute = value / BD_REPEATS_MULTIPLEX_BASE >= 1
        val autoSilent = value / BD_REPEATS_MULTIPLEX_BASE >= 2
        val recurrenceCode = value % BD_REPEATS_MULTIPLEX_BASE
        val recurrenceType = RecurrenceType.fromCode(recurrenceCode)

        return Triple(autoExecute, autoSilent, recurrenceType)
    }

    /**
     * Computes the next occurrence date based on the current next occurrence date and recurrence rules.
     */
    fun calculateNextOccurrenceDate(currentDateStr: String?, repeats: Int?): String {
        if (currentDateStr.isNullOrBlank()) return java.time.LocalDate.now().toString()
        val date = try {
            java.time.LocalDate.parse(currentDateStr)
        } catch (e: Exception) {
            java.time.LocalDate.now()
        }
        val repeatsVal = repeats ?: 0
        val (_, _, recurrenceType) = decode(repeatsVal)

        val nextDate = when (recurrenceType) {
            RecurrenceType.ONCE -> date
            RecurrenceType.WEEKLY -> date.plusWeeks(1)
            RecurrenceType.EVERY_2_WEEKS -> date.plusWeeks(2)
            RecurrenceType.MONTHLY -> date.plusMonths(1)
            RecurrenceType.EVERY_2_MONTHS -> date.plusMonths(2)
            RecurrenceType.QUARTERLY -> date.plusMonths(3)
            RecurrenceType.HALF_YEARLY -> date.plusMonths(6)
            RecurrenceType.YEARLY -> date.plusYears(1)
            RecurrenceType.FOUR_MONTHS -> date.plusMonths(4)
            RecurrenceType.FOUR_WEEKS -> date.plusWeeks(4)
            RecurrenceType.DAILY -> date.plusDays(1)
            else -> date.plusMonths(1)
        }
        return nextDate.toString()
    }
}
