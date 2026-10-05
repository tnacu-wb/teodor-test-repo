//
//  MarketingRequestTests.swift
//  SimpleNetworkTests
//
//  Created by Louis Faria-Softly on 07/05/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

final class MarketingRequestTests: XCTestCase {

    let webservice = Webservice.developmentGraphQL

    func testGetMarketingPreferencesTests() {
        do {
            let brandCode: MarketingBrandCode = .premierInn
            let email = "testmail@mailinator.com"

            let resource = try webservice.getMarketingPreferences(for: email, and: .premierInn, isBusiness: false)

            XCTAssertNotNil(resource)

            // Validate query
            let query = resource.parameters?["query"] as? String
            XCTAssertEqual(query!, GraphQL.getMarketingPermissionsQuery)

            // Validate parameters
            let parameters = resource.parameters
            XCTAssertNil(parameters?["error"])

            // Validate variables
            let variables = parameters?["variables"] as? [String: Any]
            XCTAssertNotNil(variables)

            let request = variables?["request"] as? [String: Any]
            XCTAssertNotNil(request)

            let contactType = request?["contactType"] as? String
            let brand = request?["brandCodes"] as? String
            let contactValue = request?["contactValue"] as? String

            XCTAssertEqual(contactType, "email")
            XCTAssertEqual(brand, brandCode.rawValue)
            XCTAssertEqual(contactValue, email)

        } catch {
            XCTFail("Unexpected error \(error)")
        }
    }

    func testUpdateMarketingPreferencesTests() {
        do {
            let brandCode: MarketingBrandCode = .premierInn
            let email = "testmail@mailinator.com"
            let isoCode = "GB"

            let resource = try webservice.updateMarketingPreferences(brands: [brandCode], emailAddress: email, optIn: true, isoCountryCode: isoCode)

            XCTAssertNotNil(resource)

            // Validate query
            let query = resource.parameters?["query"] as? String
            XCTAssertEqual(query!, GraphQL.updateMarketingPermissionsMutation)

            // Validate parameters
            let parameters = resource.parameters
            XCTAssertNil(parameters?["error"])

            // Validate variables
            let variables = parameters?["variables"] as? [String: Any]
            XCTAssertNotNil(variables)

            let customer = variables?["customer"] as? PIDictionary
            let customerId = customer?["customerId"] as? String
            XCTAssertEqual(customerId, email)


            let brand = variables?["brandCodes"] as? [String]
            let optIn = variables?["optIn"] as? Bool
            let doubleOptIn = variables?["doubleOptIn"] as? Bool

            XCTAssertEqual(brand?.first, brandCode.rawValue)
            XCTAssertEqual(optIn, true)
            XCTAssertEqual(doubleOptIn, false)

        } catch {
            XCTFail("Unexpected error \(error)")
        }
    }

}
