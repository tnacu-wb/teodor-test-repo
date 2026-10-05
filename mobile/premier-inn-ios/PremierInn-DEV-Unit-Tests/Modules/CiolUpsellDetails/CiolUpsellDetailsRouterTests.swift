//
//  CiolUpsellDetailsRouterTests.swift
//  PremierInnTests
//
//  Created by Oltean Vasile Bogdan on 24.09.2024.
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

class CiolUpsellDetailsRouterTests: XCTestCase {
    
    private var mockNavigationController: MockNavigationController?
    private var mockViewController: MockViewController?
    
    var router: CiolUpsellDetailsRouter?
    
    override func setUp() {
        super.setUp()
        
        mockViewController = MockViewController()
        guard let viewController = mockViewController else { return }
        
        mockNavigationController = MockNavigationController(rootViewController: viewController)
        
        router = CiolUpsellDetailsRouter()
        router?.viewController = mockViewController
    }
    
    override func tearDown() {
        router = nil
        mockNavigationController = nil
        mockViewController = nil
        super.tearDown()
    }
    
    func testShowMenuOrAllergyInfo_PresentsViewController() {
        // Act
        router?.showMenuOrAllergyInfo(url: "http://www.google.com")
        
        // Assert
        XCTAssertTrue(mockViewController?.presentViewControllerDidCall == true, "showMenuOrAllergyInfo should trigger a present")
    }
}
