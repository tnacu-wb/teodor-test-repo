//
//  OTPTests.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 18/08/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

final class OTPTests: XCTestCase {
    let webservice = Webservice.developmentGraphQL

    func testGenerateOTPRequest() {
        do {
            let resource = try webservice.generateOTP(email: "TestRef")

            XCTAssertNotNil(resource)

            // Validate parameters
            let params = resource.parameters
            XCTAssertNotNil(params)

            let variables = params?["variables"] as? PIDictionary
            XCTAssertNotNil(variables)

            XCTAssertEqual(variables?["email"] as? String, "TestRef")

            // Validate query
            let query = params?["query"] as? String
            XCTAssertEqual(query!, GraphQL.generateOTPMutation)
        } catch {
            XCTAssertEqual(error as! GraphQLError, GraphQLError.missingData)
        }
    }

    func testVerifyOTPRequest() {
        do {
            let resource = try webservice.verifyOTP(bookingReference: "TestRef", otpCode: "123")

            XCTAssertNotNil(resource)

            // Validate parameters
            let params = resource.parameters
            XCTAssertNotNil(params)

            let variables = params?["variables"] as? PIDictionary
            XCTAssertNotNil(variables)

            XCTAssertEqual(variables?["bookingReference"] as? String, "TestRef")
            XCTAssertEqual(variables?["otpCode"] as? String, "123")

            // Validate query
            let query = params?["query"] as? String
            XCTAssertEqual(query!, GraphQL.verifyOTPMutation)
        } catch {
            XCTAssertEqual(error as! GraphQLError, GraphQLError.missingData)
        }
    }

    func testProvisionKeyRequest() {
        do {
            let resource = try webservice.digitalKeyProvision(bookingReference: "TestRef", otpCode: "1234", email: "test@Mail.com", reservationId: "1234")

            XCTAssertNotNil(resource)

            // Validate parameters
            let params = resource.parameters
            XCTAssertNotNil(params)

            let variables = params?["variables"] as? PIDictionary
            let provisionKeys = variables?["digitalkeyProvisionRequest"] as? PIDictionary

            XCTAssertNotNil(variables)
            XCTAssertNotNil(provisionKeys)

            XCTAssertEqual(provisionKeys?["bookingReference"] as? String, "TestRef")
            XCTAssertEqual(provisionKeys?["otpCode"] as? String, "1234")
            XCTAssertEqual(provisionKeys?["email"] as? String, "test@Mail.com")
            XCTAssertEqual(provisionKeys?["reservationId"] as? String, "1234")
            // Validate query
            let query = params?["query"] as? String
            XCTAssertEqual(query!, GraphQL.provisionDigitalKeyMutation)
        } catch {
            XCTAssertEqual(error as! GraphQLError, GraphQLError.missingData)
        }
    }

    func testCheckInRequest() {
        do {
            let resource = try webservice.digitalKeyCheckIn(reservationId: "1234", hotelCode: "MANOLD")

            XCTAssertNotNil(resource)

            // Validate parameters
            let params = resource.parameters
            XCTAssertNotNil(params)

            let variables = params?["variables"] as? PIDictionary
            let checkInCriteria = variables?["digitalKeyCheckInCriteria"] as? PIDictionary
            XCTAssertNotNil(variables)
            XCTAssertNotNil(checkInCriteria)

            XCTAssertEqual(checkInCriteria?["reservationId"] as? String, "1234")
            XCTAssertEqual(checkInCriteria?["hotelId"] as? String, "MANOLD")

            // Validate query
            let query = params?["query"] as? String
            XCTAssertEqual(query!, GraphQL.digitalKeyCheckinMutation)
        } catch {
            XCTAssertEqual(error as! GraphQLError, GraphQLError.missingData)
        }
    }

}
