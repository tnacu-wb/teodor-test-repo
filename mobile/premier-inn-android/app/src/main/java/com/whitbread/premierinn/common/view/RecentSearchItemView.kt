package com.whitbread.premierinn.common.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.format.format
import com.whitbread.premierinn.data.common.BRAND_HUB
import com.whitbread.premierinn.data.common.BRAND_PI
import com.whitbread.premierinn.data.common.BRAND_PI_GERMANY
import com.whitbread.premierinn.data.common.BRAND_ZIP
import com.whitbread.premierinn.databinding.DashboardRecentSearchItemBinding
import com.whitbread.premierinn.domain.recentsearch.entity.RecentSearch
import com.whitbread.premierinn.landing.MessageProvider
import org.threeten.bp.LocalDate

class RecentSearchItemView(val context: Context, recentSearch: RecentSearch) {

    private val dashboardRecentSearchItemBinding = DashboardRecentSearchItemBinding.inflate(LayoutInflater.from(context), null, false)

    val view: View = dashboardRecentSearchItemBinding.root

    init {
        setUp(recentSearch)
    }

    private fun setUp(recentSearchItem: RecentSearch) {

        setPlaceTypeImage(recentSearchItem.hotelBrand)

        setLocationName(recentSearchItem.searchTerm)

        setDepartureDate(recentSearchItem.departureDate)

        setArrivalDate(recentSearchItem.arrivalDate)

        setRoomCriteria(
                roomsCount = recentSearchItem.roomsCount,
                adults = recentSearchItem.adults.sum(),
                children = recentSearchItem.children.sum().plus(recentSearchItem.infants.sum())
        )
    }

    private fun setPlaceTypeImage(hotelBrand: String?) {
        when (hotelBrand) {
            BRAND_PI, BRAND_PI_GERMANY -> dashboardRecentSearchItemBinding.locationTypeImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_hotel_purple))
            BRAND_HUB -> dashboardRecentSearchItemBinding.locationTypeImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_hub_hotel))
            BRAND_ZIP -> dashboardRecentSearchItemBinding.locationTypeImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_zip_logo))
            else -> dashboardRecentSearchItemBinding.locationTypeImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_destination))
        }
    }

    private fun setLocationName(recentSearchLocation: String?) {
        dashboardRecentSearchItemBinding.recentSearchLocationTitle.text = recentSearchLocation
    }

    private fun setDepartureDate(recentSearchDepartureDate: LocalDate) {
        val departureDate = recentSearchDepartureDate.format(DateFormat.WEEKDAY_DAY_MONTH)
        dashboardRecentSearchItemBinding.departureDate.text = departureDate
    }

    private fun setArrivalDate(recentSearchArrivalData: LocalDate) {
        val arrivalDate = recentSearchArrivalData.format(DateFormat.WEEKDAY_DAY_MONTH)
        dashboardRecentSearchItemBinding.arrivalDate.text = arrivalDate
    }

    private fun setRoomCriteria(
            roomsCount: Int,
            adults: Int,
            children: Int
    ) {
        if (children > 0)
            dashboardRecentSearchItemBinding.guestRoomInfo.text = MessageProvider(context.resources).guestsAndRooms(
                    adults + children,
                    roomsCount
            )
        else
            dashboardRecentSearchItemBinding.guestRoomInfo.text =
                    MessageProvider(context.resources).adultsAndRooms(adults, roomsCount)

    }

    fun setRoundedTopCornersBackGround() {
        view.background = ContextCompat.getDrawable(
                context,
                R.drawable.rounded_top_corners_white_background
        )
    }

    fun setGreyBorderBackGround() {
        view.background = ContextCompat.getDrawable(
                context,
                R.drawable.rectangular_grey_bordered_background
        )
    }
}