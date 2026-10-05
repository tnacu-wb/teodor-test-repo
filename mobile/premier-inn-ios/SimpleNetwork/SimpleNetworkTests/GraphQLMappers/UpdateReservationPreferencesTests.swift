//
//  UpdateReservationPreferencesTests.swift
//  SimpleNetworkTests
//
//  Created by Muresan, Andreea (Cognizant) on 19.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SimpleNetwork
import XCTest

class UpdateReservationPreferencesTests: XCTestCase {
    var sut = PIDictionary()
    
    override func setUp() {
        let fileURL = Bundle.module.url(forResource: "graphQLUpdateReservationPreferences", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        let dataDictionary = jsonDictionary["data"] as! PIDictionary
        let updateReservationPreferences = dataDictionary["updateReservationPreferences"] as! String
        
        sut = UpdateReservationPreferencesMapper.map(from: updateReservationPreferences)
    }
    
    func testUpdateReservationPreferences() {
        let updateReservationPreferences = sut["updateReservationPreferences"] as! String
        
        XCTAssertEqual(updateReservationPreferences, "{statusCode=204}")
    }
}
