package com.whitbread.premierinn.ciol.adapter

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.whitbread.premierinn.common.utils.isoCodeToEmoji
import com.whitbread.premierinn.databinding.CountryItemViewBinding
import com.whitbread.premierinn.domain.countries.entity.CountryDomain

class CountryItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {

    private val binding = CountryItemViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        layoutParams = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    fun setState(country: CountryDomain, showCountryName: Boolean, onItemSelected: (country: CountryDomain) -> Unit) {
        binding.countryTextView.text =
            String.format(
                "%s %s",
                isoCodeToEmoji(country.countryIsoCode),
                if (showCountryName) country.countryName else country.nationality
            )
        binding.countryContainer.setOnClickListener {
            onItemSelected(country)
        }
    }
}
