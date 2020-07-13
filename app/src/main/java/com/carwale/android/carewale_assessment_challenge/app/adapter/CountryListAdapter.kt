package com.carwale.android.carewale_assessment_challenge.app.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carwale.android.carewale_assessment_challenge.R
import com.carwale.android.carewale_assessment_challenge.app.room.entities.CountryDetails
import com.carwale.android.carewale_assessment_challenge.app.utils.formatNumber

class CountryListAdapter : RecyclerView.Adapter<CountryListAdapter.CountryListAdapterViewHolder>() {

    private val TAG = "CountryListAdapter"

    private val countryList = ArrayList<CountryDetails>()
    private var countryName = String()

    fun setData(commentList: List<CountryDetails>?, countryName : String?) {
        this.countryName = countryName!!
        this.countryList.clear()
        commentList?.let { this.countryList.addAll(it) }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CountryListAdapterViewHolder {
        return CountryListAdapterViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.country_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CountryListAdapterViewHolder, position: Int) {
        if(position == 0 && countryName.equals(countryList[position].countryCode, ignoreCase = true)){
            holder.location.typeface = Typeface.DEFAULT_BOLD
            holder.infected.typeface = Typeface.DEFAULT_BOLD
            holder.death.typeface = Typeface.DEFAULT_BOLD
            holder.recovered.typeface = Typeface.DEFAULT_BOLD
        }else{
            holder.location.typeface = Typeface.DEFAULT
            holder.infected.typeface = Typeface.DEFAULT
            holder.death.typeface = Typeface.DEFAULT
            holder.recovered.typeface = Typeface.DEFAULT
        }
        holder.location.text = countryList[position].countryName
        holder.infected.text = formatNumber(countryList[position].totalConfirmed)
        holder.death.text = formatNumber(countryList[position].totalDeaths)
        holder.recovered.text = formatNumber(countryList[position].totalRecovered)
    }

    override fun getItemCount(): Int {
        return countryList.size
    }

    class CountryListAdapterViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val location = itemView.findViewById(R.id.location) as TextView
        val infected = itemView.findViewById(R.id.infected) as TextView
        val death = itemView.findViewById(R.id.death) as TextView
        val recovered = itemView.findViewById(R.id.recovered) as TextView
    }
}