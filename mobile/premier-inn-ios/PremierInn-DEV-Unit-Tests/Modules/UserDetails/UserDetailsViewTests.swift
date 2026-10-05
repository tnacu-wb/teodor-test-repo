//
//  UserDetailsViewTests.swift
//  PremierInnTests
//
//  Created by Marcello Mascia on 09/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import Formeka
@testable import PremierInn
import SimpleNetwork

private class MockPresenter: UserDetailsPresenterProtocol {

    var didCalluserDetailsDidChange = false
	var shouldShowLoginDidCall = false
	var submitButtonBackgroundColorDidCall = false
	var submitButtonForegroundColorDidCall = false
	var submitButtonTitleDidCall = false
	var cardsDidCall = false
	var userDidCall = false
	var roomsDidCall = false
	var bookerIsStayingDidCall = false
	var purposeGetterDidCall = false
	var purposeSetterDidCall = false
	var viewIsReadyDidCall = false
	var salutationRowDidTapDidCall = false
	var submitButtonDidTapDidCall = false
	var bookerIsStayingDidChangeDidCall = false
	var loginButtonDidTapDidCall = false
	var setSalutationDidCall = false
    var userTappedSearchAddress = false
    var userTappedDeleteAccount = false
    var cityTaxRequired: Bool = false
    var localizedCost: String?
    var localizedCostWithCityTax: String?
    var purposeMessages: [TripPurposeMessageModel]?
    var firstRoomConfig: RoomConfig?

	var submitButtonBackgroundColor: UIColor {
		submitButtonBackgroundColorDidCall = true
		return .red
	}
	var submitButtonForegroundColor: UIColor {
		submitButtonForegroundColorDidCall = true
		return .green
	}
	var submitButtonTitle: String? {
		submitButtonTitleDidCall = true
		return nil
	}
	var cards: [CardType]? {
		cardsDidCall = true
		return nil
	}
	var user: User? {
		userDidCall = true
		return nil
	}
	var rooms: [Room] {
		roomsDidCall = true
		return [Room(), Room()]
	}
	var bookerIsStaying: Bool {
		bookerIsStayingDidCall = true
		return true
	}
	var purpose: TripPurpose? {
		get {
			purposeGetterDidCall = true
			return nil
		}
		set {
			purposeSetterDidCall = true
		}
	}

	func viewIsReady() {

		viewIsReadyDidCall = true
	}

	func salutationRowDidTap(indexPath: IndexPath) {

		salutationRowDidTapDidCall = true
	}

	func submitButtonDidTap() {

		submitButtonDidTapDidCall = true
	}

	func bookerIsStayingDidChange(isStaying: Bool) {

		bookerIsStayingDidChangeDidCall = true
	}

	func loginButtonDidTap() {

		loginButtonDidTapDidCall = true
	}

	func setSalutation(_ salutation: String?, at indexPath: IndexPath) {

		setSalutationDidCall = true
	}

	func userTappedSearchAddress(with searchTerm: SearchTerm) {

        userTappedSearchAddress = true
    }

    func marketingSwitchChanged(to value: Bool, for email: String, firstName: String, lastName: String) {}

    func countryChanged(for: String, email: String) {}

    func createAccountChanged(to selected: Bool) {}

    func updateNewsletterRow(email: String, country: String) {}
    
    func didTapDeleteAccount() {

        userTappedDeleteAccount = true
    }

    func updatePurpose(_ purpose: PremierInn.TripPurposeSelections?) {
        purposeSetterDidCall = true
    }
    
    func userDetailsDidChange() {
        didCalluserDetailsDidChange = true
    }

}

class UserDetailsViewTests: XCTestCase {

	private var presenter: MockPresenter!
	private var controller: UserDetailsViewController!

	override func setUp() {
		super.setUp()

		presenter = MockPresenter()

		controller = UserDetailsViewController()
		controller.presenter = presenter
	}

	override func tearDown() {

		presenter = nil
		controller = nil

		super.tearDown()
	}

    func testViewDidLoad() {

		controller.viewDidLoad()

		XCTAssertTrue(presenter.viewIsReadyDidCall)
    }
}
