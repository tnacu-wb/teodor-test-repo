//
//  UrgencyMessagingTests.swift
//  PremierInn
//
//  Created by Emil Vaklinov 24/06/2026
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import Testing
import SimpleNetwork
@testable import PremierInn

@Suite("Urgency Messaging Tests")
struct UrgencyMessagingTests {

    // AC1: When numberAvailable is within 1-9, urgencyMessagingRoomsAvailable should be set to that number
    @Test("urgencyMessagingRoomsAvailable for rooms within 1-9", arguments: [1, 2, 5, 9])
    func urgencyMessagingRoomsAvailableWithinThreshold(rooms: Int) {
        let viewModel = makeRateSectionViewModel(numberAvailable: rooms)
        #expect(viewModel.urgencyMessagingRoomsAvailable == rooms)
    }

    // AC2: When numberAvailable is outside 1-9, urgencyMessagingRoomsAvailable should be nil
    @Test("urgencyMessagingRoomsAvailable is nil for rooms outside 1-9", arguments: [nil, -1, 0, 10, 11, 50, 100, 388])
    func urgencyMessagingRoomsAvailableOutsideThreshold(rooms: Int?) {
        let viewModel = makeRateSectionViewModel(numberAvailable: rooms)
        #expect(viewModel.urgencyMessagingRoomsAvailable == nil)
    }

    // AC3: Boundary values for urgency messaging
    @Test("boundary values for urgency messaging")
    func boundaryValues() {
        #expect(makeRateSectionViewModel(numberAvailable: 0).urgencyMessagingRoomsAvailable == nil)
        #expect(makeRateSectionViewModel(numberAvailable: 1).urgencyMessagingRoomsAvailable == 1)
        #expect(makeRateSectionViewModel(numberAvailable: 9).urgencyMessagingRoomsAvailable == 9)
        #expect(makeRateSectionViewModel(numberAvailable: 10).urgencyMessagingRoomsAvailable == nil)
    }

    // AC4: Correct dynamic messaging
    @Test("urgency messaging message formats correctly", arguments: [1, 5, 9])
    func messageFormatsCorrectly(rooms: Int) {
        let key = "urgencyMessagingRoomsLeft"
        let format = NSLocalizedString(key, comment: "")

        // Ensure localization string is actually a format template.
        #expect(format.contains("%"), "Localization for \(key) should contain a format placeholder")

        let message = String.localizedStringWithFormat(format, rooms)

        #expect(message.contains("\(rooms)"), "Message should include room count \(rooms)")
        #expect(!message.contains("%"), "Message should not contain unresolved format tokens")
    }

    // AC5: Badge displayed per room type independently
    @Test("multiple room types display badge independently")
    func multipleRoomTypesDisplayBadgeIndependently() {
        let standardRate = Rate(dictionary: [
            "code": "FLEX",
            "classification": "FLEX",
            "name": "Flex",
            "description": "Flexible rate",
            "rooms": [[
                "type": "DOUBLE",
                "options": [[
                    "lettingType": "DBL",
                    "roomClass": "STANDARD",
                    "totalCost": ["amount": 100, "currency": "GBP"],
                    "numberAvailable": 5
                ]]
            ]]
        ])

        let premierPlusRate = Rate(dictionary: [
            "code": "FLEX",
            "classification": "FLEX",
            "name": "Flex",
            "description": "Flexible rate",
            "rooms": [[
                "type": "DOUBLE",
                "options": [[
                    "lettingType": "DBL",
                    "roomClass": "PREMIER_PLUS",
                    "totalCost": ["amount": 150, "currency": "GBP"],
                    "numberAvailable": 15
                ]]
            ]]
        ])

        let standardVM = RateSectionViewModel.createFrom(
            [standardRate].ratesSeparatedByTieredRooms.first!,
            roomConfiguration: nil,
            hotelBrand: .premierInn,
            andCriteria: Criteria(),
            and: 0
        )

        let premierPlusVM = RateSectionViewModel.createFrom(
            [premierPlusRate].ratesSeparatedByTieredRooms.first!,
            roomConfiguration: nil,
            hotelBrand: .premierInn,
            andCriteria: Criteria(),
            and: 0
        )

        #expect(standardVM.urgencyMessagingRoomsAvailable == 5, "Standard should show badge")
        #expect(premierPlusVM.urgencyMessagingRoomsAvailable == nil, "Premier Plus should hide badge")
    }

    @Test("urgency messaging uses minimum availability for matching letting type")
    func urgencyMessagingUsesMinimumAvailabilityForMatchingLettingType() {
        let rate = Rate(dictionary: [
            "code": "FLEX",
            "classification": "FLEX",
            "name": "Flex",
            "description": "Flexible rate",
            "rooms": [[
                "type": "DOUBLE",
                "options": [[
                    "lettingType": "DBL",
                    "roomClass": "STANDARD",
                    "totalCost": ["amount": 100, "currency": "GBP"],
                    "numberAvailable": 8
                ], [
                    "lettingType": "DBL",
                    "roomClass": "STANDARD",
                    "totalCost": ["amount": 110, "currency": "GBP"],
                    "numberAvailable": 3
                ]]
            ]]
        ])

        let viewModel = RateSectionViewModel.createFrom(
            [rate].ratesSeparatedByTieredRooms.first!,
            roomConfiguration: nil,
            hotelBrand: .premierInn,
            andCriteria: Criteria(),
            and: 0
        )

        #expect(viewModel.urgencyMessagingRoomsAvailable == 3)
    }
}

private extension UrgencyMessagingTests {
    func makeRateSectionViewModel(numberAvailable: Int?) -> RateSectionViewModel {
        var roomOption: PIDictionary = [
            "lettingType": "DBL",
            "roomClass": "STANDARD",
            "totalCost": ["amount": 100, "currency": "GBP"]
        ]

        if let numberAvailable {
            roomOption["numberAvailable"] = numberAvailable
        }

        let rate = Rate(dictionary: [
            "code": "FLEX",
            "classification": "FLEX",
            "name": "Flex",
            "description": "Flexible rate",
            "rooms": [[
                "type": "DOUBLE",
                "options": [roomOption]
            ]]
        ])

        let roomRates = [rate].ratesSeparatedByTieredRooms.first!

        return RateSectionViewModel.createFrom(
            roomRates,
            roomConfiguration: nil,
            hotelBrand: .premierInn,
            andCriteria: Criteria(),
            and: 0
        )
    }
}
