package com.carwale.android.carewale_assessment_challenge.app.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.carwale.android.carewale_assessment_challenge.app.room.entities.CountryDetails.CountryDetails.tableName
import com.carwale.android.carewale_assessment_challenge.app.room.entities.CountryDetails.CountryDetails.Column

@Entity(tableName = tableName)
data class CountryDetails(

    /**
     * Primary key for CountryDetails table.
     */
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = Column.countryName)
    val countryName: String,
    @ColumnInfo(name = Column.countryCode)
    val countryCode: String?,
    @ColumnInfo(name = Column.slug)
    val slug: String?,

    @ColumnInfo(name = Column.newConfirmed)
    val newConfirmed: Long?,
    @ColumnInfo(name = Column.totalConfirmed)
    val totalConfirmed: Long?,
    @ColumnInfo(name = Column.newDeaths)
    val newDeaths: Long?,
    @ColumnInfo(name = Column.totalDeaths)
    val totalDeaths: Long?,
    @ColumnInfo(name = Column.newRecovered)
    val newRecovered: Long?,
    @ColumnInfo(name = Column.totalRecovered)
    val totalRecovered: Long?,
    @ColumnInfo(name = Column.parentId)
    val parentId: Int = 0

) {
    object CountryDetails {
        const val tableName = "country_list_details"

        object Column {
            const val countryName = "country_name"
            const val countryCode = "country_code"
            const val slug = "slug"
            const val newConfirmed = "new_confirmed"
            const val totalConfirmed = "total_confirmed"
            const val newDeaths = "new_deaths"
            const val totalDeaths = "total_deaths"
            const val newRecovered = "new_recovered"
            const val totalRecovered = "total_recovered"
            const val parentId = "parent_id"// foreign key
        }
    }
}
