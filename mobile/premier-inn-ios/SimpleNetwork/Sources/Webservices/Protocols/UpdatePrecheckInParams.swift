//
//  UpdatePrecheckInParams.swift
//  SimpleNetwork
//
//  Created by Raiu, George Marius (Cognizant) on 04.04.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//


public struct UpdatePrecheckInParams {
    let hotelId: String
    let reservationId: String
    let arrivalTime: String

    public init(hotelId: String, reservationId: String, arrivalTime: String) {
        self.hotelId = hotelId
        self.reservationId = reservationId
        self.arrivalTime = arrivalTime
    }

    enum Constants {
        static let hotelId = "hotelId"
        static let reservationId = "reservationId"
        static let arrivalTime = "arrivalTime"

        static let decodingKey = "preCheckInStatus"
    }
}
