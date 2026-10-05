//
//  GuestDetailsPresenterTests.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 20.02.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Testing
import SimpleNetwork
@testable import PremierInn

class MockGuestDetailsView: GuestDetailsViewBlueprint {
    func updateBalance(priceVM: any PremierInn.CIOLPriceBreakdownViewModelProtocol) {}
    
    var presenter: (any PremierInn.GuestDetailsPresenterBlueprint)?
    var didCallUpdate = false
    var didCallHide = false
    var didCallShow =  false
    var didCallError = false
    func update(with: [PremierInn.Guest], priceVM: (any PremierInn.CIOLPriceBreakdownViewModelProtocol)?) {
        didCallUpdate = true
    }
    
    func showLoadingIndicator() {
        didCallShow = true
    }
    
    func hideLoadingIndicator() {
        didCallHide = true
    }
    
    func showError(title: String, message: String?, shouldDie: Bool) {
        didCallError = true
    }
}

final class MockGuestDetailsInteractor: GuestDetailsInteractorBlueprint {
    var defaultAnalytics: PIDictionary = [:]
    
    var paymentViewLayout: PremierInn.WebViewControllerLayout = .general

    private(set) var isThirdPartyBookingWithCityTaxReturnValue = false
    private(set) var isHandleContinueButtonTappedCalled = false
    
    func didReceiveAuthorizationMessage(string: String) {}
    
    var prestayDelegate: (any PremierInn.PrestayDelegate)?
    
    func getEditModel(for index: Int) -> PremierInn.EditDetailsInputParams? {
        nil
    }
    
    var didCallValidate = false
    var output: (any PremierInn.GuestDetailsInteractorOutput)?
    
    var editedUsers: (any PremierInn.EditedGuestsModelBlueprint)?
    
    var guests: [PremierInn.Guest] = []

    var priceVM: PremierInn.GuestDetailsInteractor.GuestDetailsPriceBreakdownViewModel = PremierInn.GuestDetailsInteractor.GuestDetailsPriceBreakdownViewModel(ctaTitle: PILocalizedString("Continue"), totalValue: "", items: [])
    
    func validate() {
        didCallValidate = true
    }

    func didUpdateUserDetails(with editDetailsModel: PremierInn.EditDetailsModel) {

    }

    var isThirdPartyBookingWithCityTax: Bool {
        isThirdPartyBookingWithCityTaxReturnValue
    }

    func handleContinueButtonTap() {
        isHandleContinueButtonTappedCalled = true
    }
}


class MockGuestDetailsRouter: GuestDetailsRouterBlueprint {
    func processPayment(with cccpiPageParams: PremierInn.ThreeCiPageParams, using threeCiPageDelegate: any PremierInn.ThreeCiPageDelegate, and webDelegate: any PremierInn.WebViewControllerDelegate, authorizationDelegate: any PremierInn.AuthorizationDelegate, webviewLayout: PremierInn.WebViewControllerLayout) {}

    var didCallEdit = false

    func showEdit(with input: PremierInn.EditDetailsInputParams, delegate: any PremierInn.EditDetailsViewDelegate) {
        didCallEdit = true
    }

    func goToUpsell(ciolUpsellInputParams: PremierInn.CiolUpsellInputParams, prestayDelegate: any PremierInn.PrestayDelegate) {

    }

    func goToPayment(inputParams: PremierInn.CiolReviewAndPayInputParams) {

    }

    func goToCompletion(ciolConfirmationDetails: PremierInn.CiolConfirmationDetails) {

    }
}


class GuestDetailsPresenterTests {
    
    var sut: GuestDetailsPresenter!
    var mockView: MockGuestDetailsView!
    var mockInteractor: MockGuestDetailsInteractor!
    var mockRouter: MockGuestDetailsRouter!

    init() throws {
        mockView = MockGuestDetailsView()
        mockInteractor = MockGuestDetailsInteractor()
        mockRouter = MockGuestDetailsRouter()

        sut = GuestDetailsPresenter()
        sut.view = mockView
        sut.interactor = mockInteractor
        sut.router = mockRouter
    }

    @Test func testUpdate() async throws {
        sut.viewIsReady()
        #expect(mockView.didCallUpdate)
    }

    @Test func testContinue() async throws {
        sut.handleContinueButtonTap()
        #expect(mockInteractor.isHandleContinueButtonTappedCalled)
    }

    @Test func testDidFinishNoError() async throws {
        sut.didFinish(error: nil)
        #expect(mockView.didCallHide)
    }

    @Test func testDidFinishError() async throws {
        sut.didFinish(error: CIOLError.performPrestayChecks)
        #expect(mockView.didCallHide)
        #expect(mockView.didCallError)
    }

    @Test func testDidStart() async throws {
        sut.didStart()
        #expect(mockView.didCallShow)
    }

    @Test func testReload() async throws {
        sut.reload()
        #expect(mockView.didCallUpdate)
    }
}
