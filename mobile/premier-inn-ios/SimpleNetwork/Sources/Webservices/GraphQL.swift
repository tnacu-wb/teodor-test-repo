//
//  GraphQL.swift
//  SimpleNetwork
//
//  Created by Georgios Aikaterinakis on 11/05/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation
import Alamofire

public extension String {
    static let queryKey = "query"
    static let variablesKey = "variables"
    static let dataKey = "data"
    static let graphQLPath = "/graphql"

    static let apolloClientNameKey = "apollographql-client-name"
    static let apolloClientNameValue = "ios"
    static let apolloClientVersionKey = "apollographql-client-version"
    static let apolloClientVersionValue = "1.0"
}

public enum GraphQLError: Error {
    case decodeError
    case missingData
    case missingHotelCode
    case missingToken
}

class GraphQL: Webservice, WebserviceProtocol {
    let apiHost: String

    override init(scheme: String, host: String, port: Int? = nil) {
        self.apiHost = host

        super.init(scheme: scheme, host: host, port: port)
    }

    internal func resource<T>(
        url: URL,
        parameters: PIDictionary? = nil,
        data: Data? = nil,
        method: HTTPMethod,
        encoding: ParameterEncoding,
        headers: [String: String]? = nil,
        authCredentials: AuthCredentials? = nil,
        shouldSendAuthToken: Bool = false,
        parse: @escaping (Any) throws -> T?
    ) -> Resource<T> {
        var headers = headers ?? [String: String]()

        if let token = UserSessionManager.sharedInstance.idToken,
           BookingDetails.sharedInstance.bookingMode == .business || shouldSendAuthToken {
            headers["Authorization"] = "Bearer \(token)"
        }

        // Apollo
        headers[.apolloClientNameKey] = .apolloClientNameValue
        headers[.apolloClientVersionKey] = .apolloClientVersionValue

        return Resource(
            url: url,
            parameters: parameters,
            data: data,
            method: method,
            encoding: encoding,
            headers: headers,
            authCredentials: authCredentials,
            parse: parse
        )
    }
}

extension GraphQL {
    func getEnvironmentURL(host: String) -> String {
        let newUrl = host.replacingOccurrences(of: "api", with: "https://www")

        return newUrl
    }

    static func getBookingChannel(business: Bool) -> PIDictionary {
        var dict = PIDictionary()

        dict["channel"] = business ? Channel.BB.rawValue : Channel.PI.rawValue
        dict["subchannel"] = Constants.bookingChannel
        dict["language"] = LanguageManager.supportedLanguage.rawValue

        return dict
    }

    static func getBookingChannel(bookingDetails: BookingDetails) -> PIDictionary {
        var dict = PIDictionary()

        dict["channel"] = bookingDetails.bookingChannel.rawValue
        dict["subchannel"] = Constants.bookingChannel
        dict["language"] = LanguageManager.supportedLanguage.rawValue

        return dict
    }
}
