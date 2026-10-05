//
//  Auth0Service.swift
//  SimpleNetwork
//
//  Created by Marcello Mascia on 14/08/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import Alamofire
import Auth0
import SimpleKeychain

private extension String {
    static let scope = "openid profile user_id offline_access"
    static let operaCompanyIdKey = "https://premierinn.com/operaCompanyId"
}

final class Auth0ConfigurableRealmService: Webservice {
    enum Domain {
        static let development = "auth0.premierinn.digital"
        static let production = "auth0.premierinn.com"
    }

    enum ClientId {
        static let development = "1v4m1df7ZJCcEEkb6drvdgTtAY3hYgry"
        static let production = "VKHFCruuP9oLTIsh2Irf1eW6Fv6pWrqa"
    }

    private let authentication: Authentication
    private var credentialsManager: CredentialsManager
    private let realm: String

    init(scheme: String, host: String, port: Int? = nil, clientId: String, domain: String, realm: String) {
        Auth0KeyedUnarchiverMapping.register()

        self.authentication = Auth0.authentication(clientId: clientId, domain: domain)
        self.credentialsManager = CredentialsManager(
            authentication: self.authentication,
            storage: SimpleKeychain(accessibility: .whenUnlockedThisDeviceOnly)
        )
        self.realm = realm

        super.init(scheme: scheme, host: host, port: port)
    }

    private func extractInfo(from token: String) {
        let jwtDecoder = JWTDecoder()
        let decryptedToken = jwtDecoder.decode(jwtToken: token)
        UserSessionManager.sharedInstance.operaCompanyId = decryptedToken[.operaCompanyIdKey] as? String
    }
}

extension Auth0ConfigurableRealmService: WebserviceProtocol {
    func login(username: String, password: String, isBusiness: Bool = false, completion: @escaping (Result<Bool>) -> Void) {
        self.authentication
            .login(usernameOrEmail: username, password: password, realmOrConnection: realm, audience: nil, scope: .scope)
            .start { result in
            switch result {
            case .failure(let error):
                _ = self.credentialsManager.clear()

                completion(.failure(error: error))

            case .success(let credentials):
                UserSessionManager.sharedInstance.idToken = credentials.idToken
                _ = self.credentialsManager.store(credentials: credentials)

                completion(.success(result: true))
            }
        }
    }

    func getUserCredentials(completion: @escaping (Result<Bool>) -> Void) {
        guard credentialsManager.hasValid() else { completion(.failure(error: RequestsManagerError.missingCredentials))
return }

        credentialsManager.credentials { result in
            switch result {
            case .success(let credentials):
                guard credentials.idToken.isEmpty == false
                    else { return completion(.failure(error: RequestsManagerError.missingToken)) }

                UserSessionManager.sharedInstance.idToken = credentials.idToken

                self.extractInfo(from: credentials.idToken)

                completion(.success(result: true))
            case .failure(let error):
                return completion(.failure(error: error))
            }
        }
    }

    func logout() throws {
        UserSessionManager.sharedInstance.idToken = nil
        UserSessionManager.sharedInstance.operaCompanyId = nil
        _ = credentialsManager.clear()
    }
}
