//
//  GetPreferencesTests.swift
//  SimpleNetwork
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 19.11.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

final class GetPreferencesTests: XCTestCase {

    let webservice = Webservice.developmentGraphQL

    func testGetPreferences() {
        let dataDict = getDictFor(file: "getPreferences", in: type(of: self))
        guard let getHotelPreferences = dataDict["getHotelPreferences"] as? PIDictionary else { return }
        
        let hotelPreferences = getHotelPreferences["hotelPreferences"] as? [PIDictionary]
        XCTAssertNotNil(hotelPreferences, "Hotel Preferences needs to return a value")
        
        guard let preference = hotelPreferences?.first else { return }
        XCTAssertEqual(preference["description"] as! String, "Anniversary")
        XCTAssertEqual(preference["code"] as! String, "ANNV")
        XCTAssertEqual(preference["preferenceGroup"] as! String, "EVENTS")
        XCTAssertEqual(preference["housekeeping"] as! Bool, false)
        XCTAssertEqual(preference["orderSequence"] as! Int, 2)
        XCTAssertEqual(preference["hotelId"] as! String, "LONEUS")
        XCTAssertEqual(preference["label"] as! String, "Jahrestag")
    }

    func testGetHotelPreferences() {
        do {
            let resource = try webservice.getHotelPreferences(hotelCode: "LONEUS")

            XCTAssertNotNil(resource)

            // Validate parameters
            let params = resource.parameters
            XCTAssertNotNil(params)

            let variables = params?["variables"] as? PIDictionary
            XCTAssertNotNil(variables)

            XCTAssertEqual(variables?["hotelId"] as? String, "LONEUS")
            XCTAssertEqual(variables?["preferenceGroupsCodes"] as? String, "EVENTS")

            // Validate query
            let query = params?["query"] as? String
            XCTAssertEqual(query!, GraphQL.getHotelPreferences)
        } catch {
            XCTAssertEqual(error as! GraphQLError, GraphQLError.missingData)
        }
    }
}
