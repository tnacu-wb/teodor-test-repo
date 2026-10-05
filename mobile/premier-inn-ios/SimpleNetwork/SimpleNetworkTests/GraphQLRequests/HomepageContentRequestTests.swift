//
//  HomepageContentRequestTests.swift
//  SimpleNetwork
//
//  Created by Santa Gurung on 14/02/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

final class HomepageContentRequestTests: XCTestCase {

    let webservice = Webservice.developmentGraphQL
    
    func testHomepageContentVariablesAreValid() {
        do {
            let resource = try webservice.homepageAppsContent(
                channel: Channel.PI,
                subchannel: "apps",
                language: "en",
                country: "gb"
            )
            XCTAssertNotNil(resource)

            let parameters = resource.parameters

            // Validate query
            let query = parameters?["query"] as? String
            XCTAssertEqual(query!, GraphQL.homepageAppsContent)
            
            // Validate variables
            let variables = parameters?["variables"] as? PIDictionary
            XCTAssertNotNil(variables)
            let channel = variables?["channel"] as? String
            let subchannel = variables?["subchannel"] as? String
            let language = variables?["language"] as? String
            let country = variables?["country"] as? String
            XCTAssertEqual(channel, "PI")
            XCTAssertEqual(subchannel, "apps")
            XCTAssertEqual(language, "en")
            XCTAssertEqual(country, "gb")
        } catch {
            XCTFail("Error: \(error)")
        }
    }
}
