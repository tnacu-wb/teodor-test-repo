//
//  RouterConfigurator.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 11/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

protocol RouterConfigurable {
    static func configure()
    static var title: String { get }
}

enum RouterConfig: RouterConfigurable {
    static func configure() {
#if DEV
        Router.current = SettingsManager.sharedInstance.currentRouterType.router
        Router.isRestMigrated = Router.current.isRouterRestMigrated
        Router.isAutocompleteMigrated = Router.current.isRouterAutocompleteMigrated
        Router.versioning = SettingsManager.sharedInstance.versioning
        RequestsManager.shouldPinCertificates = SettingsManager.shouldPinCertificates
#else
        Router.current = .production
        Router.isRestMigrated = Router.current.isRouterRestMigrated
        Router.isAutocompleteMigrated = Router.current.isRouterAutocompleteMigrated
        RequestsManager.shouldPinCertificates = false
#endif
        AkamaiProtection.configureSDK(baseURLString: Router.baseURLString)
    }

    static var title: String {
        switch Router.current {
        case .production:
            return "Prod"
        case .developmentGraphQL:
            return "⚛️DEV"
        case .uatGraphQL:
            return "⚛️UAT"
        case .qaGraphQLDit:
            return "🐛⚛️DIT"
        case .qaGraphQLSit:
            return "🐛⚛️💩"
        case .demoGraphQL:
            return "⚛️Demo"
        case .preprodGraphQL:
            return "⚛️PreProd"
        case .perfGraphQL:
            return "🐛⚛️Perf"
        case .hulkGraphQL:
            return "⚛️Hulk"
        case .wandaGraphQL:
            return "⚛️Wanda"
        default:
            return "CUST"
        }
    }
}

private extension Router {
    var isRouterRestMigrated: Bool {
        switch self {
        case .production, .hulkGraphQL, .wandaGraphQL:
            return SettingsManager.sharedInstance.featureUseOperaRestProdEndpoint == true
        default:
            return SettingsManager.sharedInstance.featureUseOperaRestLowerEnvironmentEndpoint == true
        }
    }

    var isRouterAutocompleteMigrated: Bool {
        switch self {
        case .production, .hulkGraphQL, .wandaGraphQL:
            return SettingsManager.sharedInstance.featureUseSnowdropOperaProdEndpoint == true
        default:
            return SettingsManager.sharedInstance.featureUseSnowdropOperaLowerEnvEndpoint == true
        }
    }
}
