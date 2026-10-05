//
//  PaymentMethodsViewTests.swift
//  PremierInnTests
//
//  Created by Marcello Mascia on 26/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

private class MockPresenter: PaymentMethodsPresenterProtocol {

    var paymentMethodsTracking: PaymentMethodsTracking { return ("", "") }
    var viewIsReadyDidCall = false
	var linkCellDidSelectDidCall = false

    func viewIsReady() {

        viewIsReadyDidCall = true
    }

	func linkCellDidSelect(with action: PaymentMethodsLinkAction, section: PaymentMethodsCardSection) {

		linkCellDidSelectDidCall = true
	}

    func selectedPaymentCard(at index: Int) {

    }

    func ctaDidTap() {
        
    }
}

class PaymentMethodsViewTests: XCTestCase {
    
    private var presenter: MockPresenter!
    private var viewController: PaymentMethodsViewController!

    // MARK: - Lifecycle

    override func setUp() {
        super.setUp()

        presenter = MockPresenter()

        viewController = PaymentMethodsViewController()
        viewController.presenter = presenter
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

    func testViewModel() {

        viewController.loadView()
        viewController.loadViewModel(sections: [], ctaTitle: nil, infoFooterMessage: nil)

        XCTAssertNotNil(viewController.viewModel?.indexPath(forRowNamed: PaymentMethodsViewRow.savedCardSyncInfo.rawValue))
    }
}
