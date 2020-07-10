package com.carwale.android.carewale_assessment_challenge.app.di

import android.app.Application
import com.carwale.android.carewale_assessment_challenge.app.application.CarewaleAssessmentChallengeApplication
import com.carwale.android.carewale_assessment_challenge.core.di.GoogleServiceModule
import com.carwale.android.carewale_assessment_challenge.core.di.NetworkServiceModule
import com.carwale.android.carewale_assessment_challenge.core.di.ViewModelFactoryModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        // Dagger support
        AndroidInjectionModule::class,
        // Global
        CarWaleAssessMentChallengeAppDatabaseModule::class,
        NetworkServiceModule::class,
        GoogleServiceModule::class,
        ViewModelFactoryModule::class,
        // feature
        FeatureBindingModule::class
    ]
)
interface AppComponent : AndroidInjector<CarewaleAssessmentChallengeApplication> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }

    override fun inject(newsApp: CarewaleAssessmentChallengeApplication)
}
