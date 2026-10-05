//
//  AmendDatesRouterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 20/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockAmendDatesRouterDelegate: AmendDatesRouterDelegate {

    var finishedAmendDidCall = false

    func finishedAmend() {

        finishedAmendDidCall = true
    }
}

class AmendDatesRouterTests: XCTestCase {

    private var router: AmendDatesRouter?

    private var delegate: MockAmendDatesRouterDelegate?

    override func setUp() {

        router = AmendDatesRouter()

        delegate = MockAmendDatesRouterDelegate()
        router?.delegate = delegate
    }

    func testFinishedAmend() {

        router?.finishedAmend()

        XCTAssert(delegate?.finishedAmendDidCall == true)
    }
}
