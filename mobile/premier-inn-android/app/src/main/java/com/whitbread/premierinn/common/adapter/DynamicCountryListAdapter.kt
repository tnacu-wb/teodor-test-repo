package com.whitbread.premierinn.common.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.GERMANY
import com.whitbread.premierinn.common.UK
import com.whitbread.premierinn.common.UNITED_ARAB_EMIRATES
import com.whitbread.premierinn.common.USA
import com.whitbread.premierinn.common.utils.isoCodeToEmoji
import com.whitbread.premierinn.domain.countries.entity.CountryDomain

class DynamicCountryListAdapter(val context: Context) : BaseAdapter() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    private var items: List<CountrySpinnerItem> = listOf()

    override fun getView(pos: Int, view: View?, parent: ViewGroup?): View {
        val item = getItem(pos)
        return when (item) {
            is NothingSelectedItem -> {
                layoutInflater.inflate(item.layoutResId, parent, false)
            }
            is CountryItem -> {
                val itemView = if (view?.id == R.id.country_item) view else layoutInflater.inflate(R.layout.country_dropdown_item, parent, false)
                val countryNameView = itemView.findViewById<TextView>(R.id.country_item_name)
                val countryFlag = itemView.findViewById<TextView>(R.id.country_flag_character)
                countryNameView.text = item.country.countryName
                countryFlag.text = isoCodeToEmoji(item.country.countryIsoCode)
                itemView
            }
            is Divider -> {
                layoutInflater.inflate(item.layoutResId, parent, false)
            }
        }
    }

    override fun getItem(pos: Int): CountrySpinnerItem {
        return items[pos]
    }

    override fun getItemId(pos: Int): Long {
        return pos.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }

    fun setCountries(countries: List<CountryItem>, nothingSelectedItem: NothingSelectedItem? = null, divider: Divider) {

        val defaultCountries = filterAndKeepDefault(countries)

        this.items = nothingSelectedItem?.let {
            listOf(it).plus(divider).plus(defaultCountries).plus(divider).plus(countries) } ?: countries
        notifyDataSetChanged()
    }
}

sealed class CountrySpinnerItem
data class CountryItem(val country: CountryDomain) : CountrySpinnerItem()
data class NothingSelectedItem(val layoutResId: Int) : CountrySpinnerItem()
data class Divider(val layoutResId: Int) : CountrySpinnerItem()

fun filterAndKeepDefault(countries: List<CountryItem>): MutableList<CountryItem> {
    val desiredOrder = listOf(UK, GERMANY, USA, UNITED_ARAB_EMIRATES)
    val countriesById = countries.associateBy { it.country.countryIsoCode }
    return desiredOrder.mapNotNull { countriesById[it] }.toMutableList()
}