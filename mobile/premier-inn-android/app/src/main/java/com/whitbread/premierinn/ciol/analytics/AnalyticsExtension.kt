package com.whitbread.premierinn.ciol.analytics

import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.ERROR_MESSAGE_VALUE
import com.whitbread.premierinn.ciol.entity.PreStayUiModel
import com.whitbread.premierinn.ciol.entity.upsells.UpsellEntry
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellType
import com.whitbread.premierinn.ciol.utils.getId
import com.whitbread.premierinn.ciol.utils.getNumberOfSelections
import com.whitbread.premierinn.ciol.utils.getUpsellName
import com.whitbread.premierinn.ciol.utils.getUpsellPrice
import com.whitbread.premierinn.ciol.utils.getUpsellType
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type.CIOL_FLOW
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.format.DateFormat.SLASHED_DAY_MONTH_YEAR
import com.whitbread.premierinn.common.format.format
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.ciol.entity.isMealType
import com.whitbread.premierinn.domain.common.COMMA_STRING_DOMAIN
import com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.formattedCheckInDay
import com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.formattedCheckOutDay

fun logCiolAnalytics(
    analytics: TrackingAnalytics,
    preStayModel: PreStayUiModel?,
    screenName: String,
    action: String = EMPTY_STRING,
    extrasDescription: String = EMPTY_STRING,
    screenType: String = CIOL_FLOW,
    isError: Boolean = false,
    isContinueButton: Boolean = false,
    totalPrice: String = EMPTY_STRING,
    pushToken: String = EMPTY_STRING
) {
    val checkInDay =
        preStayModel?.let { formattedCheckInDay(it.preStayDetails.startDate) ?: EMPTY_STRING }
            ?: EMPTY_STRING
    val checkOutDay =
        preStayModel?.let { formattedCheckOutDay(it.preStayDetails.endDate) ?: EMPTY_STRING }
            ?: EMPTY_STRING
    analytics.track(
        screenName, CiolAnalyticsData(
            CiolAnalyticsModel(
                bookingId = preStayModel?.bookingReference ?: EMPTY_STRING,
                action = action,
                checkInDate = preStayModel?.preStayDetails?.startDate?.format((SLASHED_DAY_MONTH_YEAR))
                    ?: EMPTY_STRING,
                checkInDay = checkInDay,
                checkOutDay = checkOutDay,
                checkInOutDay = "$checkInDay-$checkOutDay",
                checkOutDate = preStayModel?.preStayDetails?.endDate?.format((SLASHED_DAY_MONTH_YEAR))
                    ?: EMPTY_STRING,
                noNights = preStayModel?.preStayDetails?.numberOfNights.toString(),
                noRooms = preStayModel?.preStayDetails?.rooms?.size.toString(),
                noAdults = preStayModel?.preStayDetails?.numberOfAdults.toString(),
                noChildren = preStayModel?.preStayDetails?.numberOfChildren.toString(),
                extraDescription = extrasDescription,
                hotelId = preStayModel?.preStayDetails?.hotelId.toString(),
                rateCode = preStayModel?.rateCode ?: EMPTY_STRING,
                rateDescription = preStayModel?.rateDescription ?: EMPTY_STRING,
                rateName = preStayModel?.rateName ?: EMPTY_STRING,
                errorMessage = if (isError) ERROR_MESSAGE_VALUE else EMPTY_STRING,
                pushToken = pushToken,
                ),
            screenType = screenType,
            isContinueButton = isContinueButton,
            totalPrice = totalPrice
        )
    )
}

fun setCompletionData(selectedUpsells: List<UpsellEntry>, noNights: Int, outstandingBalance: Double): CiolCompletionAnalyticsModel {
    val upsellsId = selectedUpsells.distinctBy { it.getId() }
        .joinToString(separator = COMMA_STRING_DOMAIN) { it.getId() }
    val upsellsRevenue =
        selectedUpsells.sumOf { it.getNumberOfSelections() * it.getUpsellPrice() * noNights }
    val totalRevenue = upsellsRevenue + outstandingBalance
    val foodRevenue = selectedUpsells.filter { it.getId().isMealType() }
        .sumOf { it.getNumberOfSelections() * it.getUpsellPrice() * noNights }
    val wifiRevenue = selectedUpsells.filter { it.getId().getUpsellType() == UpsellType.WIFI }
        .sumOf { it.getNumberOfSelections() * it.getUpsellPrice() * noNights }
    val eciRevenue = selectedUpsells.filter { it.getId().getUpsellType() == UpsellType.ECI }
        .sumOf { it.getNumberOfSelections() * it.getUpsellPrice() * noNights }
    val lcoRevenue = selectedUpsells.filter { it.getId().getUpsellType() == UpsellType.LCO }
        .sumOf { it.getNumberOfSelections() * it.getUpsellPrice() * noNights }
    val description = selectedUpsells.groupingBy { it.getUpsellName() }
        .fold(0) { acc, item -> acc + item.getNumberOfSelections() }.map {
            "${it.value} x ${it.key}"
        }.joinToString(", ")

    return CiolCompletionAnalyticsModel(
        revenue = totalRevenue.setRevenueChange(),
        foodRevenueChange = foodRevenue.setRevenueChange(),
        totalRevenueChange = upsellsRevenue.setRevenueChange(),
        eciRevenueChange = eciRevenue.setRevenueChange(),
        lcoRevenueChange = lcoRevenue.setRevenueChange(),
        wifiRevenueChange = wifiRevenue.setRevenueChange(),
        extraCode = upsellsId,
        extraDescription = description
    )
}

private fun Double.setRevenueChange(): String = if (this == 0.0) EMPTY_STRING else this.toString()
