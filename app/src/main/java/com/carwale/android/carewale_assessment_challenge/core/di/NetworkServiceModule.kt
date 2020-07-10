package com.carwale.android.carewale_assessment_challenge.core.di

import android.app.Application
import com.carwale.android.carewale_assessment_challenge.app.api.ApiService
import com.carwale.android.carewale_assessment_challenge.app.utils.FsConfig
import com.carwale.android.carewale_assessment_challenge.app.utils.MConstants
import com.carwale.android.carewale_assessment_challenge.app.utils.NetworkConnectionInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
object NetworkServiceModule {

    @Singleton
    @Provides
    fun provideMoviesService(application: Application): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val oktHttpClient = OkHttpClient.Builder()
            .connectTimeout(MConstants.CONNECTION_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(MConstants.CONNECTION_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(MConstants.CONNECTION_TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(NetworkConnectionInterceptor(application.applicationContext))
            .addInterceptor(logging)

        return Retrofit.Builder()
            .baseUrl(FsConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(oktHttpClient.build())
            .build()
            .create(ApiService::class.java)
    }
}