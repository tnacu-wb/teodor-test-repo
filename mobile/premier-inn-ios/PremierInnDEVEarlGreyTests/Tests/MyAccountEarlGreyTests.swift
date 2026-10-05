//
//  MyAccountEarlGreyTests.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 28/04/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import EarlGrey
import Hippolyte
import SimpleNetwork

class MyAccountEarlGreyTests: BaseEarlGreyTests {

    override func setUp() {
        super.setUp()

        homePage.goToMyAccount()
    }

    override func tearDown() {
        super.tearDown()

//        MyAccountPage.goToHome()
    }

}

class ResetPasswordEarlGreyTests: BaseEarlGreyTests {

    override func setUp() {
        super.setUp()

        setupMockAPI()

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
        loginPage.tapResetPassword()
    }

    override func tearDown() {

        super.tearDown()

        loginPage.dismiss()
        MyAccountPage.goToHome()
    }

    // MARK: Config

    override func setupMockAPI() {

        Hippolyte.shared.start()
    }

    func testPasswordResetSuccess() {

        Hippolyte.shared.clearStubs()

        loadMock(with: ResetPasswordMock.successTrue)

        let testEmail = "testes@whitbread.com"

        resetPasswordPage.enterTestEmail(emailAddress: testEmail)
        resetPasswordPage.tapResetButton()
        resetPasswordPage.checkPasswordReset(for: testEmail)
        resetPasswordPage.tapConfirmPasswordResetButton()
    }

    func testPasswordResetSuccessFalse() {

        Hippolyte.shared.clearStubs()

        loadMock(with: ResetPasswordMock.successFalse)

        let testEmail = "testes@whitbread.com"

        resetPasswordPage.enterTestEmail(emailAddress: testEmail)
        resetPasswordPage.tapResetButton()
        resetPasswordPage.checkErrorVisible()
        resetPasswordPage.goBack()
    }
}

fileprivate struct MockPack {
    var login: LoginMock = .nickJones
    var userMock: GetUserMock = .nickJones
    var reservations: ReservationsMock = .nickJones
    var marketing: MarketingPreferencesMock = .piOptIn
}

class MarketingEarlGreyTests: BaseEarlGreyTests {

    override func setUp() {
        super.setUp()

        setupMockAPI()

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
    }

    override func tearDown() {

        MyAccountPage.goToHome()

        super.tearDown()
    }

    override func setupMockAPI() {

        Hippolyte.shared.start()
    }

    private func load(mockPack: MockPack) {

        Hippolyte.shared.clearStubs()

        loadMock(with: mockPack.login)
        loadMock(with: mockPack.userMock)
        loadMock(with: mockPack.reservations)
        loadMock(with: mockPack.marketing)
    }

    func testMarketingOptedIn() {

        load(mockPack: MockPack())

        loginPage.performLogin(username: "ntwisp@me.com", password: "Password1")
        MyAccountPage.tapPersonalDetails()
        personalDetailsPage.checkMarketingSwitch(on: false)
        personalDetailsPage.goBackToMyAccount()
    }

    func testMarketingOptedOut() {

        var mockPack = MockPack()
        mockPack.marketing = .piOptOut

        load(mockPack: mockPack)

        loginPage.performLogin(username: "ntwisp@me.com", password: "Password1")
        MyAccountPage.tapPersonalDetails()
        personalDetailsPage.checkMarketingSwitch(on: true)
        personalDetailsPage.goBackToMyAccount()
    }
}
