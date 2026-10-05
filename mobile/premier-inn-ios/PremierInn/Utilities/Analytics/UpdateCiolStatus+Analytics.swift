//
//  UpdateCiolStatus+Analytics.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 20/03/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

protocol AnalyticsUpdateCiolStatusTrackable {
    func trackUpdateCiolStatus(
        screen: String,
        payload: UpdateCiolStatusPayload,
        error: Error?,
        response: UpdateCiolStatusResponse?
    )
}

extension AnalyticsManager: AnalyticsUpdateCiolStatusTrackable {
    func trackUpdateCiolStatus(
        screen: String,
        payload: UpdateCiolStatusPayload,
        error: Error?,
        response: UpdateCiolStatusResponse?
    ) {
        let eventName = PIAnalytics.Action.ciolUpdateUdfc20Status
        let errorName = PIAnalytics.Error.ciolUpdateUdfc20StatusFailed
        let extraData = getExtraDataDict(screen: screen, payload: payload, ciolStatus: response?.ciolStatus)
        let nsError = NSError(domain: errorName, code: PIAnalytics.ErrorCode.customErrorCode)

        if response?.ciolStatus != nil {
            trackAction(eventName, userInfo: extraData)
        } else if let error {
            track(error: error, name: errorName, extraData: extraData)
        } else {
            track(error: nsError, name: errorName, extraData: extraData)
        }
    }

    private func getExtraDataDict(
        screen: String,
        payload: UpdateCiolStatusPayload,
        ciolStatus: CiolStatus?
    ) -> PIDictionary {
        typealias Keys = PIAnalytics.Keys
        var dictionary: PIDictionary = [:]

        if !payload.reservationIds.isEmpty {
            dictionary[Keys.checkInOnlineUpdateStatusReservationIds] = payload.reservationIds.joined(separator: ", ")
        }

        dictionary[Keys.checkInOnlineUpdateStatusHotelId] = payload.hotelId
        dictionary[Keys.ciolStatusUpdateRequestEvent] = payload.ciolStatus.rawValue

        if let ciolStatus {
            dictionary[Keys.ciolStatusUpdateResponseEvent] = ciolStatus.rawValue
        }

        let dictWithDefaultValues = analyticsProperties(stateType: screen)

        dictionary.merge(dictWithDefaultValues) { current, _ in current }

        return dictionary
    }
}
