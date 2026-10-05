//
//  WebserviceTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 29/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class WebserviceTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {

		Router.current = .production

		super.tearDown()
    }

	func testRoutingConfiguration() {

		XCTAssertEqual(Router.current, .production)
	}

	func testThings() {

		XCTAssertEqual(Router.current, .production)

		Router.current = .production
		XCTAssertEqual(Router.current, .production)
	}

    func testWebserviceHost() {

        XCTAssertEqual(Webservice.liveMicroservices.baseURL?.absoluteString, "https://api.whitbread.co.uk")
        XCTAssertEqual(Webservice.uatMicroservicesAlpha2.baseURL?.absoluteString, "https://api-uat.whitbread.co.uk")
    }

    func testWebservice() {

        let webservice = Webservice(scheme: "http", host: "www.pippo.com", port: 44)

        XCTAssertEqual(webservice.scheme, "http")
        XCTAssertEqual(webservice.host, "www.pippo.com")
        XCTAssertEqual(webservice.port, 44)
    }
}
