//
//  DatatransPaymentPresenterTests.swift
//  PremierInnTests
//
//  Copyright © 2025 Whitbread. All rights reserved.
//

import XCTest
import Datatrans
@testable import SimpleNetwork
@testable import PremierInn_DEV

// MARK: - Mock View

private class MockDatatransPaymentView: UIViewController, DatatransPaymentViewProtocol {

    var showLoadingCalled = false
    var hideLoadingCalled = false

    func showLoading() {
        showLoadingCalled = true
    }

    func hideLoading() {
        hideLoadingCalled = true
    }
}

// MARK: - Mock Interactor

private class MockDatatransPaymentInteractor: DatatransPaymentInteractorProtocol {

    var initiatePaymentSessionCallCount = 0
    var result: Result<DatatransPaymentSessionResponse, DatatransPaymentFlowError>?

    func initiatePaymentSession(
        completion: @escaping (Result<DatatransPaymentSessionResponse, DatatransPaymentFlowError>) -> Void
    ) {
        initiatePaymentSessionCallCount += 1
        if let result = result {
            completion(result)
        }
    }
}

// MARK: - Mock Router

private class MockDatatransPaymentRouter: DatatransPaymentRouterProtocol {

    var presentSDKCalled = false
    var presentSDKTransactionId: String?
    var presentSDKController: UIViewController?

    var showSuccessDialogCalled = false
    var showErrorDialogCalled = false
    var showErrorDialogMessage: String?
    var dismissCalled = false

    func presentSDK(transactionId: String, from controller: UIViewController) {
        presentSDKCalled = true
        presentSDKTransactionId = transactionId
        presentSDKController = controller
    }

    func showSuccessDialog() {
        showSuccessDialogCalled = true
    }

    func showErrorDialog(message: String) {
        showErrorDialogCalled = true
        showErrorDialogMessage = message
    }

    func dismiss() {
        dismissCalled = true
    }
}

// MARK: - Tests

class DatatransPaymentPresenterTests: XCTestCase {

    private var mockView: MockDatatransPaymentView!
    private var mockInteractor: MockDatatransPaymentInteractor!
    private var mockRouter: MockDatatransPaymentRouter!
    private var presenter: DatatransPaymentPresenter!

    override func setUp() {
        super.setUp()
        mockView = MockDatatransPaymentView()
        mockInteractor = MockDatatransPaymentInteractor()
        mockRouter = MockDatatransPaymentRouter()

        presenter = DatatransPaymentPresenter()
        presenter.view = mockView
        presenter.interactor = mockInteractor
        presenter.router = mockRouter
    }

    override func tearDown() {
        presenter = nil
        mockView = nil
        mockInteractor = nil
        mockRouter = nil
        super.tearDown()
    }

    // MARK: - viewDidLoad

    func testViewDidLoadCallsShowLoadingThenInitiatePaymentSession() {
        mockInteractor.result = nil // Don't complete immediately

        presenter.viewDidLoad()

        XCTAssertTrue(mockView.showLoadingCalled)
        XCTAssertEqual(mockInteractor.initiatePaymentSessionCallCount, 1)
    }

    // MARK: - Session Success

    func testOnSessionSuccessPresentSDKIsCalledWithCorrectTransactionId() {
        let expectedTransactionId = "txn-abc-123"
        let session = DatatransPaymentSessionResponse(transactionId: expectedTransactionId)
        mockInteractor.result = .success(session)

        presenter.viewDidLoad()

        let expectation = expectation(description: "main queue dispatch")
        DispatchQueue.main.async {
            expectation.fulfill()
        }
        waitForExpectations(timeout: 1)

        XCTAssertTrue(mockRouter.presentSDKCalled)
        XCTAssertEqual(mockRouter.presentSDKTransactionId, expectedTransactionId)
        XCTAssertTrue(mockRouter.presentSDKController === mockView)
    }

    // MARK: - Session Failure

    func testOnSessionFailureHideLoadingAndShowErrorDialogAreCalled() {
        mockInteractor.result = .failure(.basketNotFound)

        presenter.viewDidLoad()

        let expectation = expectation(description: "main queue dispatch")
        DispatchQueue.main.async {
            expectation.fulfill()
        }
        waitForExpectations(timeout: 1)

        XCTAssertTrue(mockView.hideLoadingCalled)
        XCTAssertTrue(mockRouter.showErrorDialogCalled)
        XCTAssertEqual(
            mockRouter.showErrorDialogMessage,
            DatatransPaymentFlowError.basketNotFound.localizedMessage
        )
    }

    // MARK: - TransactionDelegate: didFinish

    func testTransactionDidFinishCallsShowSuccessDialog() {
        // Simulate a successful session first to set isPaymentInProgress
        let session = DatatransPaymentSessionResponse(transactionId: "txn-1")
        mockInteractor.result = .success(session)
        presenter.viewDidLoad()

        let setupExpectation = expectation(description: "setup dispatch")
        DispatchQueue.main.async {
            setupExpectation.fulfill()
        }
        waitForExpectations(timeout: 1)

        // Call transactionDidFinish via the TransactionDelegate conformance
        presenter.transactionDidFinish(TransactionSuccess(transactionId: "txn-1"))

        XCTAssertTrue(mockRouter.showSuccessDialogCalled)
        XCTAssertTrue(mockView.hideLoadingCalled)
    }

    // MARK: - TransactionDelegate: didFail

    func testTransactionDidFailCallsShowErrorDialog() {
        // Simulate a successful session first to set isPaymentInProgress
        let session = DatatransPaymentSessionResponse(transactionId: "txn-1")
        mockInteractor.result = .success(session)
        presenter.viewDidLoad()

        let setupExpectation = expectation(description: "setup dispatch")
        DispatchQueue.main.async {
            setupExpectation.fulfill()
        }
        waitForExpectations(timeout: 1)

        // Reset to check showErrorDialog on the fail path
        mockRouter.showErrorDialogCalled = false
        mockRouter.showErrorDialogMessage = nil

        let error = TransactionError(
            transactionId: "txn-1",
            code: .technicalError,
            message: "Something went wrong"
        )
        presenter.transactionDidFail(error)

        XCTAssertTrue(mockRouter.showErrorDialogCalled)
        XCTAssertNotNil(mockRouter.showErrorDialogMessage)
    }

    // MARK: - TransactionDelegate: didCancel

    func testTransactionDidCancelCallsDismiss() {
        // Simulate a successful session first to set isPaymentInProgress
        let session = DatatransPaymentSessionResponse(transactionId: "txn-1")
        mockInteractor.result = .success(session)
        presenter.viewDidLoad()

        let setupExpectation = expectation(description: "setup dispatch")
        DispatchQueue.main.async {
            setupExpectation.fulfill()
        }
        waitForExpectations(timeout: 1)

        presenter.transactionDidCancel()

        XCTAssertTrue(mockRouter.dismissCalled)
        XCTAssertTrue(mockView.hideLoadingCalled)
    }

    // MARK: - Concurrency Guard

    func testDoubleCallToViewDidLoadDoesNotTriggerSecondSessionInit() {
        mockInteractor.result = nil // Don't complete — keep in progress state

        presenter.viewDidLoad()
        presenter.viewDidLoad()

        XCTAssertEqual(mockInteractor.initiatePaymentSessionCallCount, 1)
    }
}
