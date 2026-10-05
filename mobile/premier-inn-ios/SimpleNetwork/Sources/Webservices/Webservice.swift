//
//  Webservice.swift
//  PremierInn
//
//  Created by Marcello Mascia on 04/01/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation

enum ResponseParserError: LocalizedError {
    case keyNotFound(String)

	var errorDescription: String? {
		switch self {
		case .keyNotFound(let key):
			return "Json parse error: key \"\(key)\" not found"
		}
	}
}

enum WebserviceError: LocalizedError {
    case missingUserSession
    case missingBearerToken
    case invalidPath(String)
    case notImplemented(String)
    case missingRequiredValues(String)

    var errorDescription: String? {
        switch self {
        case .invalidPath(let path):
			return "Invalid path: " + path
        case .notImplemented(let functionName):
			return functionName + " not implemented"
        case .missingUserSession:
			return NSLocalizedString("User session not valid", comment: "")
        case .missingBearerToken:
            return NSLocalizedString("Missing User token", comment: "")
        case .missingRequiredValues(let value):
            return value + " Missing"
        }
    }
}

public struct WebserviceVersion: Hashable {
    let webserviceAction: WebserviceAction
    let version: String

    public init(webserviceAction: WebserviceAction, version: String) {
        self.webserviceAction = webserviceAction
        self.version = version
    }
}
public typealias Versioning = Set<WebserviceVersion>

extension Versioning {
    subscript(service: WebserviceAction) -> String? {
        first { $0.webserviceAction == service }?.version
    }
}

public class Webservice {
    // microservices
    static let liveMicroservices = Microservices(scheme: "https", host: "api.whitbread.co.uk")
    static let migratedLiveMicroservices = Microservices(scheme: "https", host: "restapi.premierinn.com")

    static let uatMicroservicesAlpha2 = Microservices(scheme: "https", host: "api-uat.whitbread.co.uk")
    static let migratedDevMicroservices = Microservices(scheme: "https", host: "restapi.dev.premierinn.digital")
    static let migratedDitMicroservices = Microservices(scheme: "https", host: "restapi.dit.premierinn.digital")
    static let migratedUatMicroservices = Microservices(scheme: "https", host: "restapi.uat.premierinn.digital")
    static let migratedSitMicroservices = Microservices(scheme: "https", host: "restapi.sit.premierinn.digital")
    static let migratedDemoMicroservices = Microservices(scheme: "https", host: "restapi.demo.premierinn.digital")
    static let migratedPerfMicroservices = Microservices(scheme: "https", host: "restapi.perf.premierinn.digital")
    static let migratedPreProdMicroservices = Microservices(scheme: "https", host: "restapi.preprod.premierinn.digital")

    // auth0
    static let productionMigratedAuth0 = Auth0ConfigurableRealmService(
        scheme: "https",
        host: "api.whitbread.co.uk",
        clientId: Auth0ConfigurableRealmService.ClientId.production,
        domain: Auth0ConfigurableRealmService.Domain.production,
        realm: "pi-prod"
    )
    static let productionMigratedBBAuth0 = Auth0ConfigurableRealmService(
        scheme: "https",
        host: "api.whitbread.co.uk",
        clientId: Auth0ConfigurableRealmService.ClientId.production,
        domain: Auth0ConfigurableRealmService.Domain.production,
        realm: "bb-prod"
    )

    static let migratedUatAuth0 = Auth0ConfigurableRealmService(
        scheme: "https",
        host: "api-uat.whitbread.co.uk",
        clientId: Auth0ConfigurableRealmService.ClientId.development,
        domain: Auth0ConfigurableRealmService.Domain.development,
        realm: "pi-uat"
    )
    static let migratedBBUatAuth0 = Auth0ConfigurableRealmService(
        scheme: "https",
        host: "api-uat.whitbread.co.uk",
        clientId: Auth0ConfigurableRealmService.ClientId.development,
        domain: Auth0ConfigurableRealmService.Domain.development,
        realm: "bb-uat"
    )
    static let migratedSitAuth0 = Auth0ConfigurableRealmService(
        scheme: "https",
        host: "api-uat.whitbread.co.uk",
        clientId: Auth0ConfigurableRealmService.ClientId.development,
        domain: Auth0ConfigurableRealmService.Domain.development,
        realm: "pi-sit"
    )
    static let migratedBBSitAuth0 = Auth0ConfigurableRealmService(
        scheme: "https",
        host: "api-uat.whitbread.co.uk",
        clientId: Auth0ConfigurableRealmService.ClientId.development,
        domain: Auth0ConfigurableRealmService.Domain.development,
        realm: "bb-sit"
    )
    static let migratedDemoAuth0 = Auth0ConfigurableRealmService(
        scheme: "https",
        host: "api-uat.whitbread.co.uk",
        clientId: Auth0ConfigurableRealmService.ClientId.development,
        domain: Auth0ConfigurableRealmService.Domain.development,
        realm: "pi-uat"
    )
    static let migratedBBDemoAuth0 = Auth0ConfigurableRealmService(
        scheme: "https",
        host: "api-uat.whitbread.co.uk",
        clientId: Auth0ConfigurableRealmService.ClientId.development,
        domain: Auth0ConfigurableRealmService.Domain.development,
        realm: "bb-uat"
    )
    static let migratedPerfAuth0 = Auth0ConfigurableRealmService(
        scheme: "https",
        host: "api-uat.whitbread.co.uk",
        clientId: Auth0ConfigurableRealmService.ClientId.development,
        domain: Auth0ConfigurableRealmService.Domain.development,
        realm: "pi-perf"
    )
    static let migratedBBPerfAuth0 = Auth0ConfigurableRealmService(
        scheme: "https",
        host: "api-uat.whitbread.co.uk",
        clientId: Auth0ConfigurableRealmService.ClientId.development,
        domain: Auth0ConfigurableRealmService.Domain.development,
        realm: "bb-perf"
    )


    // GraphQL
    static let developmentGraphQL = GraphQL(scheme: "https", host: "api.dev.premierinn.digital")
    static let ditGraphQL = GraphQL(scheme: "https", host: "api.dit.premierinn.digital")
    static let uatGraphQL = GraphQL(scheme: "https", host: "api.uat.premierinn.digital")
    static let sitGraphQL = GraphQL(scheme: "https", host: "api.sit.premierinn.digital")
    static let perfGraphQL = GraphQL(scheme: "https", host: "api.perf.premierinn.digital")
    static let demoGraphQL = GraphQL(scheme: "https", host: "api.demo.premierinn.digital")
    static let preprodGraphQL = GraphQL(scheme: "https", host: "api.preprod.premierinn.digital")
    static let productionGraphQL = GraphQL(scheme: "https", host: "api.premierinn.com")
    static let hulkGraphQL = GraphQL(scheme: "https", host: "api.hulk.premierinn.com")
    static let wandaGraphQL = GraphQL(scheme: "https", host: "api.wanda.premierinn.com")

    let scheme: String
    let host: String
    let port: Int?
    var baseURL: URL? {
        var components = URLComponents()
        components.scheme = scheme
        components.host = host
        components.port = port

        return components.url
    }

    init(scheme: String, host: String, port: Int? = nil) {
        self.scheme = scheme
        self.host = host
        self.port = port
    }
}

extension Webservice: Equatable {
    public static func == (lhs: Webservice, rhs: Webservice) -> Bool {
        lhs.scheme == rhs.scheme && lhs.host == rhs.host
    }
}
