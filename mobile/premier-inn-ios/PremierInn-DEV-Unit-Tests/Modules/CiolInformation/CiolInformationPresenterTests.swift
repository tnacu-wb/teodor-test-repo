//
//  CiolInformationPresenterTests.swift
//  PremierInnTests
//
//  Created by Muresan, Andreea (Cognizant) on 11.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

class CiolInformationPresenterTests: XCTestCase {
    var presenter: CiolInformationPresenter!
    var mockView: MockCiolInformationView!
    var mockInteractor: MockCiolInformationInteractor!
    var mockRouter: MockCiolInformationRouter!
    var mockAnalytics: MockAnalyticsManager!

    override func setUp() {
        super.setUp()

        mockAnalytics = MockAnalyticsManager()
        mockView = MockCiolInformationView()
        mockInteractor = MockCiolInformationInteractor()
        mockRouter = MockCiolInformationRouter()
        
        presenter = CiolInformationPresenter(
            analytics: mockAnalytics
        )
        presenter.view = mockView
        presenter.interactor = mockInteractor
        presenter.router = mockRouter
    }
    
    override func tearDown() {
        presenter = nil
        mockView = nil
        mockInteractor = nil
        mockRouter = nil
        super.tearDown()
    }
    
    func testViewIsReady() {
        presenter.viewIsReady()
        
        XCTAssertTrue(mockView.isDataReloaded)
    }

    func testTrackAnalyticsWhenActionButtonTapped() {
        presenter.logActionAnalytics()
        let action = mockAnalytics.actions.first
        let userInfo = mockAnalytics.userInfos.first

        XCTAssertEqual(action, PIAnalytics.Action.ciolBottomSheetActionName)
        XCTAssertEqual(userInfo?[PIAnalytics.Keys.checkInOnlineBottomSheetClick], "true")
    }
}

class MockCiolInformationView: CiolInformationViewProtocol {
    var customAnalyticsParameters: PIDictionary?
    
    var isDataReloaded = false

    func reloadData(with viewModel: CiolInformationModel) {
        isDataReloaded = true
    }

}

class MockCiolInformationInteractor: CiolInformationInteractorProtocol {
    var viewModel = CiolInformationModel(image: nil,
                                         title: "Title",
                                         subtitle: "This is a subtitle",
                                         showSubtitle: true,
                                         description: .init(type: .string("Description")),
                                         showCTA: true,
                                         ctaTitle: "Understood",
                                         delegate: MockCiolInformationDelegate())
}

class MockCiolInformationRouter: CiolInformationRouterProtocol {
    var icCloseCalled = false
    var isShowQrCodeCalled = false
    
    func close() {
        icCloseCalled = true
    }
    
    func showQrCode() {
        isShowQrCodeCalled = true
    }
}
