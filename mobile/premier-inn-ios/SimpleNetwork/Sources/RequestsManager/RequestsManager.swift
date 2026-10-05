//
//  RequestsManager.swift
//  PremierInn
//
//  Created by Marcello Mascia on 03/06/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Foundation
import Alamofire
import AlamofireImage
import UIKit

public enum Result<T> {
    case success(result: T)
    case failure(error: Error)
}

public enum RequestsManagerError: Error {
    case unexpectedResponseError
    case serverError(PIDictionary?)
    case maintenanceMode
	case missingCredentials
	case missingToken
	case missingUser
}

class SNInterceptor: RequestInterceptor {
    private let retryLimit = 1
    private var _requestsManager: RequestsManager?

    public init(requestsManager: RequestsManager) {
        _requestsManager = requestsManager
    }
    typealias AdapterResult = Swift.Result<URLRequest, Error>


    public func retry(
        _ request: Request,
        for session: Alamofire.Session,
        dueTo error: Error,
        completion: @escaping (RetryResult) -> Void
    ) {
        // Only do this retry logic for graphQL calls
        guard request.response?.url?.absoluteString.contains("graphql") == true && error.asAFError?
              .responseCode == 401 && request.retryCount < retryLimit else {
            completion(.doNotRetry)
            return
        }

        printDev("\nretried after error: \(error.localizedDescription)\nretry count: \(request.retryCount)\n")
        UserSessionManager.sharedInstance.refreshUser { success, _ in
            if success == true {
                completion(.retry)
            } else {
                completion(.doNotRetry)
            }
        }
    }

    func adapt(_ urlRequest: URLRequest, for session: Alamofire.Session, completion: @escaping (AdapterResult) -> Void) {
        var urlRequest = urlRequest
        guard let token = UserSessionManager.sharedInstance.idToken else { completion(.success(urlRequest))
return }
        if urlRequest.headers.value(for: "Authorization") != nil {
            urlRequest.headers.add(.authorization(bearerToken: token))
        }

        completion(.success(urlRequest))
    }
}

public class RequestsManager: NSObject {
    // 🚨 THIS IS A TEST 🚨
    public var testForcePushValue: String?
    // 🚨 TSET A SI SIHT 🚨

    public static var shouldPinCertificates = false

    var cachedManager: Alamofire.Session?
    private var manager: Alamofire.Session {
        if let cachedManager = cachedManager {
            return cachedManager
        }

        let trustManager = ServerTrustManager(allHostsMustBeEvaluated: false, evaluators: RequestsManager.policies)
        let interceptor = SNInterceptor(requestsManager: self)
        let configuration = URLSessionConfiguration.af.default
        cachedManager = Alamofire.Session(
            configuration: configuration,
            interceptor: interceptor,
            serverTrustManager: trustManager
        )
        // cachedManager = Session(serverTrustPolicyManager: ServerTrustPolicyManager(policies: RequestsManager.policies))

        // This stops data being added to cache, .reloadIgnoringLocalCacheData still results in cacheing of some data.
        // Need to get access to device Cache.db and inspect data.
        // TODO: Implement proper caching strategy as outlined in some tech backlog tickets (image caching against server policy)
//        cachedManager?.delegate.dataTaskWillCacheResponse = { session, dataTask, cachedResponse in
//
//            if dataTask.originalRequest?.url?.scheme == "https" {
//                return nil
//            }
//
//            return cachedResponse
//        }

        return cachedManager!
    }
    private static var policies: [String: ServerTrustEvaluating] {
        var dict: [String: ServerTrustEvaluating] = [:]

        // Ignore validation for following Webservices
        let notValidatedWebservices: [Webservice] = [
            // .uatMicroservicesAlpha2,
            // .uatMicroservicesQA,
            // .uatAEM,
            // .uatSnowdrop,
            // .preProdAuth0
        ]
        notValidatedWebservices.forEach { webservice in
            dict[webservice.host] = DisabledEvaluator()
        }

        /*
         https://infinum.co/the-capsized-eight/ssl-pinning-revisited
         – PINNING
         To download a new certificate locally, run this command in the terminal:

         openssl s_client -servername api.whitbread.co.uk -connect api.whitbread.co.uk:443 -prexit -showcerts
         openssl s_client -connect api.whitbread.co.uk:443 -prexit -showcerts

         openssl s_client -connect api.whitbread.co.uk:443 < /dev/null | openssl x509 -outform DER > microservices-live.cer
         openssl s_client -connect www.premierinn.com:443 < /dev/null | openssl x509 -outform DER > premierinn-live.cer
         openssl s_client -connect secure.premierinn.com:443 < /dev/null | openssl x509 -outform DER > middleware4-live.cer
         */

        if shouldPinCertificates {
            let publicKeys = Bundle.simpleNetworkResources.af.publicKeys
            let hosts: [Webservice] = [.liveMicroservices]

            hosts.forEach { webservice in
                dict[webservice.host] = PublicKeysTrustEvaluator(
                    keys: publicKeys,
                    performDefaultValidation: true,
                    validateHost: true
                )
            }
        }

        return dict
    }

    // Not sure this should be public, I can cancel all requests from any part of the app
    // A better solution would be to make a public func that cancels requests of a specific type i.e. Login
    public func cancelConnections() {
        guard cachedManager != nil else { return }

        manager.session.invalidateAndCancel()
        cachedManager = nil
    }

    func signedRequest<T>(
        with resource: Resource<T>,
        acceptHeader: String = "application/json",
        timeout: TimeInterval = 15,
        cachePolicy: URLRequest.CachePolicy = .reloadIgnoringLocalCacheData
    ) throws -> DataRequest {
        var request = URLRequest(url: resource.url, cachePolicy: cachePolicy, timeoutInterval: timeout)
        request.httpMethod = resource.method.rawValue
        request.addValue(acceptHeader, forHTTPHeaderField: "Accept")

        resource.headers?.forEach { (key, value) in
            request.setValue(value, forHTTPHeaderField: key)
        }

        if let version = resource.version {
            request.setValue(version, forHTTPHeaderField: "version")
        }

        if let credentials = resource.authCredentials {
            let authHeader = HTTPHeader.authorization(username: credentials.username, password: credentials.password)
            request.addValue(authHeader.value, forHTTPHeaderField: authHeader.name)
        }

        if let testForcePushValue = testForcePushValue {
            request.addValue(testForcePushValue, forHTTPHeaderField: "X-iOS-PushTest")
        }

        return manager.request(try resource.encoding.encode(request, with: resource.parameters))
    }

    func loadData<T>(resource: Resource<T>, timeout: TimeInterval = 45, completion: ((Data?, Error?) -> Void)?) {
        do {
            let request = try signedRequest(with: resource, acceptHeader: "application/octet-stream", timeout: timeout)

            request.validate(statusCode: 200..<300).validateForGraphQL(url: resource.url).responseData { response in
                self.handleResponseData(response: response, resource: resource, completion: completion)
            }
        } catch {
            completion?(nil, error)
        }
    }

    func load<T>(resource: Resource<T>, timeout: TimeInterval = 45, completion: ((T?, Error?) -> Void)?) {
        do {
            let request = try signedRequest(with: resource, timeout: timeout)

            request.validate(statusCode: 200..<300).validateForGraphQL(url: resource.url).responseJSON { response in
                self.handleResponse(response: response, resource: resource, completion: completion)
            }
        } catch {
            completion?(nil, error)
        }
    }

    func load<T>(resource: Resource<T>, timeout: TimeInterval = 45) async throws -> T? {
        try await withCheckedThrowingContinuation { continuation in
            do {
                let request = try self.signedRequest(with: resource, timeout: timeout)

                request.validate(statusCode: 200..<300).validateForGraphQL(url: resource.url).responseJSON { response in
                    self.handleResponse(response: response, resource: resource, completion: { data, error in
                        if let error {
                            continuation.resume(throwing: error)
                        } else {
                            continuation.resume(returning: data)
                        }
                    })
                }
            } catch {
                continuation.resume(throwing: error)
            }
        }
    }

    func handleResponseData<T>(
        response: AFDataResponse<Data>,
        resource: Resource<T>,
        completion: ((Data?, Error?) -> Void)?
    ) {
        switch response.result {
        case .success:

            completion?(response.data, nil)

        case .failure(let error):

            completion?(nil, checkErrorMessage(data: response.data) ?? error)
        }
    }

    func handleResponse<T>(response: AFDataResponse<Any>, resource: Resource<T>, completion: ((T?, Error?) -> Void)?) {
        switch response.result {
        case .success(let data):
            do {
                let result = try resource.parse(data)

                completion?(result, nil)
            } catch {
                completion?(nil, error)
            }

        case .failure(let error):

            completion?(nil, checkErrorMessage(data: response.data) ?? error)
        }
    }

    func checkErrorMessage(data: Data?) -> Error? {
        guard let data = data else { return nil }
        guard let dict = try? JSONSerialization.jsonObject(with: data) as? PIDictionary else { return nil }

        return RequestsManagerError.serverError(dict)
    }

    public static func postRequest(with url: URL, parameters: [String: String]) throws -> URLRequest {
        let resource = Resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: URLEncoding.default,
            headers: nil
        ) {_ in }

        var request = URLRequest(url: resource.url)
        request.httpMethod = resource.method.rawValue

        return try resource.encoding.encode(request, with: resource.parameters)
    }

    public static func setupImageCache() {
        UIImageView.af.sharedImageDownloader = ImageDownloader(
            maximumActiveDownloads: 10,
            imageCache: AutoPurgingImageCache(
                memoryCapacity: 50 * 1024 * 1024,
                preferredMemoryUsageAfterPurge: 40 * 1024 * 1024
            )
        )
    }

    public static func setupCountries() {
        Country.refreshCountries()
    }
}

extension DataRequest {
    func checkGraphQLErrorsForExpiredToken(data: Data?) -> Bool {
        guard let data = data else { return false }
        guard let dict = try? JSONSerialization.jsonObject(with: data) as? PIDictionary else { return false }
         // Check the errors array
        guard let errorsArray = dict["errors"] as? [PIDictionary], errorsArray.isEmpty == false else { return false }
        // If the errorType is 401 we treat that as the session has expired
        let isAppSyncError401 = errorsArray.contains(where: { $0["errorType"] as? String == "401" })
        let isApolloError401 = errorsArray
            .contains(where: { ($0["extensions"] as? PIDictionary)?["errorType"] as? Int == 401 })

        return isAppSyncError401 || isApolloError401
    }

    public func validateForGraphQL(url: URL?) -> Self {
        guard url?.absoluteString.contains("graphql") == true else { return self }
        return validate { _, _, data in
            if self.checkGraphQLErrorsForExpiredToken(data: data) {
                let reason: AFError.ResponseValidationFailureReason = .unacceptableStatusCode(code: 401)
                return .failure(AFError.responseValidationFailed(reason: reason))
            } else {
                return .success(())
            }
        }
    }
}
