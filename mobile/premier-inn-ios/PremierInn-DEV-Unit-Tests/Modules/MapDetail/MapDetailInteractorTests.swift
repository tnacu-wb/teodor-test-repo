//
//  MapDetailInteractorTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 08/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
import MapKit

@testable import PremierInn

private class MockDelegate: MapDetailInteractorDelegate {

    var finishedLoadingHotelDidCall = false

    func finishedLoadingHotel() {

        finishedLoadingHotelDidCall = true
    }
}

class MapDetailInteractorTests: XCTestCase {

    fileprivate var mockDelegate: MockDelegate?

    var interactor: MapDetailInteractor?

    private var hotel: Hotel? {
        guard let hotelFileURL = Bundle(for: type(of: self)).url(forResource: "hotelInfo", withExtension: "json") else { return nil }
        guard let hotelData = try? Data(contentsOf: hotelFileURL) else { return nil }
        guard let hotelJSON = try? JSONSerialization.jsonObject(with: hotelData, options: .allowFragments) as? PIDictionary else { return nil }
        guard let hotel = try? Hotel(dictionary: hotelJSON) else { return nil }

        return hotel
    }
    private var referenceAnnotation: MKAnnotation? {
        return PISuggestion(dictionary: ["name": "Nik's Mom's", "lat": 2, "long": 2])
    }

    override func setUp() {
        super.setUp()

        mockDelegate = MockDelegate()

        interactor = MapDetailInteractor(hotel: hotel, referencePoint: referenceAnnotation, delegate: mockDelegate, and: .journeyPlanner)
    }

    override func tearDown() {
        super.tearDown()
    }

    func testFinishedLoading() {

        XCTAssert(mockDelegate?.finishedLoadingHotelDidCall == true)
    }
}
