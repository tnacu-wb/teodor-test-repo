//
//  PIDictionary+Extensions_Tests.swift
//  PremierInn UnitTests
//
//  Created by Filippo Minelle on 07/07/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Quick
import XCTest

@testable import PremierInn

class PIDictionary_Extensions_Spec: QuickSpec {

    var dictionary: PIDictionary = [:]

    override func spec() {

        describe("when tracking analytics") {

            context("given a PIDictionary converted into AdobeTrackable strings dictionary") {

                dictionary = ["int_10": 10,
                              "int_100": Int(100),
                              "double_5_09": Double(5.09),
                              "double_13": Double(13),
                              "double_1_3": Double(-1.3),
                              "float_9_0921": Float(9.0921),
                              "float_2": Float(2),
                              "float_123_4": Float(-123.40),
                              "string": "string",
                              "words": "string bla bla bla :)",
                              "boolean_false": false,
                              "boolean_true": true,
                              "date_02_02_1997": Date(timeIntervalSinceReferenceDate: -123456789.0),
                              "date_20_04_2010": Date(timeIntervalSinceReferenceDate: 293456789.0),
                              "date_07_07_2020": Date(timeIntervalSinceReferenceDate: 615816789.0)]

                guard let convertedDictionary = self.dictionary.trackingDictionary else {

                    XCTFail()
                    return
                }

                it("the Bool values should be converted into strings values") {
                    XCTAssertEqual(convertedDictionary["boolean_false"], "false")
                    XCTAssertEqual(convertedDictionary["boolean_true"], "true")
                }

                it("the Int values should be converted into strings values") {
                    XCTAssertEqual(convertedDictionary["int_10"], "10")
                    XCTAssertEqual(convertedDictionary["int_100"], "100")
                }

                it("the Double values should be converted into strings values") {
                    XCTAssertEqual(convertedDictionary["double_5_09"], "5.090000")
                    XCTAssertEqual(convertedDictionary["double_13"], "13.000000")
                    XCTAssertEqual(convertedDictionary["double_1_3"], "-1.300000")
                }

                it("the Double values should be converted into strings values") {
                    XCTAssertEqual(convertedDictionary["float_9_0921"], "9.092100")
                    XCTAssertEqual(convertedDictionary["float_2"], "2.000000")
                    XCTAssertEqual(convertedDictionary["float_123_4"], "-123.400002")
                }

                it("the String values should be converted into strings values") {
                    XCTAssertEqual(convertedDictionary["string"], "string")
                    XCTAssertEqual(convertedDictionary["words"], "string bla bla bla :)")
                }

                it("the Date values should be converted into strings values with dd/MM/yyyy format") {
                    XCTAssertEqual(convertedDictionary["date_02_02_1997"], "02/02/1997")
                    XCTAssertEqual(convertedDictionary["date_20_04_2010"], "20/04/2010")
                    XCTAssertEqual(convertedDictionary["date_07_07_2020"], "07/07/2020")
                }
            }

        }
    }
}
