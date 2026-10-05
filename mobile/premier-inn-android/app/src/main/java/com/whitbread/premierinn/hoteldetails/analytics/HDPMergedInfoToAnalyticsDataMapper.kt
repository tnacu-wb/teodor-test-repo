package com.whitbread.premierinn.hoteldetails.analytics

import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.APP_INCENTIVE
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.FREE_BREAKFAST
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.SITEWIDE_PROMOTIONS
import com.whitbread.premierinn.common.analytics.CampaignDataModel
import com.whitbread.premierinn.common.utils.DateUtils
import com.whitbread.premierinn.common.utils.getFirstRateTag
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomRateDomain
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.hoteldetails.HDPMergedInfo
import io.reactivex.functions.Function
import org.threeten.bp.LocalDate

class HDPMergedInfoToAnalyticsDataMapper (
        private val screenType: String,
        private val getStringResource: GetStringResource,
        private val simplePersistenceManager: SimplePersistenceManager,
): Function<HDPMergedInfo, HDPAnalyticsData> {

    private val appIncentivePromoCode by lazy { getStringResource(Key.APP_PROMO_CODE) }
    private val freeBreakfastPromoCode by lazy { simplePersistenceManager.getFreeBreakfastPromotionCode() }

    override fun apply(hdpMergedInfo: HDPMergedInfo): HDPAnalyticsData {
        val (promoCode, promoName) = getPromoInfo(hdpMergedInfo.hotelAvailabilityDomain.roomRateDomainList, hdpMergedInfo.promoKind)
        // Only include rate tags if there's an active user discount code
        val rateTags = if (promoCode != null) {
            getFirstRateTag(hdpMergedInfo.hotelAvailabilityDomain.listOfRatesClassification)
        } else {
            null
        }
        return HDPAnalyticsData(
                screenType = screenType,
                products = toProductString(hdpMergedInfo.hotelAvailabilityDomain, hdpMergedInfo.input.hotelCode(), hdpMergedInfo.orderedRateDisplayInfo),
                placeName = hdpMergedInfo.input.hotelName() ?: EMPTY_STRING_DOMAIN,
                nights = hdpMergedInfo.input.searchResultsInput()?.nights().toString(),
                numOfRooms = hdpMergedInfo.input.searchResultsInput()?.numRooms().toString(),
                searchType = getSearchType(hdpMergedInfo.input.cameFromMapView(), hdpMergedInfo.input.hotelName(), hdpMergedInfo.input.hotelCode()),
                checkIn = hdpMergedInfo.hotelAvailabilityDomain.startDate.toSlashedDate(),
                checkout = hdpMergedInfo.hotelAvailabilityDomain.endDate.toLocalDate().plusDays(1).toString().toSlashedDate(),
                adults = hdpMergedInfo.input.searchResultsInput()?.numAdults().toString(),
                children = hdpMergedInfo.input.searchResultsInput()?.numChildren().toString(),
                guests = hdpMergedInfo.input.searchResultsInput()?.totalNumOfGuests().toString(),
                leadDays = DateUtils.getLeadDays(hdpMergedInfo.hotelAvailabilityDomain.startDate.toLocalDate(), LocalDate.now()).toString(),
                roomTypes = hdpMergedInfo.hotelAvailabilityDomain.getListOfRoom(),
                results = "1",
                startEndDay = getStartEndDateString(hdpMergedInfo.hotelAvailabilityDomain.startDate, hdpMergedInfo.hotelAvailabilityDomain.endDate.toLocalDate().plusDays(1).toString()),
                startDay = getWeekDay(hdpMergedInfo.hotelAvailabilityDomain.startDate.toLocalDate()),
                endDay = getWeekDay(hdpMergedInfo.hotelAvailabilityDomain.endDate.toLocalDate().plusDays(1)),
                event = "1",
                pushToken = hdpMergedInfo.pushToken,
                promoCode = promoCode,
                promoName = promoName,
                rateTags = rateTags,
                campaignModel = hdpMergedInfo.input.campaignModel() ?: CampaignDataModel(),
                trackingCode = hdpMergedInfo.input.trackingCode()
        )
    }

    private fun getPromoInfo(roomRates: List<RoomRateDomain>?, promoKind: String?): Pair<String?, String?> {
        val promoCode = roomRates.orEmpty()
            .firstOrNull { !it.promotionCode.isNullOrEmpty() }
            ?.promotionCode
            ?: return Pair(null, null)

        val promoName = when (promoCode) {
            appIncentivePromoCode -> APP_INCENTIVE
            freeBreakfastPromoCode -> FREE_BREAKFAST
            else -> promoKind ?: SITEWIDE_PROMOTIONS
        }

        return Pair(promoCode, promoName)
    }
}