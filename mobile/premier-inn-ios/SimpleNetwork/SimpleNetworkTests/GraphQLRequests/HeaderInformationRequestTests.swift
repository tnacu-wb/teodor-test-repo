//
//  HeaderInformationRequestTests.swift
//  SimpleNetworkTests
//
//  Created by Louis Faria-Softly on 14/11/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import XCTest
import Alamofire
@testable import SimpleNetwork

final class HeaderInformationRequestTests: XCTestCase {

    let webservice = Webservice.developmentGraphQL

    func testHeaderInformation() {

        do {
            let resource = try webservice.headerInformation()

            XCTAssertNotNil(resource)

            // Validate query
            let query = resource.parameters?["query"] as? String
            XCTAssertEqual(query!, GraphQL.headerInformationQuery)
            // Validate parameters
            let parameters = resource.parameters
            XCTAssertNil(parameters?["error"])
            let variables = resource.parameters?["variables"] as? PIDictionary
            XCTAssertNotNil(variables)
            let language = variables?["language"] as? String
            let country = variables?["country"] as? String
            XCTAssertEqual(language, "en")
            XCTAssertEqual(country, "gb")

        } catch {
            XCTFail("Unexpected error \(error)")
        }
    }
}
