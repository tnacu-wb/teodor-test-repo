//
//  PaymentMethodsInteractorTests.swift
//  PremierInnTests
//
//  Created by Marcello Mascia on 28/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockDelegate: PaymentMethodsInteractorDelegate {

	var cardDidUpdateDidCall = false
    var cardDeleteDidCall = false

	func cardDidUpdate() {

		cardDidUpdateDidCall = true
	}

    func cardDelete() {

        cardDeleteDidCall = true
    }

}

class PaymentMethodsInteractorTests: XCTestCase {

    override func setUp() {
        super.setUp()

	}
    
    override func tearDown() {

		super.tearDown()
    }

	func testSections() {

		let user = try! User(title: "Mr", firstName: "John", lastName: "Doe")
		user.paymentPreference = PaymentPreference(dict: nil)
		user.paymentPreference?.card = PaymentCard.empty

		let interactor = PaymentMethodsInteractor(with: user)

		XCTAssertEqual(interactor.sections.count, 1)
	}
}
