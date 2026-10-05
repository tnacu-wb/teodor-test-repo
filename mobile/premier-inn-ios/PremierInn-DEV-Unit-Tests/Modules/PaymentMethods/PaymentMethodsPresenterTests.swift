//
//  PaymentMethodsPresenterTests.swift
//  PremierInnTests
//
//  Created by Marcello Mascia on 28/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockView: PaymentMethodsViewProtocol {

    var customAnalyticsParameters: PIDictionary?
    
	var setTitleDidCall = false
	var loadViewModelDidCall = false
	var askForConfirmationDidCall = false
	var dismissDidCall = false
	var showErrorDidCall = false

	func setTitle(_ title: String?) {

		setTitleDidCall = true
	}

    func loadViewModel(sections: [PaymentMethodsCardSection], ctaTitle: String?, infoFooterMessage: String?) {

        loadViewModelDidCall = true
    }

	func askForConfirmation(title: String, message: String, cancelButtonTitle: String, confirmButtonTitle: String, completion: @escaping (Bool) -> Void) {

		askForConfirmationDidCall = true

		completion(true)
	}

	func dismiss() {

		dismissDidCall = true
	}

	func showError(title: String?, message: String) {

		showErrorDidCall = true
	}

    func handle(error: Error) {

    }

    func showCancelButton() {
        
    }
}

private class MockInteractor: PaymentMethodsInteractorProtocol {

    var paymentMethodsTracking: PaymentMethodsTracking { return ("", "") }
    var customAnalyticsParameters: PIDictionary?
	var sectionsDidCall = false
	var deleteDidCall = false
	var cardForSectionDidCall = false
    var scope: PaymentMethodsScope = .myPI
    var ctaTitle: String? = ""
    var infoFooterMessage: String? = nil

	var sections: [PaymentMethodsCardSection] {
		sectionsDidCall = true

		return []
	}

	func delete(section: PaymentMethodsCardSection, completion: @escaping (Bool, Error?) -> Void) {

		deleteDidCall = true

		completion(true, nil)
	}

	func cardForSection(section: PaymentMethodsCardSection) -> PaymentCard? {

		cardForSectionDidCall = true

		return PaymentCard.empty
	}

    func shouldCheckWithUserBeforeChanging(to cardAtIndex: Int) -> Bool {

        return false
    }

    func selectedCard(at index: Int) {

    }
}

private class MockRouter: PaymentMethodsRouterProtocol {

	var editDidCall = false
    var deleteDidCall = false

	func edit(card: PaymentCard) {

		editDidCall = true
	}

    func selectedCard() {

    }

    func addNewCard() {

    }

    func useNewCard() {
        
    }

    func deleteCard() {

        deleteDidCall = true
    }

    func confirmUserSelection(with title: String, message: String, completion: @escaping () -> Void) {
        
    }
}

class PaymentMethodsPresenterTests: XCTestCase {
    
	private var view: MockView!
	private var interactor: MockInteractor!
	private var router: MockRouter!
	var presenter: PaymentMethodsPresenter!

	override func setUp() {
		super.setUp()

		view = MockView()
		interactor = MockInteractor()
		presenter = PaymentMethodsPresenter()
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

		XCTAssertTrue(view.setTitleDidCall)
		XCTAssertTrue(view.loadViewModelDidCall)
	}

	func testLinkSelection_Delete() {

        let section = PaymentMethodsCardSection(
            cardName: nil,
            cardType: nil,
            cardHiddenNumber: nil,
            cardHolderName: nil,
            cardExpiration: nil,
            links: [],
            usageDescription: "",
            cardImageURL: nil,
            selectable: false,
            selected: false,
            cardInfoMessage: nil,
            isBookingFlow: false,
            pibaMessaging: nil,
            isDeleteHidden: false,
            accessibilityLabel: nil
        )
		presenter.linkCellDidSelect(with: .delete, section: section)

		XCTAssertTrue(view.askForConfirmationDidCall)
		XCTAssertTrue(interactor.deleteDidCall)
        wait(for: .ocd, description: #function)
        XCTAssertTrue(router.deleteDidCall)
	}

	func testLinkSelection_Edit() {

        let section = PaymentMethodsCardSection(
            cardName: nil,
            cardType: nil,
            cardHiddenNumber: nil,
            cardHolderName: nil,
            cardExpiration: nil,
            links: [],
            usageDescription: "",
            cardImageURL: nil,
            selectable: false,
            selected: false,
            cardInfoMessage: nil,
            isBookingFlow: false,
            pibaMessaging: nil,
            isDeleteHidden: false,
            accessibilityLabel: nil
        )
		presenter.linkCellDidSelect(with: .edit, section: section)

		XCTAssertTrue(interactor.cardForSectionDidCall)
		XCTAssertTrue(router.editDidCall)
	}
}
