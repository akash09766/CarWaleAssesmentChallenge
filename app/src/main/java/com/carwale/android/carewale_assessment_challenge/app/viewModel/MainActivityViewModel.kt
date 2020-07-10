package com.carwale.android.carewale_assessment_challenge.app.viewModel

import androidx.lifecycle.ViewModel
import com.carwale.android.carewale_assessment_challenge.app.dataSource.DataRepository
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(private val dataRepository: DataRepository) : ViewModel() {

    private val TAG = MainActivityViewModel::class.java.simpleName

}