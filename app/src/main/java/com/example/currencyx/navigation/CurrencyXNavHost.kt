package com.example.currencyx.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.currencyx.ui.screens.ConversionScreen
import com.example.currencyx.ui.screens.CurrencySelectionScreen
import com.example.currencyx.ui.screens.SplashScreen
import com.example.currencyx.viewModel.ConversionViewModel

@Composable
fun CurrencyXNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    conversionViewModel: ConversionViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = SplashScreen.route,
        modifier = modifier
    ) {
        composable(route = SplashScreen.route) {
            SplashScreen(navController = navController)
        }

        composable(route = ConversionScreen.route) {
            ConversionScreen(navController = navController, viewModel = conversionViewModel)
        }

        composable(route = CurrencySelectionScreen.route) {
            CurrencySelectionScreen(navController = navController, viewModel = conversionViewModel)
        }
    }
}