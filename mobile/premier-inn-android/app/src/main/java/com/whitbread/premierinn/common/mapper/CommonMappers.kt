package com.whitbread.premierinn.common.mapper

import android.os.Parcelable
import com.whitbread.premierinn.additionalinformation.entity.AnswerType
import com.whitbread.premierinn.additionalinformation.entity.EmployeeQuestionsModel
import com.whitbread.premierinn.api.response.booking.BookingPrice
import com.whitbread.premierinn.common.ParcelableHomepageBanner
import com.whitbread.premierinn.common.ParcelablePrice
import com.whitbread.premierinn.common.ParcelablePromoContent
import com.whitbread.premierinn.common.ParcelableSrpBanner
import com.whitbread.premierinn.common.ParcelableTerms
import com.whitbread.premierinn.common.ULTIMATE_WIFI_24_HRS
import com.whitbread.premierinn.common.format.PriceFormat
import com.whitbread.premierinn.data.common.WIFI_UPSELL_ALLOWED_ID
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.remote.ApiCommon
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.HomepageBannerDomain
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.PromoContentDomain
import com.whitbread.premierinn.domain.common.SrpBannerDomain
import com.whitbread.premierinn.domain.common.TermsDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.AncillaryCloseOutItem
import com.whitbread.premierinn.domain.graphql.hdp.entity.ExtrasItemDomain
import com.whitbread.premierinn.reviewbooking.AdditionalInformation
import com.whitbread.premierinn.summary.models.ParcelableAncillariesCloseOutItem
import com.whitbread.premierinn.summary.models.ParcelableExtrasItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PriceDomainParcelable(val amount: Float, val currency: String) : Parcelable

fun PriceDomain.toParcelable(): PriceDomainParcelable {
    return PriceDomainParcelable(
        amount = this.amount,
        currency = this.currency
    )
}

fun PriceDomain.toParcelablePrice(): ParcelablePrice {
    return ParcelablePrice(
        amount = amount,
        currency = currency
    )
}

fun PriceDomain.toBookingPrice(): BookingPrice {
    return BookingPrice(
        amount = amount,
        currency = currency
    )
}

fun ParcelablePrice.toPriceDomain(): PriceDomain {
    return PriceDomain(
        amount = amount,
        currency = currency
    )
}

fun BookingPrice.toParcelablePrice(): ParcelablePrice {
    return ParcelablePrice(
        amount = amount,
        currency = currency
    )
}

fun BookingPrice.toPriceDomain(): PriceDomain {
    return PriceDomain(
        amount = amount,
        currency = currency
    )
}

fun ParcelablePrice.toApiPrice(): ApiCommon.Price {
    return ApiCommon.Price(
        amount = amount,
        currency = currency
    )
}

fun ApiCommon.Price.toParcelablePrice(): ParcelablePrice {
    return ParcelablePrice(
        amount = amount,
        currency = currency
    )
}

fun ApiCommon.Price.toBookingPrice(): BookingPrice {
    return BookingPrice(
        amount = amount,
        currency = currency
    )
}

fun List<ExtrasItemDomain>.toParcelableExtrasItemDomain(listOfUpsellAllowedForBB: List<String>?): List<ParcelableExtrasItem> {

    return this.filter {
        !it.name.isNullOrEmpty()
                && it.id.isNotEmpty()
                && it.price != null
                && !it.description.isNullOrEmpty()
                && !it.currency.isNullOrEmpty()
                && it.available != null || it.available != 0
    }.filter {
        if (listOfUpsellAllowedForBB != null && it.id == ULTIMATE_WIFI_24_HRS) {
            listOfUpsellAllowedForBB.contains(WIFI_UPSELL_ALLOWED_ID)
        } else true
    }.map {
        ParcelableExtrasItem(
            name = it.name!!,
            id = it.id,
            price = it.price!!,
            imageSrc = it.imageSrc ?: EMPTY_STRING_DOMAIN,
            description = it.description!!,
            currency = it.currency!!,
            order = it.order ?: 0,
            available = it.available ?: 0
        )
    }
}

fun List<ParcelableExtrasItem>?.toExtrasItemDomain(deviceLocaleProvider: DeviceLocaleProvider): List<ExtrasItemDomain> {
    val extrasItems = mutableListOf<ExtrasItemDomain>()
    this?.forEach {
        extrasItems.add(
            ExtrasItemDomain(
                name = it.name ?: EMPTY_STRING_DOMAIN,
                id = it.id,
                price = it.price ?: 0.00,
                imageSrc = it.imageSrc ?: EMPTY_STRING_DOMAIN,
                description = it.description ?: EMPTY_STRING_DOMAIN,
                currency = it.currency ?: GBP,
                order = it.order ?: 0,
                available = it.available ?: 0,
                formattedPrice = PriceFormat.format(PriceDomain(it.price!!.toFloat(), it.currency!!), deviceLocaleProvider)
            )
        )
    }
    return extrasItems.sortedBy { it.order }
}

fun List<AncillaryCloseOutItem>.toParcelableAncillariesCloseout(): List<ParcelableAncillariesCloseOutItem> {
    val listOfParcelableAncillariesCloseOutItems = mutableListOf<ParcelableAncillariesCloseOutItem>()
    this?.forEach {
        listOfParcelableAncillariesCloseOutItems.add(
            ParcelableAncillariesCloseOutItem(
                startDate = it.startDate,
                endDate = it.endDate,
                upsellCodes = it.upsellCodes
            )
        )
    }
    return listOfParcelableAncillariesCloseOutItems
}

fun List<ParcelableAncillariesCloseOutItem>.toAncillariesCloseout(): List<AncillaryCloseOutItem> {
    val listOfAncillariesCloseOutItems = mutableListOf<AncillaryCloseOutItem>()
    this?.forEach {
        listOfAncillariesCloseOutItems.add(
            AncillaryCloseOutItem(
                startDate = it.startDate,
                endDate = it.endDate,
                upsellCodes = it.upsellCodes
            )
        )
    }
    return listOfAncillariesCloseOutItems
}

fun List<EmployeeQuestionsModel>.toSelectedAdditionalInformation(): List<AdditionalInformation> {
    val listOfAdditionalInfo = mutableListOf<AdditionalInformation>()
    this.forEach { eachItem ->
        when (eachItem.answerType) {
            AnswerType.TEXT_FIELD ->
                if (!eachItem.inputText.isNullOrBlank()) {
                        listOfAdditionalInfo.add(
                            AdditionalInformation(
                                question = eachItem.questionHeader,
                                answer = eachItem.inputText
                            )
                        )
                }

            AnswerType.DROPDOWN ->
                if (!eachItem.selectedOption.isNullOrBlank()) {
                        listOfAdditionalInfo.add(
                            AdditionalInformation(
                                question = eachItem.questionHeader,
                                answer = eachItem.selectedOption
                            )
                        )
                }
        }
    }

    return listOfAdditionalInfo
}

fun PromoContentDomain.toParcelablePromoContent(): ParcelablePromoContent {
    return ParcelablePromoContent(
        homepageBanner = this.homepageBanner.toParcelableHomepageBanner(),
        srpBanner = this.srpBanner.toParcelableSrpBanner()
    )
}

fun HomepageBannerDomain.toParcelableHomepageBanner(): ParcelableHomepageBanner {
    return ParcelableHomepageBanner(
        title = this.title,
        datePrefixText = this.datePrefixText,
        date = this.date,
        discountAmount = this.discountAmount,
        discountPercentageSign = this.discountPercentageSign,
        discountText = this.discountText,
        offerDescription = this.offerDescription,
        buttonText = this.buttonText,
        disclaimer = this.disclaimer,
        terms = this.terms.toParcelableTerms()
    )
}

    fun SrpBannerDomain.toParcelableSrpBanner(): ParcelableSrpBanner {
        return ParcelableSrpBanner(
            title = this.title,
            date = this.date,
            subTitle = this.subTitle,
            terms = this.terms.toParcelableTerms()
        )
    }

fun TermsDomain.toParcelableTerms(): ParcelableTerms {
    return ParcelableTerms(
        text = this.text,
        url = this.url
    )
}