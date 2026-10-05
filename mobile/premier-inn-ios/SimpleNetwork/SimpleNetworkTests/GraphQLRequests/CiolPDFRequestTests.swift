//
//  RegCardRequestTests.swift
//  SimpleNetwork
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 19.03.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//
import XCTest
@testable import SimpleNetwork

final class RegCardRequestTests: XCTestCase {

    let webservice = Webservice.developmentGraphQL

    func testAttachPDFCiol() {
        do {
            let resource = try webservice.attachFileToReservation(params: .init(fileName: "res_23122.pdf", reservationId: "23122", overwriteExistingFile: true, description: "Pre-check-in registration card", hotelId: "HAPTI", global: true, fileAttachment: "file"))
            XCTAssertNotNil(resource)

            // Validate parameters
            let params = resource.parameters
            XCTAssertNotNil(params)

            let variables = params?["variables"] as? PIDictionary
            XCTAssertNotNil(variables)

            XCTAssertEqual(variables?[AuthorizationFileAttachmentParams.Constants.reservationId] as? String, "23122")
            XCTAssertEqual(variables?[AuthorizationFileAttachmentParams.Constants.hotelId] as? String, "HAPTI")
            XCTAssertEqual(variables?[AuthorizationFileAttachmentParams.Constants.fileAttachment] as? String, "file")
            XCTAssertEqual(variables?[AuthorizationFileAttachmentParams.Constants.fileName] as? String, "res_23122.pdf")
            XCTAssertEqual(variables?[AuthorizationFileAttachmentParams.Constants.global] as? Bool, true)
            XCTAssertEqual(variables?[AuthorizationFileAttachmentParams.Constants.overwriteExistingFile] as? Bool, true)
            XCTAssertEqual(variables?[AuthorizationFileAttachmentParams.Constants.description] as? String, "Pre-check-in registration card")

        } catch {
            XCTAssertEqual(error as! GraphQLError, GraphQLError.missingData)
        }
    }

    func testUpdatePreCheckInStatus() {
        do {
            let resource = try webservice.updatePreCheckInStatus(params: .init(hotelId: "HAPTI", reservationId: "23122", arrivalTime: "00/00/0000"))
            XCTAssertNotNil(resource)

            // Validate parameters
            let params = resource.parameters
            XCTAssertNotNil(params)

            let variables = params?["variables"] as? PIDictionary
            XCTAssertNotNil(variables)

            XCTAssertEqual(variables?[UpdatePrecheckInParams.Constants.reservationId] as? String, "23122")
            XCTAssertEqual(variables?[UpdatePrecheckInParams.Constants.hotelId] as? String, "HAPTI")
            XCTAssertEqual(variables?[UpdatePrecheckInParams.Constants.arrivalTime] as? String, "00/00/0000")

        } catch {
            XCTAssertEqual(error as! GraphQLError, GraphQLError.missingData)
        }
    }
}
