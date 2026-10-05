//
//  PreStayRouterTests.swift
//  PremierInnTests
//
//  Created by Florin Velesca on 30.08.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockNavigationController: UINavigationController {
    
    var pushDidCall = false
    
    override func pushViewController(_ viewController: UIViewController, animated: Bool) {
        
        pushDidCall = true
    }
}

private class MockViewController: UIViewController {
    
    var presentViewControllerDidCall = false
    
    override func present(_ viewControllerToPresent: UIViewController, animated flag: Bool, completion: (() -> Void)? = nil) {
        
        presentViewControllerDidCall = true
    }
}

class PreStayRouterTests: XCTestCase {
    
    private var mockNavigationController: MockNavigationController?
    private var mockViewController: MockViewController?
    
    var router: PreStayRouter?

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
        
        router = PreStayRouter()
        router?.viewController = mockViewController
    }
    
    override func tearDown() {
        router = nil
        mockNavigationController = nil
        mockViewController = nil
        super.tearDown()
    }
    
    func testContinueToNextStep_PushesCiolUpsellViewController() {
        // Act
        let priceBreakdown = PreStayInteractor.CIOLPriceBreakdownViewModel(ctaTitle: PILocalizedString("Continue"), totalValue: "", items: []) as CIOLPriceBreakdownViewModelProtocol
        let bookingSummary = PreStayInteractor.BookingSummaryCIOLViewModel(image: nil,
                                                                           hotelName: nil,
                                                                           duration: nil,
                                                                           summary: nil)

        let inputParams = CiolUpsellInputParams(availableUpsells: [], bookedUpsells: [], rooms: [], hasChildren: false, isMultiRoom: false, priceBreakdownViewModel: priceBreakdown, bookingSummaryViewModel: bookingSummary, flow: .bookingConfirmation, leadBookerFirstName: "", hotelBrand: nil, address: nil, bookingReference: "", stay: stay, reservationID: "", hotelID: "", arrivalDate: Date(), departureDate: Date(), analyticsParams: PIDictionary())

        router?.goToUpsell(ciolUpsellInputParams: inputParams, prestayDelegate: MockCiolUpsellInteractor())
        // Assert
        XCTAssert(mockNavigationController?.pushDidCall == true, "continueToNextStep should trigger a push on the navigationController")
    }
}
