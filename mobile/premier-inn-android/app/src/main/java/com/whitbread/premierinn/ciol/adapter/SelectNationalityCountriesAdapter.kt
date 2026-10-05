package com.whitbread.premierinn.ciol.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whitbread.premierinn.domain.countries.entity.CountryDomain

class SelectNationalityCountriesAdapter(
    private var countries: List<CountryDomain>,
    private var showCountryName: Boolean,
    private val onCountryClicked: (country: CountryDomain) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun updateFilteredCountries(filteredCountries: List<CountryDomain>) {
        countries = filteredCountries
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SelectNationalityCountriesViewHolder(CountryItemView(parent.context))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SelectNationalityCountriesViewHolder) {
            holder.bindItem(countries[position], showCountryName)
        }
    }

    override fun getItemCount(): Int = countries.size

    inner class SelectNationalityCountriesViewHolder(private val countryItemView: CountryItemView) :
        RecyclerView.ViewHolder(countryItemView) {
        fun bindItem(country: CountryDomain, showCountryName: Boolean) {
            countryItemView.setState(country, showCountryName, onCountryClicked)
        }
    }
}