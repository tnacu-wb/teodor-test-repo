//
//  RegistrationPresenterTests.swift
//  PremierInnTests
//
//  Created by Freddie Parks on 27/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn
import Formeka
import SimpleNetwork

private class MockRouter: RegisterRouterInput {

    var selectedSalutationDidCall = false
    var selectedDismissView = false
    var showAddressPickerScreenDidCall = false
    var registerDidComplete = false

    var viewController: UIViewController?

    func selectedSalutation(completion: @escaping (String?) -> Void) {

        selectedSalutationDidCall = true
    }

    func dismissRegisterView() {

        selectedDismissView = true
    }

    func completeRegister() {
        registerDidComplete = true
    }
}

private class MockInteractor: RegisterInteractorInput {

    var marketingPreferenceViewModel: MarketingPreferenceViewModelType {
        MockMarketingPreferenceViewModel(isOptIn: false)
    }

    var registerTracking: RegisterTracking { return ("", "") }
    var shouldUpdateBiometricDidCall = false
    var biometricStatusChangedDidCall = false
    var performRegistrationDidCall = false
    var shouldUpdateBiometricSettings: Bool {

        shouldUpdateBiometricDidCall = true
        return false
    }

    func performRegistration(with registerParameters: RegisterParameters, completion: @escaping (Error?) -> Void) {

        performRegistrationDidCall = true

        completion(nil)
    }

    func biometricStatusChanged(enabled: Bool) {

        biometricStatusChangedDidCall = true
    }

    func updateCountry(code: String) {}
}

private extension MockInteractor {
    private struct MockMarketingPreferenceViewModel: MarketingPreferenceViewModelType {
        let isOptIn: Bool
    }
}

class RegistrationPresenterTests: XCTestCase {

    private var router: MockRouter!
    private var interactor: MockInteractor!
    private var presenter: RegisterPresenter!

    override func setUp() {
        super.setUp()

        router = MockRouter()
        interactor = MockInteractor()

        presenter = RegisterPresenter()
        presenter?.router = router
        presenter?.interactor = interactor
    }

    override func tearDown() {

        presenter = nil
        interactor = nil

        super.tearDown()
    }

    func testDismiss() {

        presenter.dismissRegisterDidTap()

        XCTAssertTrue(router.selectedDismissView)
    }

    func testSelectedSalutation() {

        presenter.salutationRowDidTap()

        XCTAssertTrue(router.selectedSalutationDidCall)
    }

    func testTouchIdActivation() {

        presenter.biometricActivationCompleted(activated: true)

        XCTAssertTrue(interactor.biometricStatusChangedDidCall)
    }

    func testFormSubmission() {

        presenter.submitForm(viewModel: mockViewModel())

        XCTAssertTrue(interactor.performRegistrationDidCall)
        XCTAssertTrue(router.registerDidComplete)
    }
}

private extension RegistrationPresenterTests {

    private func row(tag: RegisterRow, value: FormekaValue?) -> FormekaModelRow {

        let row = FormekaModelRow(tag: tag.rawValue, cellSetup: { _, _, _ in nil })
        row.value = value

        return row
    }

    func mockViewModel() -> FormekaViewModel {

        let rows: [FormekaModelRow] = [
            row(tag: .title, value: "Mr"),
            row(tag: .firstName, value: "Marcello"),
            row(tag: .lastName, value: "Mascia"),
            row(tag: .password, value: "Mysecretpassword1"),
            row(tag: .marketingOptIn, value: false)
        ]

        return FormekaViewModel(sections: [FormekaModelSection(header: nil, rows: rows, footer: nil)])
    }
}
