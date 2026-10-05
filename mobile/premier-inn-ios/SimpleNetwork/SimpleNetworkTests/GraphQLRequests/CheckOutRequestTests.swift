//
//  CheckOutRequestTests.swift
//  SimpleNetwork
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 25.03.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//
import XCTest
@testable import SimpleNetwork

final class CheckOutRequestTests: XCTestCase {

    let webservice = Webservice.developmentGraphQL

    func testConfirmPreCheckOut() {
        do {
            let resource  = try webservice.confirmPreCheckInOut(basketReference: "AKU-f547a8a5-c0d9-4652-9bdc-24ee366df732", type: .checkOut)

            XCTAssertNotNil(resource)

            // Validate parameters
            let params = resource.parameters
            XCTAssertNotNil(params)

            let variables = params?["variables"] as? PIDictionary
            XCTAssertNotNil(variables)

            XCTAssertEqual(variables?["basketReference"] as? String, "AKU-f547a8a5-c0d9-4652-9bdc-24ee366df732")
            // Validate query
            let query = params?["query"] as? String
            XCTAssertEqual(query!, GraphQL.confirmPreCheckInOutQuery(resolver: CiolRequestType.checkOut.rawValue, includeIsCiol: false))
        } catch {
            XCTAssertEqual(error as! GraphQLError, GraphQLError.missingData)
        }
    }
}

