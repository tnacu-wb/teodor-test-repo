//
//  AddNewCardInteractorTests.swift
//  PremierInnTests
//
//  Created by Georgios Aikaterinakis on 08/10/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockAddNewCardDataProvider: AddNewCardDataProvider {

    var initiateSaveCardDidCall = false

    func initiateSaveCard(initiateSaveCardParameters: SimpleNetwork.InitiateSaveCardParameters, completion: @escaping (SimpleNetwork.CCCPPaymentProviderResponse?, (any Error)?) -> Void) {
        initiateSaveCardDidCall = true

        completion(nil, nil)
    }
}

class AddNewCardInteractorTests: XCTestCase {

    // MARK: - Properties

    private var interactor: AddNewCardInteractor!
    private var addNewCardDataProvider: MockAddNewCardDataProvider!

    // MARK: - Lifecycle

    override func setUp() {
        super.setUp()

        interactor = AddNewCardInteractor()
        addNewCardDataProvider = MockAddNewCardDataProvider()
        interactor.addNewCardDataProvider = addNewCardDataProvider

        if let user = try? User(title: "Mr", firstName: "Name", lastName: "Surname", email: "test@test.com") {

            user.address = try? Address(dictionary: ["addressline1": "Royal Victoria Dock", "addressline2": "2 Festoon Way", "addressline3": "London", "countryCode": "GB", "postcode": "E16 1SJ"])
            UserSessionManager.sharedInstance.piLoggedIn(with: user)
        }
    }

    override func tearDown() {

        interactor = nil

        super.tearDown()
    }

    // MARK: - Tests

    func testLoggedOut() {

        UserSessionManager.sharedInstance.piUserLoggedOut()

        XCTAssertNil(interactor.addNewCardViewModel)
    }

    func testViewModelDefaults() {

        let viewModel = interactor.addNewCardViewModel

        XCTAssertNotNil(viewModel)
        XCTAssertNotNil(viewModel?.billingAddressViewModel)
        XCTAssertNotNil(viewModel?.paymentMethodsViewModel)
        XCTAssertEqual(viewModel?.paymentMethodsViewModel?.paymentMethods.count, 2)
        // if we update these hardcoded values without updating the tests we'll get errors
        XCTAssertEqual(viewModel?.paymentMethodsViewModel?.paymentMethods.first(where: { $0.type == .CARD })?.imageUrls?.count, 8)
        XCTAssertEqual(viewModel?.paymentMethodsViewModel?.paymentMethods.first(where: { $0.type == .PIBA })?.imageUrls?.count, 1)
        XCTAssertFalse(viewModel?.paymentMethodsViewModel?.shouldShowCNP == true)
        XCTAssertFalse(viewModel?.paymentMethodsViewModel?.cnpEnabled == true)
        XCTAssertTrue(viewModel?.paymentMethodsViewModel?.paymentMethods.first(where: { $0.type == .CARD })?.selected == true)
        XCTAssertFalse(viewModel?.paymentMethodsViewModel?.paymentMethods.first(where: { $0.type == .PIBA })?.selected == true)
    }

    func testViewModelPIBASelected() {

        var viewModel = interactor.addNewCardViewModel

        interactor.selected(paymentType: "PIBA")
        viewModel = interactor.addNewCardViewModel

        XCTAssertFalse(viewModel?.paymentMethodsViewModel?.paymentMethods.first(where: { $0.type == .CARD })?.selected == true)
        XCTAssertTrue(viewModel?.paymentMethodsViewModel?.paymentMethods.first(where: { $0.type == .PIBA })?.selected == true)
        XCTAssertTrue(viewModel?.paymentMethodsViewModel?.shouldShowCNP == true)
        XCTAssertFalse(viewModel?.paymentMethodsViewModel?.cnpEnabled == true)

        interactor.setCnpRequired(toggle: true)
        viewModel = interactor.addNewCardViewModel
        XCTAssertFalse(viewModel?.paymentMethodsViewModel?.paymentMethods.first(where: { $0.type == .CARD })?.selected == true)
        XCTAssertTrue(viewModel?.paymentMethodsViewModel?.paymentMethods.first(where: { $0.type == .PIBA })?.selected == true)
        XCTAssertTrue(viewModel?.paymentMethodsViewModel?.shouldShowCNP == true)
        XCTAssertTrue(viewModel?.paymentMethodsViewModel?.cnpEnabled == true)
    }

    func testInitiateAddNewCard() {

        interactor.initiateAddNewCard(values: [:], memorableWord: nil) { _ in
            self.addNewCardDataProvider.initiateSaveCardDidCall = true
        }
        
        // no address, fail
        UserSessionManager.sharedInstance.piUserLoggedOut()

        interactor.initiateAddNewCard(values: [:], memorableWord: nil) { _ in
            self.addNewCardDataProvider.initiateSaveCardDidCall = false
        }
    }
}
