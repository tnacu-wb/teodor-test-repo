//
//  CiolInformationRouterTests.swift
//  PremierInnTests
//
//  Created by Muresan, Andreea (Cognizant) on 11.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

private class MockNavigationController: UINavigationController {
    
    var dismissDidCall = false
    
    override func dismiss(animated flag: Bool, completion: (() -> Void)? = nil) {
        dismissDidCall = true
    }
}

private class MockViewController: UIViewController {
    
}

class CiolInformationRouterTests: XCTestCase {
    private var mockNavigationController: MockNavigationController?
    private var mockViewController: MockViewController?
    
    var router: CiolInformationRouter?
    
    override func setUp() {
        super.setUp()
        
        mockViewController = MockViewController()
        guard let viewController = mockViewController else { return }
        
        mockNavigationController = MockNavigationController(rootViewController: viewController)
        
        router = CiolInformationRouter(completion: nil)
        router?.viewController = mockViewController
    }
    
    override func tearDown() {
        router = nil
        mockNavigationController = nil
        mockViewController = nil
        super.tearDown()
    }
    
    func tesClose() {
        router?.close()
        XCTAssert(mockNavigationController?.dismissDidCall == true, "The Ciol Information View Controller should be dismissed")
    }
    
}
