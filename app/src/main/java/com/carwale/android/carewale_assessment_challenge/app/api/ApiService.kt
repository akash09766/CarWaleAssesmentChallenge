package com.carwale.android.carewale_assessment_challenge.app.api

import com.carwale.android.carewale_assessment_challenge.app.model.globalData.CovidGlobalDataResponse
import com.carwale.android.carewale_assessment_challenge.app.utils.FsConfig
import retrofit2.http.GET

/**
 * List of API Calls.
 */
interface ApiService {

    /**
     * get covid data .
     */

    @GET(FsConfig.SUMMARY_API)
    suspend fun getCovidData(): CovidGlobalDataResponse
}
