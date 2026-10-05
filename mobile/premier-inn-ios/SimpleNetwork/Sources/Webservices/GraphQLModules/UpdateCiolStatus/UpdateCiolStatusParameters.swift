//
//  UpdateCiolStatusParameters.swift
//  SimpleNetwork
//
//  Created by Rodrigues, Seymour (Contractor) on 11/03/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

extension GraphQL {
    static func updateCiolStatusParameters(payload: UpdateCiolStatusPayload) -> PIDictionary {
        var parameters: PIDictionary = [:]
        parameters["reservationIds"] = payload.reservationIds
        parameters["hotelId"] = payload.hotelId
        parameters["ciolStatus"] = payload.ciolStatus.rawValue
        return parameters
    }
}
