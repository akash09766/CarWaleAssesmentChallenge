package com.carwale.android.carewale_assessment_challenge.app.model.globalData


import com.google.gson.annotations.SerializedName

data class CovidGlobalDataResponse(@SerializedName("Countries")
                                   val countries: List<CountriesItem>?,
                                   @SerializedName("Global")
                                   val global: Global)


data class CountriesItem(@SerializedName("NewRecovered")
                         val newRecovered: Int = 0,
                         @SerializedName("NewDeaths")
                         val newDeaths: Int = 0,
                         @SerializedName("TotalRecovered")
                         val totalRecovered: Int = 0,
                         @SerializedName("TotalConfirmed")
                         val totalConfirmed: Int = 0,
                         @SerializedName("Country")
                         val country: String = "",
                         @SerializedName("CountryCode")
                         val countryCode: String = "",
                         @SerializedName("Slug")
                         val slug: String = "",
                         @SerializedName("NewConfirmed")
                         val newConfirmed: Int = 0,
                         @SerializedName("TotalDeaths")
                         val totalDeaths: Int = 0)


data class Global(@SerializedName("NewRecovered")
                  val newRecovered: Int = 0,
                  @SerializedName("NewDeaths")
                  val newDeaths: Int = 0,
                  @SerializedName("TotalRecovered")
                  val totalRecovered: Int = 0,
                  @SerializedName("TotalConfirmed")
                  val totalConfirmed: Int = 0,
                  @SerializedName("NewConfirmed")
                  val newConfirmed: Int = 0,
                  @SerializedName("TotalDeaths")
                  val totalDeaths: Int = 0)


