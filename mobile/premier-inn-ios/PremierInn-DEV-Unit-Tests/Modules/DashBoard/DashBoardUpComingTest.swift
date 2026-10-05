//
//  DashBoardUpCommingTest.swift
//  PremierInnTests
//
//  Created by Simon Antoine on 22/01/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import XCTest

@testable import PremierInn

class DashBoardUpComingTest: XCTestCase {
    
    private var VC: DashboardUpcomingBookingView!
    private var viewController: DashboardViewController!
    
    override func setUp() {
        viewController = DashboardViewController()
        VC = UIView.fromNib(nibName: String(describing: DashboardUpcomingBookingView.self))
//        analytics = MockAnalyticsManager()
//        viewController.analytics = analytics
    }

    override func tearDown() {
        
    }

    func testButtonDashBoard_noBusiness() {
        let dash = DashboardUpcomingBooking(
            identifier: "1234",
            hotelCode: "12345",
            imageUrl: nil,
            hotel: "5678",
            arrivalDate: Date(timeIntervalSinceReferenceDate: 615816789.0),
            checkOutDate: Date(timeIntervalSinceReferenceDate: 615916789.0),
            guestsNumber: 1,
            roomNumber: 1,
            roomType: "hello",
            isCheckedIn: false,
            isBusinessTrip: false,
            actions: [DashboardUpcomingBookingAction(type: .BOOKING_DETAILS, title: "Booking Details"),
                      DashboardUpcomingBookingAction(type: .CIOL, title: "CIOL"),
                      DashboardUpcomingBookingAction(type: .DIRECTIONS, title: "Directions"),
                      DashboardUpcomingBookingAction(type: .UPSELLS, title: "Add Meals")
                     ]
        )

        VC.update(with: dash)

        var array = [String]()
        for case let viewAction as PaymentOptionActionView in VC.actionsView.arrangedSubviews{
            array.append(viewAction.action.text!)
        }

        XCTAssertEqual(array[safe: 0], "Booking Details")
        XCTAssertEqual(array[safe: 1], "CIOL")
        XCTAssertEqual(array[safe: 2], "Directions")
        XCTAssertEqual(array[safe: 3], "Add Meals")
        XCTAssertEqual(array.count, 4)
    }
    
    func testButtonDashBoard_IsBusiness() {
        let dash = DashboardUpcomingBooking.init(identifier: "cdcd",
                                                 hotelCode: "12345",
                                                 imageUrl: nil,
                                                 hotel: "abab",
                                                 arrivalDate: Date(timeIntervalSinceReferenceDate: 615816789.0),
                                                 checkOutDate: Date(timeIntervalSinceReferenceDate: 615916789.0),
                                                 guestsNumber: 1,
                                                 roomNumber: 1,
                                                 roomType: "hello",
                                                 isCheckedIn: false,
                                                 isBusinessTrip: true,
                                                 actions: [DashboardUpcomingBookingAction.init(type: .BOOKING_DETAILS,
                                                                                               title: "Booking Details"),DashboardUpcomingBookingAction.init(type: .CIOL,
                                                                                                                                                             title: "CIOL"),
                                                                                                                         DashboardUpcomingBookingAction.init(type: .DIRECTIONS,
                                                                                                                                                             title: "Directions"),
                                                                                                                         DashboardUpcomingBookingAction.init(type: .UPSELLS,
                                                                                                                                                             title: "upsells")])
        VC.update(with: dash)
        
        var array = [String]()
        for case let viewAction as PaymentOptionActionView in VC.actionsView.arrangedSubviews{
            array.append(viewAction.action.text!)
        }
        
        XCTAssertEqual(array[0], "Booking Details")
        XCTAssertEqual(array[1], "Directions")
        XCTAssertEqual(array.count, 2)
    }
}
