package com.example.currencyx

import com.example.currencyx.utils.Utils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito
import java.text.NumberFormat

class UtilsTest {
    @Test
    fun testFormatAmountForCurrency() {
        // Mock the NumberFormat to ensure consistent formatting
        val numberFormat = Mockito.mock(NumberFormat::class.java)
        Mockito.`when`(numberFormat.format(42.0)).thenReturn("$42.00")
        Mockito.`when`(numberFormat.format(12345.6789)).thenReturn("$12,345.6789")

        val currencyCode = "USD"
        val amount = 42.0

        val formattedAmount = Utils.formatAmountForCurrency(amount, currencyCode)

        assertEquals("US$42.00", formattedAmount)
    }

    @Test
    fun testContainsOnlyLetters() {
        assertTrue(Utils.containsOnlyLetters("abcXYZ"))
        assertTrue(Utils.containsOnlyLetters("abc"))
        assertFalse(Utils.containsOnlyLetters("abc123"))
        assertFalse(Utils.containsOnlyLetters("123"))
        assertFalse(Utils.containsOnlyLetters(""))
    }
}