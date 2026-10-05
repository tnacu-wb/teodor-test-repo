//
//  UpdateCiolStatusPayload.swift
//  SimpleNetwork
//
//  Created by Rodrigues, Seymour (Contractor) on 11/03/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

public struct UpdateCiolStatusPayload {
    public let reservationIds: [String]
    public let hotelId: String
    public let ciolStatus: CiolStatus

    public init(
        reservationIds: [String],
        hotelId: String,
        ciolStatus: CiolStatus
    ) {
        self.reservationIds = reservationIds
        self.hotelId = hotelId
        self.ciolStatus = ciolStatus
    }
}
