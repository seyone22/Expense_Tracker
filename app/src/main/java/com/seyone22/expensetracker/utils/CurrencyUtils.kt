package com.seyone22.expensetracker.utils

enum class TransactionType {
    INCOME,
    EXPENSE
}

fun getValueWithType(value: Double?): Pair<Double, TransactionType>? {
    if (value == null) return null
    return if (value < 0) {
        Pair(value * -1, TransactionType.EXPENSE)
    } else {
        Pair(value, TransactionType.INCOME)
    }
}