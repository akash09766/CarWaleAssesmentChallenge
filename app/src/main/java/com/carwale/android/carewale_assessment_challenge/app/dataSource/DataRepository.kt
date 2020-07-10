package com.carwale.android.carewale_assessment_challenge.app.dataSource

import android.util.Log
import com.carwale.android.carewale_assessment_challenge.app.api.ApiService
import com.carwale.android.carewale_assessment_challenge.app.api.GoogleService
import com.carwale.android.carewale_assessment_challenge.app.errorManagement.NetworkError
import com.carwale.android.carewale_assessment_challenge.app.errorManagement.NetworkErrorForGoogle
import com.carwale.android.carewale_assessment_challenge.app.model.globalData.CovidGlobalDataResponse
import com.carwale.android.carewale_assessment_challenge.app.room.dao.GlobalDetailsDao
import com.carwale.android.carewale_assessment_challenge.app.room.entities.GlobalDetailsWithCountry
import com.carwale.android.carewale_assessment_challenge.app.utils.MConstants
import com.carwale.android.carewale_assessment_challenge.app.utils.convertToDbModel
import com.carwale.android.carewale_assessment_challenge.core.ui.ViewState
import dagger.Binds
import dagger.Module
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

interface DataRepository {
    fun getGooglePing(): Flow<String>
    fun getCovidData(): Flow<ViewState<CovidGlobalDataResponse>>
    fun getCovidDataFromDB(id : Int): Flow<ViewState<GlobalDetailsWithCountry>>
}

@Singleton
class DefaultDataRepository @Inject constructor(
    private val apiService: ApiService,
    private val googleService: GoogleService,
    private val globalDetailsDao: GlobalDetailsDao
    ) : DataRepository {
    val TAG = "DefaultDataRepository"

    override fun getCovidDataFromDB(id: Int): Flow<ViewState<GlobalDetailsWithCountry>> {
        return flow {
            Log.d(TAG, "getCovidDataFromDB()")
            emit(ViewState.loading(true))

            globalDetailsDao.getGlobalDetailsWithCountry(id = id)
                .collect { globalDetailsWithCountry ->
                    Log.d(TAG, "getCovidDataFromDB() collect ")

                    if (globalDetailsWithCountry != null) {
                        Log.d(TAG, "getCovidDataFromDB() from DB************************  size : ${globalDetailsWithCountry.countryList.size}")
                        emit(ViewState.loading(false))
                        emit(ViewState.success(globalDetailsWithCountry))
                    }
                }
        }.catch { error ->
            emit(ViewState.loading(false))
            emit(ViewState.error(NetworkError(error).getAppErrorMessage()))
        }.flowOn(Dispatchers.IO)
    }


    override fun getCovidData(
    ): Flow<ViewState<CovidGlobalDataResponse>> {
        Log.d(TAG, "getCovidData: ")
        return flow {
            emit(ViewState.loading(true))
            getGooglePing().collect { response ->
                if (response.contentEquals(MConstants.SUCCESS)) {
                    val covidGlobalDataResponse = apiService.getCovidData()
                    globalDetailsDao.insertGlobalDetailsWithCountry(covidGlobalDataResponse.convertToDbModel(covidGlobalDataResponse)!!)
                    emit(ViewState.loading(false))
                        emit(ViewState.success(covidGlobalDataResponse))
                } else {
                    emit(ViewState.loading(false))
                    emit(ViewState.error(response))
                }
            }
        }.catch { error ->
            emit(ViewState.loading(false))
            emit(ViewState.error(NetworkError(error).getAppErrorMessage()))
        }.flowOn(Dispatchers.IO)
    }

    override fun getGooglePing(): Flow<String> = flow {
        val response = googleService.getGoogle()
        if (response.isSuccessful)
            emit(MConstants.SUCCESS)
    }.catch {
        emit(NetworkErrorForGoogle(it).getAppErrorMessage())
    }.flowOn(Dispatchers.IO)
}

@Module
interface DataRepositoryModule {
    /* Exposes the concrete implementation for the interface */
    @Binds
    fun it(it: DefaultDataRepository): DataRepository
}