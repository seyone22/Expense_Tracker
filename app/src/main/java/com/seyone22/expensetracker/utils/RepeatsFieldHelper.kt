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
}
