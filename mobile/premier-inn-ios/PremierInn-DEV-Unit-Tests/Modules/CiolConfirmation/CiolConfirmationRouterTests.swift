//
//  CiolConfirmationRouterTests.swift
//  PremierInn
//
//  Created by Velesca, Florin (Cognizant) on 11.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

private class MockNavigationController: UINavigationController {
    
    var popToRootDidCall = false
    
    override func popToRootViewController(animated: Bool) -> [UIViewController]? {
        popToRootDidCall = true
        return []
    }
}

 private class MockViewController: UIViewController {
    
}

class CiolConfirmationRouterTests: XCTestCase {

    private var mockNavigationController: MockNavigationController!
    private var mockViewController: MockViewController!
    
    var router: CiolConfirmationRouter?
    
    override func setUp() {
        super.setUp()

        mockViewController = MockViewController()
        guard let viewController = mockViewController else { return }
        
        mockNavigationController = MockNavigationController(rootViewController: viewController)
        
        router = CiolConfirmationRouter(ciolFlow: .myBookings)
        router?.viewController = mockViewController
    }

    override func tearDown() {
        router = nil
        mockNavigationController = nil
        mockViewController = nil
        super.tearDown()
    }

    func testNavigateToHome_PopToRootViewController() {
        // Act
        router?.navigateToMyBookings()

        // Assert
        XCTAssertTrue(mockNavigationController.popToRootDidCall, "navigateToMyBookings should trigger pop to root")
    }
}
