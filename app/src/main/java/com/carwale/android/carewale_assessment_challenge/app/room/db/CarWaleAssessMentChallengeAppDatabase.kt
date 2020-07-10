package com.carwale.android.carewale_assessment_challenge.app.room.db

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.carwale.android.carewale_assessment_challenge.app.room.dao.GlobalDetailsDao
import com.carwale.android.carewale_assessment_challenge.app.room.entities.CountryDetails
import com.carwale.android.carewale_assessment_challenge.app.room.entities.GlobalDetails

@Database(
    entities = [GlobalDetails::class,CountryDetails::class],
    version = CarWaleAssessMentDatabaseMigrations.latestVersion
)
abstract class CarWaleAssessMentChallengeAppDatabase : RoomDatabase() {

    /**
     * Get image and comments Details
     */
    abstract fun imageDetailsDao(): GlobalDetailsDao

    companion object {

        private const val databaseName = "axxcess-db"

        fun buildDefault(context: Context) =
            Room.databaseBuilder(context, CarWaleAssessMentChallengeAppDatabase::class.java, databaseName)
                .addMigrations(*CarWaleAssessMentDatabaseMigrations.allMigrations)
                .build()

        @VisibleForTesting
        fun buildTest(context: Context) =
            Room.inMemoryDatabaseBuilder(context, CarWaleAssessMentChallengeAppDatabase::class.java)
                .build()
    }
}