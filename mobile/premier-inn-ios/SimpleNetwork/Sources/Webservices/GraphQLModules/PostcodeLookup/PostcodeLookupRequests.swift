//
//  PostcodeLookupRequests.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 14/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation
import Alamofire

extension GraphQL {
    func addressLookup(postCode: String) throws -> Resource<[AddressSummary]> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.addressLookUpQuery
        parameters[.variablesKey] = try GraphQL.partialAddressVariables(postCode: postCode)

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default
        ) { data -> [AddressSummary]? in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let addressDict = dataDict["partialAddress"] as? [PIDictionary]
                else { throw RequestsManagerError.unexpectedResponseError }

            return addressDict.compactMap { try? AddressSummary(dictionary: $0, postCode: postCode) }
        }
    }

    func addressLookup(postCode: String, id: String) throws -> Resource<Address> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.formattedAddressQuery
        guard let encodedId = id.addingPercentEncoding(withAllowedCharacters: .afURLQueryAllowed)
            else { throw WebserviceError.missingRequiredValues("Address Identifier") }

        parameters[.variablesKey] = try GraphQL.formattedAddressVariables(id: encodedId)
        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default
        ) { data -> Address? in
            guard let data = data as? PIDictionary, let dataDict = data["data"] as? PIDictionary,
                  let formattedAddressDict = dataDict["formattedAddress"] as? PIDictionary
            else { throw RequestsManagerError.unexpectedResponseError }

            return try? Address(dictionary: formattedAddressDict)
        }
    }
}
