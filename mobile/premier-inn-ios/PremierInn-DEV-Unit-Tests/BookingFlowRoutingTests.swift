//
//  BookingFlowRoutingTests.swift
//  PremierInnTests
//
//  Created by Freddie Parks on 23/03/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

class BookingFlowRoutingTests: XCTestCase {

    var hotel: Hotel? {

        do {
            let fileURL = Bundle(for: type(of: self)).url(forResource: "hotelInfo", withExtension: "json")!
            let data = try Data(contentsOf: fileURL)
            guard let jsonDictionary = try JSONSerialization.jsonObject(with: data, options: .allowFragments) as? PIDictionary else { return nil }
            let hotel = try Hotel(dictionary:jsonDictionary)

            return hotel
        } catch {
            XCTFail("\(error)")
            return nil
        }
    }

    var user: User? {

        do {
            let user = try User(title: "mr", firstName: "justin", lastName: "pogg")
            return user
        } catch {
            XCTFail("\(error)")
            return nil
        }
    }

    var company: Company? {

        var requestCompanyDictionary: PIDictionary = [
            "companyDetails": [
                "companyName": "Pogg Inc"
            ]
        ]
        requestCompanyDictionary["allowCentralCreditCard"] = true

        guard let companyData = try? JSONSerialization.data(withJSONObject: requestCompanyDictionary, options: .prettyPrinted) else { return nil }

        do {
            let decoder = JSONDecoder()
            return try decoder.decode(Company.self, from: companyData)
        } catch {
            return nil
        }
    }
    var companyWithQuestions: Company? {

        var requestCompanyDictionary: PIDictionary = [
            "companyDetails": [
                "companyName": "Pogg Inc"
            ],
            "companyManagementDetails": [
                "purchaseOrderManagement": [
                    "label": "Purchase order",
                    "mandatory": false,
                    "active": true,
                    "location": "B",
                    "managementInformationAnswer": [
                        "answerType": "F"
                    ]
                ]
            ]
        ]
        requestCompanyDictionary["allowCentralCreditCard"] = true

        guard let companyData = try? JSONSerialization.data(withJSONObject: requestCompanyDictionary, options: .prettyPrinted) else { return nil }

        do {
            let decoder = JSONDecoder()
            return try decoder.decode(Company.self, from: companyData)
        } catch {
            return nil
        }
    }

    override class func setUp() {
        super.setUp()

        let remoteConfig = MockRemoteConfig(employeeQuestionsOperaFeature: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
    }

    override class func tearDown() {
        super.tearDown()
    }

    // HDP Routing Tests

    func testHDPShouldRouteToAccessibleRooms() {

        BookingDetails.sharedInstance.hotel = hotel
        BookingDetails.sharedInstance.rate = BookingDetails.sharedInstance.hotel?.rates.first

        let router = HotelDetailsRouter()

        let viewController = router.nextController(withRooms: [], accessibleRoomImages: [], twinRoomImages: [], withUpsells: true, andShouldShowRoomSelectionScreen: true)
        XCTAssert(viewController is BathroomSelectionViewController)

        let viewController2 = router.nextController(withRooms: [], accessibleRoomImages: [], twinRoomImages: [], withUpsells: false, andShouldShowRoomSelectionScreen: true)
        XCTAssert(viewController2 is BathroomSelectionViewController)
    }

    func testHDPShouldRouteToUpsells() {

        BookingDetails.sharedInstance.hotel = hotel
        BookingDetails.sharedInstance.rate = BookingDetails.sharedInstance.hotel?.rates.first

        let router = HotelDetailsRouter()

        let viewController = router.nextController(withRooms: [], accessibleRoomImages: [], twinRoomImages: [], withUpsells: true, andShouldShowRoomSelectionScreen: false)
        XCTAssert(viewController is UpsellsViewController)
    }

    func testHDPShouldRouteToUserDetails() {

        UserSessionManager.sharedInstance.piUserLoggedOut()

        let router = HotelDetailsRouter()

        let viewController = router.nextController(withRooms: [], accessibleRoomImages: [], twinRoomImages: [], withUpsells: false, andShouldShowRoomSelectionScreen: false)
        XCTAssert(viewController is UserDetailsViewController)
    }

    func testHDPShouldRouteToBusinessQuestions() {

        UserSessionManager.sharedInstance.piUserLoggedOut()

        BookingDetails.sharedInstance.hotel = hotel
        BookingDetails.sharedInstance.hotel?.update(paymentProvider: .cccp)
        BookingDetails.sharedInstance.rate = BookingDetails.sharedInstance.hotel?.rates.first

        if let user = user {
            user.company = companyWithQuestions
            UserSessionManager.sharedInstance.piLoggedIn(with: user)
        }

        let router = HotelDetailsRouter()

        let viewController = router.nextController(withRooms: [], accessibleRoomImages: [], twinRoomImages: [], withUpsells: false, andShouldShowRoomSelectionScreen: false)
        XCTAssertTrue(viewController is FormBusinessCardQuestionsController, "actual viewController is \(String(describing: viewController)), shouldShowEmployeeQuestionsForOpera=\(BookingDetails.sharedInstance.shouldShowEmployeeQuestionsForOpera), businessQuestionsCount=\(String(describing: UserSessionManager.sharedInstance.currentUser?.company?.businessCardQuestions?.count))")
    }

    func testHDPShouldRouteToReviewAndBook() {

        UserSessionManager.sharedInstance.piUserLoggedOut()

        BookingDetails.sharedInstance.hotel = hotel
        BookingDetails.sharedInstance.hotel?.update(paymentProvider: .cccp)
        BookingDetails.sharedInstance.rate = BookingDetails.sharedInstance.hotel?.rates.first

        if let user = user {
            user.company = company
            UserSessionManager.sharedInstance.piLoggedIn(with: user)
        }

        let router = HotelDetailsRouter()

        let viewController = router.nextController(withRooms: [], accessibleRoomImages: [], twinRoomImages: [], withUpsells: false, andShouldShowRoomSelectionScreen: false)
        XCTAssert(viewController is ReviewAndBookViewController)
    }

    // Bathroom Selection Routing Tests

    func testBathroomSelectionShouldRouteToUpsells() {

        UserSessionManager.sharedInstance.piUserLoggedOut()

        BookingDetails.sharedInstance.hotel = hotel
        BookingDetails.sharedInstance.rate = BookingDetails.sharedInstance.hotel?.rates.first

        let router = BathroomSelectionRouter()

        let viewController = router.nextController(with: true, and: BookingDetails.sharedInstance)
        XCTAssert(viewController is UpsellsViewController)
    }

    func testBathroomSelectionShouldRouteToUserDetails() {

        UserSessionManager.sharedInstance.piUserLoggedOut()

        let router = BathroomSelectionRouter()

        let viewController = router.nextController(with: false, and: BookingDetails.sharedInstance)
        XCTAssert(viewController is UserDetailsViewController)
    }

    func testBathroomSelectionShouldRouteToBusinessQuestions() {

        UserSessionManager.sharedInstance.piUserLoggedOut()

        BookingDetails.sharedInstance.hotel = hotel
        BookingDetails.sharedInstance.hotel?.update(paymentProvider: .cccp)
        BookingDetails.sharedInstance.rate = BookingDetails.sharedInstance.hotel?.rates.first

        if let user = user {
            user.company = companyWithQuestions
            UserSessionManager.sharedInstance.piLoggedIn(with: user)
        }

        let router = BathroomSelectionRouter()

        let viewController = router.nextController(with: false, and: BookingDetails.sharedInstance)
        XCTAssertTrue(viewController is FormBusinessCardQuestionsController, "actual viewController is \(String(describing: viewController)), shouldShowEmployeeQuestionsForOpera=\(BookingDetails.sharedInstance.shouldShowEmployeeQuestionsForOpera), businessQuestionsCount=\(String(describing: UserSessionManager.sharedInstance.currentUser?.company?.businessCardQuestions?.count))")
    }

    func testBathroomSelectionShouldRouteToReviewAndBook() {

        UserSessionManager.sharedInstance.piUserLoggedOut()

        BookingDetails.sharedInstance.hotel = hotel
        BookingDetails.sharedInstance.hotel?.update(paymentProvider: .cccp)
        BookingDetails.sharedInstance.rate = BookingDetails.sharedInstance.hotel?.rates.first

        if let user = user {
            user.company = company
            UserSessionManager.sharedInstance.piLoggedIn(with: user)
        }

        let router = BathroomSelectionRouter()

        let viewController = router.nextController(with: false, and: BookingDetails.sharedInstance)
        XCTAssert(viewController is ReviewAndBookViewController)
    }

    // Upsells Routing Tests

    func testUpsellsShouldRouteToUserDetails() {

        UserSessionManager.sharedInstance.piUserLoggedOut()

        let router = UpsellsRouter()

        let viewController = router.nextViewController(with: BookingDetails.sharedInstance)
        XCTAssert(viewController is UserDetailsViewController)
    }

    func testUpsellsShouldRouteToBusinessQuestions() {

        UserSessionManager.sharedInstance.piUserLoggedOut()

        BookingDetails.sharedInstance.hotel = hotel
        BookingDetails.sharedInstance.hotel?.update(paymentProvider: .cccp)
        BookingDetails.sharedInstance.rate = BookingDetails.sharedInstance.hotel?.rates.first

        if let user = user {
            user.company = companyWithQuestions
            UserSessionManager.sharedInstance.piLoggedIn(with: user)
            BookingDetails.sharedInstance.paymentMethod = PaymentMethod(type: .storedCompanyBB, method: nil)
        }

        let router = UpsellsRouter()

        let viewController = router.nextViewController(with: BookingDetails.sharedInstance)
        XCTAssertTrue(viewController is FormBusinessCardQuestionsController, "actual viewController is \(String(describing: viewController)), shouldShowEmployeeQuestionsForOpera=\(BookingDetails.sharedInstance.shouldShowEmployeeQuestionsForOpera), businessQuestionsCount=\(String(describing: UserSessionManager.sharedInstance.currentUser?.company?.businessCardQuestions?.count))")
    }

    func testUpsellsShouldRouteToReviewAndBook() {

        UserSessionManager.sharedInstance.piUserLoggedOut()

        BookingDetails.sharedInstance.hotel = hotel
        BookingDetails.sharedInstance.hotel?.update(paymentProvider: .cccp)
        BookingDetails.sharedInstance.rate = BookingDetails.sharedInstance.hotel?.rates.first

        if let user = user {
            user.company = company
            UserSessionManager.sharedInstance.piLoggedIn(with: user)
        }

        let router = UpsellsRouter()

        let viewController = router.nextViewController(with: BookingDetails.sharedInstance)
        XCTAssert(viewController is ReviewAndBookViewController)
    }
}
