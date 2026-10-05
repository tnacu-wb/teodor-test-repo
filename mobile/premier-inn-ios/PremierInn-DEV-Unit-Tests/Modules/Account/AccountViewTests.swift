//
//  AccountViewTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Marcello Mascia on 19/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockPresenter: AccountViewEventHandler {

    var viewIsReadyDidCall = false
    var viewDidAppearDidCall = false
    var passwordWasUpdatedDidCall = false
    var userDetailsWereUpdatedDidCall = false
    var userWasRegisteredDidCall = false
    var myDetailsButtonDidTapDidCall = false
    var paymentMethodsButtonDidTapDidCall = false
    var changePasswordButtonDidTapDidCall = false
    var bookingPreferencesButtonDidTapDidCall = false
    var loginButtonDidTapDidCall = false
    var registerButtonDidTapDidCall = false
    var logoutButtonDidTapDidCall = false

    func viewIsReady() {

        viewIsReadyDidCall = true
    }

    func viewDidAppear() {

        viewDidAppearDidCall = true
    }

    func passwordWasUpdated() {

        passwordWasUpdatedDidCall = true
    }

    func userDetailsWereUpdated() {

        userDetailsWereUpdatedDidCall = true
    }

    func userWasRegistered() {

        userWasRegisteredDidCall = true
    }

    func myDetailsButtonDidTap() {

        myDetailsButtonDidTapDidCall = true
    }

    func paymentMethodsButtonDidTap() {

        paymentMethodsButtonDidTapDidCall = true
    }

    func changePasswordButtonDidTap() {

        changePasswordButtonDidTapDidCall = true
    }

    func bookingPreferencesButtonDidTap() {

        bookingPreferencesButtonDidTapDidCall = true
    }

    func newsletterUpdatesButtonDidTap() {
        
    }

    func loginButtonDidTap() {

        loginButtonDidTapDidCall = true
    }

    func registerButtonDidTap() {

        registerButtonDidTapDidCall = true
    }

    func logoutButtonDidTap() {

        logoutButtonDidTapDidCall = true
    }

    func userDetailsDidFinish(with bookingDetails: BookingDetails, output: FormStep1Output, sender: UIViewController) {

    }
}

class AccountViewTests: XCTestCase {

    private var viewController: AccountViewController!
    private var presenter: MockPresenter!
    private var analytics: MockAnalyticsManager!

    struct ViewModel: AccountViewModel {
        let username: String?
        let email: String?
        var business: (isBusiness: Bool, companyName: String?)
        let userLoggedIn: Bool
        var customLinks: [CustomAccountLinkViewModel]
        let shouldShowPaymentMethods: Bool
    }

    override func setUp() {

        presenter = MockPresenter()

        viewController = AccountViewController()
        viewController.tableView = UITableView()
        viewController.eventHandler = presenter

        analytics = MockAnalyticsManager()
        viewController.analytics = analytics
    }

    override func tearDown() {

        presenter = nil
        viewController = nil
        analytics = nil

        super.tearDown()

    }

    // MARK: - Tests

    func testView_whenViewAppears_invokesTrackState() {
        UserSessionManager.sharedInstance.piUserLoggedOut()

        viewController.beginAppearanceTransition(true, animated: false)
        viewController.endAppearanceTransition()

        let state = analytics.states.first

        XCTAssertEqual(state, "iOS:PI:UK: My Account")
    }

    func testViewIsReady() {

        viewController?.viewDidLoad()
        XCTAssertTrue(presenter.viewIsReadyDidCall)
    }

    func testViewDidAppear() {

        viewController.viewDidAppear(true)
        wait(for: .ocd, description: #function)
        XCTAssertTrue(self.presenter.viewDidAppearDidCall)
    }

    func testLoginButton() {

        viewController.loginButtonDidTap()
        XCTAssertTrue(presenter.loginButtonDidTapDidCall)
    }

    func testLogoutButton() {

        viewController.logoutButtonDidTap()
        XCTAssertTrue(presenter.logoutButtonDidTapDidCall)
    }

    func testRegisterButton() {

        viewController.registerButtonDidTap()
        XCTAssertTrue(presenter.registerButtonDidTapDidCall)
    }

    func testViewModel() {

        let viewModel = ViewModel(username: nil, email: nil, business: (false, nil), userLoggedIn: false, customLinks: [], shouldShowPaymentMethods: true)
        viewController.reloadData(with: viewModel)

        let viewModel2 = ViewModel(username: "Nicholas", email: "ntwisp@me.com", business: (false, nil), userLoggedIn: true, customLinks: [], shouldShowPaymentMethods: true)
        viewController.reloadData(with: viewModel2)
    }

    func testScroll() {

        viewController.scrollToTop()
    }

    func testShowMessage() {

        viewController.showSuccessConfirmation(withMessage: "Huzzah!")
    }
}
