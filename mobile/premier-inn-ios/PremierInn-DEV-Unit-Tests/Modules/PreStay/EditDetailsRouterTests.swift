//
//  EditDetailsRouterTests.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 25.09.2024.
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

private class MockEditDetailsViewController: EditDetailsViewController {
   var presentViewControllerDidCall = false
   
   override func present(_ viewControllerToPresent: UIViewController, animated flag: Bool, completion: (() -> Void)? = nil) {
       presentViewControllerDidCall = true
   }
}

class EditDetailsRouterTests: XCTestCase {
    
    private var mockNavigationController: MockNavigationController?
    private var mockViewController: MockEditDetailsViewController?
    
    var router: EditDetailsRouter?
    
    override func setUp() {
        super.setUp()
        
        mockViewController = MockEditDetailsViewController()
        guard let viewController = mockViewController else { return }
        
        mockNavigationController = MockNavigationController(rootViewController: viewController)
        
        router = EditDetailsRouter()
        router?.viewController = mockViewController
    }
    
    override func tearDown() {
        router = nil
        mockNavigationController = nil
        mockViewController = nil
        super.tearDown()
    }
    
    func testContinueToNextStep_PopEditDetailsView() {
        // Act
        router?.goBackToPreStayView()
        
        // Assert
        XCTAssert(mockNavigationController?.popDidCall == true, "continueToNextStep should trigger a pop on the navigationController")
    }
}
