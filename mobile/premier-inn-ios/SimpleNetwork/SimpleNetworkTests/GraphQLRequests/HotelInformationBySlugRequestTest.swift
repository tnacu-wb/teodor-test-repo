//
//  HotelInformationBySlugRequestTest.swift
//  SimpleNetworkTests
//
//  Created by Florin Velesca on 29.08.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

final class HotelInfoRequestTests: XCTestCase {

    let webservice = Webservice.developmentGraphQL

    func testFetchHotelInformation() {
        do {
            let slug = "london-blackfriars-fleet-street.html"
            let country = LanguageManager.supportedLanguage.countryCode
            let language = LanguageManager.supportedLanguage.rawValue
            let resource = try webservice.getHotelBySlug(slug: slug)

            XCTAssertNotNil(resource)

            // Validate query
            let query = resource.parameters?["query"] as? String
            XCTAssertEqual(query!, GraphQL.hotelInfoBySlugQuery)

            // Validate parameters
            let parameters = resource.parameters
            XCTAssertNil(parameters?["error"])

            // Validate variables
            let variables = parameters?["variables"] as? [String: Any]
            XCTAssertNotNil(variables)

            let fetchedSlug = variables?["slug"] as? String
            let fetchedCountry = variables?["country"] as? String
            let fetchedLanguage = variables?["language"] as? String

            XCTAssertEqual(fetchedSlug, slug)
            XCTAssertEqual(fetchedCountry, country)
            XCTAssertEqual(fetchedLanguage, language)

        } catch {
            XCTFail("Unexpected error \(error)")
        }
    }
}
