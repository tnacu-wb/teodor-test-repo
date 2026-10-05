//
//  StayTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 23/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class StayTests: XCTestCase {

    static let staysDictionary: PIDictionary = {
        let fileURL = Bundle.module.url(forResource: "stay", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)

        return try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
    }()

    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
    func testStay_StaysDictionary() {

        XCTAssertThrowsError(try Stay(dictionary: ["absoluteRubbish": "ha ha ha"]))

        do {
            guard let reservationsArr = StayTests.staysDictionary["stays"] as? [PIDictionary],
                  let firstStay = reservationsArr.first else { throw ResponseParserError.keyNotFound("stays") }

            let mappedStay = StayMapper.map(input: firstStay, isBusiness: false)
            let stay = try Stay(dictionary: mappedStay)

            let summaryDic = stay.dictionary
            XCTAssert(summaryDic["hotelCode"] as? String == stay.hotelCode)

        } catch {
            print(error)
            XCTFail()
        }
    }

    func testStay_LocalDictionary() {

        XCTAssertThrowsError(try Stay(dictionary: ["absoluteRubbish": "ha ha ha"]))

        do {
            let stay = try Stay(dictionary: [
                "hotelCode": "HELL",
                "hotelName": "Hello Hotel",
                "lastName": "Apps",
                "identifier": "BrapBrap",
                "arrivalDate": "2020-12-20",
                "checkOutDate": "2020-12-23"
            ])
            XCTAssert(stay.hotelCode == "HELL")
        } catch {
            print(error)
            XCTFail()
        }
    }
    
}
