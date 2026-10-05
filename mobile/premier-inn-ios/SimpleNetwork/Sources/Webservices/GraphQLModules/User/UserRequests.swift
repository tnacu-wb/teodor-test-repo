//
//  UserRequests.swift
//  SimpleNetwork
//
//  Created by Georgios Aikaterinakis on 02/12/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation
import Alamofire

public struct RegisterParameters {
    public let user: User
    public let password: String
    public let marketingOptIn: Bool
    public let doubleOptIn: Bool
    public let isoCountryCode: String
    public let brandCodes: [MarketingBrandCode]

    public init(
        user: User,
        password: String,
        marketingOptIn: Bool,
        doubleOptIn: Bool,
        isoCountryCode: String,
        brandCodes: [MarketingBrandCode]
    ) {
        self.user = user
        self.password = password
        self.marketingOptIn = marketingOptIn
        self.doubleOptIn = doubleOptIn
        self.isoCountryCode = isoCountryCode
        self.brandCodes = brandCodes
    }
}

extension GraphQL {
    func initiateSaveCard(initiateSaveCardParameters: InitiateSaveCardParameters) throws
        -> Resource<CCCPPaymentProviderResponse> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }
        let environmentURL = getEnvironmentURL(host: apiHost)

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.initiateSaveCardMutation
        parameters[.variablesKey] = try GraphQL.getInitiateSaveCardVariables(
            initiateSaveCardParameters: initiateSaveCardParameters,
            environment: environmentURL
        )

        var headers: [String: String] = [:]
        if let token = UserSessionManager.sharedInstance.idToken {
            headers["Authorization"] = "Bearer \(token)"
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
            guard let initiateSaveCardDict = dataDict["saveCard"] as? PIDictionary
                else { throw RequestsManagerError.unexpectedResponseError }

            do {
                let iframeData = try JSONSerialization.data(withJSONObject: initiateSaveCardDict, options: .prettyPrinted)

                return try JSONDecoder().decode(CCCPPaymentProviderResponse.self, from: iframeData)
            } catch {
                throw DashboardComponentError.decodeError
            }
        }
    }

    func anonymousNewsletterPreferences(
        email: String,
        countryOfResidence: String
    ) throws -> Resource<AnonymousNewsletterPreferences> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.anonNewsLetterPreferences
        parameters[.variablesKey] = [
            "email": email,
            "brandCode": "PINN",
            "countryOfResidence": countryOfResidence,
            "language": LanguageManager.supportedLanguage.rawValue
        ]

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default,
            headers: nil
        ) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let anonNewsletterDict = dataDict["anonymousNewsletterPreferences"] as? PIDictionary
                else { throw RequestsManagerError.unexpectedResponseError }

            do {
                let newsData = try JSONSerialization.data(withJSONObject: anonNewsletterDict, options: .prettyPrinted)

                return try JSONDecoder().decode(AnonymousNewsletterPreferences.self, from: newsData)
            } catch {
                throw DashboardComponentError.decodeError
            }
        }
    }

    func forgotPassword(emailAddress: String, isBusiness: Bool) throws -> Resource<Bool> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.forgotPasswordMutation
        parameters[.variablesKey] = GraphQL.forgotPasswordVariables(isBusiness: isBusiness, email: emailAddress)

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary,
                  let forgotPasswordDict = dataDict["forgotPassword"] as? PIDictionary
            else { throw GraphQLError.missingData }
            guard let success = forgotPasswordDict["success"] as? Bool
                else { throw ResponseParserError.keyNotFound("success") }

            return success
        }
    }

    func register(registerParameters: RegisterParameters, sensorData: String) throws -> Resource<Bool> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath) else {
            throw WebserviceError.invalidPath(.graphQLPath)
        }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.createAccountMutation
        parameters[.variablesKey] = GraphQL.createAccountVariables(registerParameters: registerParameters)

        let headers: [String: String] = [Constants.akamaiSensorDataKey: sensorData]

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default,
            headers: headers
        ) { data in
                guard let data = data as? PIDictionary else {
                    throw RequestsManagerError.unexpectedResponseError
                }
                guard let dataDict = data["data"] as? PIDictionary,
                      let createAccountDict = dataDict["createAccount"] as? PIDictionary else {
                    throw GraphQLError.missingData
                }
                guard let isSuccess = createAccountDict["success"] as? Bool else {
                    throw ResponseParserError.keyNotFound("success")
                }
                return isSuccess
            }
    }
}
