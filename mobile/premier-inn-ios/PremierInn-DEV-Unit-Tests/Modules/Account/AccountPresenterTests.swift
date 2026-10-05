//
//  AccountPresenterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Marcello Mascia on 19/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockView: AccountViewProtocol {

    var reloadDataDidCall = false
    var scrollToTopDidCall = false
    var successMessage = String.localizedStringWithFormat(PILocalizedString("userPreferenceUserRegisteredBannerTitle", comment: "User preference user registered banner title"), "Test")

    func reloadData(with accountViewModel: AccountViewModel) {

        reloadDataDidCall = true
    }

    func scrollToTop() {

        scrollToTopDidCall = true
    }

    func showSuccessConfirmation(withMessage message: String) {

        successMessage = message
    }
}

private class MockRouter: AccountRouterProtocol {

    var openMyDetailsDidCall = false
    var openPaymentMethodsDidCall = false
    var openChangePasswordDidCall = false
    var openBookingPreferencesDidCall = false
    var openLoginDidCall = false
    var openRegisterDidCall = false

    func openMyDetails(withCompletionDelegate: UserDetailsRouterDelegate?, deleteAccountDelegate: DeleteAccountDelegate?) {

        openMyDetailsDidCall = true
    }

    func openPaymentMethods(paymentMethodDelegate: PaymentMethodsRouterDelegate, cardDetailsDelegate: CardDetailsRouterDelegate, addNewCardRouterDelegate: AddNewCardRouterDelegate) {
        openPaymentMethodsDidCall = true
    }

    func openChangePassword(withCompletionDelegate completionDelegate: ChangePasswordCompletionInput?) {

        openChangePasswordDidCall = true
    }

    func openBookingPreferences() {

        openBookingPreferencesDidCall = true
    }

    func openNewsletterPreferences(with emailAddress: String) {
        
    }

    func openLogin() {

        openLoginDidCall = true
    }

    func openRegister(withCompletionDelegate completionDelegate: RegisterCompletionInput?) {

        openRegisterDidCall = true
    }
}

private class MockInteractor: AccountInteractorProtocol {

    var getViewModelDidCall = false
    var getAccountMessageDidCall = false
    var getDetailsChangedMessageDidCall = false
    var getPasswordChangedMessageDidCall = false
    var userLoggedOutDidCall = false
    var getDeletedCardMessageDidCall = false
    var getSavedCardMessageDidCall = false
    var getUpdatedCardMessageDidCall = false

    private struct ViewModel: AccountViewModel {

        let username: String?
        let email: String?
        var business: (isBusiness: Bool, companyName: String?)
        let userLoggedIn: Bool
        var customLinks: [CustomAccountLinkViewModel]
        let shouldShowPaymentMethods: Bool
    }

    var accountCreatedMessage: String {

        getAccountMessageDidCall = true

        return String.localizedStringWithFormat(PILocalizedString("userPreferenceUserRegisteredBannerTitle", comment: "User preference user registered banner title"), "Test")
    }

    var detailsChangedMessage: String {

        getDetailsChangedMessageDidCall = true

        return PILocalizedString("accountPreferencesUserDetailsUpdatedSuccessBannerTitle", comment: "Your personal details have been successfully updated")
    }

    var passwordChangedMessage: String {

        getPasswordChangedMessageDidCall = true

        return PILocalizedString("accountPreferencesPasswordUpdatedSuccessBannerTitle", comment: "Account preferences password updated success banner title")
    }

    var deletedCardMessage: String {

        getDeletedCardMessageDidCall = true

        return PILocalizedString("cardDeletedSuccessBannerTitle")
    }

    var savedCardMessage: String {

        getSavedCardMessageDidCall = true

        return PILocalizedString("cardSavedSuccessBannerTitle")
    }

    var updatedCardMessage: String {

        getUpdatedCardMessageDidCall = true

        return PILocalizedString("Your payment details have been saved")
    }

    var accountViewModel: AccountViewModel? {

        getViewModelDidCall = true

        return ViewModel(
            username: "fred",
            email: "cumslut@meme.cum",
            business: (true, "Company Name"),
            userLoggedIn: true,
            customLinks: [],
            shouldShowPaymentMethods: true
        )
    }

    func userLoggedOut() {

        userLoggedOutDidCall = true
    }

    func updateUser(shouldAttemptLogin: Bool, completion: @escaping () -> Void) {
        completion()
    }
}

class AccountPresenterTests: XCTestCase {

    private var view: MockView!
    private var presenter: AccountPresenter!
    private var router: MockRouter!
    private var interactor: MockInteractor?

    override func setUp() {

        view = MockView()
        router = MockRouter()
        interactor = MockInteractor()

        presenter = AccountPresenter()
        presenter.interactor = interactor
        presenter.view = view
        presenter.router = router
    }

    override func tearDown() {

        presenter = nil
        view = nil
        router = nil
        
        super.tearDown()
    }

    func testViewIsReady() {

        presenter.viewIsReady()
        XCTAssertTrue(view.reloadDataDidCall)
    }

    func testSuccessMessage_Registration() {

        UserSessionManager.sharedInstance.loggedIn(with: try! User(title: "Mr", firstName: "Test", lastName: "Mc Tester"))
        presenter.userWasRegistered()
        presenter.viewDidAppear()

        let message = String.localizedStringWithFormat(PILocalizedString("userPreferenceUserRegisteredBannerTitle", comment: "User preference user registered banner title"), "Test")

        XCTAssertEqual(view.successMessage, message)
    }

    func testSuccessMessage_ChangePassword() {

        presenter.passwordWasUpdated()
        presenter.viewDidAppear()

        XCTAssertEqual(view.successMessage, PILocalizedString("accountPreferencesPasswordUpdatedSuccessBannerTitle", comment: "Account preferences password updated success banner title"))
    }

    func testSuccessMessage_DetailsChange() {

        presenter.userDetailsWereUpdated()
        presenter.viewDidAppear()

        XCTAssertEqual(view.successMessage, PILocalizedString("accountPreferencesUserDetailsUpdatedSuccessBannerTitle", comment: "Your personal details have been successfully updated"))
    }

    func testMyDetailsButtonDidTap() {

        presenter.myDetailsButtonDidTap()
        XCTAssertTrue(router.openMyDetailsDidCall)
    }

    func testRegisterButtonDidTap() {

        presenter.registerButtonDidTap()
        XCTAssertTrue(router.openRegisterDidCall)
    }

    func testLoginButtonDidTap() {

        presenter.loginButtonDidTap()
        XCTAssertTrue(router.openLoginDidCall)
    }

    func testBookingPreferencesButtonDidTap() {

        presenter.bookingPreferencesButtonDidTap()
        XCTAssertTrue(router.openBookingPreferencesDidCall)
    }

    func testChangePasswordButtonDidTap() {

        presenter.changePasswordButtonDidTap()
        XCTAssertTrue(router.openChangePasswordDidCall)
    }

    func testPaymentMethodsButtonDidTap() {

        presenter.paymentMethodsButtonDidTap()

        wait(for: .ocd, description: #function)

        XCTAssertTrue(router.openPaymentMethodsDidCall)
    }
}
