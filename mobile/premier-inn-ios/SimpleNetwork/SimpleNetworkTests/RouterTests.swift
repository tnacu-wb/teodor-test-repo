//
//  RouterTests.swift
//  SimpleNetworkTests
//
//  Created by Georgios Aikaterinakis on 11/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

class RouterTests: XCTestCase {

    private let requestsManager = RequestsManager()

    private let mockGraphQLRouter = MockGraphQLRouter()

    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.

        BookingDetails.sharedInstance.reset()
        
        mockGraphQLRouter.serviceDidCall = false
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }

    // test a GraphQL migrated call
    func testGraphQLRouterOperaCall() {

        Router.current = mockGraphQLRouter

        requestsManager.hotelAvailability(hotelCode: "LONHOL", hotelBrand: HotelBrand.premierInn, bookingDetails: BookingDetails.sharedInstance, allowEmployeeOffer: true) { [self] availability, error in

            XCTAssertTrue(mockGraphQLRouter.serviceDidCall)
        }
    }
    
    // test a non-GraphQL call
    func testGraphQLRouterNonGraphQLCall() {

        Router.current = mockGraphQLRouter

        requestsManager.getUser(userId: "") { [self] _ in

            XCTAssertTrue(mockGraphQLRouter.serviceDidCall)
        }
    }

    func testAutocompleteURL() {

        // ProductionRouter
        Router.isAutocompleteMigrated = false
        Router.current = ProductionRouter()
        var webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.liveMicroservices)

        Router.isAutocompleteMigrated = true
        webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.migratedLiveMicroservices)

        // UAT
        Router.isAutocompleteMigrated = false
        Router.current = UatGraphQLRouter()
        webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.uatMicroservicesAlpha2)

        Router.isAutocompleteMigrated = true
        webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.migratedUatMicroservices)

        // Dev
        Router.isAutocompleteMigrated = false
        Router.current = DevelopmentGraphQLRouter()
        webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.uatMicroservicesAlpha2)

        Router.isAutocompleteMigrated = true
        webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.migratedDevMicroservices)

        // Generic Lower
        Router.isAutocompleteMigrated = false
        Router.current = LowerEnvGraphQLRouter()
        webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.uatMicroservicesAlpha2)

        Router.isAutocompleteMigrated = true
        webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.migratedUatMicroservices)

        // DIT
        Router.isAutocompleteMigrated = false
        Router.current = DitGraphQLRouter()
        webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.uatMicroservicesAlpha2)

        Router.isAutocompleteMigrated = true
        webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.migratedDitMicroservices)

        // SIT
        Router.isAutocompleteMigrated = false
        Router.current = SitGraphQLRouter()
        webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.uatMicroservicesAlpha2)

        Router.isAutocompleteMigrated = true
        webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.migratedSitMicroservices)

        // DEMO
        Router.isAutocompleteMigrated = false
        Router.current = DemoGraphQLRouter()
        webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.uatMicroservicesAlpha2)

        Router.isAutocompleteMigrated = true
        webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.migratedDemoMicroservices)

        // Pre-Prod
        Router.isAutocompleteMigrated = false
        Router.current = PreprodGraphQLRouter()
        webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.uatMicroservicesAlpha2)

        Router.isAutocompleteMigrated = true
        webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.migratedPreProdMicroservices)

        // PERF
        Router.isAutocompleteMigrated = false
        Router.current = PerfGraphQLRouter()
        webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.uatMicroservicesAlpha2)

        Router.isAutocompleteMigrated = true
        webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.migratedPerfMicroservices)

        // HULK
        Router.isAutocompleteMigrated = false
        Router.current = HulkGraphQLRouter()
        webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.liveMicroservices)

        Router.isAutocompleteMigrated = true
        webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.migratedLiveMicroservices)

        // WANDA
        Router.isAutocompleteMigrated = false
        Router.current = WandaGraphQLRouter()
        webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.liveMicroservices)

        Router.isAutocompleteMigrated = true
        webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.migratedLiveMicroservices)

        // REST flag shouldn't affect
        // ProductionRouter
        Router.isRestMigrated = true
        Router.isAutocompleteMigrated = false
        Router.current = ProductionRouter()
        webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.liveMicroservices)

        Router.isRestMigrated = false
        Router.isAutocompleteMigrated = true
        webservice = try? Router.current.service(for: .suggestions) as? Webservice
        XCTAssertEqual(webservice, Webservice.migratedLiveMicroservices)
    }
}

private class MockGraphQLRouter: Router {

    let mockService = MockService(scheme: "", host: "")

    var serviceDidCall = false

    override func service(for action: WebserviceAction) throws -> WebserviceProtocol {

        serviceDidCall = true

        return try super.service(for: action)
    }
}

private class MockService: Webservice, WebserviceProtocol {


}
