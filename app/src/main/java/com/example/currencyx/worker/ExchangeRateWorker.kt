package com.example.currencyx.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.currencyx.R
import com.example.currencyx.model.Currency
import com.example.currencyx.di.repository.AppDaoRepository
import com.example.currencyx.di.repository.CurrencyXApiRepository
import com.example.currencyx.utils.NotificationUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@HiltWorker
class ExchangeRateWorker @AssistedInject constructor(
    @Assisted context: Context, // Injected Context for the worker.
    @Assisted params: WorkerParameters,
    private val api: CurrencyXApiRepository, // Injected API repository for data retrieval.
    private val dao: AppDaoRepository // Injected DAO repository for database operations.
) : CoroutineWorker(context, params) {

    companion object {
        val TAG: String = ExchangeRateWorker::class.java.simpleName // TAG for logging purposes.
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val mContext = applicationContext

            // Display a notification about updating exchange rates.
            NotificationUtils.showNotification(
                context = mContext,
                title = mContext.getString(R.string.exchange_rate_notification_title),
                message = mContext.getString(R.string.exchange_rate_notification_message)
            )

            // Delay for 5 seconds (simulate notification display).
            delay(5000)

            // Update the exchange rate data from the API and store it in the database.
            updateExchangeRateData(api, dao)

            // Update the list of currencies data from the API and store it in the database.
            updateCurrenciesData(api, dao)

            delay(5000) // Delay for 5 seconds.

            // Dismiss the notification.
            NotificationUtils.dismissNotification(mContext)

            return@withContext Result.success()
        } catch (e: Exception) {
            // Log any errors that occur.
            Log.e("ExchangeRateWorker", "${e.message}")

            // Create a Data object to pass the error message to the result.
            val outputData = Data.Builder().putString("error", "${e.message}").build()

            // Return a failure result with the error data.
            return@withContext Result.failure(outputData)
        }
    }

    private suspend fun updateExchangeRateData(api: CurrencyXApiRepository, dao: AppDaoRepository) {
        try {
            // Fetch the latest exchange rates from the API.
            val mExchangeRate = api.getLatestExchangeRates()

            // Insert the retrieved exchange rate data into the database.
            dao.insertExchangeRate(exchangeRate = mExchangeRate)
        } catch (e: Exception) {
            // Handle exceptions and log error messages
            Log.e("updateExchangeRateData", "${e.message}")
        }
    }

    private suspend fun updateCurrenciesData(api: CurrencyXApiRepository, dao: AppDaoRepository) {
        try {
            // Initialize a list to store currency data.
            val mCurrencies = mutableListOf<Currency>()

            // Fetch a list of currencies from the API and convert it into a list of Currency objects.
            api.getCurrencies().forEach { (code, name) ->
                mCurrencies.add(Currency(0, code, name))
            }

            // Check if any currencies were retrieved.
            if (mCurrencies.isNotEmpty()) {
                // Insert the retrieved currency data into the database.
                dao.insertCurrencies(mCurrencies)
            }
        } catch (e: Exception) {
            // Handle exceptions and log error messages
            Log.e("updateCurrenciesData", "${e.message}")
        }
    }
}