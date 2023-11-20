package com.example.currencyx.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.currencyx.R
import com.example.currencyx.navigation.NavDestination
import com.example.currencyx.ui.theme.CurrencyXTheme
import com.example.currencyx.viewModel.SplashUiState
import com.example.currencyx.viewModel.SplashViewModel
import kotlinx.coroutines.delay

object SplashScreen : NavDestination {
    override val route: String = "splash_Screen"
}

@Composable
fun SplashScreen(
    navController: NavHostController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    viewModel.initUiState()

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(all = 16.dp)
        )
        CircularProgressIndicator(
            modifier = Modifier
                .size(48.dp)
                .padding(vertical = 15.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            SplashUiState.LOADING -> {
                // Handle loading case if needed
            }

            SplashUiState.DONE -> {
                delay(1500)
                navigateToNextScreen(navController = navController, route = ConversionScreen.route)
            }

            SplashUiState.ERROR -> {
                // Handle error case if needed
            }
        }
    }
}

private fun navigateToNextScreen(navController: NavHostController, route: String) {
    navController.navigate(route = route) {
        popUpTo(route = SplashScreen.route) {
            inclusive = true
        }
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    CurrencyXTheme {
        SplashScreen(navController = rememberNavController())
    }
}