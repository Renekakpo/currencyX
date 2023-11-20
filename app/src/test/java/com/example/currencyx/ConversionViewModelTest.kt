package com.example.currencyx

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.currencyx.di.repository.AppDaoRepository
import com.example.currencyx.model.Currency
import com.example.currencyx.utils.data.TestData
import com.example.currencyx.viewModel.ConversionViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ConversionViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set up the main dispatcher for testing
    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var viewModel: ConversionViewModel

    // Create a mock AppDaoRepository
    private val mockDao = mockk<AppDaoRepository>()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setupViewModel() {
        // Set the main dispatcher to testDispatcher
        Dispatchers.setMain(testDispatcher)

        // Mock data for ExchangeRate and Currencies
        val exchangeRate = TestData.exchangeRate
        val currencies = TestData.dummyCurrencies

        // Set up the behavior of the mock repository
        coEvery { mockDao.getExchangeRate() } returns flowOf(exchangeRate)
        coEvery { mockDao.getCurrencies() } returns flowOf(currencies)

        viewModel = ConversionViewModel(mockDao)

        viewModel.initDataForQuickAccess()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the main dispatcher
        testDispatcher.cleanupTestCoroutines() // Cleanup after the tests
    }

    @Test
    fun testInitConversionResults() {
        val currencies = TestData.dummyCurrencies

        viewModel.initConversionResults(currencies)

        val conversionResults = viewModel.conversionResults.value

        // Assert that the conversionResults list is correctly initialized
        assertEquals(currencies.size, conversionResults.size)

        for (result in conversionResults) {
            assertEquals(0.0, result.convertedAmount, 0.01) // Check that the amounts are initialized to 0.0
        }
    }

    @Test
    fun testSetSelectedCurrency() {
        val currency = Currency(code ="XOF", name ="CFA Franc BCEAO")

        viewModel.setSelectedCurrency(currency)

        val selectedCurrency = viewModel.selectedCurrency.value

        // Assert that the selected currency is set correctly
        assertEquals(currency, selectedCurrency)
    }

    @Test
    fun testConversionWithZeroAmount() {
        val amount = 0.0
        val baseCurrency = Currency(code = "USD", name = "United States Dollar")
        val targetCurrencies = TestData.dummyCurrencies

        viewModel.convertCurrency(amount, baseCurrency, targetCurrencies)

        Thread.sleep(1000)

        val conversionResults = viewModel.conversionResults.value

        assertEquals(targetCurrencies.size, conversionResults.size)

        for (result in conversionResults) {
            assertEquals(0.0, result.convertedAmount, 0.01)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testConvertCurrency() = testDispatcher.runBlockingTest {
        val amount = 1000000000000.0
        val baseCurrency = Currency(code ="USD", name = "United States Dollar")
        val targetCurrencies = TestData.dummyCurrencies

        viewModel.convertCurrency(amount, baseCurrency, targetCurrencies)

        // Allow some time for the coroutine to complete and update conversionResults
        delay(1500)

        val conversionResults = viewModel.conversionResults.value

        // Assert that conversion results are calculated correctly
        assertEquals(targetCurrencies.size, conversionResults.size)

        for (result in conversionResults) {
            assertNotEquals(0.0, result.convertedAmount) // Check that the converted amounts are not all 0.0
        }
    }

    @Test
    fun testInitDataForQuickAccess() {
        val exchangeRate = TestData.exchangeRate
        val currencies = TestData.dummyCurrencies

        coEvery { mockDao.getExchangeRate() } returns flowOf(exchangeRate)
        coEvery { mockDao.getCurrencies() } returns flowOf(currencies)

        viewModel = ConversionViewModel(mockDao)

        // Call initDataForQuickAccess to collect new data
        viewModel.initDataForQuickAccess()

        // Ensure the latestExchangeRates and currencies are updated
        assertEquals(exchangeRate, viewModel.latestExchangeRateData)
        assertEquals(currencies, viewModel.currenciesData)
    }

    @Test
    fun testConversionWithInvalidBaseCurrency() {
        val amount = 100.0
        val baseCurrency = Currency(code = "INVALID", name = "Invalid Currency")
        val targetCurrencies = TestData.dummyCurrencies

        viewModel.convertCurrency(amount, baseCurrency, targetCurrencies)

        // Delay for some time to allow the coroutine to complete
        Thread.sleep(10000)

        val conversionResults = viewModel.conversionResults.value

        assertEquals(targetCurrencies.size, conversionResults.size)

        for (result in conversionResults) {
            assertNotEquals(0.0, result.convertedAmount, 0.00000001)
        }
    }

    @Test
    fun testConversionWithEmptyTargetCurrencies() = testDispatcher.runBlockingTest {
        val amount = 100.0
        val baseCurrency = Currency(code = "USD", name = "United States Dollar")
        val targetCurrencies = emptyList<Currency>()

        viewModel.convertCurrency(amount, baseCurrency, targetCurrencies)

        // Delay for some time to allow the coroutine to complete
        delay(1500)

        val conversionResults = viewModel.conversionResults.value
        assertTrue(conversionResults.isEmpty()) // There should be no conversion results
    }

    @Test
    fun testErrorHandling() = testDispatcher.runBlockingTest {
        val amount = 100.0
        val baseCurrency = Currency(code = "USD", name = "United States Dollar")
        val targetCurrencies = TestData.dummyCurrencies

        // Simulate an exception in the repository
        coEvery { mockDao.getExchangeRate() } throws Exception("Network error")

        viewModel.convertCurrency(amount, baseCurrency, targetCurrencies)

        // Delay for some time to allow the coroutine to complete
        delay(1500)

        val conversionResults = viewModel.conversionResults.value
        assertTrue(conversionResults.isEmpty()) // There should be no conversion results
        // You can also assert that an error message is logged or displayed
    }
}