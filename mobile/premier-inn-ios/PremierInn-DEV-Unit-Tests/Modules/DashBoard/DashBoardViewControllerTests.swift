//
//  DashBoardPresenterTests.swift
//  PremierInnTests
//
//  Created by Simon Antoine on 15/01/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import XCTest

import SimpleNetwork
@testable import PremierInn

class DashBoardViewControllerTests: XCTestCase {
    
    private var viewController: DashboardViewController!

    override func setUp() {
        
        viewController = DashboardViewController()
    }
    
    override func tearDown() {
        
        viewController = nil

        super.tearDown()
    }
    
    func test_Analytics_DashBoard_Displayed() {
        UserSessionManager.sharedInstance.piUserLoggedOut()

        XCTAssertEqual(viewController.screenName, "iOS:PI:UK: Home Dashboard")
        XCTAssertEqual(viewController.screenType, "iOS: Home")
//        XCTAssertNotNil(viewController.customParameters)
    }
    
    func test_Analytics_DashBoard_Hidden() {
//        XCTAssertNil(viewController.customParameters)
    }
}
