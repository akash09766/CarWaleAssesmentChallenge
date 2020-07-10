package com.carwale.android.carewale_assessment_challenge.app.di

import android.app.Application
import com.carwale.android.carewale_assessment_challenge.app.room.dao.GlobalDetailsDao
import com.carwale.android.carewale_assessment_challenge.app.room.db.CarWaleAssessMentChallengeAppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object CarWaleAssessMentChallengeAppDatabaseModule {
    @Singleton
    @Provides
    fun provideDb(app: Application): CarWaleAssessMentChallengeAppDatabase = CarWaleAssessMentChallengeAppDatabase.buildDefault(app)

    @Singleton
    @Provides
    fun provideGlobalDetailsDao(db: CarWaleAssessMentChallengeAppDatabase): GlobalDetailsDao = db.imageDetailsDao()
}