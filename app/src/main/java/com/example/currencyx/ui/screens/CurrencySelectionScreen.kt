package com.example.currencyx.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.currencyx.R
import com.example.currencyx.model.Currency
import com.example.currencyx.navigation.NavDestination
import com.example.currencyx.ui.theme.CurrencyXTheme
import com.example.currencyx.utils.Utils
import com.example.currencyx.viewModel.ConversionViewModel

object CurrencySelectionScreen : NavDestination {
    override val route: String = "currency_selection_screen"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelectionScreen(
    navController: NavController,
    viewModel: ConversionViewModel
) {
    val currencies by viewModel.currencies.collectAsState(initial = emptyList())
    val selectedCurrency by viewModel.selectedCurrency.collectAsState(null)
    var searchText by remember { mutableStateOf("") }

    // Filtered list of currencies based on the search query
    val filteredCurrencies = if (searchText.isNotBlank()) {
        currencies.filter { currency ->
            currency.code.contains(searchText, ignoreCase = true) || currency.name.contains(
                searchText,
                ignoreCase = true
            )
        }
    } else {
        currencies
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Back arrow: Navigation back
            IconButton(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back_button_description_text),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            // Header: Display the name of the screen
            Text(
                text = stringResource(R.string.currency_selection_ui_header),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // SubHeader: Tells the user what to do on this page
        Text(
            text = stringResource(R.string.currency_selection_ui_subheader),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search Field
        TextField(
            value = searchText,
            onValueChange = {
                if (Utils.containsOnlyLetters(it)) {
                    searchText = it
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(50.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.small
                ),
            placeholder = {
                Text(
                    stringResource(R.string.search_currency_placeholder_text),
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Scrollable list of available currencies within the app
        LazyColumn {
            items(filteredCurrencies) { currency ->
                CurrencySelectionItem(
                    isSelected = selectedCurrency == currency,
                    currency = currency,
                    onCurrencySelected = {
                        viewModel.setSelectedCurrency(it)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
fun CurrencySelectionItem(
    isSelected: Boolean,
    currency: Currency,
    onCurrencySelected: (Currency) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCurrencySelected(currency) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${currency.code} - ${currency.name}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f)
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
@Preview
fun CurrencySelectionScreenPreview() {
    CurrencyXTheme {
        CurrencySelectionScreen(
            navController = rememberNavController(),
            viewModel = hiltViewModel()
        )
    }
}