package com.whitbread.premierinn.common.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.utils.isoCodeToEmoji
import com.whitbread.premierinn.domain.countries.entity.CountryDomain

class CountryListAdapter(val context: Context, val countries: List<CountryDomain>) : BaseAdapter() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(pos: Int, view: View?, parent: ViewGroup?): View {
        val itemView = view ?: layoutInflater.inflate(R.layout.country_dropdown_item, parent, false)
        val countryNameView = itemView.findViewById<TextView>(R.id.country_item_name)
        val countryFlag = itemView.findViewById<TextView>(R.id.country_flag_character)
        countryNameView.text = countries[pos].countryName
        countries[pos].countryIsoCode.let {
            countryFlag.text = isoCodeToEmoji(it)
        }
        return itemView
    }

    override fun getItem(pos: Int): CountryDomain {
        return countries[pos]
    }

    override fun getItemId(pos: Int): Long {
        return pos.toLong()
    }

    override fun getCount(): Int {
        return countries.size
    }
}