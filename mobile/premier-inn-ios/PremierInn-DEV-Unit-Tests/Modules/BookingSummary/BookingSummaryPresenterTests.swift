//
//  BookingSummaryPresenterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Marcello Mascia on 04/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockView: BookingSummaryViewProtocol {
    
    var setupDidCall = false
    var showViewModelDidCall = false
    var showErrorDidCall = false
    var toggleActivityDidCall = false
    var toggleActivityCount = 0
    
    func setup(withTitle: String) {
        
        setupDidCall = true
    }
    
    func showViewModel(with: BookingSummaryViewModel) {
        
        showViewModelDidCall = true
    }
    
    func showError(title: String, message: String?) {
        
        showErrorDidCall = true
    }
    
    func toggleActivity(isOn: Bool) {
        
        toggleActivityDidCall = true
        toggleActivityCount += 1
    }
}

private class MockInteractor: BookingSummaryInteractorProtocol {
    
    var loadDidCall = false
    var shouldShowError = false
    
    var mealModels: [BookingSummaryMealViewModel] {
        
        return []
    }
    
    var wifiModels: [BookingSummaryWifiViewModel] {
        
        return []
    }
    
    var extrasModels: [BookingSummaryExtrasViewModel] {
        return []
    }
    
    func load(completion: @escaping (Reservation?, BookingDetails?, Hotel?, Error?) -> Void) {
        
        loadDidCall = true
        
        if shouldShowError {
            return completion(nil, nil, nil, nil)
        }
        
        let hotel = try! Hotel(dictionary: ["limitedAvailability": true, "hotelInfo": ["name": "Hotel name", "code": "FAKECODE", "brand": "PI", "address": ["postcode": "POST CODE", "line1": ""]]])
        
        completion(nil, nil, hotel, nil)
    }
}

class BookingSummaryPresenterTests: XCTestCase {
    
    private var view: MockView!
    private var interactor: MockInteractor!
    private var presenter: BookingSummaryPresenter!
    
    override func setUp() {
        
        view = MockView()
        interactor = MockInteractor()
        
        presenter = BookingSummaryPresenter()
        presenter.view = view
        presenter.interactor = interactor
    }
    
    override func tearDown() {
        
        presenter = nil
        view = nil
        interactor = nil
        
        super.tearDown()
    }
    
    func testViewIsReady() {
        
        presenter.viewIsReady()
        XCTAssertTrue(view.setupDidCall)
        XCTAssertTrue(view.toggleActivityDidCall)
        XCTAssertEqual(view.toggleActivityCount, 2)
        XCTAssertTrue(interactor.loadDidCall)
        XCTAssertFalse(view.showErrorDidCall)
        XCTAssertTrue(view.showViewModelDidCall)
    }
    
    func testViewIsReady_Error() {
        
        interactor.shouldShowError = true
        presenter.viewIsReady()
        
        XCTAssertTrue(view.setupDidCall)
        XCTAssertTrue(view.toggleActivityDidCall)
        XCTAssertEqual(view.toggleActivityCount, 2)
        XCTAssertTrue(interactor.loadDidCall)
        XCTAssertTrue(view.showErrorDidCall)
        XCTAssertFalse(view.showViewModelDidCall)
    }
}
