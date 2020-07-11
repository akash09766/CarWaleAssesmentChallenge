package com.carwale.android.carewale_assessment_challenge.app.activity

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.carwale.android.carewale_assessment_challenge.app.adapter.CountryListAdapter
import com.carwale.android.carewale_assessment_challenge.app.utils.MConstants
import com.carwale.android.carewale_assessment_challenge.app.utils.formatNumber
import com.carwale.android.carewale_assessment_challenge.app.utils.showLongSnackBar
import com.carwale.android.carewale_assessment_challenge.app.viewModel.MainActivityViewModel
import com.carwale.android.carewale_assessment_challenge.core.ui.ViewState
import com.carwale.android.carewale_assessment_challenge.core.ui.base.BaseActivity
import com.carwale.android.carewale_assessment_challenge.core.utils.getViewModel
import com.carwale.android.carewale_assessment_challenge.core.utils.observeNotNull
import com.carwale.android.carewale_assessment_challenge.core.utils.setVisibilityWithGONE
import com.carwale.android.carewale_assessment_challenge.core.utils.viewBinding
import com.carwale.android.carewale_assessment_challenge.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    private val TAG = "MainActivity"

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val viewModel by lazy { getViewModel<MainActivityViewModel>() }

    private val  countryListAdapter = CountryListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        initView()
        getData()
        setObservers()
    }

    private fun initView() {
        binding.countryRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = countryListAdapter
        }
    }

    private fun getData() {
        viewModel.getCovidDataFromDb(MConstants.DEFAULT_PRIMARY_KET_GLOBAL_LIST_TABLE)
        viewModel.getCovidData()
    }

    private fun setObservers() {
        viewModel._globalDetailsWithCountry.observeNotNull(this) { state ->
            when (state) {
                is ViewState.Success -> {
                    Log.d(
                        TAG,
                        "setObservers: _globalDetailsWithCountry Success totalConfirmed : ${state.data.globalDetails?.totalConfirmed}"
                    )

                    binding.infectedValue.text = formatNumber(state.data.globalDetails?.totalConfirmed)
                    binding.deathsValue.text =  formatNumber(state.data.globalDetails?.totalDeaths)
                    binding.recoveredValue.text = formatNumber(state.data.globalDetails?.totalRecovered)

                    countryListAdapter.setData(state.data.countryList)

                    state.data.countryList.forEach {
                        Log.d(
                            TAG,
                            "setObservers: country : ${it.countryName} and totalConfirmed : ${it.totalConfirmed}"
                        )
                    }
                }
                is ViewState.Loading -> {
                    Log.d(TAG, "setObservers: _globalDetailsWithCountry Loading : ${state.data}")
                    binding.progressCircular.setVisibilityWithGONE(state.data)
                }

                is ViewState.Error -> {
                    Log.e(TAG, state.message)
                    showLongSnackBar(binding.root, state.message)
                }
            }
        }

        viewModel._covidGlobalDataResponse.observeNotNull(this) { state ->
            when (state) {
                is ViewState.Success -> {
                    Log.d(TAG, "setObservers: _covidGlobalDataResponse")
                }
                is ViewState.Loading -> {
                    Log.d(TAG, "setObservers: _covidGlobalDataResponse Loading : ${state.data}")
                }
                is ViewState.Error -> {
                    Log.e(TAG, state.message)
                    showLongSnackBar(binding.root, state.message)
                }
            }
        }
    }
}