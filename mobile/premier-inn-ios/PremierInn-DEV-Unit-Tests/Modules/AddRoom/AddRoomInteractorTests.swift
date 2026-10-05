//
//  AddRoomInteractorTests.swift
//  PremierInn UnitTests
//
//  Created by Filippo Minelle on 20/07/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork

@testable import PremierInn

class AddRoomInteractorTests: XCTestCase {

    // MARK: - Properties

    private var hotel: Hotel? {
        guard let hotelFileURL = Bundle(for: type(of: self)).url(forResource: "hotelInfo", withExtension: "json") else { return nil }
        guard let hotelData = try? Data(contentsOf: hotelFileURL) else { return nil }
        guard let hotelJSON = try? JSONSerialization.jsonObject(with: hotelData, options: .allowFragments) as? PIDictionary else { return nil }
        guard let hotel = try? Hotel(dictionary: hotelJSON) else { return nil }

        return hotel
    }

    private var addRoomAvailabilityRequirements: AddRoomAvailabilityRequirements {
        return AddRoomAvailabilityRequirements(existingRooms: [Room()], existingUpsells: nil, arrivalDate: Date(), numberOfNights: 2, hotelCode: "code", reservationID: "id", kidsHaveToPayForCurrentBreakfast: true, isStayingForBusiness: true, existingRateClassification: nil, isBusiness: false)
    }

    private var analytics: MockAnalyticsManager!
    private var interactor: AddRoomInteractor!

    // MARK: - Lifecycle

    override func setUp() {
        super.setUp()

        interactor = AddRoomInteractor(withRoom: nil, existingCost: nil, existingUpsells: nil, isOnlyRoom: false, isAmendableRoom: false, isCancellableRoom: false, canAmendGuests: false, roomNumber: 1, andAddRoomAvailabilityRequirements: addRoomAvailabilityRequirements, amendOperaDetails: nil)

        analytics = MockAnalyticsManager()
        interactor.analytics = analytics
    }

    override func tearDown() {

        analytics = nil
        interactor = nil

        super.tearDown()
    }

    // MARK: - Tests

    func testView_whenUpdateAdultsNumber_invokesTrackAction() {

        interactor.updateAdultsNumber(value: 1)

        let action = analytics.actions.first
        let userInfo = analytics.userInfos.first

        XCTAssertEqual(action, "amend.change")
        XCTAssertEqual(userInfo?["amend.change"], "Room 1 adults changed from 1 to 1")
    }

    func testView_whenUpdateChildrenNumber_invokesTrackAction() {

        interactor.updateChildrenNumber(value: 3)

        let action = analytics.actions.first
        let userInfo = analytics.userInfos.first

        XCTAssertEqual(action, "amend.change")
        XCTAssertEqual(userInfo?["amend.change"], "Room 1 children changed from 0 to 3")
    }

    func testView_whenUpdateRoomType_invokesTrackAction() {

        interactor.updateRoomType(RoomType.double)

        let action = analytics.actions.first
        let userInfo = analytics.userInfos.first

        XCTAssertEqual(action, "amend.change")
        XCTAssertEqual(userInfo?["amend.change"], "Room 1 changed from double to double")
    }
}
