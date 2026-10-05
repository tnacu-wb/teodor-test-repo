//
//  PaymentRequests.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 14/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation
import Alamofire

extension GraphQL {
    func cccpPayment(
        with paymentParams: CCCPPaymentParams,
        and stayDetails: CCCPStayDetails,
        and sessionId: String?,
        and isCiol: Bool,
        sensorData: String
    ) throws -> Resource<CCCPPaymentResponse> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }
        let environmentURL = getEnvironmentURL(host: apiHost)
        var parameters = PIDictionary()
        // TODO: When the back end allow us to use the one mutation for all payment requests then this can be removed
        let shouldUsePaypalInitiatePayment = paymentParams.paymentType == .PAYPAL && paymentParams
            .usePaypalInitiatePayment == true
        parameters[.queryKey] = shouldUsePaypalInitiatePayment ? GraphQL.initiatePayPalPaymentMutation : GraphQL
            .initiatePaymentMutation

        parameters[.variablesKey] = try GraphQL.getInitiatePaymentVariables(
            with: paymentParams,
            and: stayDetails,
            and: sessionId,
            hostURL: environmentURL,
            isCiol: isCiol
        )

        var headers: [String: String] = [:]
        if !shouldUsePaypalInitiatePayment {
            headers = [Constants.akamaiSensorDataKey: sensorData]
        }

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default,
            headers: headers
        ) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let initiatePaymentDict = shouldUsePaypalInitiatePayment ?
                  dataDict["initiatePaypalPayment"] as? PIDictionary : dataDict["initiatePayment"] as? PIDictionary
            else { throw RequestsManagerError.unexpectedResponseError }

            do {
                let cccpPaymentData = try JSONSerialization.data(
                    withJSONObject: initiatePaymentDict,
                    options: .prettyPrinted
                )

                let decoder = JSONDecoder()
                return try decoder.decode(CCCPPaymentResponse.self, from: cccpPaymentData)
            } catch {
                throw DashboardComponentError.decodeError
            }
        }
    }

    func paymentMethods(
        for bookingDetails: BookingDetails,
        hotel: Hotel,
        rate: Rate,
        user: User?,
        isCiol: Bool = false
    ) throws -> Resource<PaymentMethodsResponse> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.paymentMethodsQuery
        parameters[.variablesKey] = try GraphQL.paymentMethodsVariables(bookingDetails: bookingDetails, isCiol: isCiol)

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default,
            shouldSendAuthToken: true
        ) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let paymentMethodsDict = dataDict["paymentMethods"] as? [PIDictionary]
                else { throw RequestsManagerError.unexpectedResponseError }

            let paymentMethodsDictMapped = PaymentMethodsMapper.map(from: paymentMethodsDict)

            do {
                let paymentMethodsData = try JSONSerialization.data(
                    withJSONObject: paymentMethodsDictMapped,
                    options: .prettyPrinted
                )

                let decoder = JSONDecoder()
                return try decoder.decode(PaymentMethodsResponse.self, from: paymentMethodsData)
            } catch {
                throw DashboardComponentError.decodeError
            }
        }
    }

    func authorizePayment() throws -> Resource<CCCPPaymentProviderResponse> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }
        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.regCardAuthorizeMutation
        parameters[.variablesKey] = try GraphQL.getAuthorizePaymentVariables()

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let authorizeDict = dataDict["authorizeCard"] as? PIDictionary
                else { throw RequestsManagerError.unexpectedResponseError }

            do {
                let cccpPaymentData = try JSONSerialization.data(withJSONObject: authorizeDict, options: .prettyPrinted)
                let decoder = JSONDecoder()
                return try decoder.decode(CCCPPaymentProviderResponse.self, from: cccpPaymentData)
            } catch {
                throw DashboardComponentError.decodeError
            }
        }
    }

    func attachFileToReservation(params: AuthorizationFileAttachmentParams) throws -> Resource<StatusResult> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }
        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.regAttachPDFMutation
        parameters[.variablesKey] = GraphQL.getAttachFileToReservationVariables(params: params)

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let attachDict = dataDict[AuthorizationFileAttachmentParams.Constants.decodingKey] as? PIDictionary
                else { throw RequestsManagerError.unexpectedResponseError }

            do {
                let attachResult = try JSONSerialization.data(withJSONObject: attachDict, options: .prettyPrinted)

                let decoder = JSONDecoder()
                return try decoder.decode(StatusResult.self, from: attachResult)
            } catch {
                throw DashboardComponentError.decodeError
            }
        }
    }

    func updatePreCheckInStatus(params: UpdatePrecheckInParams) throws -> Resource<StatusResult> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }
        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.regCardPreCheckinMutation
        parameters[.variablesKey] = GraphQL.getUpdatePreCheckInStatusVariables(params: params)

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let attachDict = dataDict[UpdatePrecheckInParams.Constants.decodingKey] as? PIDictionary
                else { throw RequestsManagerError.unexpectedResponseError }

            do {
                let attachResult = try JSONSerialization.data(withJSONObject: attachDict, options: .prettyPrinted)

                let decoder = JSONDecoder()
                return try decoder.decode(StatusResult.self, from: attachResult)
            } catch {
                throw DashboardComponentError.decodeError
            }
        }
    }

    func initMobileSDKPayment(basketId: String) throws -> Resource<DatatransPaymentSessionResponse> {
        let path = "/api/payments/mobile-sdk"

        guard let url = baseURL?.appendingPathComponent(path)
            else { throw WebserviceError.invalidPath(path) }

        let parameters: PIDictionary = ["basketId": basketId]

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default,
            shouldSendAuthToken: true
        ) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }

            let jsonData = try JSONSerialization.data(withJSONObject: data, options: .prettyPrinted)
            let decoder = JSONDecoder()
            return try decoder.decode(DatatransPaymentSessionResponse.self, from: jsonData)
        }
    }
}
