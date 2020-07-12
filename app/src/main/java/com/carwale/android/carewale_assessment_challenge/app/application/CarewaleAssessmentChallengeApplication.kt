package com.carwale.android.carewale_assessment_challenge.app.application

import android.app.Application
import com.carwale.android.carewale_assessment_challenge.app.prefs.Prefs
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class CarewaleAssessmentChallengeApplication : Application(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    companion object {
        var prefs: Prefs? = null
    }

    override fun onCreate() {
        prefs = Prefs(applicationContext)
        super.onCreate()
        // Init DI magic ✨
        AppInjector.init(this)
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector
}