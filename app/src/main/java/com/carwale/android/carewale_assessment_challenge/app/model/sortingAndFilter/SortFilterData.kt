package com.carwale.android.carewale_assessment_challenge.app.model.sortingAndFilter

import com.carwale.android.carewale_assessment_challenge.app.utils.MConstants

class SortFilterData {

    var sortCategory = MConstants.LOCATION_SORT_CATEGORY
    var sortOrder = MConstants.ASCENDING_SORT

    var globalInfected: Long = 0
    var globalDeath: Long = 0
    var globalRecovered: Long = 0

    var minInfectedSelected: Long = 0
    var maxInfectedSelected: Long = 0

    var minDeathSelected: Long = 0
    var maxDeathSelected: Long = 0

    var minRecoveredSelected: Long = 0
    var maxRecoveredSelected: Long = 0
}