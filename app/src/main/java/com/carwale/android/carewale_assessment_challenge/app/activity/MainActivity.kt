package com.carwale.android.carewale_assessment_challenge.app.activity

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.carwale.android.carewale_assessment_challenge.R
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.slider.RangeSlider

class MainActivity : BaseActivity() {

    private val TAG = "MainActivity"

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val viewModel by lazy { getViewModel<MainActivityViewModel>() }

    private val countryListAdapter = CountryListAdapter()
    private lateinit var sortBottomSheetDialog: BottomSheetDialog
    private lateinit var filterBottomSheetDialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        initView()
        getData()
        setObservers()
        setListeners()
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

                    binding.infectedValue.text =
                        formatNumber(state.data.globalDetails?.totalConfirmed)
                    binding.deathsValue.text = formatNumber(state.data.globalDetails?.totalDeaths)
                    binding.recoveredValue.text =
                        formatNumber(state.data.globalDetails?.totalRecovered)

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

    private fun setListeners() {
        binding.sortBtn.setOnClickListener {
            showSortBottomSheet()
        }
        binding.filterBtn.setOnClickListener {
            showFilterBottomSheet()
        }
    }

    private fun showSortBottomSheet() {
        val bottomView =
            this.layoutInflater.inflate(
                R.layout.sort_country_list_bottom_sheet_layout,
                binding.root,
                false
            )

        val sortDoneBtn = bottomView.findViewById(R.id.sort_done_btn) as MaterialButton
        val sortCategoryGroup =
            bottomView.findViewById(R.id.sort_category_options) as MaterialButtonToggleGroup
        val sortOrderGroup =
            bottomView.findViewById(R.id.sort_order_options) as MaterialButtonToggleGroup

        when (viewModel.getSortFilterData().sortCategory) {
            MConstants.LOCATION_SORT_CATEGORY -> {
                Log.d(TAG, "setListeners: LOCATION_SORT_CATEGORY ")
                sortCategoryGroup.check(R.id.location_sort_category)
            }
            MConstants.INFECTED_SORT_CATEGORY -> {
                Log.d(TAG, "setListeners: INFECTED_SORT_CATEGORY ")
                sortCategoryGroup.check(R.id.infected_sort_category)
            }
            MConstants.DEATH_SORT_CATEGORY -> {
                Log.d(TAG, "setListeners: DEATH_SORT_CATEGORY ")
                sortCategoryGroup.check(R.id.death_sort_category)
            }
            MConstants.RECOVERED_SORT_CATEGORY -> {
                Log.d(TAG, "setListeners: RECOVERED_SORT_CATEGORY ")
                sortCategoryGroup.check(R.id.recovered_sort_category)
            }
        }

        when (viewModel.getSortFilterData().sortOrder) {
            MConstants.ASCENDING_SORT -> {
                Log.d(TAG, "setListeners: ASCENDING_SORT ")
                sortOrderGroup.check(R.id.ascending_sort_order)
            }
            MConstants.DESCENDING_SORT -> {
                Log.d(TAG, "setListeners: DESCENDING_SORT ")
                sortOrderGroup.check(R.id.descending_sort_order)
            }
        }


        sortDoneBtn.setOnClickListener {

            val sortFilterData = viewModel.getSortFilterData()

            when (sortCategoryGroup.checkedButtonId) {
                R.id.location_sort_category -> {
                    sortFilterData.sortCategory = MConstants.LOCATION_SORT_CATEGORY
                }
                R.id.infected_sort_category -> {
                    sortFilterData.sortCategory = MConstants.INFECTED_SORT_CATEGORY
                }
                R.id.death_sort_category -> {
                    sortFilterData.sortCategory = MConstants.DEATH_SORT_CATEGORY
                }
                R.id.recovered_sort_category -> {
                    sortFilterData.sortCategory = MConstants.RECOVERED_SORT_CATEGORY
                }
            }

            when (sortOrderGroup.checkedButtonId) {
                R.id.ascending_sort_order -> {
                    Log.d(TAG, "setListeners: ASCENDING_SORT ")
                    sortFilterData.sortOrder = MConstants.ASCENDING_SORT
                }
                R.id.descending_sort_order -> {
                    Log.d(TAG, "setListeners: DESCENDING_SORT ")
                    sortFilterData.sortOrder = MConstants.DESCENDING_SORT
                }
            }

            viewModel.setSortFilterData(sortFilterData)

            hideSortBottomSheet()
        }

        sortBottomSheetDialog = BottomSheetDialog(this)
        sortBottomSheetDialog.setContentView(bottomView)
        sortBottomSheetDialog.show()
    }

    private fun showFilterBottomSheet() {
        val bottomView =
            this.layoutInflater.inflate(
                R.layout.filter_country_list_bottom_sheet_layout,
                binding.root,
                false
            )

        val filterDoneBtn = bottomView.findViewById(R.id.filter_done_btn) as MaterialButton

        // ------------------------------ infected filter section ------------------------------
        val infectedRangeSlider = bottomView.findViewById(R.id.infected_range_slider) as RangeSlider
        val selectedInfectedMinValue =
            bottomView.findViewById(R.id.infected_filter_min_value) as TextView
        val selectedInfectedMaxValue =
            bottomView.findViewById(R.id.infected_filter_max_value) as TextView

        infectedRangeSlider.valueFrom = 0.toFloat()
        infectedRangeSlider.valueTo = viewModel.getSortFilterData().globalInfected.toFloat()

        if (viewModel.getSortFilterData().maxInfectedSelected == 0L) {
            selectedInfectedMinValue.text = 0.toString()
            selectedInfectedMaxValue.text = viewModel.getSortFilterData().globalInfected.toString()
            infectedRangeSlider.values = listOf(0F, viewModel.getSortFilterData().globalInfected.toFloat())
        } else {
            selectedInfectedMinValue.text = 0.toString()
            selectedInfectedMaxValue.text = viewModel.getSortFilterData().maxInfectedSelected.toString()
            infectedRangeSlider.values = listOf(viewModel.getSortFilterData().minInfectedSelected.toFloat(), viewModel.getSortFilterData().maxInfectedSelected.toFloat())
        }

        infectedRangeSlider.addOnChangeListener { rangeSlider, _, _ ->
            Log.d(TAG, "showFilterBottomSheet: infectedRangeSlider min : ${rangeSlider.values}")
            selectedInfectedMinValue.text = rangeSlider.values[0].toLong().toString()
            selectedInfectedMaxValue.text = rangeSlider.values[1].toLong().toString()
        }

        // ------------------------------ death filter section ------------------------------
        val deathRangeSlider = bottomView.findViewById(R.id.death_range_slider) as RangeSlider
        val selectedDeathMinValue =
            bottomView.findViewById(R.id.death_filter_min_value) as TextView
        val selectedDeathMaxValue =
            bottomView.findViewById(R.id.death_filter_max_value) as TextView

        deathRangeSlider.valueFrom = 0.toFloat()
        deathRangeSlider.valueTo = viewModel.getSortFilterData().globalDeath.toFloat()

        if (viewModel.getSortFilterData().maxDeathSelected == 0L) {
            selectedDeathMinValue.text = 0.toString()
            selectedDeathMaxValue.text = viewModel.getSortFilterData().globalDeath.toString()
            deathRangeSlider.values = listOf(0F, viewModel.getSortFilterData().globalDeath.toFloat())
        } else {
            selectedDeathMinValue.text = 0.toString()
            selectedDeathMaxValue.text = viewModel.getSortFilterData().maxDeathSelected.toString()
            deathRangeSlider.values = listOf(viewModel.getSortFilterData().minDeathSelected.toFloat(), viewModel.getSortFilterData().maxDeathSelected.toFloat())
        }

        deathRangeSlider.addOnChangeListener { rangeSlider, _, _ ->
            Log.d(TAG, "showFilterBottomSheet: deathRangeSlider min : ${rangeSlider.values}")
            selectedDeathMinValue.text = rangeSlider.values[0].toLong().toString()
            selectedDeathMaxValue.text = rangeSlider.values[1].toLong().toString()
        }

        // ------------------------------ recovered filter section ------------------------------
        val recoveredRangeSlider = bottomView.findViewById(R.id.recovered_range_slider) as RangeSlider
        val selectedRecoveredMinValue =
            bottomView.findViewById(R.id.recovered_filter_min_value) as TextView
        val selectedRecoveredMaxValue =
            bottomView.findViewById(R.id.recovered_filter_max_value) as TextView

        recoveredRangeSlider.valueFrom = 0.toFloat()
        recoveredRangeSlider.valueTo = viewModel.getSortFilterData().globalRecovered.toFloat()

        if (viewModel.getSortFilterData().maxRecoveredSelected == 0L) {
            selectedRecoveredMinValue.text = 0.toString()
            selectedRecoveredMaxValue.text = viewModel.getSortFilterData().globalRecovered.toString()
            recoveredRangeSlider.values = listOf(0F, viewModel.getSortFilterData().globalRecovered.toFloat())
        } else {
            selectedRecoveredMinValue.text = 0.toString()
            selectedRecoveredMaxValue.text = viewModel.getSortFilterData().maxRecoveredSelected.toString()
            recoveredRangeSlider.values = listOf(viewModel.getSortFilterData().minRecoveredSelected.toFloat(), viewModel.getSortFilterData().maxRecoveredSelected.toFloat())
        }

        recoveredRangeSlider.addOnChangeListener { rangeSlider, _, _ ->
            Log.d(TAG, "showFilterBottomSheet: recoveredRangeSlider min : ${rangeSlider.values}")
            selectedRecoveredMinValue.text = rangeSlider.values[0].toLong().toString()
            selectedRecoveredMaxValue.text = rangeSlider.values[1].toLong().toString()
        }

        filterDoneBtn.setOnClickListener {

            val sortFilterData = viewModel.getSortFilterData()
            Log.d(TAG, "showFilterBottomSheet: Min infected : ${infectedRangeSlider.values[0].toLong()} and Max infected : ${infectedRangeSlider.values[1].toLong()}")
            sortFilterData.minInfectedSelected = infectedRangeSlider.values[0].toLong()
            sortFilterData.maxInfectedSelected = infectedRangeSlider.values[1].toLong()

            Log.d(TAG, "showFilterBottomSheet: Min death : ${deathRangeSlider.values[0].toLong()} and Max death : ${deathRangeSlider.values[1].toLong()}")
            sortFilterData.minDeathSelected = deathRangeSlider.values[0].toLong()
            sortFilterData.maxDeathSelected = deathRangeSlider.values[1].toLong()

            Log.d(TAG, "showFilterBottomSheet: Min recovered : ${recoveredRangeSlider.values[0].toLong()} and Max recovered : ${recoveredRangeSlider.values[1].toLong()}")
            sortFilterData.minRecoveredSelected = recoveredRangeSlider.values[0].toLong()
            sortFilterData.maxRecoveredSelected = recoveredRangeSlider.values[1].toLong()

            viewModel.setSortFilterData(sortFilterData)

            hideFilterBottomSheet()
        }

        filterBottomSheetDialog = BottomSheetDialog(this)
        filterBottomSheetDialog.setContentView(bottomView)
        filterBottomSheetDialog.show()
    }

    private fun hideSortBottomSheet() {
        if (::sortBottomSheetDialog.isInitialized) {
            sortBottomSheetDialog.dismiss()
        }
    }

    private fun hideFilterBottomSheet() {
        if (::filterBottomSheetDialog.isInitialized) {
            filterBottomSheetDialog.dismiss()
        }
    }
}