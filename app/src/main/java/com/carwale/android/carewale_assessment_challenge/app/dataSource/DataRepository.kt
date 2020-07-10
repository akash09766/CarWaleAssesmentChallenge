package com.carwale.android.carewale_assessment_challenge.app.dataSource

import com.carwale.android.carewale_assessment_challenge.app.api.ApiService
import com.carwale.android.carewale_assessment_challenge.app.api.GoogleService
import com.carwale.android.carewale_assessment_challenge.app.errorManagement.NetworkError
import com.carwale.android.carewale_assessment_challenge.app.errorManagement.NetworkErrorForGoogle
import com.carwale.android.carewale_assessment_challenge.app.model.globalData.CovidGlobalDataResponse
import com.carwale.android.carewale_assessment_challenge.app.room.dao.GlobalDetailsDao
import com.carwale.android.carewale_assessment_challenge.app.utils.MConstants
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
}

@Singleton
class DefaultDataRepository @Inject constructor(
    private val apiService: ApiService,
    private val googleService: GoogleService,
    private val globalDetailsDao: GlobalDetailsDao
    ) : DataRepository {
    val TAG = "DefaultDataRepository"

    override fun getCovidData(
    ): Flow<ViewState<CovidGlobalDataResponse>> {
        return flow {
            emit(ViewState.loading(true))
            getGooglePing().collect { response ->
                if (response.contentEquals(MConstants.SUCCESS)) {
                    val imgurSearchResponse =
                        apiService.getCovidData()
                        emit(ViewState.loading(false))
                        emit(ViewState.success(imgurSearchResponse))
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