//
//  DatatransPaymentInteractorTests.swift
//  PremierInnTests
//
//  Copyright © 2025 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork
@testable import PremierInn_DEV

// MARK: - Mock Data Provider

private class MockDatatransPaymentDataProvider: DatatransPaymentDataProvider {
    var response: DatatransPaymentSessionResponse?
    var error: Error?

    func initMobileSDKPayment(
        basketId: String,
        completion: @escaping (_ response: DatatransPaymentSessionResponse?, _ error: Error?) -> Void
    ) {
        completion(response, error)
    }
}

// MARK: - Tests

class DatatransPaymentInteractorTests: XCTestCase {

    private var mockDataProvider: MockDatatransPaymentDataProvider!
    private var interactor: DatatransPaymentInteractor!

    override func setUp() {
        super.setUp()
        mockDataProvider = MockDatatransPaymentDataProvider()
        interactor = DatatransPaymentInteractor(basketId: "test-basket-id", dataProvider: mockDataProvider)
    }

    override func tearDown() {
        interactor = nil
        mockDataProvider = nil
        super.tearDown()
    }

    // MARK: - Success

    func testSuccessResponseMapsToSuccessWithCorrectTransactionId() {
        let expectedTransactionId = "txn-12345-abcde"
        let sessionResponse = DatatransPaymentSessionResponse(transactionId: expectedTransactionId)
        mockDataProvider.response = sessionResponse

        let expectation = expectation(description: "completion called")
        var capturedResult: Result<DatatransPaymentSessionResponse, DatatransPaymentFlowError>?

        interactor.initiatePaymentSession { result in
            capturedResult = result
            expectation.fulfill()
        }

        waitForExpectations(timeout: 1)

        switch capturedResult {
        case .success(let response):
            XCTAssertEqual(response.transactionId, expectedTransactionId)
        default:
            XCTFail("Expected .success but got \(String(describing: capturedResult))")
        }
    }

    // MARK: - Error Code Mapping

    func testEachErrorCodeMapsToCorrectFlowError() {
        let mapping: [(DatatransPaymentErrorCode, DatatransPaymentFlowError)] = [
            (.invalidRequest, .invalidRequest),
            (.basketNotFound, .basketNotFound),
            (.bookingAlreadyPaid, .bookingAlreadyPaid),
            (.bookingAlreadyConfirmed, .bookingAlreadyConfirmed),
            (.paymentMethodNotAvailable, .paymentMethodNotAvailable),
            (.gatewayError, .gatewayError),
            (.serviceUnavailable, .serviceUnavailable)
        ]

        for (errorCode, expectedFlowError) in mapping {
            let errorBody: [String: Any] = [
                "error": [
                    "code": errorCode.rawValue,
                    "message": "Test error message"
                ]
            ]
            let responseData = try? JSONSerialization.data(withJSONObject: errorBody)
            let nsError = NSError(
                domain: "TestDomain",
                code: 400,
                userInfo: ["responseData": responseData as Any]
            )
            mockDataProvider.response = nil
            mockDataProvider.error = nsError

            let expectation = expectation(description: "completion for \(errorCode.rawValue)")
            var capturedResult: Result<DatatransPaymentSessionResponse, DatatransPaymentFlowError>?

            interactor.initiatePaymentSession { result in
                capturedResult = result
                expectation.fulfill()
            }

            waitForExpectations(timeout: 1)

            switch capturedResult {
            case .failure(let flowError):
                XCTAssertEqual(
                    String(describing: flowError),
                    String(describing: expectedFlowError),
                    "Error code \(errorCode.rawValue) should map to \(expectedFlowError)"
                )
            default:
                XCTFail("Expected .failure(\(expectedFlowError)) for code \(errorCode.rawValue)")
            }
        }
    }

    // MARK: - Nil Response and Nil Error

    func testNilResponseAndNilErrorMapsToUnknown() {
        mockDataProvider.response = nil
        mockDataProvider.error = nil

        let expectation = expectation(description: "completion called")
        var capturedResult: Result<DatatransPaymentSessionResponse, DatatransPaymentFlowError>?

        interactor.initiatePaymentSession { result in
            capturedResult = result
            expectation.fulfill()
        }

        waitForExpectations(timeout: 1)

        switch capturedResult {
        case .failure(let flowError):
            XCTAssertEqual(String(describing: flowError), String(describing: DatatransPaymentFlowError.unknown))
        default:
            XCTFail("Expected .failure(.unknown) but got \(String(describing: capturedResult))")
        }
    }

    // MARK: - Timeout

    func testTimeoutErrorMapsToTimeout() {
        mockDataProvider.response = nil
        mockDataProvider.error = URLError(.timedOut)

        let expectation = expectation(description: "completion called")
        var capturedResult: Result<DatatransPaymentSessionResponse, DatatransPaymentFlowError>?

        interactor.initiatePaymentSession { result in
            capturedResult = result
            expectation.fulfill()
        }

        waitForExpectations(timeout: 1)

        switch capturedResult {
        case .failure(let flowError):
            XCTAssertEqual(String(describing: flowError), String(describing: DatatransPaymentFlowError.timeout))
        default:
            XCTFail("Expected .failure(.timeout) but got \(String(describing: capturedResult))")
        }
    }

    // MARK: - No Connectivity

    func testConnectivityErrorMapsToNoConnectivity() {
        mockDataProvider.response = nil
        mockDataProvider.error = URLError(.notConnectedToInternet)

        let expectation = expectation(description: "completion called")
        var capturedResult: Result<DatatransPaymentSessionResponse, DatatransPaymentFlowError>?

        interactor.initiatePaymentSession { result in
            capturedResult = result
            expectation.fulfill()
        }

        waitForExpectations(timeout: 1)

        switch capturedResult {
        case .failure(let flowError):
            XCTAssertEqual(
                String(describing: flowError),
                String(describing: DatatransPaymentFlowError.noConnectivity)
            )
        default:
            XCTFail("Expected .failure(.noConnectivity) but got \(String(describing: capturedResult))")
        }
    }
}
