package com.carwale.android.carewale_assessment_challenge.app.room.dao

import androidx.room.*
import com.carwale.android.carewale_assessment_challenge.app.room.entities.CountryDetails
import com.carwale.android.carewale_assessment_challenge.app.room.entities.GlobalDetails
import com.carwale.android.carewale_assessment_challenge.app.room.entities.GlobalDetailsWithCountry
import kotlinx.coroutines.flow.Flow

@Dao
interface GlobalDetailsDao {

    @Transaction
    @Query("SELECT * FROM global_details WHERE id =:id")
    fun getGlobalDetailsWithCountry(id: Int): Flow<GlobalDetailsWithCountry?>

    @Transaction
    fun insertGlobalDetailsWithCountry(GlobalDetails: GlobalDetails) {
        insertCountryDetailsList(GlobalDetails.countryList?.toList())
        insertGlobalDetails(GlobalDetails)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGlobalDetails(GlobalDetails: GlobalDetails)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCountryDetailsList(CountryDetails: List<CountryDetails?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCountryDetails(CountryDetails: CountryDetails?)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateCountryDetails(CountryDetails: List<CountryDetails?>?): Int

    @Query("DELETE FROM global_details WHERE id =:id")
    fun deleteGlobalDetailById(id: Int)

    @Query("DELETE FROM global_details")
    fun nukeGlobalDetailsTable()

    @Query("DELETE FROM country_list_details")
    fun nukeCountryDetailsTable()
}