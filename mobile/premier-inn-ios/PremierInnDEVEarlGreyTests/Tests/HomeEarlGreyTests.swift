//
//  HomeEarlGreyTests.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 16/04/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import EarlGrey

class HomeEarlGreyTests: BaseEarlGreyTests {

    override func setUp() {
        super.setUp()
    }

    override func tearDown() {
        super.tearDown()
    }

    func testHomePageElements() { homePage.checkPageElementsExist() }
}
