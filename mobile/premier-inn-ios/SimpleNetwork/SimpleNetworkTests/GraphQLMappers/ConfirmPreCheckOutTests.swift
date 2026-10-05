//
//  ConfirmPreCheckOutTests.swift
//  SimpleNetwork
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 25.03.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import SimpleNetwork
import XCTest

class ConfirmPreCheckOutTests: XCTestCase {
    var sut: ConfirmPreCheckInOut?
    
    override func setUp() {
        let fileURL = Bundle.module.url(forResource: "graphQLConfirmPreCheckOut", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        let dataDictionary = jsonDictionary["data"] as! PIDictionary
        let confirmPreCheckOut = dataDictionary["confirmPreCheckOut"] as! PIDictionary
        
        do {
            let confirmPreCheckOutData = try JSONSerialization.data(withJSONObject: confirmPreCheckOut, options: .prettyPrinted)

            let decoder = JSONDecoder()
            sut = try decoder.decode(ConfirmPreCheckInOut.self, from: confirmPreCheckOutData)
        } catch {
            XCTFail("error thrown when decoding HeaderInformation")
        }
    }
    
    func testconfirmPreCheckOut() {
        XCTAssertNotNil(sut)
        XCTAssertEqual(sut?.basketReference, "AKU-f547a8a5-c0d9-4652-9bdc-24ee366df732")
        XCTAssertEqual(sut?.basketStatus.rawValue, "PRE_CHECKED_OUT")
        XCTAssertNil(sut?.basketError)
    }
}
