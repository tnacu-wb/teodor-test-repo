//
//  CiolPaymentActionsRequests.swift
//  SimpleNetwork
//
//  Created by Rodrigues, Seymour (Contractor) on 26/05/2026.
//

import Foundation
import Alamofire

extension GraphQL {
    func ciolPaymentActions(
        basketReference: String
    ) throws -> Resource<CiolPaymentActionsResponse> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath) else {
            throw WebserviceError.invalidPath(.graphQLPath)
        }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.ciolPaymentActionsQuery
        parameters[.variablesKey] = ["basketReference": basketReference]

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default
        ) { data -> CiolPaymentActionsResponse? in
            guard let data = data as? PIDictionary,
                  let dataDict = data["data"] as? PIDictionary,
                  let responseDict = dataDict["checkInOnlinePaymentActions"] else {
                throw RequestsManagerError.unexpectedResponseError
            }

            do {
                let ciolPaymentActionsData = try JSONSerialization.data(withJSONObject: responseDict)

                let ciolPaymentActionsResponse = try JSONDecoder().decode(
                    CiolPaymentActionsResponse.self,
                    from: ciolPaymentActionsData
                )

                return ciolPaymentActionsResponse
            } catch {
                throw GraphQLError.decodeError
            }
        }
    }
}
