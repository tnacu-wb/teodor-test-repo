//
//  DigitalKeyRequests.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 18/08/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation
import Alamofire

extension GraphQL {
    func generateOTP(email: String) throws -> Resource<Bool> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.generateOTPMutation
        parameters[.variablesKey] = ["email": email]

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default,
            headers: nil
        ) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let responseDict = dataDict["digitalKeyGenerateOtp"] as? PIDictionary
                else { throw RequestsManagerError.unexpectedResponseError }
            return responseDict["success"] as? Bool ?? false
        }
    }

    func verifyOTP(bookingReference: String, otpCode: String) throws -> Resource<Bool> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.verifyOTPMutation
        parameters[.variablesKey] = ["bookingReference": bookingReference, "otpCode": otpCode]

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default,
            headers: nil
        ) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let responseDict = dataDict["verifyOtp"] as? PIDictionary
                else { throw RequestsManagerError.unexpectedResponseError }
            return responseDict["success"] as? Bool ?? false
        }
    }

    func digitalKeyProvision(
        bookingReference: String,
        otpCode: String,
        email: String,
        reservationId: String
    ) throws -> Resource<DigitalKeyProvisionResponse> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.provisionDigitalKeyMutation
        parameters[.variablesKey] = GraphQL.provisionKeyVariables(
            bookingReference: bookingReference,
            otpCode: otpCode,
            email: email,
            reservationId: reservationId
        )

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default,
            headers: nil
        ) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let digitalKeyDict = dataDict["digitalKeyProvision"] as? PIDictionary
                else { throw RequestsManagerError.unexpectedResponseError }

            do {
                let digitalKeyData = try JSONSerialization.data(withJSONObject: digitalKeyDict, options: .prettyPrinted)

                return try JSONDecoder().decode(DigitalKeyProvisionResponse.self, from: digitalKeyData)
            } catch {
                throw DashboardComponentError.decodeError
            }
        }
    }

    func digitalKeyCheckIn(reservationId: String, hotelCode: String) throws -> Resource<DigitalKeyCheckInResponse> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.digitalKeyCheckinMutation
        parameters[.variablesKey] = GraphQL.digitalKeyCheckInVariables(hotelId: hotelCode, reservationId: reservationId)

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default,
            headers: nil
        ) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let digitalCheckInDict = dataDict["digitalKeyCheckIn"] as? PIDictionary
                else { throw RequestsManagerError.unexpectedResponseError }

            do {
                let digitalCheckInData = try JSONSerialization.data(
                    withJSONObject: digitalCheckInDict,
                    options: .prettyPrinted
                )

                return try JSONDecoder().decode(DigitalKeyCheckInResponse.self, from: digitalCheckInData)
            } catch {
                throw DashboardComponentError.decodeError
            }
        }
    }
}
