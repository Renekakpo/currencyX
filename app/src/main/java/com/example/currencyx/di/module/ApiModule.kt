package com.example.currencyx.di.module

import com.example.currencyx.network.CurrencyXApi
import com.example.currencyx.di.repository.CurrencyXApiRepository
import com.example.currencyx.utils.Constant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constant.API_BASE_URL).build()
    }

    @Singleton
    @Provides
    fun provideCurrencyXApi(retrofit: Retrofit): CurrencyXApi {
        return retrofit.create(CurrencyXApi::class.java)
    }

    @Provides
    fun provideCurrencyXApiRepository(api: CurrencyXApi): CurrencyXApiRepository = CurrencyXApiRepository(api)
}