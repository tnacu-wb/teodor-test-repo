//
//  BiometricAuthenticationTests.swift
//  PremierInnTests
//
//  Created by Filippo Minelle on 07/08/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import EarlGrey
import Hippolyte
import Quick

@testable import PremierInn

class BiometricAuthenticationTestsSpec: QuickSpec {

    override func spec() {

        let homePage = HomePage()
        let loginPage = LoginPage()

        describe("WHEN the user in the login page") {

            context("THEN logs in for valid credentials") {

                beforeEach {

                    Biometrics.enrolled()

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

                it("IT should appear a Touch/Face ID message") {

                    let alertTitle = EarlGrey.selectElement(with: grey_text((BiometricAuthentication.biometryTypeAvailable == .faceId) ? "Enable Face ID" : "Enable Touch ID"))
                    let alertEnable = EarlGrey.selectElement(with: grey_text("Enable"))

                    let buttonLoginWithTouchFaceID = EarlGrey.selectElement(with: grey_text((BiometricAuthentication.biometryTypeAvailable == .faceId) ? "Log in with Face ID" : "Log in with Touch ID"))

                    loginPage.performLogin(username: "g.aikaterinakis@mailinator.com", password: "GeorgeA1")

                    alertTitle.assert(grey_sufficientlyVisible())
                    alertEnable.assert(grey_sufficientlyVisible())

                    alertEnable.perform(grey_tap())

                    loginPage.loginIsSuccessful()

                    MyAccountPage.logout()
                    MyAccountPage.tapLogin()

                    buttonLoginWithTouchFaceID.assert(grey_sufficientlyVisible())

                    loginPage.dismiss()
                    MyAccountPage.goToHome()
                }
            }
        }
    }
}
