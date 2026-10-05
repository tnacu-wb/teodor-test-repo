//
//  VenueCellViewModel.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 18/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

struct VenueCellViewModel {
    private let provider: VenueCellViewModelDataProviding

    init(dataProvider: VenueCellViewModelDataProviding) {
        self.provider = dataProvider
    }

    var bannerType: VenueCellBannerType? {
        provider.brand.bannerType
    }

    var formattedNameString: String {
        provider.name.count > Constants.hotelDetailMaxTitleLength
        ? provider.name.truncated(withLength: Constants.hotelDetailMaxTitleLength)
        : provider.name
    }

    var lastFewRoomsString: String? {
        provider.limitedAvailability
        ? PILocalizedString("hotelDetailsLastFewRooms")
        : nil
    }

    var fromHotelDistanceString: String? {
        provider.getDistanceString(formatter: .distanceFormatter)
    }

    var parkingImage: String? {
        provider.parkings.first?.squareIconName
    }

    var parkingString: String? {
        provider.parkings.first?.title
    }

    var lowestCostString: String? {
        provider.getLowestCost()?.localizedValue
    }

    var primaryImages: [URL] {
        provider.primaryImages
    }

    var capsuleMessageString: String? {
        provider.messagingFlag?.text
    }

    var premierPlusRoomsString: String? {
        provider.offersPremierPlus
        ? PILocalizedString("premierPlusRooms")
        : nil
    }

    var shouldShowMessagePremierPlusCapsuleStack: Bool {
        capsuleMessageString != nil ||
        premierPlusRoomsString != nil
    }

    var shouldShowDiscountAppliedCapsule: Bool {
        provider.cheapestRate?.classification == provider.employeeOfferRateCode
    }

    var shouldShowCapsulesGroup: Bool {
        shouldShowMessagePremierPlusCapsuleStack ||
        shouldShowDiscountAppliedCapsule
    }
}
