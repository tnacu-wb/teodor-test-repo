//
//  BookingConfirmationInteractorTests.swift
//  PremierInnTests
//
//  Created by Marcello Mascia on 11/10/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import XCTest
import PassKit
@testable import SimpleNetwork
@testable import PremierInn

final class BookingConfirmationInteractorTests: XCTestCase {

    var sut: BookingConfirmationInteractor!
    private var output: MockBookingConfirmationInteractorOutput!

    override func setUp() {
        super.setUp()

        output = MockBookingConfirmationInteractorOutput()

        let summary = getStay()
        
        sut = BookingConfirmationInteractor(summary: summary, isBookingFlowComplete: true)
        sut.dataProvider = output
    }
    
    override func tearDown() {

        sut = nil
        output = nil

        super.tearDown()
    }
    
    func testLatestReservationDetailsUpdatesStayBookingStatus() {
        let expectation = expectation(description: "loadLatestDetails completes")

        sut.fetchHotel()
        sut.loadLatestDetails { _ in
            expectation.fulfill()
        }
        waitForExpectations(timeout: 1.0)
        XCTAssertEqual(self.sut.summary.stayBookingStatus, .checkedIn, "Expected stayBookingStatus to be .checkedIn when first room is checked in")
    }

    func testLoadLatestDetailsDoesNotSendStaysDidChangeNotification() {
        let completionExpectation = expectation(description: "loadLatestDetails completes")
        let noNotificationExpectation = expectation(
            forNotification: .staysDidChange,
            object: nil,
            handler: nil
        )
        noNotificationExpectation.isInverted = true

        sut.fetchHotel()
        sut.loadLatestDetails { success in
            XCTAssertTrue(success)
            completionExpectation.fulfill()
        }

        wait(for: [completionExpectation, noNotificationExpectation], timeout: 1.0)
    }

    func testLoadLatestDetailsPassesReservationArrivalAndDepartureDatesToGetPackages() throws {
        // GIVEN
        let reservationDictionary: [String: Any] = [
            "reservationDetails": [
                "hotelCode": "LINMIL",
                "confirmationNumber": "BBER264250",
                "arrivalDate": "2099-08-11",
                "departureDate": "2099-08-13",
                "booker": [:],
                "rooms": [["bookingStatus": "InHouse"]],
                "roomBreakdown": [["roomId": "12345"]],
                "operaBasketReference": "abc-123-abc-123"
            ]
        ]
        let reservation = try Reservation(dictionary: reservationDictionary)
        output.reservationResult = (reservation, nil)

        let completionExpectation = expectation(description: "loadLatestDetails completes")

        // WHEN
        sut.fetchHotel()
        sut.loadLatestDetails { success in
            XCTAssertTrue(success)
            completionExpectation.fulfill()
        }

        // THEN
        wait(for: [completionExpectation], timeout: 1.0)

        XCTAssertTrue(output.getPackagesDidCall)

        let capturedCriteria = try XCTUnwrap(output.getPackagesBookingDetails?.criteria)
        let expectedArrivalDate = try XCTUnwrap(DateFormatter.parameterFormatter.date(from: "2099-08-11"))
        let expectedDepartureDate = try XCTUnwrap(DateFormatter.parameterFormatter.date(from: "2099-08-13"))
        let capturedDepartureDate = try XCTUnwrap(capturedCriteria.checkOutDate)

        XCTAssertTrue(Calendar.current.isDate(capturedCriteria.arrivalDate, inSameDayAs: expectedArrivalDate))
        XCTAssertTrue(Calendar.current.isDate(capturedDepartureDate, inSameDayAs: expectedDepartureDate))
    }

    func getStay(isCheckInOnlineAvailable: Bool = false) -> Stay {
        var dictionary = PIDictionary()
        dictionary["hotelCode"] = "LONBLA"
        dictionary["hotelName"] = "Alpha2 London Blackfriars (Fleet Street)"
        dictionary["hotelLatitude"] = 50.823071
        dictionary["hotelLongitude"] = -0.140976
        dictionary["lastName"] = "APPS"
        dictionary["identifier"] = "BBER264250"
        dictionary["arrivalDate"] = "2017-10-09"
        dictionary["checkOutDate"] = "2017-10-10"
        dictionary["imageURL"] = "/content/dam/pi/websites/hotelimages/gb/en/B/BRIPTI/BRIPTI 1.jpg"
        dictionary["checkInOnlineAvailable"] = isCheckInOnlineAvailable
        dictionary["roomIds"] = ["4742348304832"]
        
        return try! Stay(dictionary: dictionary)
    }
    
    func testLoadHotel() {

        sut.fetchHotel()

        XCTAssertTrue(output.loadHotelDidCall)
    }
    
    func test_isCheckInOnlineAvailable() {

        let stay = getStay(isCheckInOnlineAvailable: true)
        let userDef = SimpleStorageManager<Stay>(dataSource: .standard)
        _ = userDef.update(with: [stay])
        
        sut = BookingConfirmationInteractor(summary: stay,
                                                   isBookingFlowComplete: false)
        
        let remoteConfig = MockRemoteConfig(featureCIOL: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        XCTAssertTrue(sut.isCheckInOnlineAvailable)
    }
    
    func test_featureCIOLOff() {

        let stay = getStay(isCheckInOnlineAvailable: true)
        sut = BookingConfirmationInteractor(summary: stay,
                                                   isBookingFlowComplete: false)

        let remoteConfig = MockRemoteConfig(featureCIOL: false)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        XCTAssertFalse(sut.isCheckInOnlineAvailable)
    }
    
    func test_isCheckInOnlineUnavailable() {

        let stay = getStay(isCheckInOnlineAvailable: false)
        let userDef = SimpleStorageManager<Stay>(dataSource: .standard)
        _ = userDef.update(with: [stay])
        
        sut = BookingConfirmationInteractor(summary: stay,
                                                   isBookingFlowComplete: false)

        let remoteConfig = MockRemoteConfig(featureCIOL: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        XCTAssertFalse(sut.isCheckInOnlineAvailable)
    }
    
    func test_checkOutCalled() {
        output.confirmPreCheckInOut(basketReference: "", type: .checkOut, isCiol: false) { _, _ in
            // Empty completion handler - test only verifies method is called
        }
        XCTAssertTrue(output.confirmCheckOutInCalled)
    }

    func test_updateCiolStatus() {
        sut.updateCiolStatus(to: .ciolStarted)
        XCTAssertEqual(output.isUpdateCiolStatusCalled, true)
        XCTAssertEqual(output.ciolStatus, .ciolStarted)

        sut.updateCiolStatus(to: .walletPass)
        XCTAssertEqual(output.isUpdateCiolStatusCalled, true)
        XCTAssertEqual(output.ciolStatus, .walletPass)
    }
}
