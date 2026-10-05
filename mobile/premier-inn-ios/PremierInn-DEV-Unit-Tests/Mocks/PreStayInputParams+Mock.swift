//
//  PreStayInputParams+Mock.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 13/03/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
@testable import PremierInn

extension PreStayInputParams {
    static func mock(rooms: [Room], isDirect: Bool = true) -> Self {
        .init(
            flow: CIOLStartFlow.bookingConfirmation,
            stay: Stay.mock,
            hotelCode: "LONEUS",
            hotelBrand: HotelBrand.premierInnGermany,
            adultsCountDescription: "",
            adultsCount: 2,
            childrenCountDescription: "",
            childrenCount: 2,
            nightsCountDescription: "",
            roomsCountDescription: "",
            rooms: rooms,
            selectedPreference: nil,
            isBusinessTrip: false,
            hasDERegCard: false,
            isDirect: isDirect
        )
    }

    static func createDetailedMock(
        leadGuest: User? = nil,
        accompanyingGuest: User? = nil,
        hotelBrand: HotelBrand? = nil,
        selectedPreference: HotelPreferenceViewModel? = nil,
        hasDERegCard: Bool = false,
        isBusinessTrip: Bool = false,
        isDirect: Bool = true,
        email: String? = nil,
        contactNumber: String? = nil,
        address: Address? = nil
    ) -> PreStayInputParams {

        var roomDictionary = PIDictionary()
        roomDictionary["roomId"] = "XVCBS"
        roomDictionary["adults"] = 2

        let room1 = Room(dictionary: roomDictionary)

        room1.leadGuest = leadGuest
        room1.accompanyingGuest = accompanyingGuest

        return PreStayInputParams(
            flow: .myBookings,
            stay: Stay.mock,
            hotelCode: "LONEUS",
            hotelBrand: hotelBrand,
            reservationId: "324543",
            bookingFlowId: "3668429",
            arrivalDate: Date(),
            checkOutDate: Date(),
            adultsCountDescription: "",
            adultsCount: 3,
            childrenCountDescription: "",
            childrenCount: 0,
            nightsCountDescription: "",
            roomsCountDescription: "",
            rooms: [room1],
            leadBookerTitle: "Mr",
            leadBookerFirstName: "Booker",
            leadBookerLastName: "User",
            email: email,
            contactNumber: contactNumber,
            address: address,
            selectedPreference: selectedPreference,
            isBusinessTrip: isBusinessTrip,
            hasDERegCard: hasDERegCard,
            isDirect: isDirect
        )
    }
}
