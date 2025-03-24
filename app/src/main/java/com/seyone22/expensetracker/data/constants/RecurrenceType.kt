package com.seyone22.expensetracker.data.constants

enum class RecurrenceType(val code: Int, val description: String) {
    ONCE(0, "Once"),
    WEEKLY(1, "Weekly"),
    EVERY_2_WEEKS(2, "Every 2 Weeks"),
    MONTHLY(3, "Monthly"),
    EVERY_2_MONTHS(4, "Every 2 Months"),
    QUARTERLY(5, "Quarterly"),
    HALF_YEARLY(6, "Half-Yearly"),
    YEARLY(7, "Yearly"),
    FOUR_MONTHS(8, "Four Months"),
    FOUR_WEEKS(9, "Four Weeks"),
    DAILY(10, "Daily"),
    IN_N_DAYS(11, "In (n) Days"),
    IN_N_MONTHS(12, "In (n) Months"),
    EVERY_N_DAYS(13, "Every (n) Days"),
    EVERY_N_MONTHS(14, "Every (n) Months"),
    MONTH_LAST_DAY(15, "Month (last day)"),
    MONTH_LAST_BUSINESS_DAY(16, "Monthly (last business day)");

    companion object {
        // Decode the integer value to the corresponding RecurrenceType
        fun fromCode(code: Int): RecurrenceType {
            return values().find { it.code == code }
                ?: throw IllegalArgumentException("Invalid recurrence code: $code")
        }

        // Encode the RecurrenceType to its integer code
        fun encode(recurrence: RecurrenceType): Int {
            return recurrence.code
        }
    }
}
