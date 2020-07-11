package com.carwale.android.carewale_assessment_challenge.app.utils

object MConstants {

    const val CONNECTION_TIME_OUT = 25L
    const val DEFAULT_PRIMARY_KET_GLOBAL_LIST_TABLE = 0

    const val DEFAULT_INITIAL_MAX_VALUE_SORT_FILTER_DATA = -1L
    const val DEFAULT_INITIAL_MIN_VALUE_SORT_FILTER_DATA = 0L

    const val LOCATION_SORT_CATEGORY = 1
    const val INFECTED_SORT_CATEGORY = 2
    const val DEATH_SORT_CATEGORY = 3
    const val RECOVERED_SORT_CATEGORY = 4

    const val ASCENDING_SORT = 1
    const val DESCENDING_SORT = 2

    // server response type
    const val SUCCESS = "SUCCESS"
    const val FAILED = "FAILED"
    const val ERROR = "ERROR"

    // network error message
    const val NOT_CONNECTED_TO_INTERNET = "You are not connected to internet."
    const val SERVER_NOT_RESPONDING =
        "Somethings wrong with our servers at the moment. Our best minds are on it. Please try again after some time."
    const val INVALID_SERVER_RESPONSE =
        "Somethings wrong with our servers at this movement.Our best minds are on it.\n Please try after some time."

    const val NO_INTERNET =
        "The internet connection you are connected to is not working please check."

    const val TRY_AGAIN = "Something went wrong please try again!"
    const val SNACK_BAR_OKAY_BTN_TEXT = "Okay"


    const val WENT_WRONG_ID = 1
    const val BACKEND_ERROR__ID = 2
    const val NETWORK_ERROR__ID = 3

}