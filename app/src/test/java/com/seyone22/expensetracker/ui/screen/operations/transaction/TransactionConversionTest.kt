package com.seyone22.expensetracker.ui.screen.operations.transaction

import org.junit.Assert.assertEquals
import org.junit.Test

class TransactionConversionTest {

    @Test
    fun testSafeDoubleParsing() {
        val emptyInput = ""
        val invalidInput = "abc"
        val validInput = "123.45"
        val commasInput = "1,234.56"

        assertEquals(0.0, emptyInput.toDoubleOrNull() ?: 0.0, 0.001)
        assertEquals(0.0, invalidInput.toDoubleOrNull() ?: 0.0, 0.001)
        assertEquals(123.45, validInput.toDoubleOrNull() ?: 0.0, 0.001)
        assertEquals(0.0, commasInput.toDoubleOrNull() ?: 0.0, 0.001) // toDoubleOrNull is strict about commas
    }

    @Test
    fun testCurrencyConversionMath() {
        // baseConvRate represents how many base currency units equals 1 unit of this currency
        // e.g. Base = USD
        // EUR baseConvRate = 1.10 (meaning 1 EUR = 1.10 USD)
        // GBP baseConvRate = 1.30 (meaning 1 GBP = 1.30 USD)
        
        val amountInSource = 100.0 // 100 EUR
        val sourceRate = 1.10
        val targetRate = 1.30

        // Convert 100 EUR to GBP:
        // Value in USD = 100 * 1.10 = 110 USD.
        // Value in GBP = 110 / 1.30 = 84.615 GBP.
        val convertedAmount = amountInSource * (sourceRate / targetRate)

        assertEquals(84.615, convertedAmount, 0.001)
    }
}
