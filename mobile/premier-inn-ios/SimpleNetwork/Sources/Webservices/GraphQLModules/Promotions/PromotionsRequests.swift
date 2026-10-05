//
//  PromotionsRequests.swift
//  SimpleNetwork
//
//  Created by Georgios Aikaterinakis on 27/11/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation
import Alamofire

extension GraphQL {
    func getPromotionsInformation(criteria: PromotionsInformationCriteria) throws -> Resource<PromotionsInformation> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.promotionsInformationQuery
        parameters[.variablesKey] = try GraphQL.promotionsInformationVariables(criteria: criteria)

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default
        ) { data -> PromotionsInformation? in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let promotionsInformationDict = dataDict["promotionsInformation"] as? PIDictionary
                else { throw RequestsManagerError.unexpectedResponseError }

            do {
                let promotionsInformationData = try JSONSerialization.data(withJSONObject: promotionsInformationDict)
                let promotionsInformation = try JSONDecoder().decode(
                    PromotionsInformation.self,
                    from: promotionsInformationData
                )
                return promotionsInformation
            } catch {
                throw GraphQLError.decodeError
            }
        }
    }

    func validateDiscountCode(criteria: PromotionsInformationCriteria) throws -> Resource<ValidateDiscountCodeResult> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.validateDiscountCode
        parameters[.variablesKey] = try GraphQL.promotionsInformationVariables(criteria: criteria)

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default
        ) { data -> ValidateDiscountCodeResult? in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let promotionsInformationDict = dataDict["promotionsInformation"] as? PIDictionary
                else { throw RequestsManagerError.unexpectedResponseError }

            do {
                let promotionsInformationData = try JSONSerialization.data(withJSONObject: promotionsInformationDict)
                let promotionsInformation = try JSONDecoder().decode(
                    ValidateDiscountCodeResult.self,
                    from: promotionsInformationData
                )
                return promotionsInformation
            } catch {
                throw GraphQLError.decodeError
            }
        }
    }
}
