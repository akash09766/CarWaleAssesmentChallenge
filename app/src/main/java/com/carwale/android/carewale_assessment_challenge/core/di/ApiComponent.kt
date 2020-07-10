package com.carwale.android.carewale_assessment_challenge.core.di

import dagger.Component

@Component(modules = [AppModule::class])
interface ApiComponent {
    fun inject(apiService: NetworkServiceModule)
}