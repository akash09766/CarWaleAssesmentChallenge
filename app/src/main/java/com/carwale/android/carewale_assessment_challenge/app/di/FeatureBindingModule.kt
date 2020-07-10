package com.carwale.android.carewale_assessment_challenge.app.di

import com.carwale.android.carewale_assessment_challenge.app.activity.MainActivity
import com.carwale.android.carewale_assessment_challenge.app.dataSource.DataRepositoryModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Describes list of activities and fragment which require
 * DI.
 *
 * Each [ContributesAndroidInjector] generates a sub-component
 * for each activity under the hood
 */
@Module(
    includes = [
        SearchScreenViewModelModule::class,
        DataRepositoryModule::class
    ]
)
interface FeatureBindingModule {
    @ContributesAndroidInjector
    fun contributeMainActivity(): MainActivity
}