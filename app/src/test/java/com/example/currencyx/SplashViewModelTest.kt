package com.example.currencyx

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.currencyx.di.repository.AppDaoRepository
import com.example.currencyx.di.repository.CurrencyXApiRepository
import com.example.currencyx.model.Currency
import com.example.currencyx.utils.data.TestData
import com.example.currencyx.viewModel.SplashViewModel
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class SplashViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Create a test coroutine dispatcher
    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var mockApi: CurrencyXApiRepository

    @Mock
    private lateinit var mockDao: AppDaoRepository

    private lateinit var viewModel: SplashViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        viewModel = SplashViewModel(mockApi, mockDao)
        Dispatchers.setMain(testDispatcher) // Set the main dispatcher to testDispatcher
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the main dispatcher
        testDispatcher.cleanupTestCoroutines() // Cleanup after the tests
    }

    @Test
    fun testSplashViewModelInstantiation() {
        val viewModel = SplashViewModel(mockApi, mockDao)
        assertNotNull(viewModel)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchCurrencies should insert currencies into the local database`(): Unit =
        testDispatcher.runBlockingTest {
            // Given
            val currencyData = mapOf("USD" to "US Dollar", "EUR" to "Euro")
            val currencies = currencyData.map { (code, name) -> Currency(0, code, name) }

            // Mock the API call to return currency data
            `when`(mockApi.getCurrencies()).thenReturn(currencyData)

            // When
            viewModel.fetchCurrencies()

            // Then
            // Verify that the API call is made
            verify(mockApi).getCurrencies()

            // Verify that the fetched currencies are inserted into the local database
            verify(mockDao).insertCurrencies(currencies)
        }

    /**
     * Should be run independently
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchLatestExchangeRates should fetch and store latest exchange rates`(): Unit =
        testDispatcher.runBlockingTest {
            // Given
            val exchangeRateData = TestData.exchangeRate
            // Mock the API call to return exchange rate data
            `when`(mockApi.getLatestExchangeRates()).thenReturn(exchangeRateData)

            // When
            viewModel.fetchLatestExchangeRates()

            // Then
            // Verify that the API call is made
            verify(mockApi).getLatestExchangeRates()

            // Verify that the fetched exchange rate data is inserted into the local database
            verify(mockDao).insertExchangeRate(exchangeRateData)
        }
}