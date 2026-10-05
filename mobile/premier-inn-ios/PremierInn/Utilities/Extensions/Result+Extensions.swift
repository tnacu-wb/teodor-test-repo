//
//  Result+Extensions.swift
//  PremierInn
//
//  Created by Marcello Mascia on 31/10/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation

enum UserSessionError: Error {
    case sessionExpired
    case generic(Error)
}

extension UserSessionError: LocalizedError {
    var errorDescription: String? {
        switch self {
        case .generic(let error):
            return error.localizedDescription
        default:
            return String(describing: self)
        }
    }
}

extension Result where T == User {
    func handle() -> UserSessionError? {
        switch self {
        case .success(let user):
            UserSessionManager.sharedInstance.piLoggedIn(with: user)
            AnalyticsManager.shared.trackState(PIAnalytics.StateNames.login, data: LoginInteractor.analyticsDictionary)
            AnalyticsManager.shared.log(event: FirebaseAnalytics.Event.login, parameters: nil)

            return nil

        case .failure(let error):
            AnalyticsManager.shared.track(error: error, name: PIAnalytics.Error.remoteLoginError)

            guard error.isSessionExpired else { return .generic(error) }

            return .sessionExpired
        }
    }
}

extension Error {
    var isSessionExpired: Bool {
        guard let error = self as? RequestsManagerError else { return false }

        switch error {
        case .serverError(let dict):
            if let details = dict?["details"] as? [String] {
                return details.first?.containsTracesOfAnExpiredSession() == true
            }
        default:
            break
        }

        return false
    }
}

private extension String {
    func containsTracesOfAnExpiredSession() -> Bool {
        // Let's check for an easy one first...
        if contains("SESSION_ID_NOT_FOUND") {
            return true
        }

        // Then check for something more specific...
        let string = self as NSString
        let regex = try? NSRegularExpression(pattern: "SessionID [a-z0-9]+ is no longer valid", options: .caseInsensitive)
        let matches = regex?.matches(in: self, options: [], range: NSRange(location: 0, length: string.length))

        return matches?.isEmpty == false
    }
}
