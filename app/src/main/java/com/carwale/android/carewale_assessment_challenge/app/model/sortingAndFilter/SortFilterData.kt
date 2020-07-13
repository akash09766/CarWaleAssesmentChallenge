package com.carwale.android.carewale_assessment_challenge.app.model.sortingAndFilter

import com.carwale.android.carewale_assessment_challenge.app.utils.MConstants

class SortFilterData {

    var sortCategory = MConstants.INFECTED_SORT_CATEGORY
    var sortOrder = MConstants.DESCENDING_SORT

    var countryName = ""

    var globalInfected: Long = 0
    var globalDeath: Long = 0
    var globalRecovered: Long = 0

    var minInfectedSelected: Long = MConstants.DEFAULT_INITIAL_MIN_VALUE_SORT_FILTER_DATA
    var maxInfectedSelected: Long = MConstants.DEFAULT_INITIAL_MAX_VALUE_SORT_FILTER_DATA

    var minDeathSelected: Long = MConstants.DEFAULT_INITIAL_MIN_VALUE_SORT_FILTER_DATA
    var maxDeathSelected: Long = MConstants.DEFAULT_INITIAL_MAX_VALUE_SORT_FILTER_DATA

    var minRecoveredSelected: Long = MConstants.DEFAULT_INITIAL_MIN_VALUE_SORT_FILTER_DATA
    var maxRecoveredSelected: Long = MConstants.DEFAULT_INITIAL_MAX_VALUE_SORT_FILTER_DATA
}