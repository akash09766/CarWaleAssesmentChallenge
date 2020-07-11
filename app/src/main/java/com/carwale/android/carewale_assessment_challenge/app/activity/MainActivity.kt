package com.carwale.android.carewale_assessment_challenge.app.activity

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.carwale.android.carewale_assessment_challenge.R
import com.carwale.android.carewale_assessment_challenge.app.adapter.CountryListAdapter
import com.carwale.android.carewale_assessment_challenge.app.model.sortingAndFilter.SortFilterData
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

class MainActivity : BaseActivity() {

    private val TAG = "MainActivity"

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val viewModel by lazy { getViewModel<MainActivityViewModel>() }

    private val countryListAdapter = CountryListAdapter()
    private lateinit var mCustomSelectProfilePicBottomSheetDialog: BottomSheetDialog

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
            showBottomSheet()
        }
    }

    private fun showBottomSheet() {
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

            val sortFilterData = SortFilterData()

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

            hideBottomSheet()
        }

        mCustomSelectProfilePicBottomSheetDialog = BottomSheetDialog(this)
        mCustomSelectProfilePicBottomSheetDialog.setContentView(bottomView)
        mCustomSelectProfilePicBottomSheetDialog.show()
    }

    private fun hideBottomSheet() {
        if (::mCustomSelectProfilePicBottomSheetDialog.isInitialized) {
            mCustomSelectProfilePicBottomSheetDialog.dismiss()
        }
    }
}