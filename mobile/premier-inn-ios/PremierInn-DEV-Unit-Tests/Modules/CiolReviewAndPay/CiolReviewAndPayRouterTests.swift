//
//  CiolReviewAndPayRouterTests.swift
//  PremierInn
//
//  Created by Velesca, Florin (Cognizant) on 11.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private final class MockNavigationController: UINavigationController {
    
    var pushDidCall = false
    
    override func pushViewController(_ viewController: UIViewController, animated: Bool) {
        pushDidCall = true
    }
}

private final class MockViewController: UIViewController {
}

final class CiolReviewAndPayRouterTests: XCTestCase {
    private var mockNavigationController: MockNavigationController?
    private var mockViewController: MockViewController?
    
    var router: CiolReviewAndPayRouter?

    var stay: Stay {
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
        return try! Stay(dictionary: dictionary)
    }

    override func setUp() {
        super.setUp()
        
        mockViewController = MockViewController()
        guard let viewController = mockViewController else { return }
        
        mockNavigationController = MockNavigationController(rootViewController: viewController)
        
        router = CiolReviewAndPayRouter(ciolFlow: .bookingConfirmation)
        router?.viewController = mockViewController
    }
    
    override func tearDown() {
        router = nil
        mockNavigationController = nil
        mockViewController = nil
        super.tearDown()
    }
    
    func testNavigateToConfirmation() {
        router?.navigateToConfirmationScreen(ciolConfirmationDetails: CiolConfirmationDetails(bookerFirstName: "",
                                                                                              hotelBrand: .premierInn,
                                                                                              ciolStartFlow: .myBookings,
                                                                                              hotelImage: nil,
                                                                                              analyticsInfo: [:],
                                                                                              stay: stay))
        XCTAssert(mockNavigationController?.pushDidCall == true, "The Confirmation screen should be pushed")
    }
    
}
