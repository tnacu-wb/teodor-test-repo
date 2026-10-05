//
//  AboutInteractorTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 21/02/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import XCTest
import SwiftUI
@testable import PremierInn

class AboutRouterTests: XCTestCase {

    override func setUp() {
        super.setUp()
    }

    override func tearDown() {

        super.tearDown()
    }

    func testBuild() {
        let viewController = AboutRouter.build()
        XCTAssert(viewController is UIHostingController<AboutView>)
    }
}
