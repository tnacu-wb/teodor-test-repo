//
//  CiolConfirmationInteractorTests.swift
//  PremierInn
//
//  Created by Velesca, Florin (Cognizant) on 11.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

class CiolConfirmationInteractorTests: XCTestCase {

    var interactor: CiolConfirmationInteractor!

    let stay = try! Stay(dictionary: ["hotelCode": "AVC241",
                                      "hotelName": "London",
                                      "identifier": "MVC",
                                      "arrivalDate": "Mon, 12",
                                      "checkOutDate": "Tue, 13"])
    override func setUp() {
        super.setUp()
        interactor = CiolConfirmationInteractor(ciolConfirmationDetails: CiolConfirmationDetails(bookerFirstName: "Test",
                                                                                                 hotelBrand: .premierInn,
                                                                                                 ciolStartFlow: .myBookings,
                                                                                                 hotelImage: nil,
                                                                                                 analyticsInfo: [:],
                                                                                                 stay: stay))
    }

    override func tearDown() {
        interactor = nil
        super.tearDown()
    }
}
