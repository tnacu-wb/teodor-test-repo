//
//  CiolUpsellRouterTests.swift
//  PremierInnTests
//
//  Created by Florin Velesca on 30.08.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
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

class CiolUpsellRouterTests: XCTestCase {
    
    private var mockNavigationController: MockNavigationController?
    private var mockViewController: MockViewController?
    
    var router: CiolUpsellRouter?
    
    override func setUp() {
        super.setUp()
        
        mockViewController = MockViewController()
        guard let viewController = mockViewController else { return }
        
        mockNavigationController = MockNavigationController(rootViewController: viewController)
        
        router = CiolUpsellRouter()
        router?.viewController = mockViewController
    }
    
    override func tearDown() {
        router = nil
        mockNavigationController = nil
        mockViewController = nil
        super.tearDown()
    }
    
    func testContinueToNextStep_PushesViewController() {
        // Act
        router?.showPayment()
        
        // Assert
        XCTAssertTrue(mockNavigationController?.pushDidCall == true, "showPayment should trigger a push on the navigationController")
    }
}
