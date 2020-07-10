package com.carwale.android.carewale_assessment_challenge.core.ui.base


import android.app.Service
import com.carwale.android.carewale_assessment_challenge.core.di.base.Injectable
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject

/**
 * Base service providing Dagger support
 */
abstract class DaggerrService : Service(), Injectable {
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
}
