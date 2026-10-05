//
//  CiolConfirmationPresenterTests.swift
//  PremierInn
//
//  Created by Velesca, Florin (Cognizant) on 11.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

class CiolConfirmationPresenterTests: XCTestCase {

    var presenter: CiolConfirmationPresenter!
    var mockView: MockCiolConfirmationView!
    var mockInteractor: MockCiolConfirmationInteractor!

    override func setUp() {
        super.setUp()
        mockView = MockCiolConfirmationView()
        mockInteractor = MockCiolConfirmationInteractor()
        presenter = CiolConfirmationPresenter()
        presenter.view = mockView
        presenter.interactor = mockInteractor
    }

    override func tearDown() {
        presenter = nil
        mockView = nil
        mockInteractor = nil
        super.tearDown()
    }

    func testViewDidLoad_CallsFetchConfirmationDetails() {
        // Act
        presenter.viewIsReady()

        // Assert
        XCTAssertTrue(mockView.isDisplayConfirmationCalled)
    }
}

// Mocks
class MockCiolConfirmationView: CiolConfirmationViewProtocol {
    var customAnalyticsParameters: PIDictionary? = [:]
    
    var isShowErrorCalled = false
    var isDisplayConfirmationCalled = false
    var isToggleIndicatorCalled = false
    var displayedMessage: String?

    func displayConfirmation(details: CiolConfirmationDetails) {
        isDisplayConfirmationCalled = true
    }
    
    func toggleLoadingIndicator(should show: Bool) {
        isToggleIndicatorCalled = true
    }
    
    func showError(title: String, message: String?) {
        isShowErrorCalled = true
    }
}

class MockCiolConfirmationInteractor: CiolConfirmationInteractorProtocol {
    var roomKeyInstructionsModel: InstructionsViewModel?
    
    var customAnalyticsParameters: PIDictionary? = [:]
    
    var categoryLabelsCalled = false
    var categoryLabelsShouldError = false
    
    let stay = try! Stay(dictionary: ["hotelCode": "AVC241",
                                      "hotelName": "London",
                                      "identifier": "MVC",
                                      "arrivalDate": "Mon, 12",
                                      "checkOutDate": "Tue, 13"])
    
    lazy var ciolConfirmationDetails = CiolConfirmationDetails(bookerFirstName: "Test",
                                                               hotelBrand: .premierInn,
                                                               ciolStartFlow: .myBookings,
                                                               hotelImage: nil,
                                                               analyticsInfo: [:],
                                                               stay: stay)
    
    func refreshStays(completion: @escaping (Bool?) -> Void) {
        
    }

	func callBookingConfirmation() {

	}
}
