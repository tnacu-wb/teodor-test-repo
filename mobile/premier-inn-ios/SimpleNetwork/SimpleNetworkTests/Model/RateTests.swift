//
//  RateTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 23/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class RateTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
    func testRates() {
        
        let rate = Rate(dictionary: ["classification": "F", "description": "A classy rate", "totalCost": ["amount": "191.50", "currency": "GBP"]])
        XCTAssertEqual(rate.totalCost.amount, 191.50)
        XCTAssertEqual(rate.classification, "F")
        
        let rate2 = Rate(dictionary: ["classification": "A", "description": "A sleezy rate", "totalCost": ["amount": "191.52", "currency": "EUR"]])
        XCTAssertEqual(rate2.totalCost.amount, 191.52)
        XCTAssertEqual(rate2.classification, "A")
        
        let rate3 = Rate(dictionary: ["test": "test"])
        XCTAssertEqual(rate3.totalCost.amount, 0)
        XCTAssertEqual(rate3.classification, nil)
        
        let rate4 = Rate(dictionary: ["classification": "ZZZ", "totalCost": ["amount": "191.52", "currency": "EUR"]])
        XCTAssertEqual(rate4.totalCost.amount, 191.52)
        XCTAssertEqual(rate4.classification, "ZZZ")
    }
    
    // The == and < operators have been removed because we previously had a customer order for the rates on the front end.
    
    //    func testRateEqualComparison() {
    //
    //        // name, cellCode, totalCost decide if rates are equal
    //        let rate1 = Rate(dictionary: ["classification": "F", "name": "name1", "code": "Code", "description": "A classy rate", "totalCost": ["amount": "191.50", "currency": "GBP"], "cityTax": ["amount": "191.50", "currency": "GBP"]])
    //
    //        var rate2 = Rate(dictionary: ["classification": "F", "name": "name1", "code": "Code", "description": "A classy rate", "totalCost": ["amount": "191.50", "currency": "GBP"], "cityTax": ["amount": "191.50", "currency": "GBP"]])
    //
    //        XCTAssertTrue(rate1 == rate2)
    //
    //        // different names
    //        rate2 = Rate(dictionary: ["classification": "F", "name": "name2", "code": "Code", "description": "A classy rate", "totalCost": ["amount": "191.50", "currency": "GBP"], "cityTax": ["amount": "191.50", "currency": "GBP"]])
    //        XCTAssertFalse(rate1 == rate2)
    //
    //        // different cellCodes
    //        rate2 = Rate(dictionary: ["classification": "F", "name": "name1", "code": "Code2", "description": "A classy rate", "totalCost": ["amount": "191.50", "currency": "GBP"], "cityTax": ["amount": "191.50", "currency": "GBP"]])
    //        XCTAssertFalse(rate1 == rate2)
    //
    //        // different totalCosts
    //        rate2 = Rate(dictionary: ["classification": "F", "name": "name1", "code": "Code", "description": "A classy rate", "totalCost": ["amount": "200.50", "currency": "GBP"], "cityTax": ["amount": "191.50", "currency": "GBP"]])
    //        XCTAssertFalse(rate1 == rate2)
    //    }
    
    //    func testRateComparison() {
    //
    //        // Business Flex is highest, Non-Flex is lowest, everything in the middle is sorted by totalCost
    //        let nonFlexRate = Rate(dictionary: ["classification": "F", "name": "Non-Flex", "code": "Code", "description": "non-flex rate", "totalCost": ["amount": "200.50", "currency": "GBP"], "cityTax": ["amount": "191.50", "currency": "GBP"]])
    //
    //        let randomRate1 = Rate(dictionary: ["classification": "A", "name": "random rate1", "code": "Code", "description": "A classy rate", "totalCost": ["amount": "191.50", "currency": "GBP"], "cityTax": ["amount": "191.50", "currency": "GBP"]])
    //
    //        // randomRate is lowest
    //        XCTAssertTrue(nonFlexRate > randomRate1)
    //
    //        let businessFlexRate = Rate(dictionary: ["classification": "A", "name": "Business Flex", "code": "Code", "description": "business flex rate", "totalCost": ["amount": "50.50", "currency": "GBP"], "cityTax": ["amount": "191.50", "currency": "GBP"]])
    //
    //        XCTAssertTrue(nonFlexRate > businessFlexRate)
    //
    //        let randomRate2 = Rate(dictionary: ["classification": "A", "name": "random rate2", "code": "Code", "description": "A classy rate", "totalCost": ["amount": "50.50", "currency": "GBP"], "cityTax": ["amount": "191.50", "currency": "GBP"]])
    //
    //        // everything else is compared by totalCost
    //        XCTAssertTrue(randomRate1 > randomRate2)
    //    }
}
