package com.carwale.android.carewale_assessment_challenge.app.utils

import java.io.IOException

class NoConnectivityException(val msg: String) : IOException() {
    override val message: String?
        get() = msg
}