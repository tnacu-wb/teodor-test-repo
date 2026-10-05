//
//  UpdateReservationPreferencesRequestTests.swift
//  SimpleNetworkTests
//
//  Created by Muresan, Andreea (Cognizant) on 19.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import Alamofire
@testable import SimpleNetwork

final class UpdateReservationPreferencesRequestTests: XCTestCase {

    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }
    let webservice = Webservice.developmentGraphQL

    func testUpdateReservationPreferences() {
        do {
        let resource  = try webservice.updateReservationPreferences(hotelCode: "LONEUS", reservationIds: ["2287451"], preferencesCollections: [])

            XCTAssertNotNil(resource)

            // Validate parameters
            let params = resource.parameters
            XCTAssertNotNil(params)

            let variables = params?["variables"] as? PIDictionary
            XCTAssertNotNil(variables)

            XCTAssertEqual(variables?["hotelId"] as? String, "LONEUS")
            XCTAssertEqual(variables?["reservationsIds"] as? [String], ["2287451"])
            // Validate query
            let query = params?["query"] as? String
            XCTAssertEqual(query!, GraphQL.updateReservationPreferencesMutation)
        } catch {
            XCTAssertEqual(error as! GraphQLError, GraphQLError.missingData)
        }
    }
}
