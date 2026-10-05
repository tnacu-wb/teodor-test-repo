//
//  Stay+Mock.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 13/03/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
@testable import SimpleNetwork

extension Stay {
    static var mock: Self {
        try! .init(dictionary: [
              "hotelCode": "AVC241",
              "hotelName": "London",
              "identifier": "MVC",
              "arrivalDate": "Mon, 12",
              "checkOutDate": "Tue, 13"
            ]
        )
    }
}
