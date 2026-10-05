//
//  AddressSectionTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Marcello Mascia on 03/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import Formeka
import SimpleNetwork
@testable import PremierInn

class AddressSectionTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }

	private func buildController(with requirements: AddressSectionRequirements) -> FormekaViewController {

		let controller = FormekaViewController()

		let view = AddressSectionRouter.buildSection(with: requirements)
		view.parentFormekaViewController = controller

		controller.viewModel = FormekaViewModel(sections: [view.addressSection(for: controller)])

		return controller
	}
    
    func testAddressSection_AllTrue() {

		let addressRequirements = AddressSectionRequirements(
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

		let controller = buildController(with: addressRequirements)

		XCTAssertEqual(controller.viewModel?.sections.count, 1)
		XCTAssertEqual(controller.viewModel?.sections.first?.rows.count, 4)
		XCTAssertNotNil(controller.viewModel?.row(named: Step2Row.billingAddressSwitch.rawValue))
        XCTAssertTrue(controller.viewModel?.row(named: Step2Row.billingAddressSwitch.rawValue)?.value as! Bool)
		XCTAssertNotNil(controller.viewModel?.row(named: GuestDetailsRow.addressType.rawValue + Constants.bookerSuffix))
		XCTAssertNotNil(controller.viewModel?.row(named: CountryActionableRow.country.rawValue))
		XCTAssertNotNil(controller.viewModel?.row(named: CountryActionableRow.postCode.rawValue))
		XCTAssertEqual(controller.viewModel?.sections.first?.header?.height, 70)
		XCTAssertEqual(controller.viewModel?.sections.first?.footer?.height, 70)
	}

	func testAddressSection_AllTrueWithAddress() {

		let addressRequirements = AddressSectionRequirements(
			address: try? Address(dictionary: ["addressline1": "Royal Victoria Dock", "addressline2": "2 Festoon Way", "addressline3": "London", "countryCode": "GB", "postcode": "E16 1SJ"]),
            storedAddress: nil,
			shouldShowAddressSwitch: true,
            useStoredAddressSwitchDescription: nil,
			addressSwitchInitialState: false,
			shouldShowAddressForm: true,
			shouldShowHeader: true,
			shouldShowFooter: true,
            shouldShowAddressSummary: false
		)

		let controller = buildController(with: addressRequirements)

		XCTAssertEqual(controller.viewModel?.sections.count, 1)
		XCTAssertEqual(controller.viewModel?.sections.first?.rows.count, 7)
		XCTAssertNotNil(controller.viewModel?.row(named: Step2Row.billingAddressSwitch.rawValue))
        XCTAssertFalse(controller.viewModel?.row(named: Step2Row.billingAddressSwitch.rawValue)?.value as! Bool)
		XCTAssertNotNil(controller.viewModel?.row(named: GuestDetailsRow.addressType.rawValue + Constants.bookerSuffix))
		XCTAssertNotNil(controller.viewModel?.row(named: CountryActionableRow.country.rawValue))
		XCTAssertNotNil(controller.viewModel?.row(named: CountryActionableRow.postCode.rawValue))
		XCTAssertNotNil(controller.viewModel?.row(named: CountryActionableRow.addressLine1.rawValue))
		XCTAssertNotNil(controller.viewModel?.row(named: CountryActionableRow.addressLine2.rawValue))
		XCTAssertNotNil(controller.viewModel?.row(named: CountryActionableRow.addressLine3.rawValue))
		XCTAssertEqual(controller.viewModel?.sections.first?.header?.height, 70)
		XCTAssertEqual(controller.viewModel?.sections.first?.footer?.height, 70)
	}

	func testAddressSection_NoFooterHeader() {

		let addressRequirements = AddressSectionRequirements(
			address: nil,
            storedAddress: nil,
			shouldShowAddressSwitch: true,
            useStoredAddressSwitchDescription: nil,
			addressSwitchInitialState: true,
			shouldShowAddressForm: true,
			shouldShowHeader: false,
			shouldShowFooter: false,
            shouldShowAddressSummary: false
		)

		let controller = buildController(with: addressRequirements)

		XCTAssertEqual(controller.viewModel?.sections.count, 1)
		XCTAssertEqual(controller.viewModel?.sections.first?.rows.count, 4)
		XCTAssertNotNil(controller.viewModel?.row(named: Step2Row.billingAddressSwitch.rawValue))
		XCTAssertNotNil(controller.viewModel?.row(named: GuestDetailsRow.addressType.rawValue + Constants.bookerSuffix))
		XCTAssertNotNil(controller.viewModel?.row(named: CountryActionableRow.country.rawValue))
		XCTAssertNotNil(controller.viewModel?.row(named: CountryActionableRow.postCode.rawValue))
		XCTAssertNil(controller.viewModel?.sections.first?.header)
		XCTAssertEqual(controller.viewModel?.sections.first?.footer?.height, 10)
	}
}
