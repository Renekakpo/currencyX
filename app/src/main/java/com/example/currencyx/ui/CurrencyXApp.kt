package com.example.currencyx.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.currencyx.navigation.CurrencyXNavHost

@Composable
fun CurrencyXApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    CurrencyXNavHost(modifier = modifier, navController = navController)
}