package com.carwale.android.carewale_assessment_challenge.core.di

import android.app.Application
import com.carwale.android.carewale_assessment_challenge.app.api.GoogleService
import com.carwale.android.carewale_assessment_challenge.app.utils.FsConfig
import com.carwale.android.carewale_assessment_challenge.app.utils.MConstants
import com.carwale.android.carewale_assessment_challenge.app.utils.NetworkConnectionInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
object GoogleServiceModule {

    @Singleton
    @Provides
    fun provideGoogleService(application: Application): GoogleService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val oktHttpClient = OkHttpClient.Builder()
                .connectTimeout(MConstants.CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(MConstants.CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(MConstants.CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor(NetworkConnectionInterceptor(application.applicationContext))
//                .addInterceptor(logging)

        return Retrofit.Builder()
                .baseUrl(FsConfig.G_BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(oktHttpClient.build())
                .build()
                .create(GoogleService::class.java)
    }
}