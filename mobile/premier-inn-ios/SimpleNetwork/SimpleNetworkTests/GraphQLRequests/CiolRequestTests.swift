//
//  CiolRequestTests.swift
//  SimpleNetworkTests
//
//  Created by Muresan, Andreea (Cognizant) on 16.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import Alamofire
@testable import SimpleNetwork

final class CiolRequestTests: XCTestCase {

    let webservice = Webservice.developmentGraphQL

    func testConfirmPreCheckIn() {
        do {
            let resource  = try webservice.confirmPreCheckInOut(basketReference: "AKU-f547a8a5-c0d9-4652-9bdc-24ee366df732", type: .checkIn)

            XCTAssertNotNil(resource)

            // Validate parameters
            let params = resource.parameters
            XCTAssertNotNil(params)

            let variables = params?["variables"] as? PIDictionary
            XCTAssertNotNil(variables)

            XCTAssertEqual(variables?["basketReference"] as? String, "AKU-f547a8a5-c0d9-4652-9bdc-24ee366df732")
            // Validate query
            let query = params?["query"] as? String
            XCTAssertEqual(query!, GraphQL.confirmPreCheckInOutQuery(resolver: CiolRequestType.checkIn.rawValue, includeIsCiol: true))
        } catch {
            XCTAssertEqual(error as! GraphQLError, GraphQLError.missingData)
        }
    }
}
