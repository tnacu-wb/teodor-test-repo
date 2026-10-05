//
//  ReviewAndBookPresenterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Marcello Mascia on 10/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
import Formeka
@testable import PremierInn

private class MockView: ReviewAndBookViewProtocol {

    var loadViewModelDidCall = false
    var trackPaymentOptionsStateDidCall = false
    var stopEditingDidCall = false
    var validateFormDidCall = false
    var loadingOverlayDidCall = false
    var showErrorDidCall = false
    var showTimeoutErrorAlertDidCall = false
    var showGenericErrorAlertDidCall = false
    var showRowErrorDidCall = false
    var showErrorUIDidCall = false
    var showPayOnArrivalMessageDidCall = false
    var scrollToAtosPasswordRowDidCall = false
    var showRateUpdateRequestDidCall = false
    var showNoMoreAvailabilityMessageDidCall = false
    var startConfirmationPollingDidCall = false
    var stopConfirmationPollingDidCall = false
    var showPollingFinishedErrorDidCall = false
    var shouldFailFormValidation = false
    
    func loadViewModel(with bookingDetails: BookingDetails) {
        
        loadViewModelDidCall = true
    }

    func trackPaymentOptionsState(with params: PIDictionary) {

        trackPaymentOptionsStateDidCall = true
    }
    
    func stopEditing() {
        
        stopEditingDidCall = true
    }
    
    func validateForm() throws -> PIDictionary? {
        
        validateFormDidCall = true
        
        if shouldFailFormValidation {
            return nil
        }
        
        return [ReviewAndBookRow.cvv.rawValue: "123"]
    }

    func provideSuccessFeedback() { }

    func showError(title: String, message: String?) {
        
        showErrorDidCall = true
    }
    
    func showTimeoutErrorAlert(title: String, message: String, customButton: String?, shouldShowFailureButton: Bool) {

        showTimeoutErrorAlertDidCall = true
    }
    
    func showGenericErrorAlert(title: String, message: String, shouldShowFailureButton: Bool) {
        
        showGenericErrorAlertDidCall = true
    }

    func showErrorMessage(title: String, message: String, error: Error?, handler: ((UIAlertAction) -> Void)?) {

    }

    func showStorageFailureError(title: String, message: String, customButton: String, shouldShowFailureButton: Bool) {}

    func showPollingFinishedError(title: String, message: String, shouldShowFailureButton: Bool, accessibilityButtonLabel: String?) {
        showPollingFinishedErrorDidCall = true
    }

    
    func showRowError(_ error: RowValidatorError) {
        
        showRowErrorDidCall = true
    }
    
    func showErrorUI(_ ui: ErrorUI) {
        
        showErrorUIDidCall = true
    }
    
    func showPayOnArrivalMessage(title: String, message: String, completion: @escaping () -> Void) {
        
        showPayOnArrivalMessageDidCall = true
    }
    
    func scrollToAtosPasswordRow() -> Bool {
        
        scrollToAtosPasswordRowDidCall = true
        
        return true
    }
    
    func showRateUpdateRequest(rate: Rate) {
        
        showRateUpdateRequestDidCall = true
    }
    
    func showNoMoreAvailabilityMessage(title: String, message: String) {
        
        showNoMoreAvailabilityMessageDidCall = true
    }

    func startDisplayingLoadingElements() {
        loadingOverlayDidCall = true
    }

    func stopDisplayingLoadingElements() {}

    func startConfirmationPolling() { startConfirmationPollingDidCall = true }
    func stopConfirmationPolling() { stopConfirmationPollingDidCall = true }
}

private class MockRouter: ReviewAndBookRouterProtocol {

    var showEditGuestDidCall = false
    var showSummaryDidCall = false
    var showEditUpsellsDidCall = false
    var showEditBusinessCardQuestionsDidCall = false
    var showBookingConfirmationDidCall = false
    var popToRootDidCall = false
    var showBartErrorDidCall = false
    var goBackToSearchForAvailabilityControllerDidCall = false
    var processPaymentDidCall = false
    var processPaymentWithHtmlDidCall = false
    var processPaymentFor3CPDidCall = false

    func showEditGuest(bookingDetails: BookingDetails) {
        
        showEditGuestDidCall = true
    }
    
    func showSummary(bookingDetails: BookingDetails) {
        
        showSummaryDidCall = true
    }
    
    func showEditUpsells(bookingDetails: BookingDetails) {
        
        showEditUpsellsDidCall = true
    }
    
    func showEditBusinessCardQuestions(questionsAndAnswers: [BusinessCardQuestionAndAnswer]) {
        
        showEditBusinessCardQuestionsDidCall = true
    }
    
    func showBookingConfirmation(with: Stay) {
        
        showBookingConfirmationDidCall = true
    }
    
    func popToRoot() {
        
        popToRootDidCall = true
    }

    func goBack() {

    }

    func showBartError() {
        
        showBartErrorDidCall = true
    }
    
    func goBackToSearchForAvailabilityController(hotel: Hotel?) {
        
        goBackToSearchForAvailabilityControllerDidCall = true
    }

    func processPayment(with cccpiPageParams: ThreeCiPageParams, using threeCiPageDelegate: ThreeCiPageDelegate, walletAnalyticsDelegate: WalletAnalyticsDelegate, and webDelegate: WebViewControllerDelegate) {

        processPaymentFor3CPDidCall = true
    }
}

private typealias PayPalParams = (nonce: String?, deviceData: String?, error: Error?)
private class MockInteractor: ReviewAndBookInteractorProtocol {
    var trackBookingConfirmationDidCall = false
    var trackBookingFailureDidCall = false
    var trackPaymentNotTakenDidCall = false
    var saveStayToLocalStoreDidCall = false
    var appendValuesToBookingDetailsDidCall = false
    var resetBookingDetailsDidCall = false
    var stayWithBookinConfirmationDidCall = false
    var getTotalCostWithCityTaxDidCall = false
    var startCCCPPaymentDidCall = false

    var checkBasketStatusDidCall = false
    var holdBookingWithGuestsDidCall = false
    var mockBookingConfirmation = BookingConfirmation.mock!

    var shouldPayNow = false
    var makeBookingError: Error?
    var updateAvailabilityError: Error?
    var holdError: Error?
    var authenticateCardError: Error?
    
    var maxRetryAttemptReached: Bool { return true }
    var businessCardQuestionsAndAnswers: [BusinessCardQuestionAndAnswer] { return [] }
    var bookingDetails: BookingDetails { return BookingDetails()}
    var is3CHotel: Bool = false
    var cccPaymentOptionTrackingParams: PIDictionary? { return nil }

    var paypalParams = PayPalParams(nonce: nil, deviceData: nil, error: nil)

    func trackBookingConfirmation(confirmation: BookingConfirmation) {
        
        trackBookingConfirmationDidCall = true
    }

    func trackBookingFailure(error: Error) {

        trackBookingFailureDidCall = true
    }

    func trackBookingFailure_3CP(error: PremierInn.ErrorUI) {

    }

    func trackPaymentNotTaken() {
        
        trackPaymentNotTakenDidCall = true
    }
    
    func saveStayToLocalStore(summary: Stay) {
        
        saveStayToLocalStoreDidCall = true
    }

    func holdBookingWithGuests(completion: @escaping (Bool, Error?) -> Void) {
        holdBookingWithGuestsDidCall = true
        completion(true, nil)
    }

    func appendValuesToBookingDetails(values: PIDictionary) {
        
        appendValuesToBookingDetailsDidCall = true
    }
    
    func resetBookingDetails() {
        
        resetBookingDetailsDidCall = true
    }
    
    func stay(with: BookingConfirmation) throws -> Stay {
        
        stayWithBookinConfirmationDidCall = true
        
        var dictionary = PIDictionary()
        dictionary["hotelCode"] = "LONBLA"
        dictionary["hotelName"] = "Alpha2 London Blackfriars (Fleet Street)"
        dictionary["hotelLatitude"] = 50.823071
        dictionary["hotelLongitude"] = -0.140976
        dictionary["lastName"] = "APPS"
        dictionary["identifier"] = "BBER264250"
        dictionary["arrivalDate"] = "2017-10-09"
        dictionary["checkOutDate"] = "2017-10-10"
        dictionary["imageURL"] = "/content/dam/pi/websites/hotelimages/gb/en/B/BRIPTI/BRIPTI 1.jpg"
        
        return try! Stay(dictionary: dictionary)
    }

    func startCccPayment(with values: PIDictionary, paypalNonce: String?, paypalDeviceData: String?, completion: @escaping (Result<CCCPPaymentResponse>) -> Void) throws {
        startCCCPPaymentDidCall = true
    }

    func registerParameters(values: PIDictionary) throws -> RegisterParameters {

        return ReviewAndBookPresenterTests.mockParameters
    }

    func performRegistration(with registerParameters: RegisterParameters, completion: @escaping (Error?) -> Void) {

    }

    func startCccPayment() throws {
        startCCCPPaymentDidCall = true
    }
    func getPaymentMethods(completion: @escaping (Result<PaymentMethodsResponse>) -> Void) {
        let mockPaymentMethodsDictionary: PIDictionary = [
            "paymentMethods": [],
            "paymentMethodsAvailable": true
        ]

        guard let data = try? JSONSerialization.data(withJSONObject: mockPaymentMethodsDictionary, options: .prettyPrinted),
              let mockPaymentMethods = try? JSONDecoder().decode(PaymentMethodsResponse.self, from: data)
        else { return XCTFail("failed to json payment methods dictionary") }

        completion(.success(result: mockPaymentMethods))
    }

    func trackConfirmationPollingBookingStatusFailed() {}
    func trackConfirmationPollingReachedMaxAttemptsFailed() {}

    func getTotalCostWithCityTax(completion: @escaping (Error?) -> Void) {

        getTotalCostWithCityTaxDidCall = true

        completion(nil)
    }

    func checkBasketStatus(completion: @escaping (Result<BookingConfirmation>) -> Void) {

        checkBasketStatusDidCall = true

        completion(.success(result: mockBookingConfirmation))
    }

    func setCccCardType(_ cardType: String?) { 
        // Intentional empty since not testing this func
    }

    func startPaypalVault(completion: @escaping (String?, String?, Error?) -> Void) {
        if let nonce = paypalParams.nonce {
            return completion(nonce, paypalParams.deviceData, nil)
        } else {
            return completion(nil, nil, paypalParams.error)
        }
    }

    func setWalletTypeSelected(_ walletType: PremierInn.PIAnalytics.WalletType?) {
        
    }
}

class ReviewAndBookPresenterTests: XCTestCase {

    static let mockUser: User = {
        let address = try! Address(dictionary: ["addressline1": "Royal Victoria Dock", "addressline2": "2 Festoon Way", "addressline3": "London", "countryCode": "GB", "postcode": "E16 1SJ"])
        let user = try! User(dictionary: [
            "contactDetail" : ["title" : "Mr", "firstName": "Pippo", "lastName": "Paperino", "emailAddress": "ntwisp@me.com"],
            "additionalGuests": [
                ["title": "Mr", "firstName": "Marcello", "lastName": "Mascia", "nationality": "Sardinian", "email": "godTierMccree@owl.com"]
            ]
            ], sessionId: "hello")
        user.address = address
        return user
    }()
    static let mockParameters: RegisterParameters = {

        let password = "Password1"

        return RegisterParameters(
            user: mockUser,
            password: password,
            marketingOptIn: false,
            doubleOptIn: false,
            isoCountryCode: "GB",
            brandCodes: [.premierInn]
        )
    }()

    var testCompany: Company? {
        let emptyCompanyDict: PIDictionary = [:]
        let companyData = try! JSONSerialization.data(withJSONObject: emptyCompanyDict, options: .prettyPrinted)
        return try! JSONDecoder().decode(Company.self, from: companyData)
    }

    private var presenter: ReviewAndBookPresenter!
    private var view: MockView!
    private var router: MockRouter!
    private var interactor: MockInteractor!
    private var pollingController: PollingController!

    override func setUp() {
        
        view = MockView()
        router = MockRouter()
        interactor = MockInteractor()

        presenter = ReviewAndBookPresenter()
        presenter.view = view
        presenter.router = router
        presenter.interactor = interactor
        pollingController = PollingController(delegate: presenter)
    }
    
    override func tearDown() {
        
        presenter = nil
        view = nil
        router = nil
        interactor = nil
        
        super.tearDown()
    }
    
    func testViewIsReady() {
        
        presenter.viewIsReady()

        wait(for: 1 + .ocd, description: #function)

        XCTAssertTrue(view.loadViewModelDidCall)
    }

    func testHoldBookingIsCalledForBB() {
        let user = try! User(title: "Mr", firstName: "Test", lastName: "User")
        UserSessionManager.sharedInstance.loggedIn(with: user)
        UserSessionManager.sharedInstance.currentUser?.company = testCompany
        presenter.viewIsReady()
        XCTAssertTrue(interactor.holdBookingWithGuestsDidCall)
    }

    func testEditGuestButton() {
        
        presenter.editGuestButtonDidTap()
        XCTAssertTrue(router.showEditGuestDidCall)
    }
    
    func testSummaryButton() {
        
        presenter.summaryButtonDidTap()
        XCTAssertTrue(router.showSummaryDidCall)
    }
    
    func testEditAdditionalInfo() {
        
        presenter.editAdditionalInformationButtonDidTap()
        XCTAssertTrue(router.showEditBusinessCardQuestionsDidCall)
    }

    func testConfirmButton_ValidationFormFailure() {
        
        view.shouldFailFormValidation = true
        
        presenter.confirmButtonDidTap()
        XCTAssertTrue(view.stopEditingDidCall)
        XCTAssertTrue(view.loadingOverlayDidCall)
        XCTAssertTrue(view.validateFormDidCall)
        XCTAssertTrue(view.showErrorDidCall)
    }
    
    func testConfirmButton_ValidationFormSuccess() {
        
        view.shouldFailFormValidation = false
        interactor.shouldPayNow = false
        
        presenter.confirmButtonDidTap()
        XCTAssertTrue(view.stopEditingDidCall)
        XCTAssertTrue(view.loadingOverlayDidCall)
        XCTAssertTrue(view.validateFormDidCall)
        XCTAssertTrue(interactor.appendValuesToBookingDetailsDidCall)
    }
    
    func testNoMoreAvailButton() {
        
        presenter.noMoreAvailabilityButtonDidTap()
        XCTAssertTrue(router.goBackToSearchForAvailabilityControllerDidCall)
    }
    
    func testPaymentFailureButton() {
        
        presenter.paymentFailureButtonDidTap()
        XCTAssertTrue(router.goBackToSearchForAvailabilityControllerDidCall)
    }
    
    func testCancelRateUpdateButton() {
        
        presenter.cancelRateUpdateButtonDidTap()
        XCTAssertTrue(router.popToRootDidCall)
    }
}

// MARK: Opera confirmation/basket polling

extension ReviewAndBookPresenterTests {

    func testConfirmationPollingEndTime() {

        interactor.mockBookingConfirmation = BookingConfirmation.mockPollingComplete!
        pollingController.startPollingForBasketComplete()
        XCTAssertTrue(view.startConfirmationPollingDidCall)

        wait(for: 1 + .ocd, description: #function)
        XCTAssertTrue(interactor.checkBasketStatusDidCall)
        XCTAssertTrue(view.stopConfirmationPollingDidCall)
    }

    func testConfirmationPollingFailedScenario() {
        interactor.mockBookingConfirmation = BookingConfirmation.mockPollingFailed!
        pollingController.startPollingForBasketComplete()

        wait(for: 1 + .ocd, description: #function)
        XCTAssertTrue(interactor.checkBasketStatusDidCall)
        XCTAssertTrue(view.showPollingFinishedErrorDidCall)
    }

}

// MARK: PayPal Tests
extension ReviewAndBookPresenterTests {

    func testPayPalVaultValidNonceAndDeviceData() {
        interactor.paypalParams = PayPalParams(nonce: "test-nonce", deviceData: "test-device-data", error: nil)
        presenter.paypalVaultFlow(values: PIDictionary())
        XCTAssertTrue(interactor.startCCCPPaymentDidCall)
        XCTAssertTrue(!view.showErrorDidCall)
    }

    func testPayPalVaultValidNonceOnly() {
        interactor.paypalParams = PayPalParams(nonce: "test-nonce", deviceData: nil, error: nil)
        presenter.paypalVaultFlow(values: PIDictionary())
        XCTAssertTrue(interactor.startCCCPPaymentDidCall)
        XCTAssertTrue(!view.showErrorDidCall)
    }

    func testPayPalVaultUserCancelledError() {
        // In the PayPal SDK an error with code 1 means the user has cancelled the flow in this scenario we just want to do nothing.
        let mockedError = NSError(domain: "", code: 1)

        interactor.paypalParams = PayPalParams(nonce: nil, deviceData: nil, error: mockedError)
        presenter.paypalVaultFlow(values: PIDictionary())
        XCTAssertTrue(!view.showErrorDidCall)
        XCTAssertTrue(!interactor.startCCCPPaymentDidCall)
    }

    func testPayPalVaultError() {
        let mockedError = NSError(domain: "", code: 0)

        interactor.paypalParams = PayPalParams(nonce: nil, deviceData: nil, error: mockedError)
        presenter.paypalVaultFlow(values: PIDictionary())
        XCTAssertTrue(view.showErrorDidCall)
        XCTAssertTrue(!interactor.startCCCPPaymentDidCall)

    }
}
