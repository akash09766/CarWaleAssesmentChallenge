package com.carwale.android.carewale_assessment_challenge.app.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.carwale.android.carewale_assessment_challenge.app.room.entities.GlobalDetails.GlobalDetails.tableName
import com.carwale.android.carewale_assessment_challenge.app.room.entities.GlobalDetails.GlobalDetails.Column

@Entity(tableName = tableName)
data class GlobalDetails(
    
    /**
     * Primary key for GlobalDetails table.
     */

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = Column.id)
    val id: Int = 0,

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
    val totalRecovered: Long?
) {
    @Ignore
    var countryList: ArrayList<CountryDetails?>? = ArrayList()

    object GlobalDetails {
        const val tableName = "global_details"

        object Column {
            const val id = "id"
            const val newConfirmed = "new_confirmed"
            const val totalConfirmed = "total_confirmed"
            const val newDeaths = "new_deaths"
            const val totalDeaths = "total_deaths"
            const val newRecovered = "new_recovered"
            const val totalRecovered = "total_recovered"
        }
    }
}
