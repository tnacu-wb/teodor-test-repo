package com.whitbread.premierinn.hoteldetails

import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.api.response.availability.Facility
import com.whitbread.premierinn.common.mapper.toPriceDomain
import com.whitbread.premierinn.domain.common.DailyRate
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelFacilityDomain

fun HotelFacilityDomain.toFacility(): Facility{
    return Facility.builder()
        .code(Facility.Codes.valueOfCode(this.code))
        .legend(this.title)
        .icon(Urls.CONTENT_BASE_URL + this.icon)
        .build()
}


fun com.whitbread.premierinn.api.response.booking.DailyRate.toDailyRateInput(): DailyRateInput {
    return DailyRateInput(date = this.date(), price = this.toPriceParcelable())
}

fun List<DailyRate>.toDailyRatesInput():List<DailyRateInput>{
    val listOfDailyRates = mutableListOf<DailyRateInput>()
    this.forEach { dailyRate ->
        listOfDailyRates.add(DailyRateInput(dailyRate.date, dailyRate.price.toPriceParcelable()))
    }

    return listOfDailyRates
}

fun PriceDomain.toPriceParcelable(): PriceParcelable {
    return PriceParcelable(
        amount = this.amount,
        currency = this.currency
    )
}

fun com.whitbread.premierinn.api.response.booking.DailyRate.toPriceParcelable(): PriceParcelable {
    return PriceParcelable(
        amount = this.price().amount,
        currency = this.price().currency
    )
}

fun List<com.whitbread.premierinn.api.response.booking.DailyRate>.toDailyRatesInputApp():List<DailyRateInput>{
    val listOfDailyRates = mutableListOf<DailyRateInput>()
    this.forEach { dailyRate ->
        listOfDailyRates.add(DailyRateInput(dailyRate.date(), dailyRate.price().toPriceDomain().toPriceParcelable()))
    }

    return listOfDailyRates
}