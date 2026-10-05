//
//  CountriesTests.swift
//  SimpleNetworkTests
//
//  Created by Santa Gurung on 06/07/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

final class CountriesTests: XCTestCase {

    func testCountriesDecoding() {
        let dataDict = getDictFor(file: "graphQLCountries", in: type(of: self))
        let countriesDict = (dataDict["countries"] as! PIDictionary)["countries"]
        let data = try! JSONSerialization.data(withJSONObject: countriesDict!)

        let countries = try! JSONDecoder().decode([Country].self, from: data)
        let country = countries.first!
        XCTAssertEqual(country.code, "A")
        XCTAssertEqual(country.isoCode, "AT")
        XCTAssertEqual(country.flagImage, "/content/dam/global/flags/Austria.png")
        XCTAssertEqual(country.nationality, "Österreichisch")
        XCTAssertEqual(country.name, "Austria")
    }

}
