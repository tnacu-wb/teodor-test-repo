//
//  VenueCellViewModelTests.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 22/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import Testing
import SimpleNetwork
@testable import PremierInn

struct VenueCellViewModelTests {

    // MARK: - shouldShowHubBanner

    @Test("Tests for shouldShowHubBanner", arguments: HotelBrand.allCases)
    func shouldShowBanner(_ brand: HotelBrand) {
        // GIVEN
        let provider = MockVenueCellViewModelDataProvider(brand: brand)
        let sut = VenueCellViewModel(dataProvider: provider)

        // WHEN
        switch brand {
        case .hub:
            // THEN
            #expect(sut.bannerType == .hub)
        case .zip:
            // THEN
            #expect(sut.bannerType == .zip)
        default:
            // THEN
            #expect(sut.bannerType == nil)
        }
    }

    // MARK: - formattedNameString

    @Test
    func formattedNameStringTruncatesWhenNameExceedsMaxLength() {
        // GIVEN
        let maxLength = Constants.hotelDetailMaxTitleLength
        let longName = String(repeating: "A", count: maxLength + 5)
        let provider = MockVenueCellViewModelDataProvider(name: longName)
        let sut = VenueCellViewModel(dataProvider: provider)

        // WHEN
        let result = sut.formattedNameString

        // THEN
        #expect(result != longName)
        #expect(result.hasPrefix(String(repeating: "A", count: maxLength)))
        #expect(result.hasSuffix("..."))
    }

    @Test
    func formattedNameStringReturnsSameWhenNameWithinLimit() {
        // GIVEN
        let name = "Short Name"
        let provider = MockVenueCellViewModelDataProvider(name: name)
        let sut = VenueCellViewModel(dataProvider: provider)

        // WHEN
        let result = sut.formattedNameString

        // THEN
        #expect(result == name)
    }

    // MARK: - fromHotelDistanceString

    @Test
    func fromHotelDistanceString() {
        // GIVEN
        let distanceString = "123"
        let provider = MockVenueCellViewModelDataProvider(distanceString: distanceString)
        let sut = VenueCellViewModel(dataProvider: provider)

        // WHEN
        let result = sut.fromHotelDistanceString

        // THEN
        #expect(result == distanceString)
    }

    // MARK: - lastFewRoomsString

    @Test(arguments: [true, false])
    func lastFewRoomsString(
        limitedAvailability: Bool
    ) {
        // GIVEN
        let provider = MockVenueCellViewModelDataProvider(
            limitedAvailability: limitedAvailability
        )
        let sut = VenueCellViewModel(dataProvider: provider)

        // WHEN
        let result = sut.lastFewRoomsString

        // THEN
        #expect((result != nil) == limitedAvailability, "Should be \(result != nil)")
    }

    // MARK: - parking

    @Test(
        arguments: [
            ([ParkingType.free], true),
            ([], false)
        ]
    )
    func parking(
        parkings: [ParkingType],
        shouldExist: Bool
    ) {
        // GIVEN
        let provider = MockVenueCellViewModelDataProvider(
            parkings: parkings
        )
        let sut = VenueCellViewModel(dataProvider: provider)

        // WHEN
        let image = sut.parkingImage
        let title = sut.parkingString

        // THEN
        #expect((image != nil) == shouldExist, "Should be \(image != nil)")
        #expect((title != nil) == shouldExist, "Should be \(title != nil)")
    }

    // MARK: - lowestCostString

    @Test(
        arguments: [
            (Cost(amount: 100, currencyCode: "GBP") as Cost?, true),
            (nil, false)
        ]
    )
    func lowestCostString(
        cost: Cost?,
        shouldExist: Bool
    ) {
        // GIVEN
        let provider = MockVenueCellViewModelDataProvider(
            lowestCost: cost
        )
        let sut = VenueCellViewModel(dataProvider: provider)

        // WHEN
        let result = sut.lowestCostString

        // THEN
        #expect((result != nil) == shouldExist, "Should be \(result != nil)")
    }

    // MARK: - primaryImage

    @Test(
        arguments: [
            ([URL(string: "https://example.com/image.jpg")!], false),
            ([], true)
        ]
    )
    func primaryImages(
        images: [URL],
        shouldBeEmpty: Bool
    ) {
        // GIVEN
        let provider = MockVenueCellViewModelDataProvider(
            primaryImages: images
        )
        let sut = VenueCellViewModel(dataProvider: provider)

        // WHEN
        let result = sut.primaryImages

        // THEN
        #expect(result.isEmpty == shouldBeEmpty, "Should be \(result.isEmpty)")
    }

    // MARK: - capsuleMessageString

    @Test(
        arguments: [
            (MessagingFlag.mock as MessagingFlag?, true),
            (nil, false)
        ]
    )
    func capsuleMessageString(
        messagingFlag: MessagingFlag?,
        shouldExist: Bool
    ) {
        // GIVEN
        let provider = MockVenueCellViewModelDataProvider(
            messagingFlag: messagingFlag
        )
        let sut = VenueCellViewModel(dataProvider: provider)

        // WHEN
        let result = sut.capsuleMessageString

        // THEN
        #expect((result != nil) == shouldExist, "Should be \(result != nil)")
    }

    // MARK: - premierPlusRoomsString

    @Test(arguments: [true, false])
    func premierPlusRoomsString(
        offersPremierPlus: Bool
    ) {
        // GIVEN
        let provider = MockVenueCellViewModelDataProvider(
            offersPremierPlus: offersPremierPlus
        )
        let sut = VenueCellViewModel(dataProvider: provider)

        // WHEN
        let result = sut.premierPlusRoomsString

        // THEN
        #expect((result != nil) == offersPremierPlus, "Should be \(result != nil)")
    }

    // MARK: - shouldShowMessagePremierPlusCapsuleStack

    @Test(
        arguments: [
            (messagingFlag: nil, offersPremierPlus: true, expected: true),
            (messagingFlag: MessagingFlag.mock, offersPremierPlus: false, expected: true),
            (messagingFlag: MessagingFlag.mock, offersPremierPlus: true, expected: true),
            (messagingFlag: nil, offersPremierPlus: false, expected: false)
        ]
    )
    func shouldShowMessagePremierPlusCapsuleStack(
        messagingFlag: MessagingFlag?,
        offersPremierPlus: Bool,
        expected: Bool
    ) {
        // GIVEN
        let provider = MockVenueCellViewModelDataProvider(
            messagingFlag: messagingFlag,
            offersPremierPlus: offersPremierPlus
        )
        let sut = VenueCellViewModel(dataProvider: provider)

        // WHEN
        let result = sut.shouldShowMessagePremierPlusCapsuleStack

        // THEN
        #expect(result == expected, "Should be \(result)")
    }

    // MARK: - shouldShowDiscountAppliedCapsule

    @Test(
        arguments: [
            (classification: SimpleNetwork.Constants.EmployeeOffer.rateCode, expected: true),
            (classification: "EMP", expected: false),
            (classification: nil, expected: false),
            (classification: "EMP", expected: false),
            (classification: nil, expected: false)
        ]
    )
    func shouldShowDiscountAppliedCapsule(
        classification: String?,
        expected: Bool
    ) {
        // GIVEN
        var cheapestRate: Rate?

        if let classification {
            cheapestRate = Rate(dictionary: ["classification": classification as Any])
        }

        let provider = MockVenueCellViewModelDataProvider(
            cheapestRate: cheapestRate
        )
        let sut = VenueCellViewModel(dataProvider: provider)

        // WHEN
        let result = sut.shouldShowDiscountAppliedCapsule

        // THEN
        #expect(result == expected, "Should be \(result)")
    }

    // MARK: - shouldShowCapsulesGroup

    @Test(
        arguments: [
            (
                messagingFlag: MessagingFlag.mock,
                offersPremierPlus: false,
                classification: nil,
                expected: true
            ),
            (
                messagingFlag: nil,
                offersPremierPlus: true,
                classification: nil,
                expected: true
            ),
            (
                messagingFlag: nil,
                offersPremierPlus: false,
                classification: SimpleNetwork.Constants.EmployeeOffer.rateCode, // EmployeeOffer.rateCode
                expected: true
            ),
            (
                messagingFlag: MessagingFlag.mock,
                offersPremierPlus: true,
                classification: "EMP01",
                expected: true
            ),
            (
                messagingFlag: nil,
                offersPremierPlus: false,
                classification: nil,
                expected: false
            )
        ]
    )
    func shouldShowCapsulesGroup(
        messagingFlag: MessagingFlag?,
        offersPremierPlus: Bool,
        classification: String?,
        expected: Bool
    ) {
        // GIVEN
        var cheapestRate: Rate?

        if let classification {
            cheapestRate = Rate(dictionary: ["classification": classification as Any])
        }

        let provider = MockVenueCellViewModelDataProvider(
            messagingFlag: messagingFlag,
            offersPremierPlus: offersPremierPlus,
            cheapestRate: cheapestRate
        )
        let sut = VenueCellViewModel(dataProvider: provider)

        // WHEN
        let result = sut.shouldShowCapsulesGroup

        // THEN
        #expect(result == expected, "Should be \(expected)")
    }
}
