//
//  Hotel+Mock.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 08/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

extension Hotel {
    static var mock: Hotel {
        try! Hotel(dictionary: [
            "name": "Hotel Name",
            "code": "LONLEI",
            "prepaymentAllowed": true,
            "address": [
                "postcode": "a",
                "addressline1": "a",
                "addressline2": "a",
                "addressline3": "a",
                "country": "a"
            ],
            "images": [
                ["fileReference": "/content/dam/pi/websites/hotelimages/gb/en/B/BRIPTI/BRIPTI 1.jpg"]
            ],
            "contactDetails": [
                "hotelNationalPhone": "fakeNumber"
            ]
        ])
    }
}
