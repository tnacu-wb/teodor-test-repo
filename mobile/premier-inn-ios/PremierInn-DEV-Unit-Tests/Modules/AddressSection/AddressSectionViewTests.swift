//
//  AddressSectionViewTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 25/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Foundation
import XCTest
import Formeka
import SimpleNetwork

@testable import PremierInn

private class MockFormekaViewController: FormekaViewController {

    var presentDidCall = false

    override func present(_ viewControllerToPresent: UIViewController, animated flag: Bool, completion: (() -> Void)? = nil) {

        presentDidCall = true
    }
}

private class MockPresenter: AddressSectionPresenterInput {

    var billingAddressSwitchDidCall = false
    var addAddressManuallyDidCall = false
    var userPickedCountryDidCall = false
    var userPickedAddressDidCall = false
    var userTappedCountryDidCall = false
    var userCancelledCountryDidCall = false
    var postcodeSearchButtonDidCall = false
    var userCancelledPostcodeDidCall = false

    func billingAddressSwitchDidChange(isSameAsYourAddress: Bool) {

        billingAddressSwitchDidCall = true
    }

    func addAddressManuallyButtonDidTap() {

        addAddressManuallyDidCall = true
    }

    func userPicked(_ country: Country?) {

        userPickedCountryDidCall = true
    }

    func userPicked(_ address: Address?) {

        userPickedAddressDidCall = true
    }

    func userDidTapCountryRow() {

        userTappedCountryDidCall = true
    }

    func userCancelledCountryPicker() {

        userCancelledCountryDidCall = true
    }

    func postcodeSearchButtonDidTap(with initialValue: String?) {

        postcodeSearchButtonDidCall = true
    }

    func userCancelledPostcodePicker() {

        userCancelledPostcodeDidCall = true
    }
}

private struct Resources {

    static let sampleAddress: Address? = try? Address(dictionary: ["line1": "120 Holborn", "postcode": "EC1N 2TD", "countryCode": "gb"])
    static let companyAddress: Address? = try? Address(dictionary: ["line1": "120 Holborn", "postcode": "EC1N 2TD", "countryCode": "gb", "companyName": "Whitbread"])
    static let postcode = "SG7 5QN"
}

class AddressSectionViewTestsPtTwo: XCTestCase {

    var addressSectionView: AddressSectionView? {
        didSet {
            mockPresenter = MockPresenter()
            addressSectionView?.presenter = mockPresenter

            guard let section = addressSectionView?.addressSection(for: formekaController) else { return }
            formekaController.viewModel = FormekaViewModel(sections: [section])
        }
    }

    fileprivate var mockPresenter: MockPresenter?

    private let formekaController = MockFormekaViewController(nibName: String(describing: FormekaViewController.self), bundle: nil)

    private let noAddressRequirements = AddressSectionRequirements(
        address: nil,
        storedAddress: nil,
        shouldShowAddressSwitch: true,
        useStoredAddressSwitchDescription: nil,
        addressSwitchInitialState: true,
        shouldShowAddressForm: true,
        shouldShowHeader: true,
        shouldShowFooter: true,
        shouldShowAddressSummary: false
    )

    private let addressRequirements = AddressSectionRequirements(
        address: Resources.sampleAddress,
        storedAddress: nil,
        shouldShowAddressSwitch: true,
        useStoredAddressSwitchDescription: nil,
        addressSwitchInitialState: true,
        shouldShowAddressForm: true,
        shouldShowHeader: true,
        shouldShowFooter: true,
        shouldShowAddressSummary: false
    )

    private let companyAddressRequirements = AddressSectionRequirements(
        address: Resources.companyAddress,
        storedAddress: nil,
        shouldShowAddressSwitch: true,
        useStoredAddressSwitchDescription: nil,
        addressSwitchInitialState: true,
        shouldShowAddressForm: true,
        shouldShowHeader: true,
        shouldShowFooter: true,
        shouldShowAddressSummary: false
    )

    override func setUp() {
        super.setUp()

        addressSectionView = AddressSectionView(with: noAddressRequirements)
        addressSectionView?.parentFormekaViewController = formekaController
    }

    override func tearDown() {
        super.tearDown()
    }

    func testAddressSectionNoAddress() {

        let addressSection = addressSectionView?.addressSection(for: formekaController)

        XCTAssertNotNil(addressSection)
        XCTAssert(addressSection?.rows.count == 4)
    }

    func testAddressSectionWithAddress() {

        addressSectionView = AddressSectionView(with: addressRequirements)
        addressSectionView?.parentFormekaViewController = formekaController

        let addressSection = addressSectionView?.addressSection(for: formekaController)

        XCTAssertNotNil(addressSection)
        XCTAssert(addressSection?.rows.count ?? 0 > 3)
    }

    func testShowPostcodePicker() {

        addressSectionView?.showPostcodePicker(with: Resources.postcode)

        XCTAssert(formekaController.presentDidCall == true)
    }

    func testDismissPostcode() {

        addressSectionView?.dismissPostcodePicker()
    }

    func testShowCountryPicker() {

        addressSectionView?.showCountryPicker()

        XCTAssert(formekaController.presentDidCall == true)
    }

    func testDismissCountry() {

        addressSectionView?.dismissCountryPicker()
    }

    func testUpdateCompany() {

        addressSectionView = AddressSectionView(with: companyAddressRequirements)
        addressSectionView?.parentFormekaViewController = formekaController

        addressSectionView?.updateCompanyRow(with: Resources.companyAddress)
    }

    func testUpdateCountryRow() {

        addressSectionView?.updateCountryRow(with: Country(code: "gb", name: "Greatest Brittania", isoCode: "gb", dialingCode: nil, flagImage: nil, passportRequired: nil, nationality: "British"))
    }
}
