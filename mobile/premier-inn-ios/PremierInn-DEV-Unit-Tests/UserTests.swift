//
//  UserTests.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 13.11.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

class UserTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
    func test_userIsMissingNationality_FALSE() {
        
        let user = try! User(title: "Mr",
                             firstName: "Test",
                             lastName: "Test")
        user.country = Country(code: "GB",
                               name: "UK",
                               isoCode: "GB",
                               dialingCode: "40",
                               flagImage: "",
                               passportRequired: false,
                               nationality: "British")
        XCTAssertFalse(user.isMissingNationality)
    }
    
    func test_userIsMissingNationality_TRUE() {
        
        let user = try! User(title: "Mr",
                             firstName: "Test",
                             lastName: "Test")
        XCTAssertTrue(user.isMissingNationality)
    }
    
    func test_userIsMissingPassportNumber_FALSE() {
        
        let user = try! User(title: "Mr",
                             firstName: "Test",
                             lastName: "Test")
        user.passport = Passport(number: "123",
                                 countryOfIssue: "GB")
        XCTAssertFalse(user.isMissingPassportNumber)
    }
    
    func test_userIsMissingPassportNumber_TRUE() {
        
        let user = try! User(title: "Mr",
                             firstName: "Test",
                             lastName: "Test")
        XCTAssertTrue(user.isMissingPassportNumber)
    }
}
