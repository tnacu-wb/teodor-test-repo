//
//  VenueFullyBookedCellViewModelDataProviding+Mock.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 02/07/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

#if DEV
import Foundation
import SimpleNetwork

private enum MockVenueFullyBookedCellData {
    static let urls: [URL] = Array(
        repeating:
            URL(
                string: "https://premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Restaurants/Thyme/Thyme1.jpg"
            )!,
        count: 18
    )
}

struct MockVenueFullyBookedCellViewModelDataProvider: VenueFullyBookedCellViewModelDataProviding {
    let name: String
    let primaryImages: [URL]

    init(
        name: String = "London Euston",
        primaryImages: [URL] = MockVenueFullyBookedCellData.urls
    ) {
        self.name = name
        self.primaryImages = primaryImages
    }
}

#endif
