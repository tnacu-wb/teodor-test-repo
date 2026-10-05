//
//  ThirdPartyBooking+Analytics.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 15/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

protocol AnalyticsThirdPartyBookingTrackable {
    func trackThirdPartyBookingIfRequired(
        stay: Stay?,
        reservation: Reservation?
    )
}

extension AnalyticsManager: AnalyticsThirdPartyBookingTrackable {
    func trackThirdPartyBookingIfRequired(
        stay: Stay?,
        reservation: Reservation?
    ) {
        guard let stay, !stay.isDirect else {
            return
        }

        var info = PIDictionary()

        info[PIAnalytics.Keys.thirdPartyBookingID] = stay.identifier
        info[PIAnalytics.Keys.isThirdPartyBooking] = true
        info[PIAnalytics.Keys.thirdPartyAncillariesBooked] = reservation?.upsellItems.isNotEmpty ?? false

        trackState(PIAnalytics.StateNames.bookingDetails, data: info)
    }
}
