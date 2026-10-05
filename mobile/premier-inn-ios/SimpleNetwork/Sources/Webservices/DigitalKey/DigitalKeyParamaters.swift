//
//  DigitalKeyParamaters.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 18/08/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//
import Foundation

extension GraphQL {
    static func provisionKeyVariables(
        bookingReference: String,
        otpCode: String,
        email: String,
        reservationId: String
    ) -> PIDictionary {
        var dict = PIDictionary()
        dict["bookingReference"] = bookingReference
        dict["otpCode"] = otpCode
        dict["email"] = email
        dict["reservationId"] = reservationId

        return ["digitalkeyProvisionRequest": dict]
    }

    static func digitalKeyCheckInVariables(hotelId: String, reservationId: String) -> PIDictionary {
        var dict = PIDictionary()
        dict["hotelId"] = hotelId
        dict["reservationId"] = reservationId

        return ["digitalKeyCheckInCriteria": dict]
    }
}
