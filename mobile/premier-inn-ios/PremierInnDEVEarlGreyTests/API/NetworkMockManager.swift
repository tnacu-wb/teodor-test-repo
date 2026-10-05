//
//  NetworkMockManager.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 30/04/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import Hippolyte

class NetworkMockManager {

     static func dataFromJsonMock(fileName: String) -> Data? {

        guard let fileURL = Bundle(for: NetworkMockManager.self).url(forResource: fileName, withExtension: "json") else { print("Missing file: \(fileName).json"); return nil }
        guard let data = try? Data(contentsOf: fileURL) else { print("Unable to get content of file: \(fileName).json"); return nil }

        return data
    }

    static func addStubRequest(method: HTTPMethod, urlStringPattern: String, jsonName: String, statusCode: Int = 200) {

        guard let stubbedRequest = stubRequest(method: method, urlStringPattern: urlStringPattern, jsonName: jsonName, statusCode: statusCode) else { return }

        Hippolyte.shared.add(stubbedRequest: stubbedRequest)
    }

    static func stubRequest(method: HTTPMethod, urlStringPattern: String, jsonName: String, statusCode: Int = 200) -> StubRequest? {

        guard let urlRegex = try? NSRegularExpression(pattern: urlStringPattern, options: []) else {

            print("Unable to create regex with pattern: \(urlStringPattern) for \(jsonName).json")
            return nil
        }

        var response = StubResponse()
        response.body = NetworkMockManager.dataFromJsonMock(fileName: jsonName)
        response.statusCode = statusCode

        var request = StubRequest(method: method, urlMatcher: RegexMatcher(regex: urlRegex))
        request.response = response

        return request
    }

    static func addStubRequest(method: HTTPMethod, urlString: String, jsonName: String, statusCode: Int = 200) {

        guard let stubbedRequest = stubRequest(method: method, urlString: urlString, jsonName: jsonName, statusCode: statusCode) else { return }

        Hippolyte.shared.add(stubbedRequest: stubbedRequest)
    }

    static func stubRequest(method: HTTPMethod, urlString: String, jsonName: String, statusCode: Int = 200) -> StubRequest? {

        guard let url = URL(string: urlString) else {

            print("Unable to create URL with string: \(urlString) for \(jsonName).json")
            return nil
        }

        let response = StubResponse.Builder()
            .stubResponse(withStatusCode: statusCode)
            .addBody(NetworkMockManager.dataFromJsonMock(fileName: jsonName)!)
            .build()

        let request = StubRequest.Builder()
            .stubRequest(withMethod: method, url: url)
            .addResponse(response)
            .build()

        return request
    }
}
