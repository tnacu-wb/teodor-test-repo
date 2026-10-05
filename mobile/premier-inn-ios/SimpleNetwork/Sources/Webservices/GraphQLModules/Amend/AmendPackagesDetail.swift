//
//  AmendPackagesDetail.swift
//  SimpleNetwork
//
//  Created by Santa Gurung on 26/07/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import Foundation

public struct AmendPackagesDetail {
    let basketReferenceId: String
    let hotelId: String
    let arrivalDate: Date
    let departureDate: Date
    let roomsSelections: [UpsellItem]?
    let previousRoomsSelections: [UpsellItem]?
    let rooms: [Room]?

    public init(
        basketReferenceId: String,
        hotelId: String,
        arrivalDate: Date,
        departureDate: Date,
        roomsSelections: [UpsellItem]?,
        previousRoomsSelections: [UpsellItem]?,
        rooms: [Room]?
    ) {
        self.basketReferenceId = basketReferenceId
        self.hotelId = hotelId
        self.arrivalDate = arrivalDate
        self.departureDate = departureDate
        self.roomsSelections = roomsSelections
        self.previousRoomsSelections = previousRoomsSelections
        self.rooms = rooms
    }
}
