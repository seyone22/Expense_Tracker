package com.seyone22.expensetracker.data.constants

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.seyone22.expensetracker.R

data class BankStyle(
    val name: String,
    val primaryColor: Color,
    @DrawableRes val logoResId: Int // Resource ID for the bank's logo
)

object BankStyles {
    val bankStyleMap: Map<String, BankStyle> = mapOf(
        "Commercial Bank" to BankStyle(
            name = "Commercial Bank",
            primaryColor = Color(0xFF006FBA),
            logoResId = R.drawable.commercial_bank
        ),
        "Bank of Ceylon" to BankStyle(
            name = "Bank of Ceylon",
            primaryColor = Color(0xFFFFC805),
            logoResId = R.drawable.boc
        ),
    )
}
