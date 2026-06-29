package com.seyone22.expensetracker.ui.screen.operations.transaction

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SplitTransactionUiStateTest {

    data class SplitDetails(
        val splitAmount: String = "",
        val categId: String = "-1",
        val notes: String = ""
    )

    private fun validateSplitSum(totalAmount: Double, splits: List<SplitDetails>): Boolean {
        if (splits.isEmpty()) return false
        val sum = splits.sumOf { it.splitAmount.toDoubleOrNull() ?: 0.0 }
        return Math.abs(totalAmount - sum) < 0.01
    }

    @Test
    fun testSplitSumValidation() {
        val total = 100.0
        val validSplits = listOf(
            SplitDetails("40.0", "1"),
            SplitDetails("60.00", "2")
        )
        val invalidSplits = listOf(
            SplitDetails("40.0", "1"),
            SplitDetails("50.0", "2")
        )

        assertTrue(validateSplitSum(total, validSplits))
        assertFalse(validateSplitSum(total, invalidSplits))
        assertFalse(validateSplitSum(total, emptyList()))
    }
}
