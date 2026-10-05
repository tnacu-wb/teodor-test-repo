package com.whitbread.premierinn.common.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxrelay2.PublishRelay
import com.whitbread.premierinn.R
import com.whitbread.premierinn.databinding.ViewRoomRatesBinding
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.hoteldetails.PriceBoxAdapter
import com.whitbread.premierinn.hoteldetails.event.RateClickEvent
import com.whitbread.premierinn.hoteldetails.uimodel.RateBoxUiModel
import io.reactivex.Observable

class RoomRatesView @JvmOverloads constructor(context: Context,
                                              attributeSet: AttributeSet? = null,
                                              defStyleAttr: Int = 0): LinearLayout(context, attributeSet, defStyleAttr) {
    private var isAlternate = false
    private lateinit var publishRelay:PublishRelay<Any>

    private var isListExpanded = true

    val binding = ViewRoomRatesBinding.inflate(LayoutInflater.from(context), this, true)

    fun setData(rateBoxUiModels: List<RateBoxUiModel>?, label: String?,
                relay: PublishRelay<Any>, isAlternateRoom: Boolean) {
        isAlternate = isAlternateRoom
        publishRelay = relay

        binding.header.isVisible = label != null
        binding.hotelDetailsRateRoomLabel.text = label

        if (label == EMPTY_STRING_DOMAIN) {
            binding.hotelDetailsRateRoomLabel.text = context.getString(R.string.hotel_details_standard_rooms_heading)
        } else {
            binding.hotelDetailsRateRoomLabel.text = label
        }
        binding.hotelDetailsPriceBoxRv.layoutManager = LinearLayoutManager(context)
        if (rateBoxUiModels != null && rateBoxUiModels.isNotEmpty()) {
            binding.hotelDetailsPriceBoxRv.adapter = PriceBoxAdapter(rateBoxUiModels,
                onPriceClick = ::onBookOrSelectCTAClicked,
                onShowMoreClick = ::onShowMoreClicked, isListExpanded)
        } else {
            binding.hotelDetailsPriceBoxRv.visibility = GONE
        }
    }

    private fun onBookOrSelectCTAClicked(rateName: String, rateClassification: String, pmsRoomType: String,
                                         roomClass: String, formattedBaseRate: String?,
                                         promotionCode: String?, promotionTag: String?) {
        Observable.just(RateClickEvent(rateName, rateClassification, pmsRoomType, roomClass,
            isAlternate, formattedBaseRate, promotionCode, promotionTag))
            .subscribe { publishRelay.accept(it) }
    }

    private fun onShowMoreClicked(isClicked: Boolean){
        isListExpanded = isClicked
    }
}