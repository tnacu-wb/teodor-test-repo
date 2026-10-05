//
//  FullScreenImageViewerPresenterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 13/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import XCTest

@testable import PremierInn

private class MockView: FullScreenImageViewerViewProtocol {

    var updateDidCall = false

    func update(with viewModel: FullScreenImageViewerViewModel) {

        updateDidCall = true
    }
}

private class MockInteractor: FullScreenImageViewerInteractorProtocol {

    var getViewModelDidCall = false
    var trackDidCall = false

    struct ViewModel: FullScreenImageViewerViewModel {
        var carouselRoundelDesigns: [RoundelDesign]
        let startIndex: Int
    }

    var viewModel: FullScreenImageViewerViewModel? {

        getViewModelDidCall = true

        return ViewModel(
            carouselRoundelDesigns: [],
            startIndex: 0
        )
    }

    func trackState() {

        trackDidCall = true
    }
}

private class MockRouter: FullScreenImageViewerRouterProtocol {

    var closeDidCall = false
    var changeDidCall = false

    func fullscreenImageSetCloseButtonDidTap() {

        closeDidCall = true
    }

    func fullscreenImageSetDidChangePicture(atIndex index: Int) {

        changeDidCall = true
    }
}

class FullScreenImageViewerPresenterTests: XCTestCase {

    fileprivate var mockView: MockView?
    fileprivate var mockInteractor: MockInteractor?
    fileprivate var mockRouter: MockRouter?

    var presenter: FullScreenImageViewerPresenter?

    override func setUp() {
        super.setUp()

        mockView = MockView()
        mockInteractor = MockInteractor()
        mockRouter = MockRouter()

        presenter = FullScreenImageViewerPresenter()
        presenter?.view = mockView
        presenter?.interactor = mockInteractor
        presenter?.router = mockRouter
    }

    override func tearDown() {
        super.tearDown()
    }

    func testViewIsReady() {

        presenter?.viewIsReady()

        XCTAssert(mockInteractor?.trackDidCall == true)
        XCTAssert(mockInteractor?.getViewModelDidCall == true)

        XCTAssert(mockView?.updateDidCall == true)
    }

    func testCloseDidTap() {

        presenter?.closeButtonDidTap()

        XCTAssert(mockRouter?.closeDidCall == true)
    }

    func testChangedIndex() {

        presenter?.imageSetDidChangePicture(atIndex: 1)

        XCTAssert(mockRouter?.changeDidCall == true)
    }
}
