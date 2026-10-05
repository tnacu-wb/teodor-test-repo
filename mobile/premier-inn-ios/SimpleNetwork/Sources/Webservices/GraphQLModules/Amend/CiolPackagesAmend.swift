//
//  Untitled.swift
//  SimpleNetwork
//
//  Created by Raiu, George Marius (Cognizant) on 12.12.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation

public struct CiolUpsellSelection {
    public init(id: String, noOfSelections: Int) {
        self.id = id
        self.noOfSelections = noOfSelections
    }

    var id: String
    var noOfSelections: Int
}

public struct CiolUpsellRoomSelection {
    public init(reservationId: String, packagesSelection: [CiolUpsellSelection]) {
        self.reservationId = reservationId
        self.packagesSelection = packagesSelection
    }

    var reservationId: String
    public var packagesSelection: [CiolUpsellSelection]
}

public struct CiolAmendInfo {
    public init(
        basketReferenceId: String,
        hotelId: String,
        arrivalDate: Date,
        departureDate: Date,
        roomsSelections: [CiolUpsellRoomSelection],
        previousRoomsSelections: [CiolUpsellRoomSelection]
    ) {
        self.basketReferenceId = basketReferenceId
        self.hotelId = hotelId
        self.arrivalDate = arrivalDate
        self.departureDate = departureDate
        self.roomsSelections = roomsSelections
        self.previousRoomsSelections = previousRoomsSelections
    }

    let basketReferenceId: String
    let hotelId: String
    let arrivalDate: Date
    let departureDate: Date
    public let roomsSelections: [CiolUpsellRoomSelection]
    let previousRoomsSelections: [CiolUpsellRoomSelection]

    enum Constants {
        static let basketReferenceId = "basketReferenceId"
        static let hotelId = "hotelId"
        static let arrivalDate = "arrivalDate"
        static let departureDate = "departureDate"
        static let roomsSelections = "roomsSelections"
        static let previousRoomsSelections = "previousRoomsSelections"
        static let packagesSelection = "packagesSelection"
        static let reservationId = "reservationId"
        static let noOfSelections = "noOfSelections"
        static let id = "id"
        static let updateReservationPackagesRequest = "updateReservationPackagesRequest"

        static let query = "updateReservationPackagesByReservation"
    }
}
