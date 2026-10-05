//
//  AddressSectionPresenterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Marcello Mascia on 03/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockView: AddressSectionViewInput {

    var removeAddressSectionDidCall = false
    var showCountryPickerDidCall = false
    var dismissCountryPickerDidCall = false
    var showAddressSectionDidCall = false
    var showAllAddressRowsDidCall = false
    var updateCountryRowDidCall = false
    var updatePostcodeRowDidCall = false
    var configurePostcodeRowDidCall = false
    var addAddressButtonDidCall = false
    var removeAddressButtonDidCall = false
    var showPostcodePickerDidCall = false
    var dismissPostcodePickerDidCall = false
    var updateCompanyRowDidCall = false
    var toggleCompanyRowDidCall = false

    var shouldShowAddressSummary: Bool = true

    func removeAddressSection() {

        removeAddressSectionDidCall = true
    }

    func addAddressManually() {

    }

    func showCountryPicker() {

        showCountryPickerDidCall = true
    }

    func dismissCountryPicker() {

        dismissCountryPickerDidCall = true
    }

    func showAddressSection() {

        showAddressSectionDidCall = true
    }

    func showAllAddressRows(with address: Address?, completion: (() -> Void)?) {

        showAllAddressRowsDidCall = true
        completion?()
    }

    func updateCountryRow(with country: Country) {

        updateCountryRowDidCall = true
    }

    func updatePostcodeRow(with postcode: String?) {

        updatePostcodeRowDidCall = true
    }

    func configurePostcodeRow(for country: Country) {

        configurePostcodeRowDidCall = true
    }

    func addAddressButton() {

        addAddressButtonDidCall = true
    }

    func removeAddressButton() {

        removeAddressButtonDidCall = true
    }

    func showPostcodePicker(with postCode: String?) {

        showPostcodePickerDidCall = true
    }

    func dismissPostcodePicker() {

        dismissPostcodePickerDidCall = true
    }

    func updateCompanyRow(with address: Address?) {

        updateCompanyRowDidCall = true
    }

    func addAddressSummaryRow() {

    }

    func removeAddressSummaryRow() {
        
    }

    func toggleCompanyRow(with address: SimpleNetwork.Address?) {

        toggleCompanyRowDidCall = true
    }
}

class AddressSectionViewTests: XCTestCase {

    private var presenter: AddressSectionPresenter!
    private var view: MockView!

    override func setUp() {
        super.setUp()

        view = MockView()

        presenter = AddressSectionPresenter()
        presenter?.view = view
    }
    
    override func tearDown() {

        presenter = nil
        view = nil

        super.tearDown()
    }
    
    func testPostcodeSearch() {

        presenter.postcodeSearchButtonDidTap(with: nil)

        XCTAssertTrue(view.showPostcodePickerDidCall)
    }

    func testCancelCountryPicker() {

        presenter.userCancelledCountryPicker()

        XCTAssertTrue(view.dismissCountryPickerDidCall)
    }

    func testCancelPostcodePicker() {

        presenter.userCancelledPostcodePicker()

        XCTAssertTrue(view.dismissPostcodePickerDidCall)
    }

    func testCountryTap() {

        presenter.userDidTapCountryRow()

        XCTAssertTrue(view.showCountryPickerDidCall)
    }

    func testUserPickedCountry() {

        presenter.userPicked(Country(code: "asd", name: "asd", isoCode: "lol", dialingCode: nil, flagImage: nil, passportRequired: nil, nationality: nil))

        XCTAssertTrue(view.dismissCountryPickerDidCall)
        XCTAssertTrue(view.updateCountryRowDidCall)
        XCTAssertTrue(view.configurePostcodeRowDidCall)
        XCTAssertTrue(view.showAllAddressRowsDidCall)
        XCTAssertTrue(view.removeAddressButtonDidCall)
    }

    func testUserPickedAddress() {

        presenter.userPicked(try? Address(dictionary: ["addressline1": "Royal Victoria Dock", "addressline2": "2 Festoon Way", "addressline3": "London", "countryCode": "GB", "postcode": "E16 1SJ"]))

        XCTAssertTrue(view.dismissPostcodePickerDidCall)
        XCTAssertTrue(view.updateCompanyRowDidCall)
        XCTAssertTrue(view.showAllAddressRowsDidCall)
        XCTAssertTrue(view.updatePostcodeRowDidCall)
        XCTAssertTrue(view.removeAddressButtonDidCall)
    }

    func testBillingAddressSwitchChange_True() {

        presenter.billingAddressSwitchDidChange(isSameAsYourAddress: true)

        XCTAssertTrue(view.removeAddressButtonDidCall)
        XCTAssertTrue(view.removeAddressSectionDidCall)
        XCTAssertFalse(view.showAddressSectionDidCall)
        XCTAssertFalse(view.addAddressButtonDidCall)
    }

    func testBillingAddressSwitchChange_False() {

        presenter.billingAddressSwitchDidChange(isSameAsYourAddress: false)

        XCTAssertFalse(view.removeAddressButtonDidCall)
        XCTAssertFalse(view.removeAddressSectionDidCall)
        XCTAssertTrue(view.showAddressSectionDidCall)
        XCTAssertTrue(view.addAddressButtonDidCall)
    }

    func testAddAddressButton() {

        presenter.addAddressManuallyButtonDidTap()

        XCTAssertTrue(view.showAllAddressRowsDidCall)
        XCTAssertTrue(view.removeAddressButtonDidCall)
    }

    func testToggleCompanyRow() {
        presenter.userPicked(try? Address(dictionary: ["addressline1": "Royal Victoria Dock", "addressline2": "2 Festoon Way", "addressline3": "London", "countryCode": "GB", "postcode": "E16 1SJ", "type": AddressType.commercial, "companyName": "fakecomp"]))

        XCTAssertTrue(view.toggleCompanyRowDidCall)
    }
}
