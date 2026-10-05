//
//  FullScreenImageViewerRouterAndInteractorTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 13/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import XCTest

@testable import PremierInn

private class MockRouterDelegate: FullScreenImageViewerRouterDelegate {

    var closeDidCall = false
    var changeDidCall = false

    func fullscreenImageSetCloseButtonDidTap(viewController: UIViewController) {

        closeDidCall = true
    }

    func fullscreenImageSetDidChangePicture(atIndex index: Int) {

        changeDidCall = true
    }
}

class FullScreenImageViewerRouterTests: XCTestCase {

    fileprivate var mockDelegate: MockRouterDelegate?

    var router: FullScreenImageViewerRouter?

    private let testViewController: UIViewController = UIViewController()

    override func setUp() {
        super.setUp()

        mockDelegate = MockRouterDelegate()

        router = FullScreenImageViewerRouter(with: mockDelegate)
    }

    override func tearDown() {
        super.tearDown()
    }

    func testCloseNoView() {

        router?.fullscreenImageSetCloseButtonDidTap()

        XCTAssert(mockDelegate?.closeDidCall == false)
    }

    func testCloseView() {

        router?.viewController = testViewController
        router?.fullscreenImageSetCloseButtonDidTap()

        XCTAssert(mockDelegate?.closeDidCall == true)
    }

    func testChange() {

        router?.fullscreenImageSetDidChangePicture(atIndex: 1)

        XCTAssert(mockDelegate?.changeDidCall == true)
    }
}

class FullScreenImageViewerInteractorTests: XCTestCase {

    var interactor: FullScreenImageViewerInteractor?

    override func setUp() {
        super.setUp()

        guard let imageUrl = URL(string: "https://i.ytimg.com/vi/AHojzFlV4-A/maxresdefault.jpg") else { return }
        interactor = FullScreenImageViewerInteractor(with: [RoundelDesign(url: imageUrl, backgroundColor: .white, foregroundColor: .white, text: "", shouldEmbolden: false)], and: 0)
    }

    override func tearDown() {
        super.tearDown()
    }

    func testViewModel() {

        let viewModel = interactor?.viewModel

        XCTAssertNotNil(viewModel)

        XCTAssert(viewModel?.carouselRoundelDesigns.count == 1)
        XCTAssert(viewModel?.startIndex == 0)
    }
}
