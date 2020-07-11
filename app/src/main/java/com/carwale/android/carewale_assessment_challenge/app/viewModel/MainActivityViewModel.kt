package com.carwale.android.carewale_assessment_challenge.app.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carwale.android.carewale_assessment_challenge.app.dataSource.DataRepository
import com.carwale.android.carewale_assessment_challenge.app.model.globalData.CovidGlobalDataResponse
import com.carwale.android.carewale_assessment_challenge.app.model.sortingAndFilter.SortFilterData
import com.carwale.android.carewale_assessment_challenge.app.room.entities.GlobalDetailsWithCountry
import com.carwale.android.carewale_assessment_challenge.app.utils.MConstants
import com.carwale.android.carewale_assessment_challenge.app.utils.sortCountry
import com.carwale.android.carewale_assessment_challenge.core.ui.ViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    private val TAG = MainActivityViewModel::class.java.simpleName
    private lateinit var job: Job
    private var sortFilterData = SortFilterData()

    private var globalDetailsWithCountry = MutableLiveData<ViewState<GlobalDetailsWithCountry>>()
    val _globalDetailsWithCountry: LiveData<ViewState<GlobalDetailsWithCountry>>
        get() = globalDetailsWithCountry

    private var covidGlobalDataResponse = MutableLiveData<ViewState<CovidGlobalDataResponse>>()
    val _covidGlobalDataResponse: LiveData<ViewState<CovidGlobalDataResponse>>
        get() = covidGlobalDataResponse

    fun setSortFilterData(sortFilterData: SortFilterData) {
        this.sortFilterData = sortFilterData
        getCovidDataFromDb(MConstants.DEFAULT_PRIMARY_KET_GLOBAL_LIST_TABLE)
    }

    fun getSortFilterData(): SortFilterData {
        return sortFilterData
    }

    fun getCovidData() {
        Log.d(TAG, "getCovidData: ")
        job = CoroutineScope(Dispatchers.Main).launch {
            dataRepository.getCovidData().collect {
                covidGlobalDataResponse.value = it
            }
        }
    }

    fun getCovidDataFromDb(id: Int) {
        Log.d(TAG, "getCovidDataFromDb: ")
        job = CoroutineScope(Dispatchers.Main).launch {
            dataRepository.getCovidDataFromDB(id = id).collect { globalDetailsWithCountryState ->
                when (globalDetailsWithCountryState) {
                    is ViewState.Success -> {

                        sortFilterData.globalInfected = globalDetailsWithCountryState.data.globalDetails?.totalConfirmed!!
                        sortFilterData.globalDeath = globalDetailsWithCountryState.data.globalDetails?.totalDeaths!!
                        sortFilterData.globalRecovered = globalDetailsWithCountryState.data.globalDetails?.totalRecovered!!

                        globalDetailsWithCountryState.data.countryList = sortCountry(sortFilterData = sortFilterData,countryDetailsList = globalDetailsWithCountryState.data.countryList)
                        globalDetailsWithCountry.value = ViewState.Success(globalDetailsWithCountryState.data)
                    }
                    is ViewState.Loading -> {
                        globalDetailsWithCountry.value =
                            ViewState.Loading(globalDetailsWithCountryState.data)
                    }
                    is ViewState.Error -> {
                        globalDetailsWithCountry.value =
                            ViewState.Error(globalDetailsWithCountryState.message)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (::job.isInitialized) {
            job.cancel()
        }
    }
}