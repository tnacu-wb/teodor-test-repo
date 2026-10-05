//
//  CountryTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 22/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class CountryTests: XCTestCase {

    private let requestsManager = RequestsManager()

    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.

        Router.current = .uatGraphQL
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
	func testCountry() {

		let jsonString = """
        {
            "countryCode": "AT",
            "countryCodeLegacy": "A",
            "countryName": "Austria",
            "nationality": "Österreichisch"
        }
        """
		let data = jsonString.data(using: .utf8)!
		let decoder = JSONDecoder()

		do {
			let object = try decoder.decode(Country.self, from: data)

            let country1 = Country(code: "A", name: "Austria", isoCode: "AT", dialingCode: nil, flagImage: nil, passportRequired: true, nationality: "Österreichisch")

			XCTAssertEqual(object, country1)
		} catch {
			XCTFail(String(describing: error))
		}

        let expectation = self.expectation(description: "Countries request")
        var countriesList: [Country] = []

        requestsManager.getCountries { fetchedCountries, error in
            guard error == nil else { return }

            countriesList = fetchedCountries ?? []
            expectation.fulfill()
        }

        waitForExpectations(timeout: 30, handler: nil)

        XCTAssert(countriesList.count > 0)
	}
}
