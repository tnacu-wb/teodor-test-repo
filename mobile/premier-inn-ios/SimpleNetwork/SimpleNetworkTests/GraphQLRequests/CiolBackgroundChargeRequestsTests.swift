//
//  CiolBackgroundChargeRequestsTests.swift
//  SimpleNetwork
//
//  Created by Rodrigues, Seymour (Contractor) on 29/06/2026.
//

import Testing
@testable import SimpleNetwork

struct CiolBackgroundChargeRequestsTests {

    private let webService = Webservice.developmentGraphQL

    @Test
    func testCiolBackgroundCharge() throws {
        do {
            let resource  = try webService
                .ciolBackgroundCharge(basketReference: "reference", token: "token")

            // Validate parameters
            let params = resource.parameters
            #expect(params != nil)

            let variables = params?["variables"] as? PIDictionary
            #expect(variables != nil)

            #expect(variables?["basketReference"] as? String == "reference")
            #expect(variables?["token"] as? String == "token")

            // Validate query
            let query = params?["query"] as! String
            #expect(query == GraphQL.ciolBackgroundChargeQuery)

        } catch {
            #expect(error as! GraphQLError == GraphQLError.missingData)
        }
    }

}
