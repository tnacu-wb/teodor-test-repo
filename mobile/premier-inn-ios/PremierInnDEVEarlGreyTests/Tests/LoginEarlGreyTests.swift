//
//  LoginEarlGreyTests.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 17/04/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import EarlGrey
import Hippolyte
import Quick

@testable import PremierInn

class LoginSpec: QuickSpec {
    override func spec() {
        let homePage = HomePage()
        let loginPage = LoginPage()

        describe("login feature") {
            context("unsuccessful login") {
                beforeEach {
                    let appDelegate = UIApplication.shared.delegate as? AppDelegate
                    appDelegate?.resetApplicationForTesting()

                    GREYCondition(name: "Wait for main root view controller") {
                        return true
                    }.wait(withTimeout: 3)

                    homePage.hideBanner()
                    homePage.select(environment: .alpha2)

                    homePage.goToMyAccount()
                    MyAccountPage.tapLogin()
                }
                it("logs in for valid credentials") {

                    loginPage.performLogin(username: "g.aikaterinakis@mailinator.com", password: "GeorgeA1")
                    loginPage.loginIsSuccessful()

                    MyAccountPage.logout()
                    MyAccountPage.goToHome()
                }
                it("doesn't log in for invalid credentials") {

                    loginPage.performLogin(username: "test@test.com", password: "incorrectPassword:(")
                    loginPage.loginIsUnsuccessful()

                    loginPage.dismiss()
                    MyAccountPage.goToHome()
                }

                it("does not allow login with empty textfields") {

                    loginPage.tapLogin()
                    loginPage.missingEmailErrorIsShow()
                    loginPage.missingPasswordErrorIsShow()

                    loginPage.dismiss()
                    MyAccountPage.goToHome()
                }
            }
        }
    }
}

class LoginEarlGreyTests: BaseEarlGreyTests {

    override func setUp() {
        super.setUp()

        homePage.goToMyAccount()
        MyAccountPage.tapLogin()
    }

    override func tearDown() {
        super.tearDown()
    }

    override func setupMockAPI() {

        NetworkMockManager.stubLoginRequest(method: .POST, urlString: "https://wbodetest.eu.auth0.com/oauth/token", jsonName: "successfulLoginResponse", password: "correctPassword!")
        NetworkMockManager.stubLoginRequest(method: .POST, urlString: "https://wbodetest.eu.auth0.com/oauth/token", jsonName: "unsuccessfulLoginResponse", password: "incorrectPassword:(")
        NetworkMockManager.addStubRequest(method: .GET, urlString: "https://api-uat.whitbread.co.uk/customers/hotels/test@test.com?business=false", jsonName: "getUserResponse")

        Hippolyte.shared.start()
    }

    func testSuccessfulLogin() {

        loginPage.performLogin(username: "test@test.com", password: "correctPassword!")
        loginPage.loginIsSuccessful()

        MyAccountPage.logout()
        MyAccountPage.goToHome()
    }

    func testUnsuccessfulLogin() {

        loginPage.performLogin(username: "test@test.com", password: "incorrectPassword:(")
        loginPage.loginIsUnsuccessful()

        loginPage.dismiss()
        MyAccountPage.goToHome()
    }

    func testNoDataForLogin() {

        loginPage.tapLogin()
        loginPage.missingEmailErrorIsShow()
        loginPage.missingPasswordErrorIsShow()

        loginPage.dismiss()
        MyAccountPage.goToHome()
    }

    func do_testInvalidEmailForLogin(username: String) {
        loginPage.performLogin(username: username, password: "00000000")
        loginPage.invalidEmailAddressErrorIsShown()

        loginPage.dismiss()
        MyAccountPage.goToHome()
    }

    func testInvalidEmailForLogin() { do_testInvalidEmailForLogin(username: "test") }
    func testInvalidEmailForLogin2() { do_testInvalidEmailForLogin(username: "test@") }
    func testInvalidEmailForLogin3() { do_testInvalidEmailForLogin(username: "test@test") }
}
