//
//  KeyInteractorTest.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 19/08/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//
import XCTest
@testable import SimpleNetwork
@testable import PremierInn

final class KeyInteractorTest: XCTestCase {

    private var requestsManager: MockKeyInteractorInput!
    private var sut: KeyInteractor!

    override func setUp() {
        requestsManager = MockKeyInteractorInput()
    }

    override func tearDown() {
        requestsManager = nil
        sut = nil
    }

    func testUpdateCiolStatus() {
        // GIVEN
        let mockNotificationManager = MockNotificationManager(result: .allowed)
        sut = .init(
            stay: stay(hasECI: false),
            howYourKeyWorks: true,
            notificationManager: mockNotificationManager
        )
        sut.requestManager = requestsManager

        // WHEN
        sut.updateCiolStatus()

        // THEN
        XCTAssertTrue(requestsManager.isUpdateCiolStatusCalled, "Should be true")
        XCTAssertEqual(requestsManager.ciolStatus, .walletPass, "Should be .walletPass")
    }

    func testInformationRowsWhenUserHasAllowedNotification() {
        // GIVEN
        let mockNotificationManager = MockNotificationManager(result: .allowed)
        sut = .init(
            stay: stay(hasECI: false),
            howYourKeyWorks: true,
            notificationManager: mockNotificationManager
        )
        sut.requestManager = requestsManager
        
        // WHEN
        sut.loadNotificationPermissionStatus()
        
        // THEN
        XCTAssertEqual(sut.viewModel.infoRows?.count, 2)
        XCTAssertEqual(sut.viewModel.infoRows?[0].title, "Using your key")
        XCTAssertEqual(sut.viewModel.infoRows?[1].title, "Getting your key")
    }

    func testInformationRowsWhenUserHasNotAllowedNotification() {
        // GIVEN
        let mockNotificationManager = MockNotificationManager(result: .denied)
        sut = .init(
            stay: stay(hasECI: false),
            howYourKeyWorks: true,
            notificationManager: mockNotificationManager
        )
        sut.requestManager = requestsManager
        
        // WHEN
        sut.loadNotificationPermissionStatus()
        
        // THEN
        XCTAssertEqual(sut.viewModel.infoRows?.count, 3)
        XCTAssertEqual(sut.viewModel.infoRows?[0].title, "Using your key")
        XCTAssertEqual(sut.viewModel.infoRows?[1].title, "Getting your key")
        XCTAssertEqual(sut.viewModel.infoRows?[2].title, "Notifications")
    }

    // sorry no time right now to move to stay tests :D
    /*
    func testArrivalDateIsTodayAndTimeIsBeforeCheckInWithECI() {
        let stay = stay(hasECI: true)

        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: Date())

        let interactor = KeyInteractor(stay: stay)
        let currentDateTime = Calendar.current.date(bySettingHour: 1, minute: 0, second: 0, of: Date())
        interactor.currentTime = { currentDateTime! }

        let roomStayTimeText = interactor.roomStayTimeText
        XCTAssertEqual(roomStayTimeText, "Your room will be ready from 11am")
        print(roomStayTimeText)
    }

    func testArrivalDateIsTodayAndTimeIsAfterCheckInWithECI() {
        let stay = stay(hasECI: true)

        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: Date())

        let interactor = KeyInteractor(stay: stay)
        let currentDateTime = Calendar.current.date(bySettingHour: 21, minute: 0, second: 0, of: Date())
        interactor.currentTime = { currentDateTime! }

        let roomStayTimeText = interactor.roomStayTimeText
        XCTAssertEqual(roomStayTimeText, "Your room is ready")
    }

    func testArrivalDateIsTodayAndTimeIsBeforeCheckInWithoutECI() {
        let stay = stay(hasECI: false)

        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: Date())

        let interactor = KeyInteractor(stay: stay)
        let currentDateTime = Calendar.current.date(bySettingHour: 1, minute: 0, second: 0, of: Date())
        interactor.currentTime = { currentDateTime! }

        let roomStayTimeText = interactor.roomStayTimeText
        XCTAssertEqual(roomStayTimeText, "Your room will be ready from 3pm")
    }

    func testArrivalDateIsTodayAndTimeIsAfterCheckInWithoutECI() {
        let stay = stay(hasECI: false)

        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: Date())

        let interactor = KeyInteractor(stay: stay)
        let currentDateTime = Calendar.current.date(bySettingHour: 21, minute: 0, second: 0, of: Date())
        interactor.currentTime = { currentDateTime! }

        let roomStayTimeText = interactor.roomStayTimeText
        XCTAssertEqual(roomStayTimeText, "Your room is ready")

    }

    func testArrivalDateIsTomorrowWithECI() {
        let stay = stay(hasECI: true)

        let tomorrow = Calendar.current.date(byAdding: .day, value: 1, to: Date())
        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: tomorrow!)

        let interactor = KeyInteractor(stay: stay)
        let currentDateTime = Calendar.current.date(bySettingHour: 21, minute: 0, second: 0, of: Date())

        interactor.currentTime = { currentDateTime! }

        let roomStayTimeText = interactor.roomStayTimeText
        XCTAssertEqual(roomStayTimeText, "Your room will be ready from 11am \(tomorrow!.localizedShortStringFormat)")
    }

    func testArrivalDateIsTomorrowWithoutECI() {
        let stay = stay(hasECI: false)

        let tomorrow = Calendar.current.date(byAdding: .day, value: 1, to: Date())
        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: tomorrow!)

        let interactor = KeyInteractor(stay: stay)
        let currentDateTime = Calendar.current.date(bySettingHour: 21, minute: 0, second: 0, of: Date())
        interactor.currentTime = { currentDateTime! }

        let roomStayTimeText = interactor.roomStayTimeText
        XCTAssertEqual(roomStayTimeText, "Your room will be ready from 3pm \(tomorrow!.localizedShortStringFormat)")
    }

    func testCheckInTimeHasPassedWithECI() {
        let stay = stay(hasECI: true)

        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: Date())

        let interactor = KeyInteractor(stay: stay)
        let currentDateTime = Calendar.current.date(bySettingHour: 12, minute: 0, second: 0, of: Date())
        interactor.currentTime = { currentDateTime! }

        let roomStayTimeText = interactor.checkInTimeHasPassed
        XCTAssertTrue(roomStayTimeText)
    }

    func testCheckInTimeHasNotPassedWithECI() {
        let stay = stay(hasECI: true)

        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: Date())

        let interactor = KeyInteractor(stay: stay)
        let currentDateTime = Calendar.current.date(bySettingHour: 10, minute: 0, second: 0, of: Date())
        interactor.currentTime = { currentDateTime! }

        let roomStayTimeText = interactor.checkInTimeHasPassed
        XCTAssertFalse(roomStayTimeText)
    }

    func testCheckInTimeHasPassedWithoutECI() {
        let stay = stay(hasECI: false)

        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: Date())

        let interactor = KeyInteractor(stay: stay)
        let currentDateTime = Calendar.current.date(bySettingHour: 16, minute: 0, second: 0, of: Date())
        interactor.currentTime = { currentDateTime! }

        let roomStayTimeText = interactor.checkInTimeHasPassed
        XCTAssertTrue(roomStayTimeText)
    }

    func testCheckInTimeHasNotPassedWithoutECI() {
        let stay = stay(hasECI: false)

        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: Date())

        let interactor = KeyInteractor(stay: stay)
        let currentDateTime = Calendar.current.date(bySettingHour: 12, minute: 0, second: 0, of: Date())
        interactor.currentTime = { currentDateTime! }

        let roomStayTimeText = interactor.checkInTimeHasPassed
        XCTAssertFalse(roomStayTimeText)
    }
     */

}

final class MockNotificationManager: NotificationManaging {

    enum PermissionResult {
        case allowed
        case denied
    }
    
    let result: PermissionResult
    
    init(result: PermissionResult) {
        self.result = result
    }
    
    func checkNotificationPermission(completion: @escaping (Bool) -> Void) {
        switch result {
        case .allowed:
            completion(true)
        case .denied:
            completion(false)
        }
    }
    
    func openSettings() {}
}

private extension KeyInteractorTest {
    final class MockKeyInteractorInput: KeyInteractorDataProviderProtocol {

        func digitalKeyCheckIn(reservationId: String, hotelCode: String, completion: @escaping (_ response: DigitalKeyCheckInResponse?, _ error: Error?) -> Void) { }

        func findBookingSource(findBookingDetails: FindBookingDetails, completion: @escaping (FindBookingSource?, Error?) -> Void) { }

        func reservation(reservationDetails: ReservationDetails, hotelCode: String?, bookingDetails: BookingDetails?, completion: @escaping (_ confirmation: Reservation?, _ error: Error?) -> Void) { }

        private(set) var ciolStatus: CiolStatus?
        private(set) var isUpdateCiolStatusCalled = false
        func updateCiolStatus(payload: UpdateCiolStatusPayload, completion: @escaping (_ response: UpdateCiolStatusResponse?, _ error: Error?) -> Void) {
            ciolStatus = payload.ciolStatus
            isUpdateCiolStatusCalled = true
            completion(nil, nil)
        }
    }
}

private extension KeyInteractorTest {
    func stay(hasECI: Bool) -> Stay {
        var dictionary = PIDictionary()
        dictionary["hotelCode"] = "LONBLA"
        dictionary["hotelName"] = "Alpha2 London Blackfriars (Fleet Street)"
        dictionary["hotelLatitude"] = 50.823071
        dictionary["hotelLongitude"] = -0.140976
        dictionary["lastName"] = "APPS"
        dictionary["identifier"] = "BBER264250"
        dictionary["arrivalDate"] = "2017-10-11"
        dictionary["checkOutDate"] = "2017-10-10"
        dictionary["imageURL"] = "/content/dam/pi/websites/hotelimages/gb/en/B/BRIPTI/BRIPTI 1.jpg"

        let stay = try! Stay(dictionary: dictionary)

        if hasECI {
            let packages: [PIDictionary] = [
                ["packageCode": "HSCKIN",
                 "description": "Early Check-In",
                 "unitPrice": 10.00,
                 "totalQuantity": 1,
                 "computedPrice": 10.00]
            ]

            let data = try! JSONSerialization.data(withJSONObject: packages, options: .prettyPrinted)
            let decodedPackages: ReservationPackageList? = try? JSONDecoder().decode(ReservationPackageList.self, from: data)
            stay.reservationPackageList = decodedPackages
        }
        return stay
    }
}
