//
//  CiolBackgroundChargeRequests.swift
//  SimpleNetwork
//
//  Created by Rodrigues, Seymour (Contractor) on 29/06/2026.
//

import Foundation
import Alamofire

extension GraphQL {
    func ciolBackgroundCharge(
        basketReference: String,
        token: String
    ) throws -> Resource<CiolBackgroundChargeResponse> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath) else {
            throw WebserviceError.invalidPath(.graphQLPath)
        }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.ciolBackgroundChargeQuery
        parameters[.variablesKey] = [
            "basketReference": basketReference,
            "token": token
        ]

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default
        ) { data -> CiolBackgroundChargeResponse? in
            guard let data = data as? PIDictionary,
                  let dataDict = data["data"] as? PIDictionary,
                  let responseDict = dataDict["backgroundCharge"] else {
                throw RequestsManagerError.unexpectedResponseError
            }

            do {
                let ciolBackgroundChargeData = try JSONSerialization.data(withJSONObject: responseDict)

                let ciolBackgroundChargeResponse = try JSONDecoder().decode(
                    CiolBackgroundChargeResponse.self,
                    from: ciolBackgroundChargeData
                )

                return ciolBackgroundChargeResponse
            } catch {
                throw GraphQLError.decodeError
            }
        }
    }
}
