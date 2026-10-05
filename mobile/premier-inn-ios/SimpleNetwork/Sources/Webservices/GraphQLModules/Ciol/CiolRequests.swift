//
//  CiolRequests.swift
//  SimpleNetwork
//
//  Created by Muresan, Andreea (Cognizant) on 16.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation
import Alamofire

public enum CiolRequestType: String {
    case checkIn = "confirmPreCheckIn"
    case checkOut = "confirmPreCheckOut"
}

extension GraphQL {
    func confirmPreCheckInOut(
        basketReference: String,
        type: CiolRequestType,
        isCiol: Bool = false
    ) throws -> Resource<ConfirmPreCheckInOut> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }
        var parameters = PIDictionary()

        // Only include isCiol parameter for check-in, not for check-out
        let includeIsCiol = (type == .checkIn)

        parameters[.queryKey] = GraphQL.confirmPreCheckInOutQuery(resolver: type.rawValue, includeIsCiol: includeIsCiol)
        parameters[.variablesKey] = GraphQL.confirmPreCheckInOutVariables(
            basketReference: basketReference,
            isCiol: isCiol,
            includeIsCiol: includeIsCiol
        )

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary,
                  let confirmPreCheckInOut = dataDict[type.rawValue] as? PIDictionary else { throw GraphQLError.missingData }
            do {
                let infoData = try JSONSerialization.data(withJSONObject: confirmPreCheckInOut)
                let confirmPreCheckInOut = try JSONDecoder().decode(ConfirmPreCheckInOut.self, from: infoData)
                return confirmPreCheckInOut
            } catch {
                throw GraphQLError.decodeError
            }
        }
    }
}
