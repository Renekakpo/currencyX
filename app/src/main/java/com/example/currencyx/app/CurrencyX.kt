package com.example.currencyx.app

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.currencyx.worker.ExchangeRateWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class CurrencyX : Application(), Configuration.Provider {
    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        startExchangeRateWorker(applicationContext)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .build()
    }

    /**
     * Function to configure and start the periodic exchange rate worker
     */
    private fun startExchangeRateWorker(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Configure the periodic exchange rate worker to run every 30 minutes
        val exchangeRateRequest = PeriodicWorkRequestBuilder<ExchangeRateWorker>(
            repeatInterval = 30,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        // Enqueue the periodic worker with a unique tag
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            ExchangeRateWorker.TAG,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            exchangeRateRequest
        )
    }
}