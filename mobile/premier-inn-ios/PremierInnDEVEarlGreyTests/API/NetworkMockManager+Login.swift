//
//  MockLoginRequests.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 04/05/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import Hippolyte

struct LoginRequestBody: Codable, Hashable {
    let username: String
    let password: String
    let grant_type: String
    let client_id: String
    let scope: String
}

extension NetworkMockManager {

    static func stubLoginRequest(method: HTTPMethod, urlString: String, jsonName: String, username: String = "test@test.com", password: String) {

        var request = stubRequest(method: method, urlString: urlString, jsonName: jsonName)

        let requestBody = LoginRequestBody(
            username: username,
            password: password,
            grant_type: "password",
            client_id: "1v4m1df7ZJCcEEkb6drvdgTtAY3hYgry",
            scope: "openid profile user_id offline_access")
        let matcher = JSONMatcher<LoginRequestBody>(object: requestBody)

        request?.bodyMatcher = matcher

        guard let stubLoginRequest = request else {
            print("could not add stub login request")
            return
        }

        Hippolyte.shared.add(stubbedRequest: stubLoginRequest)
    }
}
