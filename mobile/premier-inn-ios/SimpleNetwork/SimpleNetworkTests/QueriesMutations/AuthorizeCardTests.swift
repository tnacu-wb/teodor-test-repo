//
//  AuthorizeCardTests.swift
//  SimpleNetwork
//
//  Created by Santa Gurung on 17/06/2026.
//

import Testing
@testable import SimpleNetwork

struct AuthorizeCardTests {

    @Test("The required variables for the authorizeCard mutation are sent")
    func requiredAuthorizeCardMutationVariblesAreSent() async throws {
        // GIVEN
        let expectedKeys: Set<String> = [
            "environment",
            "requestId",
            "country",
            "language"
        ]

        // WHEN
        let variablesKeys = try! GraphQL.getAuthorizePaymentVariables().keys

        // THEN
        #expect(Set(variablesKeys) == expectedKeys)
    }

    @Test("The variable types for authorizeCard mutation types are correct")
    func authorizeCardMutationVariablesTypesAreCorrect() {
        let variables = try! GraphQL.getAuthorizePaymentVariables()

        #expect(variables["environment"] is String)
        #expect(variables["requestId"] is String)
        #expect(variables["country"] is String)
        #expect(variables["language"] is String)
    }

}
