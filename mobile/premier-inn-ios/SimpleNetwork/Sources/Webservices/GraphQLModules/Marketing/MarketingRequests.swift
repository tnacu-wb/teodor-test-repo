//
//  MarketingRequests.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 02/05/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation
import Alamofire

extension GraphQL {
    func getMarketingPreferences(
        for emailAddress: String,
        and brandCodes: MarketingBrandCode,
        isBusiness: Bool
    ) throws -> Resource<MarketingPreferences> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.getMarketingPermissionsQuery
        parameters[.variablesKey] = GraphQL.getMarketingPreferencesParameters(
            for: emailAddress,
            and: brandCodes,
            isBusiness: isBusiness
        )

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default,
            shouldSendAuthToken: true
        ) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let contactDict = dataDict["getContactPreferences"] as? PIDictionary
                else { throw RequestsManagerError.unexpectedResponseError }

            do {
                let contactData = try JSONSerialization.data(withJSONObject: contactDict, options: .prettyPrinted)

                let decoder = JSONDecoder()
                return try decoder.decode(MarketingPreferences.self, from: contactData)
            } catch {
                throw DashboardComponentError.decodeError
            }
        }
    }

    func updateMarketingPreferences(
        brands: [MarketingBrandCode],
        emailAddress: String,
        optIn: Bool,
        isoCountryCode: String
    ) throws -> Resource<Bool> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.updateMarketingPermissionsMutation
        parameters[.variablesKey] = GraphQL.updateMarketingPreferencesParameters(
            for: brands,
            emailAddress: emailAddress,
            optIn: optIn,
            isoCountryCode: isoCountryCode
        )

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default,
            shouldSendAuthToken: true
        ) { _ in
            true
        }
    }
}
