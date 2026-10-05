//
//  GuestDetailsViewTests.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 20.02.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Testing
import SimpleNetwork
@testable import PremierInn

class MockGuestDetailsPresenter: GuestDetailsPresenterBlueprint {
    var customAnalyticsParameters: PIDictionary? = [:]
    var didCallViewReady = false
    var didCallButtonTap = false
    var didCallEdit = false

    func viewIsReady() {
        didCallViewReady = true
    }
    
    func edit(with index: Int) {
        didCallEdit = true
    }
    
    func handleContinueButtonTap() {
        didCallButtonTap = true
    }
}

class GuestDetailsViewTests {

    var sut: GuestDetailsVC!
    var mockPresenter: MockGuestDetailsPresenter!


    init() throws {
        mockPresenter = MockGuestDetailsPresenter()
        sut = GuestDetailsVC()
        sut.presenter = mockPresenter
    }

    @MainActor
    @Test func testViewReady() async throws {
        sut.viewDidLoad()
        #expect(mockPresenter.didCallViewReady)
    }

    @Test func testContinue() throws {
        sut.buttonDidTap()
        #expect(mockPresenter.didCallButtonTap)
    }

    @Test func testEdit() throws {
        sut.edit(index: 0)
        #expect(mockPresenter.didCallEdit)
    }
}
