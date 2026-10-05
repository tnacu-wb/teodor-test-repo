//
//  RoomsUpsellRouterTests.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 15.11.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

private class MockNavigationController: UINavigationController {
    var popDidCall = false

    override func popViewController(animated: Bool) -> UIViewController? {
        popDidCall = true
        return UIViewController()
    }
}

private class MockRoomsUpSellViewController: RoomsUpsellViewController { }

class RoomsUpsellRouterTests: XCTestCase {
    private var mockNavigationController: MockNavigationController!
    private var mockViewController: MockRoomsUpSellViewController!

    var router: RoomsUpsellRouter?

    override func setUp() {
        super.setUp()

        mockViewController = MockRoomsUpSellViewController()
        guard let viewController = mockViewController else { return }

        mockNavigationController = MockNavigationController(rootViewController: viewController)

        router = RoomsUpsellRouter()
        router?.viewController = mockViewController
    }

    override func tearDown() {
        router = nil
        mockNavigationController = nil
        mockViewController = nil
        super.tearDown()
    }

    func testPopBack_DidCall() {
        // Act
        router?.popController()
        // Assert
        XCTAssertTrue(mockNavigationController?.popDidCall ?? false, "popBack should trigger a pop on the navigationController")
    }
}
