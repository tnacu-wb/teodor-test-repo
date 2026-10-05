//
//  StayTests.swift
//  PremierInnTests
//
//  Created by Louis Faria-Softly on 30/04/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
import PassKit
@testable import PremierInn

final class StayTests: XCTestCase {

    override class func setUp() {
        super.setUp()
    }

    override func setUp() {
        super.setUp()
    }

    override func tearDown() {
        super.tearDown()
    }

    var stay: Stay {

        var dictionary = PIDictionary()
        dictionary["hotelCode"] = "STUAIR"
        dictionary["hotelName"] = "Alpha2 London Blackfriars (Fleet Street)"
        dictionary["hotelLatitude"] = 50.823071
        dictionary["hotelLongitude"] = -0.140976
        dictionary["lastName"] = "APPS"
        dictionary["identifier"] = "BBER264250"
        dictionary["arrivalDate"] = "2017-10-09"
        dictionary["checkOutDate"] = "2017-10-10"
        dictionary["imageURL"] = "/content/dam/pi/websites/hotelimages/gb/en/B/BRIPTI/BRIPTI 1.jpg"

        return try! Stay(dictionary: dictionary)
    }

    func testQRCode_FlagOn_1_Day_Arrival() {
        let stay = stay
        let today = Date()
        let tomorrow = Calendar.current.date(byAdding: .day, value: 1, to: today)
        let checkOut = Calendar.current.date(byAdding: .day, value: 3, to: today)

        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: tomorrow!)
        stay.checkOutDateString = DateFormatter.parameterFormatter.string(from: checkOut!)

        stay.cancelled = false

        let remoteConfig = MockRemoteConfig(kioskHotels: [["hotelCode":"STUAIR"]])
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        XCTAssertTrue(stay.qrCodeEnabledAndWithin48Hours)
    }

    func testQRCode_FlagOff_1_Day_Arrival() {
        let stay = stay
        let today = Date()
        let tomorrow = Calendar.current.date(byAdding: .day, value: 1, to: today)
        let checkOut = Calendar.current.date(byAdding: .day, value: 3, to: today)

        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: tomorrow!)
        stay.checkOutDateString = DateFormatter.parameterFormatter.string(from: checkOut!)

        stay.cancelled = false

        let remoteConfig = MockRemoteConfig(kioskHotels: nil)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
        XCTAssertFalse(stay.qrCodeEnabledAndWithin48Hours)

    }

    func testQRCode_FlagOn_4_Day_Arrival() {
        let stay = stay
        let today = Date()
        let tomorrow = Calendar.current.date(byAdding: .day, value: 4, to: today)
        let checkOut = Calendar.current.date(byAdding: .day, value: 6, to: today)

        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: tomorrow!)
        stay.checkOutDateString = DateFormatter.parameterFormatter.string(from: checkOut!)

        stay.cancelled = false

        let remoteConfig = MockRemoteConfig(kioskHotels: [["hotelCode":"STUAIR"]])
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
        XCTAssertFalse(stay.qrCodeEnabledAndWithin48Hours)

    }

    func testQRCode_FlagOff_4_Day_Arrival() {
        let stay = stay
        let today = Date()
        let tomorrow = Calendar.current.date(byAdding: .day, value: 4, to: today)
        let checkOut = Calendar.current.date(byAdding: .day, value: 6, to: today)

        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: tomorrow!)
        stay.checkOutDateString = DateFormatter.parameterFormatter.string(from: checkOut!)

        stay.cancelled = false

        let remoteConfig = MockRemoteConfig(kioskHotels: nil)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
        XCTAssertFalse(stay.qrCodeEnabledAndWithin48Hours)

    }

    func testQRCode_Booking_Cancelled() {
        let stay = stay
        let today = Date()
        let tomorrow = Calendar.current.date(byAdding: .day, value: 1, to: today)
        let checkOut = Calendar.current.date(byAdding: .day, value: 3, to: today)

        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: tomorrow!)
        stay.checkOutDateString = DateFormatter.parameterFormatter.string(from: checkOut!)

        stay.cancelled = true

        let remoteConfig = MockRemoteConfig(kioskHotels: [["hotelCode":"STUAIR"]])
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
        XCTAssertFalse(stay.qrCodeEnabledAndWithin48Hours)

    }

    func testQRCode_Booking_Past() {
        let stay = stay
        let today = Date()
        let tomorrow = Calendar.current.date(byAdding: .day, value: -3, to: today)
        let checkOut = Calendar.current.date(byAdding: .day, value: -2, to: today)

        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: tomorrow!)
        stay.checkOutDateString = DateFormatter.parameterFormatter.string(from: checkOut!)

        stay.cancelled = false

        let remoteConfig = MockRemoteConfig(kioskHotels: [["hotelCode":"STUAIR"]])
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        XCTAssertFalse(stay.qrCodeEnabledAndWithin48Hours)
    }

    func testAppleWallet_Show_QRCode() {
        let stay = stay
        let today = Date()
        let checkIn = Calendar.current.date(byAdding: .day, value: 10, to: today)
        let checkOut = Calendar.current.date(byAdding: .day, value: 14, to: today)

        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: checkIn!)
        stay.checkOutDateString = DateFormatter.parameterFormatter.string(from: checkOut!)

        stay.cancelled = false

        let remoteConfig = MockRemoteConfig(kioskHotels: [["hotelCode":"STUAIR"]])
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
        XCTAssertTrue(stay.qrCodeIsEnabled)
    }

    func testAppleWallet_Hide_QRCode() {
        let stay = stay
        let today = Date()
        let checkIn = Calendar.current.date(byAdding: .day, value: 10, to: today)
        let checkOut = Calendar.current.date(byAdding: .day, value: 14, to: today)

        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: checkIn!)
        stay.checkOutDateString = DateFormatter.parameterFormatter.string(from: checkOut!)

        stay.cancelled = false

        let remoteConfig = MockRemoteConfig(kioskHotels: nil)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        XCTAssertFalse(stay.qrCodeIsEnabled)
    }

    func testShowWifiButton() {
        let stay = stay
        let today = Date()
        let checkOut = Calendar.current.date(byAdding: .day, value: 14, to: today)

        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: today)
        stay.checkOutDateString = DateFormatter.parameterFormatter.string(from: checkOut!)

        stay.cancelled = false

        let remoteConfig = MockRemoteConfig(wifiEnabledHotel: [["hotelCode":"STUAIR"]])
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        XCTAssertTrue(stay.showHotelWifiOption)
    }

    func testHideWifiButton_Past() {
        let stay = stay
        let today = Date()
        let checkIn = Calendar.current.date(byAdding: .day, value: -3, to: today)
        let checkOut = Calendar.current.date(byAdding: .day, value: -2, to: today)

        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: checkIn!)
        stay.checkOutDateString = DateFormatter.parameterFormatter.string(from: checkOut!)

        stay.cancelled = false

        let remoteConfig = MockRemoteConfig(wifiEnabledHotel: [["hotelCode":"STUAIR"]])
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        XCTAssertFalse(stay.showHotelWifiOption)
    }

    func testHideWifiButton_Future() {
        let stay = stay
        let today = Date()
        let checkIn = Calendar.current.date(byAdding: .day, value: 10, to: today)
        let checkOut = Calendar.current.date(byAdding: .day, value: 14, to: today)

        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: checkIn!)
        stay.checkOutDateString = DateFormatter.parameterFormatter.string(from: checkOut!)

        stay.cancelled = false

        let remoteConfig = MockRemoteConfig(wifiEnabledHotel: [["hotelCode":"STUAIR"]])
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        XCTAssertFalse(stay.showHotelWifiOption)
    }

    func testShowWifiFeatureFlag() {
        let stay = stay
        let today = Date()
        let checkOut = Calendar.current.date(byAdding: .day, value: 14, to: today)

        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: today)
        stay.checkOutDateString = DateFormatter.parameterFormatter.string(from: checkOut!)

        stay.cancelled = false
        let remoteConfig = MockRemoteConfig(wifiEnabledHotel: [["hotelCode":"STUAIR"]])
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
        XCTAssertTrue(stay.showHotelWifiOption)
    }

    func testHideWifiFeatureFlag() {
        let stay = stay
        let today = Date()
        let checkOut = Calendar.current.date(byAdding: .day, value: 14, to: today)

        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: today)
        stay.checkOutDateString = DateFormatter.parameterFormatter.string(from: checkOut!)

        stay.cancelled = false

        let remoteConfig = MockRemoteConfig(wifiEnabledHotel: nil)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
        XCTAssertFalse(stay.showHotelWifiOption)
    }

    // check-in/out times remain the same regardless of early check-in/late check-out
    // banner with early check-in/late check-out times shown when at least one is selected
    func testCheckInOutBookingConfirmationTextAndBanner() {

        var packages: [PIDictionary] = [
            ["packageCode": "BFADBF",
             "description": "Full english breakfast",
             "unitPrice": 9.00,
             "totalQuantity": 1,
             "computedPrice": 9.00],
            ["packageCode": "HSCKIN",
             "description": "Early Check-In",
             "unitPrice": 10.00,
             "totalQuantity": 1,
             "computedPrice": 10.00]
        ]

        guard let data = try? JSONSerialization.data(withJSONObject: packages, options: .prettyPrinted) else { return XCTFail("failed to json serialise packages dictionary") }
        var decodedPackages: ReservationPackageList? = try? JSONDecoder().decode(ReservationPackageList.self, from: data)

        let stay = stay
        stay.numberOfRoooms = 2

        // test when no packages in stay
        XCTAssertNil(stay.reservationPackageList)
        XCTAssertEqual(stay.reservationPackageList?.numberOfEarlyCheckInPackages ?? 0, 0)
        XCTAssertEqual(stay.reservationPackageList?.numberOfLateCheckOutPackages ?? 0, 0)

        // do not show banner when there's no early check-in/late checkout packages
        XCTAssertFalse(stay.shouldShowCheckInOutExtrasInfo)

        // assign packages
        // multiple rooms: early check-in for one room
        stay.reservationPackageList = decodedPackages
        XCTAssertNotNil(stay.reservationPackageList)
        XCTAssertEqual(stay.reservationPackageList?.numberOfEarlyCheckInPackages, 1)
        XCTAssertEqual(stay.reservationPackageList?.numberOfLateCheckOutPackages, 0)

        var checkInText = stay.checkInTimeText(hotelBrand: .premierInn)
        XCTAssertEqual(checkInText, "from 3pm")

        var checkOutText = stay.checkOutTimeText(hotelBrand: .premierInn)
        XCTAssertEqual(checkOutText, "before 12pm")

        // show banner if there's at least one early check-in/late checkout package
        XCTAssertTrue(stay.shouldShowCheckInOutExtrasInfo)

        // one room: only early check-in selected
        stay.numberOfRoooms = 1
        XCTAssertEqual(stay.reservationPackageList?.numberOfEarlyCheckInPackages, 1)
        XCTAssertEqual(stay.reservationPackageList?.numberOfLateCheckOutPackages, 0)

        checkInText = stay.checkInTimeText(hotelBrand: .premierInn)
        XCTAssertEqual(checkInText, "from 3pm")

        checkOutText = stay.checkOutTimeText(hotelBrand: .premierInn)
        XCTAssertEqual(checkOutText, "before 12pm")

        // show banner if there's at least one early check-in/late checkout package
        XCTAssertTrue(stay.shouldShowCheckInOutExtrasInfo)

        // one room: early check-in and late check-out selected
        packages = [
            ["packageCode": "HSCOU2",
             "description": "Late Checkout",
             "unitPrice": 10.00,
             "totalQuantity": 1,
             "computedPrice": 10.00],
            ["packageCode": "HSCKIN",
             "description": "Early Check-In",
             "unitPrice": 10.00,
             "totalQuantity": 1,
             "computedPrice": 10.00]
        ]

        guard let data = try? JSONSerialization.data(withJSONObject: packages, options: .prettyPrinted) else { return XCTFail("failed to json serialise packages dictionary") }
        decodedPackages = try? JSONDecoder().decode(ReservationPackageList.self, from: data)

        stay.reservationPackageList = decodedPackages
        XCTAssertNotNil(stay.reservationPackageList)
        XCTAssertEqual(stay.reservationPackageList?.numberOfEarlyCheckInPackages, 1)
        XCTAssertEqual(stay.reservationPackageList?.numberOfLateCheckOutPackages, 1)

        checkInText = stay.checkInTimeText(hotelBrand: .premierInn)
        XCTAssertEqual(checkInText, "from 3pm")

        checkOutText = stay.checkOutTimeText(hotelBrand: .premierInn)
        XCTAssertEqual(checkOutText, "before 12pm")

        // show banner if there's at least one early check-in/late checkout package
        XCTAssertTrue(stay.shouldShowCheckInOutExtrasInfo)

        // multiple rooms: early check-in and late check-out for one room
        stay.numberOfRoooms = 2

        XCTAssertEqual(stay.reservationPackageList?.numberOfEarlyCheckInPackages, 1)
        XCTAssertEqual(stay.reservationPackageList?.numberOfLateCheckOutPackages, 1)

        checkInText = stay.checkInTimeText(hotelBrand: .premierInn)
        XCTAssertEqual(checkInText, "from 3pm")

        checkOutText = stay.checkOutTimeText(hotelBrand: .premierInn)
        XCTAssertEqual(checkOutText, "before 12pm")

        // show banner if there's at least one early check-in/late checkout package
        XCTAssertTrue(stay.shouldShowCheckInOutExtrasInfo)
    }


    func testimHereRoomAllocationButtonShow() {
        // If Pre Checked In and Active Pass in Wallet and is upcoming
        let stay = stay
        stay.basketStatus = .preCheckedIn
        stay.digitalKeyIdentifier = "TestID"
        stay.keyManager = MockPassManager(isInPassManager: true)
        var today = Date()

        let remoteConfig = MockRemoteConfig(featureDigitalKeys: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: today)
        var checkOut = Calendar.current.date(byAdding: .day, value: 3, to: today)
        stay.checkOutDateString = DateFormatter.parameterFormatter.string(from: checkOut!)
        XCTAssert(stay.roomIsReadyForAllocation == true)

        // If Not Pre Checked In and Active Pass in Wallet and is upcoming
        stay.basketStatus = .complete
        stay.keyManager = MockPassManager(isInPassManager: true)
        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: today)

        stay.checkOutDateString = DateFormatter.parameterFormatter.string(from: checkOut!)
        XCTAssert(stay.roomIsReadyForAllocation == false)

        // If Pre Checked In and Active Pass is Not Wallet and is upcoming
        stay.basketStatus = .preCheckedIn
        stay.keyManager = MockPassManager(isInPassManager: false)
        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: today)
        stay.checkOutDateString = DateFormatter.parameterFormatter.string(from: checkOut!)
        XCTAssert(stay.roomIsReadyForAllocation == false)

        // If Pre Checked In and Active Pass is Not Wallet and is upcoming
        stay.basketStatus = .preCheckedIn
        stay.keyManager = MockPassManager(isInPassManager: true)
        today = Calendar.current.date(byAdding: .day, value: -3, to: today)!
        checkOut = Calendar.current.date(byAdding: .day, value: -4, to: today)!
        stay.arrivalDateString = DateFormatter.parameterFormatter.string(from: today)
        stay.checkOutDateString = DateFormatter.parameterFormatter.string(from: checkOut!)
        XCTAssert(stay.roomIsReadyForAllocation == false)
    }
}
