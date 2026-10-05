//
//  ChangePasswordRouterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 18/02/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import XCTest

@testable import PremierInn

private class MockChangePasswordCompletion: ChangePasswordCompletionInput {

    var passwordUpdatedDidCall = false

    func passwordWasUpdated() {

        passwordUpdatedDidCall = true
    }
}

class ChangePasswordRouterTests: XCTestCase {

    fileprivate var mockCompletionHandler: MockChangePasswordCompletion?
    fileprivate var router: ChangePasswordRouter?

    override func setUp() {
        super.setUp()

        mockCompletionHandler = MockChangePasswordCompletion()
        router = ChangePasswordRouter()

        router?.changePasswordCompletionInput = mockCompletionHandler
    }

    override func tearDown() {
        super.tearDown()
    }

    func testBuild() {

        _ = ChangePasswordRouter.buildController(asBusinessLogin: false)
    }

    func testPasswordDidChange() {

        router?.passwordDidChange()

        XCTAssert(mockCompletionHandler?.passwordUpdatedDidCall == true)
    }
}
