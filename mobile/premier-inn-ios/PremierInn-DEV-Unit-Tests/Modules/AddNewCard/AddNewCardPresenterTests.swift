//
//  AddNewCardPresenterTests.swift
//  PremierInnTests
//
//  Created by Georgios Aikaterinakis on 08/10/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockView: AddNewCardViewProtocol {

    var loadViewModelDidCall = false
    var updatePaymentMethodsDidCall = false
    var finishedLoadingDidCall = false
    var showErrorDidCall = false

    func loadViewModel(addNewCardViewModel: PremierInn.AddNewCardViewModel) {
        loadViewModelDidCall = true
    }
    
    func updatePaymentMethods(with viewModel: PremierInn.AddNewCardPaymentMethodsViewModel) {
        updatePaymentMethodsDidCall = true
    }

    func finishedLoading() {
        finishedLoadingDidCall = true
    }

    func showError(title: String, message: String) {
        showErrorDidCall = true
    }
}

private class MockRouter: AddNewCardRouterProtocol {

    var showAddCardWebViewDidCall = false
    var goBackToMyAccountDidCall = false

    func showAddCardWebView(cccpiPageParams: PremierInn.ThreeCiPageParams, threeCiPageDelegate: any PremierInn.ThreeCiPageDelegate) {
        showAddCardWebViewDidCall = true
    }

    func goBackToMyAccount() {
        goBackToMyAccountDidCall = true
    }
}

private class MockInteractor: AddNewCardInteractorProtocol {

    var addNewCardViewModelDidCall = false
    var selectedPaymentTypeDidCall = false
    var setCnpRequiredDidCall = false
    var initiateAddNewCardDidCall = false

    var threeCiPageParams: ThreeCiPageParams?

    public init(threeCiPageParams: ThreeCiPageParams? = nil) {

        self.threeCiPageParams = threeCiPageParams
    }

    var addNewCardViewModel: AddNewCardViewModel? {
        addNewCardViewModelDidCall = true

        let address = try? Address(dictionary: ["addressline1": "Royal Victoria Dock", "addressline2": "2 Festoon Way", "addressline3": "London", "countryCode": "GB", "postcode": "E16 1SJ"])
        let billingAddressViewModel = BillingAddressViewModel(addressRequirements: AddressSectionRequirements(
            address: address,
            storedAddress: address,
            shouldShowAddressSwitch: true,
            useStoredAddressSwitchDescription: "(E16 1SJ)",
            addressSwitchInitialState: true,
            shouldShowAddressForm: false,
            shouldShowHeader: true,
            shouldShowFooter: false,
            shouldShowAddressSummary: false
        ))

        let paymentMethods = [
            PaymentMethodViewModel(name: PILocalizedString("New Credit / Debit card"),
                                   type: .CARD,
                                   imageUrls: [],
                                   isCNPAvailable: false,
                                   selected: false)
        ]
        let paymentMethodsViewModel = PaymentMethodsViewModel(title: "Payment methods",
                                                              paymentMethods: paymentMethods,
                                                              shouldShowCNP: false,
                                                              cnpEnabled: false)

        let addNewCardViewModel = ViewModel(billingAddressViewModel: billingAddressViewModel,
                                            paymentMethodsViewModel: paymentMethodsViewModel)

        return addNewCardViewModel
    }

    func selected(paymentType: String) {
        selectedPaymentTypeDidCall = true
    }

    func setCnpRequired(toggle: Bool) {
        setCnpRequiredDidCall = true
    }

    func initiateAddNewCard(values: PIDictionary, memorableWord: String?, completion: @escaping (PremierInn.ThreeCiPageParams?) -> Void) {
        initiateAddNewCardDidCall = true

        completion(threeCiPageParams)
    }

    // ViewModels
    
    private struct ViewModel: AddNewCardViewModel {

        let billingAddressViewModel: AddNewCardBillingAddressViewModel
        let paymentMethodsViewModel: AddNewCardPaymentMethodsViewModel?
    }

    private struct BillingAddressViewModel: AddNewCardBillingAddressViewModel {

        let addressRequirements: AddressSectionRequirements
    }

    private struct PaymentMethodsViewModel: AddNewCardPaymentMethodsViewModel {

        let title: String
        let paymentMethods: [AddNewCardPaymentMethodViewModel]
        let shouldShowCNP: Bool
        let cnpEnabled: Bool
    }

    private struct PaymentMethodViewModel: AddNewCardPaymentMethodViewModel {

        let name: String
        let type: CCCPPaymentType
        let imageUrls: [URL]?
        let isCNPAvailable: Bool
        let selected: Bool
    }
}

class AddNewCardPresenterTests: XCTestCase {

    private var view: MockView!
    private var interactor: MockInteractor!
    private var router: MockRouter!
    var presenter: AddNewCardPresenter!

    override func setUp() {
        super.setUp()

        view = MockView()
        interactor = MockInteractor()
        presenter = AddNewCardPresenter()
        presenter.view = view
        presenter.interactor = interactor

        router = MockRouter()

        presenter.router = router
    }

    override func tearDown() {

        view = nil
        interactor = nil
        presenter = nil
        router = nil

        super.tearDown()
    }

    func testViewIsReady() {

        presenter.viewIsReady()

        XCTAssertTrue(interactor.addNewCardViewModelDidCall)
        XCTAssertTrue(view.loadViewModelDidCall)
    }

    func testSelectedPaymentMethod() {

        presenter.selectedPaymentMethod(type: "CARD")

        XCTAssertTrue(interactor.selectedPaymentTypeDidCall)
        XCTAssertTrue(interactor.addNewCardViewModelDidCall)
        XCTAssertTrue(view.updatePaymentMethodsDidCall)
    }

    func testToggledCNP() {

        presenter.toggledCNP(toggle: true)

        XCTAssertTrue(interactor.setCnpRequiredDidCall)
        XCTAssertTrue(interactor.addNewCardViewModelDidCall)
        XCTAssertTrue(view.updatePaymentMethodsDidCall)
    }

    func testAddCardDidTapFailed() {

        presenter.addCardDidTap(values: [:], memorableWord: nil)

        XCTAssertTrue(interactor.initiateAddNewCardDidCall)
        XCTAssertTrue(view.finishedLoadingDidCall)
        XCTAssertFalse(router.showAddCardWebViewDidCall)

    }

    func testAddCardDidTapSuccess() {

        interactor = MockInteractor(threeCiPageParams: ThreeCiPageParams(html: "html", trackingParams: nil, allowedEvents: nil))
        presenter.interactor = interactor

        presenter.addCardDidTap(values: [:], memorableWord: nil)

        XCTAssertTrue(interactor.initiateAddNewCardDidCall)
        XCTAssertTrue(view.finishedLoadingDidCall)
        XCTAssertTrue(router.showAddCardWebViewDidCall)
    }
}
