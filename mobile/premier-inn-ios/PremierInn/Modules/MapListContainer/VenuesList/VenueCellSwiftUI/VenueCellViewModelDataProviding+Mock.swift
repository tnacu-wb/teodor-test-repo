//
//  VenueCellViewModelDataProviding+Mock.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 22/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

#if DEV
import Foundation
import SimpleNetwork

private enum MockVenueCellData {
    static let urls: [URL] = Array(
        repeating:
            URL(
                string: "https://premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Restaurants/Thyme/Thyme1.jpg"
            )!,
        count: 18
    )
}

struct MockVenueCellViewModelDataProvider: VenueCellViewModelDataProviding {
    let name: String
    let brand: HotelBrand
    let limitedAvailability: Bool
    let distanceString: String?
    let parkings: [SimpleNetwork.ParkingType]
    let primaryImages: [URL]
    let messagingFlag: MessagingFlag?
    let offersPremierPlus: Bool
    let lowestCost: Cost?
    let cheapestRate: Rate?

    init(
        name: String = "London Euston",
        brand: HotelBrand = .premierInn,
        limitedAvailability: Bool = false,
        distanceString: String? = "180 miles",
        parkings: [SimpleNetwork.ParkingType] = [.free, .free, .free],
        primaryImages: [URL] = MockVenueCellData.urls,
        messagingFlag: MessagingFlag? = MessagingFlag.mock,
        offersPremierPlus: Bool = true,
        lowestCost: Cost? = Cost(amount: 183.23, currencyCode: "GBP"),
        cheapestRate: Rate? = nil
    ) {
        self.name = name
        self.brand = brand
        self.limitedAvailability = limitedAvailability
        self.distanceString = distanceString
        self.parkings = parkings
        self.primaryImages = primaryImages
        self.messagingFlag = messagingFlag
        self.offersPremierPlus = offersPremierPlus
        self.lowestCost = lowestCost
        self.cheapestRate = cheapestRate
    }

    func getDistanceString(formatter: LengthFormatter) -> String? {
        distanceString
    }

    func getLowestCost() -> Cost? {
        lowestCost
    }
}

#endif
