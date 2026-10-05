//
//  HotelDetailsViewTests.swift
//  PremierInn
//
//  Automatically Created by Ophion
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockPresenter: HotelDetailsPresenterProtocol {
  
    var screenName: String { return "" }
    var screenType: String { return "" }
    var shouldTrackScreen: Bool { return false }

    var hotelDetailsViewModel: HotelDetailsViewModel? { return nil }
    
    var continueViewCanShowAtBottom: Bool { return false }
    
    func hotelUpdated() {}
    func hotelUpdateFailed(with error: Error) {}
    func datesChanged(withNewArrivalDate newArrivalDate: Date, andNumberOfNights numberOfNights: Int) {}
    func criteriaWasUpdated(to newCriteria: Criteria) {}
    func tripAdvisorRatingTapped() {}
    
    func closeCurrentOverlay() {}
    func showHoldBookingError() {}
    func showLoadingIndicator() {}
    func hideLoadingIndicator() {}
}

class HotelDetailsViewTests: XCTestCase {
    
    private var view: HotelDetailsViewController!
    private var presenter: MockPresenter!
    
    override func setUp() {
        
        //        presenter = MockPresenter()
        //
        //        view = HotelDetailsViewController()
        //        view.eventHandler = presenter
    }
    
    override func tearDown() {
        
        //        presenter = nil
        //        view = nil
        
        super.tearDown()
    }
}

