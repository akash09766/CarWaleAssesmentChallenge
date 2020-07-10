package com.carwale.android.carewale_assessment_challenge.app.room.entities

import androidx.room.Embedded
import androidx.room.Relation
import kotlin.collections.ArrayList

class GlobalDetailsWithCountry {
    @Embedded
    var globalDetails: GlobalDetails? = null

    @Relation(parentColumn = GlobalDetails.GlobalDetails.Column.id, entityColumn = CountryDetails.CountryDetails.Column.parentId)
    var countryList: List<CountryDetails> = ArrayList()
}