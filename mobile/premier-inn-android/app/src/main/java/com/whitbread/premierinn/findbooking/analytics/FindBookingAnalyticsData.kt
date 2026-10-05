package com.whitbread.premierinn.findbooking.analytics

import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_ADOBE_CAMPAIGN_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.GOOGLE_ID
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.MICROSOFT_ID
import com.whitbread.premierinn.common.analytics.AnalyticsData
import com.whitbread.premierinn.common.analytics.CampaignDataModel

data class FindBookingAnalyticsData(val campaignModel: CampaignDataModel) : AnalyticsData {

    override fun contextData(): Map<String?, String?> {
        return mutableMapOf<String?, String?>().apply {
            if (campaignModel.campaignId.isNotEmpty()) {
                put(CIOL_ADOBE_CAMPAIGN_KEY, campaignModel.campaignId)
            }

            if (campaignModel.googleId.isNotEmpty()) {
                put(GOOGLE_ID, campaignModel.googleId)
            }

            if (campaignModel.microsoftId.isNotEmpty()) {
                put(MICROSOFT_ID, campaignModel.microsoftId)
            }
        }
    }

    companion object {
        // Error messages - matching user-facing error strings for analytics tracking
        const val ERROR_BOOKING_ALREADY_IMPORTED = "This booking has already been imported"
        const val ERROR_PAST_BOOKING_IMPORT = "Importing past booking not supported"
        const val ERROR_UNABLE_TO_RETRIEVE_BOOKING = "There was a problem retrieving your booking. " +
                "Please confirm that your details are correct or try again later"
        const val ERROR_BUSINESS_BOOKING_IMPORT = "Couldn't locate the booking. " +
                "Please confirm that your details are correct or log into Premier Inn Business"
        const val ERROR_FETCHING_BOOKING = "Couldn't locate the booking. Please confirm that your details are correct"
    }
}
