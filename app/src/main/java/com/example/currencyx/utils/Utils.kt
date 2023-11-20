package com.example.currencyx.utils

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Currency

object Utils {
    /**
     * Function to format the amount according to the currency code
     * @param amount The amount to format
     * @param currencyCode The currency code(e.g: US, XOF) of the given amount
     */
    fun formatAmountForCurrency(amount: Double, currencyCode: String): String {
        val currencyInstance = NumberFormat.getCurrencyInstance()
        currencyInstance.maximumFractionDigits = 5
        val currency = Currency.getInstance(currencyCode)

        // Set the currency and format the amount
        currencyInstance.currency = currency
        val roundedAmount = String.format("%.3f", amount).toDouble()

        return currencyInstance.format(roundedAmount)
    }

    /**
     * Checks if a given string contains only letters (A-Z or a-z).
     *
     * @param input The string to be checked.
     * @return `true` if the string contains only letters, `false` otherwise.
     */
    fun containsOnlyLetters(input: String): Boolean {
        // Use a regular expression to match only letters (A-Z or a-z)
        val regex = Regex("^[a-zA-Z]+$")
        return regex.matches(input)
    }

}