//
//  UpdateCiolStatusRequests.swift
//  SimpleNetwork
//
//  Created by Rodrigues, Seymour (Contractor) on 11/03/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import Alamofire

extension GraphQL {
    /// The purpose of this request is to update the Udfc20 criteria on the BE which updates Opera.
    /// The response received from BE has no use for mobile client.
    ///
    /// [CTECH-5851](https://whitbreadis.atlassian.net/browse/CTECH-5851)
    func updateCiolStatus(payload: UpdateCiolStatusPayload) throws -> Resource<UpdateCiolStatusResponse> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.updateCiolStatusQuery
        parameters[.variablesKey] = GraphQL.updateCiolStatusParameters(payload: payload)

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default
        ) { data -> UpdateCiolStatusResponse? in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }

            do {
                let updateCiolStatusData = try JSONSerialization.data(withJSONObject: dataDict)
                let updateCiolStatusResponse = try JSONDecoder().decode(
                    UpdateCiolStatusResponse.self,
                    from: updateCiolStatusData
                )
                return updateCiolStatusResponse
            } catch {
                throw GraphQLError.decodeError
            }
        }
    }
}
