//
//  AmendRoomCriteria.swift
//  SimpleNetwork
//
//  Created by Santa Gurung on 07/08/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import Foundation

public struct AmendRoomCriteria {
    let tempBookingRef: String
    let roomOccupancy: RoomOccupancyAmend?
    let leadGuest: LeadGuest?
    let roomType: String?
    let token: String
    let reservationId: String?
    let isBusiness: Bool
    let specialRequests: [String]?
    // ratePlanCode?

    public init(
        tempBookingRef: String,
        roomOccupancy: RoomOccupancyAmend?,
        leadGuest: LeadGuest?,
        roomType: String?,
        token: String,
        reservationId: String?,
        isBusiness: Bool,
        specialRequests: [String]?
    ) {
        self.tempBookingRef = tempBookingRef
        self.roomOccupancy = roomOccupancy
        self.leadGuest = leadGuest
        self.roomType = roomType
        self.token = token
        self.reservationId = reservationId
        self.isBusiness = isBusiness
        self.specialRequests = specialRequests
    }
}

public struct RoomOccupancyAmend {
    let adultsNumber: Int
    let childrenNumber: Int
    let cotRequired: Bool?

    public init(adultsNumber: Int, childrenNumber: Int, cotRequired: Bool?) {
        self.adultsNumber = adultsNumber
        self.childrenNumber = childrenNumber
        self.cotRequired = cotRequired
    }
}

public struct LeadGuest {
    let title: String
    let firstName: String
    let lastName: String
    let emailAddress: String?

    public init(title: String, firstName: String, lastName: String, emailAddress: String?) {
        self.title = title
        self.firstName = firstName
        self.lastName = lastName
        self.emailAddress = emailAddress
    }
}
