package com.carwale.android.carewale_assessment_challenge.app.prefs

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {
    private val PREFS_FILENAME = "com.carwale.android.carwale_assessment_challenge"
//    private val USER_LOCATION = "user_location"
    private val USER_COUNTRY = "user_country"
    private val LOCATION_PERMISSION_DENIED_STATUS_ = "location_permission_"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    /*var userLocation: String?
        get() = prefs.getString(USER_LOCATION, "")
        set(value) = prefs.edit().putString(USER_LOCATION, value).apply()*/

    var userCountry: String?
        get() = prefs.getString(USER_COUNTRY, "")
        set(value) = prefs.edit().putString(USER_COUNTRY, value).apply()

    var locationPermissionDeniedStatus: Boolean
        get() = prefs.getBoolean(LOCATION_PERMISSION_DENIED_STATUS_, false)
        set(value) = prefs.edit().putBoolean(LOCATION_PERMISSION_DENIED_STATUS_, value).apply()
}