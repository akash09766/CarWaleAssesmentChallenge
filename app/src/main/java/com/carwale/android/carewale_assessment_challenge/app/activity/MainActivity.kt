package com.carwale.android.carewale_assessment_challenge.app.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.carwale.android.carewale_assessment_challenge.R
import com.carwale.android.carewale_assessment_challenge.app.adapter.CountryListAdapter
import com.carwale.android.carewale_assessment_challenge.app.application.CarewaleAssessmentChallengeApplication
import com.carwale.android.carewale_assessment_challenge.app.fragment.AppSettingsDialog
import com.carwale.android.carewale_assessment_challenge.app.utils.MConstants
import com.carwale.android.carewale_assessment_challenge.app.utils.formatNumber
import com.carwale.android.carewale_assessment_challenge.app.utils.showLongSnackBar
import com.carwale.android.carewale_assessment_challenge.app.viewModel.MainActivityViewModel
import com.carwale.android.carewale_assessment_challenge.core.ui.ViewState
import com.carwale.android.carewale_assessment_challenge.core.ui.base.BaseActivity
import com.carwale.android.carewale_assessment_challenge.core.utils.*
import com.carwale.android.carewale_assessment_challenge.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.slider.RangeSlider
import java.io.IOException
import java.util.*

class MainActivity : BaseActivity() {

    private val TAG = "MainActivity"

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val viewModel by lazy { getViewModel<MainActivityViewModel>() }

    private val countryListAdapter = CountryListAdapter()
    private lateinit var sortBottomSheetDialog: BottomSheetDialog
    private lateinit var filterBottomSheetDialog: BottomSheetDialog
    private val REQUEST_LOCATION_PERMISSION = 210

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        initView()
        initFusedLocationProviderClient()
        getData()
        setObservers()
        setListeners()
    }

    private fun initFusedLocationProviderClient() {
        if (CarewaleAssessmentChallengeApplication.prefs?.userCountry.isNullOrEmpty()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        }
    }

    private fun initView() {
        binding.countryRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = countryListAdapter
        }
    }

    private fun getData() {
        Log.d(
            TAG,
            "getData: location : ${CarewaleAssessmentChallengeApplication.prefs?.userCountry}"
        )
        if (CarewaleAssessmentChallengeApplication.prefs?.userCountry.isNullOrEmpty()) {
            binding.userLocationPermissionContent.visible()
        } else {
            viewModel.getSortFilterData().countryName = CarewaleAssessmentChallengeApplication.prefs?.userCountry!!
            viewModel.getCovidDataFromDb(MConstants.DEFAULT_PRIMARY_KET_GLOBAL_LIST_TABLE, false)
            viewModel.getCovidData()
        }
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

                    countryListAdapter.setData(state.data.countryList,CarewaleAssessmentChallengeApplication.prefs?.userCountry)
                }
                is ViewState.Loading -> {
                    binding.userLocationPermissionContent.gone()
                    Log.d(TAG, "setObservers: _globalDetailsWithCountry Loading : ${state.data}")
                    if (state.data) {
                        binding.progressCircular.visible()
                        binding.homePageContent.gone()
                    } else {
                        binding.progressCircular.gone()
                        binding.homePageContent.visible()
                    }
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
        binding.allowLocationPermissionBtn.setOnClickListener {
            requestLocationPermission()
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
                    sortFilterData.sortOrder = MConstants.ASCENDING_SORT
                }
                R.id.descending_sort_order -> {
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
        val clearAllFilterBtn = bottomView.findViewById(R.id.clear_filter_btn) as MaterialButton

        // ------------------------------ infected filter section ------------------------------
        val infectedRangeSlider = bottomView.findViewById(R.id.infected_range_slider) as RangeSlider
        val selectedInfectedMinValue =
            bottomView.findViewById(R.id.infected_filter_min_value) as TextView
        val selectedInfectedMaxValue =
            bottomView.findViewById(R.id.infected_filter_max_value) as TextView

        infectedRangeSlider.valueFrom = 0.toFloat()
        infectedRangeSlider.valueTo = viewModel.getSortFilterData().globalInfected.toFloat()

        if (viewModel.getSortFilterData().maxInfectedSelected == MConstants.DEFAULT_INITIAL_MAX_VALUE_SORT_FILTER_DATA) {
            selectedInfectedMinValue.text = 0.toString()
            selectedInfectedMaxValue.text = viewModel.getSortFilterData().globalInfected.toString()
            infectedRangeSlider.values =
                listOf(0F, viewModel.getSortFilterData().globalInfected.toFloat())
        } else {
            selectedInfectedMinValue.text = 0.toString()
            selectedInfectedMaxValue.text =
                viewModel.getSortFilterData().maxInfectedSelected.toString()
            infectedRangeSlider.values = listOf(
                viewModel.getSortFilterData().minInfectedSelected.toFloat(),
                viewModel.getSortFilterData().maxInfectedSelected.toFloat()
            )
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

        if (viewModel.getSortFilterData().maxDeathSelected == MConstants.DEFAULT_INITIAL_MAX_VALUE_SORT_FILTER_DATA) {
            selectedDeathMinValue.text = 0.toString()
            selectedDeathMaxValue.text = viewModel.getSortFilterData().globalDeath.toString()
            deathRangeSlider.values =
                listOf(0F, viewModel.getSortFilterData().globalDeath.toFloat())
        } else {
            selectedDeathMinValue.text = 0.toString()
            selectedDeathMaxValue.text = viewModel.getSortFilterData().maxDeathSelected.toString()
            deathRangeSlider.values = listOf(
                viewModel.getSortFilterData().minDeathSelected.toFloat(),
                viewModel.getSortFilterData().maxDeathSelected.toFloat()
            )
        }

        deathRangeSlider.addOnChangeListener { rangeSlider, _, _ ->
            Log.d(TAG, "showFilterBottomSheet: deathRangeSlider min : ${rangeSlider.values}")
            selectedDeathMinValue.text = rangeSlider.values[0].toLong().toString()
            selectedDeathMaxValue.text = rangeSlider.values[1].toLong().toString()
        }

        // ------------------------------ recovered filter section ------------------------------
        val recoveredRangeSlider =
            bottomView.findViewById(R.id.recovered_range_slider) as RangeSlider
        val selectedRecoveredMinValue =
            bottomView.findViewById(R.id.recovered_filter_min_value) as TextView
        val selectedRecoveredMaxValue =
            bottomView.findViewById(R.id.recovered_filter_max_value) as TextView

        recoveredRangeSlider.valueFrom = 0.toFloat()
        recoveredRangeSlider.valueTo = viewModel.getSortFilterData().globalRecovered.toFloat()

        if (viewModel.getSortFilterData().maxRecoveredSelected == MConstants.DEFAULT_INITIAL_MAX_VALUE_SORT_FILTER_DATA) {
            selectedRecoveredMinValue.text = 0.toString()
            selectedRecoveredMaxValue.text =
                viewModel.getSortFilterData().globalRecovered.toString()
            recoveredRangeSlider.values =
                listOf(0F, viewModel.getSortFilterData().globalRecovered.toFloat())
        } else {
            selectedRecoveredMinValue.text = 0.toString()
            selectedRecoveredMaxValue.text =
                viewModel.getSortFilterData().maxRecoveredSelected.toString()
            recoveredRangeSlider.values = listOf(
                viewModel.getSortFilterData().minRecoveredSelected.toFloat(),
                viewModel.getSortFilterData().maxRecoveredSelected.toFloat()
            )
        }

        recoveredRangeSlider.addOnChangeListener { rangeSlider, _, _ ->
            Log.d(TAG, "showFilterBottomSheet: recoveredRangeSlider min : ${rangeSlider.values}")
            selectedRecoveredMinValue.text = rangeSlider.values[0].toLong().toString()
            selectedRecoveredMaxValue.text = rangeSlider.values[1].toLong().toString()
        }

        filterDoneBtn.setOnClickListener {

            val sortFilterData = viewModel.getSortFilterData()
            Log.d(
                TAG,
                "showFilterBottomSheet: Min infected : ${infectedRangeSlider.values[0].toLong()} and Max infected : ${infectedRangeSlider.values[1].toLong()}"
            )
            sortFilterData.minInfectedSelected = infectedRangeSlider.values[0].toLong()
            sortFilterData.maxInfectedSelected = infectedRangeSlider.values[1].toLong()

            Log.d(
                TAG,
                "showFilterBottomSheet: Min death : ${deathRangeSlider.values[0].toLong()} and Max death : ${deathRangeSlider.values[1].toLong()}"
            )
            sortFilterData.minDeathSelected = deathRangeSlider.values[0].toLong()
            sortFilterData.maxDeathSelected = deathRangeSlider.values[1].toLong()

            Log.d(
                TAG,
                "showFilterBottomSheet: Min recovered : ${recoveredRangeSlider.values[0].toLong()} and Max recovered : ${recoveredRangeSlider.values[1].toLong()}"
            )
            sortFilterData.minRecoveredSelected = recoveredRangeSlider.values[0].toLong()
            sortFilterData.maxRecoveredSelected = recoveredRangeSlider.values[1].toLong()

            viewModel.setSortFilterData(sortFilterData)

            hideFilterBottomSheet()
        }

        clearAllFilterBtn.setOnClickListener {
            val sortFilterData = viewModel.getSortFilterData()

            sortFilterData.maxInfectedSelected =
                MConstants.DEFAULT_INITIAL_MAX_VALUE_SORT_FILTER_DATA
            sortFilterData.minInfectedSelected =
                MConstants.DEFAULT_INITIAL_MIN_VALUE_SORT_FILTER_DATA

            sortFilterData.maxDeathSelected = MConstants.DEFAULT_INITIAL_MAX_VALUE_SORT_FILTER_DATA
            sortFilterData.minDeathSelected = MConstants.DEFAULT_INITIAL_MIN_VALUE_SORT_FILTER_DATA

            sortFilterData.maxRecoveredSelected =
                MConstants.DEFAULT_INITIAL_MAX_VALUE_SORT_FILTER_DATA
            sortFilterData.minRecoveredSelected =
                MConstants.DEFAULT_INITIAL_MIN_VALUE_SORT_FILTER_DATA

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

    private fun requestLocationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            getUserLocation()
            return true
        } else {
            if (!(CarewaleAssessmentChallengeApplication.prefs?.locationPermissionDeniedStatus!!)) {
                Log.d(TAG, "requestLocationPermission: 11111")
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                Log.d(TAG, "requestLocationPermission: 22222")
                showUserExplanationDialogForLocationPermission()
            } else {
                Log.d(TAG, "requestLocationPermission: 3333")
                showHowToEnableLocationPermissionFromSettingDialog()
            }
        }
        return false
    }

    private fun showHowToEnableLocationPermissionFromSettingDialog() {
        val fm: FragmentManager = supportFragmentManager
        val appSettingsDialog: AppSettingsDialog = AppSettingsDialog.newInstance()
        appSettingsDialog.show(fm, AppSettingsDialog::class.java.simpleName)
    }

    private fun showUserExplanationDialogForLocationPermission() {
        val dialogClickListener =
            DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        callRequestLocationPermission()
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                    }
                }
            }

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.location_permission_user_explanation_dialog_title))
        builder.setMessage(getString(R.string.user_location_permission_explanation))
            .setPositiveButton(getString(R.string.yes_dialog_btn_title), dialogClickListener)
            .setNegativeButton(getString(R.string.no_dialog_btn_title), dialogClickListener).show()
    }

    private fun callRequestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
//                CarewaleAssessmentChallengeApplication.prefs?.userLocation = "asd"
//                getData()
                getUserLocation()
                Log.d(TAG, "onRequestPermissionsResult: here 1")
            } else {
                Log.d(TAG, "onRequestPermissionsResult: here 2 ")
                CarewaleAssessmentChallengeApplication.prefs?.locationPermissionDeniedStatus = true
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun getUserLocation() {
        if (isGspEnable())
            fetchUserLocation()
        else
            askUserToEnableGPS()
    }

    private fun askUserToEnableGPS() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(getString(R.string.user_msg_enable_gps_dialog_msg))
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.user_msg_enable_gps_dialog_positive_btn_title)
            ) { dialog, _
                ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                dialog.cancel()
            }
            .setNegativeButton(
                getString(R.string.user_msg_enable_gps_dialog_negative_btn_title)
            ) { dialog, _ ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.show()
    }

    private fun isGspEnable(): Boolean {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    private fun fetchUserLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                Log.d(
                    TAG,
                    "fetchUserLocation: lat : ${location?.latitude} and long : ${location?.longitude}"
                )

                val countryName = getCountryNameFromLatLong(location?.latitude, location?.longitude)
                CarewaleAssessmentChallengeApplication.prefs?.userCountry = countryName
                viewModel.getSortFilterData().countryName =
                    if (countryName.isNullOrEmpty()) "" else countryName
                getData()

                Log.d(TAG, "fetchUserLocation: location : ${countryName}")
            }
    }

    private fun getCountryNameFromLatLong(
        latitude: Double?,
        longitude: Double?
    ): String? {
        val geocoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = geocoder.getFromLocation(latitude!!, longitude!!, 1)
            return if (addresses != null && !addresses.isEmpty()) {
                addresses[0].getCountryName()
            } else ""
        } catch (e: IOException) {
            Log.d(TAG, "getCountryName: IOException : ${e.message}")
            return ""
        }
    }
}