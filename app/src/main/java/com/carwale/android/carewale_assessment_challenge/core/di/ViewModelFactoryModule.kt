package com.carwale.android.carewale_assessment_challenge.core.di

import androidx.lifecycle.ViewModelProvider
import com.carwale.android.carewale_assessment_challenge.core.di.base.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
interface ViewModelFactoryModule {
    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}