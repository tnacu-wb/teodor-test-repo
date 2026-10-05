//
//  AddNewCardViewTests.swift
//  PremierInnTests
//
//  Created by Georgios Aikaterinakis on 08/10/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import Formeka
import SimpleNetwork
@testable import PremierInn

private class MockPresenter: AddNewCardViewEventHandler {

    var viewIsReadyDidCall = false
    var selectedPaymentMethodDidCall = false
    var toggledCNPDidCall = false
    var addCardDidTapDidCall = false

    func viewIsReady() {
        viewIsReadyDidCall = true
    }

    func selectedPaymentMethod(type: String) {
        selectedPaymentMethodDidCall = true
    }

    func toggledCNP(toggle: Bool) {
        toggledCNPDidCall = true
    }

    func addCardDidTap(values: PIDictionary, memorableWord: String?) {
        addCardDidTapDidCall = true
    }
}

class AddNewCardViewTests: XCTestCase {

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

    private var presenter: MockPresenter!
    private var viewController: AddNewCardView!

    // MARK: - Lifecycle

    override func setUp() {
        super.setUp()

        presenter = MockPresenter()

        viewController = AddNewCardView(nibName: String(describing: FormekaViewController.self), bundle: nil)
        viewController.eventHandler = presenter
    }

    override func tearDown() {

        presenter = nil
        viewController = nil

        super.tearDown()
    }

    // MARK: - Tests

    func testViewDidLoad() {

        viewController.viewDidLoad()

        XCTAssertTrue(presenter.viewIsReadyDidCall)
    }

    func testSubmitButtonDidTap() {

        viewController.viewModel = FormekaViewModel(sections: [])
        viewController.submitButtonDidTap(cell: FormekaSubmitButtonCell())

        XCTAssertTrue(presenter.addCardDidTapDidCall)
    }

    func testCreditCardPaymentMethod() {

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

        viewController.loadViewModel(addNewCardViewModel: addNewCardViewModel)

        XCTAssertNotNil(viewController.viewModel?.row(named: AddNewCardViewRow.savedCardSyncInfo.rawValue))
        XCTAssertNotNil(viewController.viewModel?.row(named: AddNewCardViewRow.paymentMethod.rawValue))
        XCTAssertNil(viewController.viewModel?.row(named: AddNewCardViewRow.cnpRow.rawValue))
        XCTAssertNil(viewController.viewModel?.row(named: AddNewCardViewRow.memorableWord.rawValue))
        XCTAssertNil(viewController.viewModel?.row(named: AddNewCardViewRow.memorableWordInfo.rawValue))
        XCTAssertNotNil(viewController.viewModel?.row(named: AddNewCardViewRow.submitButton.rawValue))
    }

    func testPIBAPaymentMethod() {

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
            PaymentMethodViewModel(name: PILocalizedString("New PIBA card"),
                                   type: .PIBA,
                                   imageUrls: [],
                                   isCNPAvailable: true,
                                   selected: false)
        ]
        let paymentMethodsViewModel = PaymentMethodsViewModel(title: "Payment methods",
                                                              paymentMethods: paymentMethods,
                                                              shouldShowCNP: true,
                                                              cnpEnabled: true)

        let addNewCardViewModel = ViewModel(billingAddressViewModel: billingAddressViewModel,
                                            paymentMethodsViewModel: paymentMethodsViewModel)

        viewController.loadViewModel(addNewCardViewModel: addNewCardViewModel)

        XCTAssertNotNil(viewController.viewModel?.row(named: AddNewCardViewRow.savedCardSyncInfo.rawValue))
        XCTAssertNotNil(viewController.viewModel?.row(named: AddNewCardViewRow.paymentMethod.rawValue))
        XCTAssertNotNil(viewController.viewModel?.row(named: AddNewCardViewRow.cnpRow.rawValue))
        XCTAssertNotNil(viewController.viewModel?.row(named: AddNewCardViewRow.memorableWord.rawValue))
        XCTAssertNotNil(viewController.viewModel?.row(named: AddNewCardViewRow.memorableWordInfo.rawValue))
        XCTAssertNotNil(viewController.viewModel?.row(named: AddNewCardViewRow.submitButton.rawValue))
    }

}
