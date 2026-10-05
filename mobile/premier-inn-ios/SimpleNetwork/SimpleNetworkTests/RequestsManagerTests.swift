//
//  RequestsManagerTests.swift
//  SimpleNetworkTests
//
//  Created by Marcello Mascia on 30/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import Alamofire
@testable import SimpleNetwork

class RequestsManagerTests: XCTestCase {

    override func setUp() {
        super.setUp()

    }
    
    override func tearDown() {

		super.tearDown()
    }
    
    func testSignedRequest() {

		let manager = RequestsManager()

        // set idToken to nil for fresh test
        UserSessionManager.sharedInstance.idToken = nil

		let resource = Resource(
			url: URL(string: "http://www.google.com")!,
			method: .post,
            encoding: PIURLEncoding.default,
			headers: ["test": "excellent"],
            authCredentials: (username: "pippo", password: "baudo", business: false)
		) { _ in

		}

		do {
            let expectation = self.expectation(description: "signedRequest initialisation")

			let request = try manager.signedRequest(with: resource, timeout: 10)
            DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 1, execute: { expectation.fulfill() })

            wait(for: [expectation], timeout: 2)

			XCTAssertEqual(request.request?.url?.absoluteString, "http://www.google.com")
			XCTAssertEqual(request.request?.httpMethod, "POST")
			XCTAssertEqual(request.request?.allHTTPHeaderFields, ["test": "excellent", "Accept": "application/json", "Authorization": "Basic cGlwcG86YmF1ZG8="])
		} catch {
			print(error)
			XCTFail()
		}
	}

	func testPostRequest() {

		do {
			let request = try RequestsManager.postRequest(with: URL(string: "http://www.google.com")!, parameters: ["very": "good"])

			XCTAssertEqual(request.url?.absoluteString, "http://www.google.com")
			XCTAssertEqual(request.httpMethod, "POST")
			XCTAssertEqual(request.allHTTPHeaderFields, ["Content-Type": "application/x-www-form-urlencoded; charset=utf-8"])
			XCTAssertEqual(String(data: request.httpBody!, encoding: .utf8), "very=good")
		} catch {
			print(error)
			XCTFail()
		}
	}

    func testResponse_Success() {

        let manager = RequestsManager()

        let resource = Resource(
            url: URL(string: "http://www.google.com")!,
            method: .post,
            encoding: PIURLEncoding.default
        ) { _ in

        }

        let response = AFDataResponse<Any>(
            request: URLRequest(url: URL(string: "http://www.google.com")!),
            response: HTTPURLResponse(url: URL(string: "http://www.google.com")!, statusCode: 200, httpVersion: nil, headerFields: nil),
            data: nil,
            metrics: URLSessionTaskMetrics(),
            serializationDuration: 5,
            result: .success("OK")
        )

        manager.handleResponse(response: response, resource: resource) { (result, error) in
            XCTAssertNil(error)
        }
    }

    func testResponse_Failure() {

        let manager = RequestsManager()

        let resource = Resource(
            url: URL(string: "http://www.google.com")!,
            method: .post,
            encoding: PIURLEncoding.default
        ) { _ in

        }

        let response = AFDataResponse<Any>(
            request: URLRequest(url: URL(string: "http://www.google.com")!),
            response: HTTPURLResponse(url: URL(string: "http://www.google.com")!, statusCode: 400, httpVersion: nil, headerFields: nil),
            data: nil,
            metrics: URLSessionTaskMetrics(),
            serializationDuration: 5,
            result: .failure(AFError.explicitlyCancelled)
        )

        manager.handleResponse(response: response, resource: resource) { (result, error) in
            XCTAssertEqual(error?.localizedDescription, "Request explicitly cancelled.")
        }
    }
}
