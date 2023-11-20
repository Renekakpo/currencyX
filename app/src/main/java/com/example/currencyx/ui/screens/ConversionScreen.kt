package com.example.currencyx.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.currencyx.R
import com.example.currencyx.model.ConversionResult
import com.example.currencyx.navigation.NavDestination
import com.example.currencyx.ui.theme.CurrencyXTheme
import com.example.currencyx.utils.Utils.formatAmountForCurrency
import com.example.currencyx.viewModel.ConversionViewModel

object ConversionScreen : NavDestination {
    override val route: String = "conversion_screen"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversionScreen(
    navController: NavHostController,
    viewModel: ConversionViewModel
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val currencies by viewModel.currencies.collectAsState(initial = emptyList())
    val conversionResults by viewModel.conversionResults.collectAsState(initial = emptyList())
    var amount by rememberSaveable { mutableStateOf("") }
    val selectedCurrency by viewModel.selectedCurrency.collectAsState(null)
    val currencySelectionButtonClicked: () -> Unit = {
        navController.navigate(CurrencySelectionScreen.route)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.app_name),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(all = 8.dp)
        )
        Text(
            text = stringResource(R.string.home_subheader_text),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(all = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = amount,
            singleLine = true,
            onValueChange = { if ( it.length <= 13) amount = it },
            label = {
                Text(
                    text = stringResource(R.string.amount_text),
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp)
                .height(60.dp),
            placeholder = {
                Text(
                    text = "0",
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary
            ),
            shape = MaterialTheme.shapes.small
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.from_text),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .border(
                            border = ButtonDefaults.outlinedButtonBorder,
                            shape = MaterialTheme.shapes.small
                        )
                        .clickable(onClick = currencySelectionButtonClicked),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedCurrency?.code ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(5.dp)
                            .size(25.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }

            }

            Spacer(modifier = Modifier.size(16.dp))

            Button(
                modifier = Modifier
                    .weight(1f)
                    .height(45.dp),
                shape = MaterialTheme.shapes.small,
                onClick = {
                    selectedCurrency?.let {
                        viewModel.convertCurrency(
                            amount = amount.toDouble(),
                            baseCurrency = it,
                            targetCurrencies = currencies
                        )
                    }

                    // Close the soft keyboard
                    keyboardController?.hide()
                }
            ) {
                Text(
                    text = stringResource(R.string.convert_button_text),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Icon(
            imageVector = Icons.Default.ImportExport,
            contentDescription = null,
            modifier = Modifier
                .padding(5.dp)
                .size(30.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // A list that displays the conversions in other currencies
        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
            items(conversionResults) { result ->
                ConversionItemRow(result = result)
            }
        }

    }

    LaunchedEffect(currencies.isNotEmpty()) {
        if (currencies.isNotEmpty() && selectedCurrency == null) {
            viewModel.setSelectedCurrency(currencies.first())
        }

        if (currencies.isNotEmpty()) {
            viewModel.initConversionResults(currencies)
            viewModel.initDataForQuickAccess()
        }
    }
}

@Composable
fun ConversionItemRow(result: ConversionResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Currency Code and Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = result.targetCurrency.code.uppercase(),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = formatAmountForCurrency(
                        amount = result.convertedAmount,
                        currencyCode = result.targetCurrency.code
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Currency Name
            Text(
                text = result.targetCurrency.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Preview
@Composable
fun ConversionScreenPreview() {
    CurrencyXTheme {
        ConversionScreen(
            navController = rememberNavController(),
            viewModel = hiltViewModel()
        )
    }
}