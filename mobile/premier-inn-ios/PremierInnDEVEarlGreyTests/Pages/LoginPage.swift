//
//  LoginPage.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 17/04/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import EarlGrey

@testable import PremierInn

class LoginPage {

    private let errorMessage = EarlGrey.selectElement(with: grey_accessibilityID("bannerCellAcc"))
    private let emailTextfield = EarlGrey.selectElement(with: grey_accessibilityID("emailCellAcc"))
    private let passwordTextfield = EarlGrey.selectElement(with: grey_accessibilityID("passwordCellAcc"))
    private let businessBookerTab = EarlGrey.selectElement(with: grey_accessibilityID("businessBookerTab"))

    private let missingEmailError = EarlGrey.selectElement(with: grey_accessibilityLabel("Please enter a valid email"))
    private let missingPasswordError = EarlGrey.selectElement(with: grey_accessibilityLabel("Please enter a valid password"))
    private let invalidEmailError = EarlGrey.selectElement(with: grey_accessibilityLabel("Please enter a valid email address"))

    private let submitButton = EarlGrey.selectElement(with: grey_accessibilityID(AccessibilityIdentifiers.Login.submitButton))
    private let cancelButton = EarlGrey.selectElement(with: grey_accessibilityID("cancelButton"))
    private let forgotPasswordButton = EarlGrey.selectElement(with: grey_accessibilityID("forgotPasswordButtonAcc"))

    func performLogin(username: String, password: String) {

        emailTextfield.perform(grey_typeText(username))
        passwordTextfield.perform(grey_typeText(password))
        tapLogin()
    }

    func performBusinessLogin(username: String, password: String) {

        businessBookerTab.perform(grey_tap())
        performLogin(username: username, password: password)
    }

    func loginIsSuccessful() {

        emailTextfield.assert(grey_notVisible())
        passwordTextfield.assert(grey_notVisible())
    }

    func loginIsUnsuccessful() {

        GREYCondition(name: "Wait for the login result and animation") {

            var error: NSError?
            self.errorMessage.assert(grey_sufficientlyVisible(), error: &error)

            return error == nil
        }.wait(withTimeout: 10, pollInterval: 0.5)

        errorMessage.assert(grey_sufficientlyVisible())
        emailTextfield.assert(grey_sufficientlyVisible())
        passwordTextfield.assert(grey_sufficientlyVisible())
    }

    func missingEmailErrorIsShow() { missingEmailError.assert(grey_sufficientlyVisible()) }
    func missingPasswordErrorIsShow() { missingPasswordError.assert(grey_sufficientlyVisible()) }
    func invalidEmailAddressErrorIsShown() { invalidEmailError.assert(grey_sufficientlyVisible()) }

    func tapLogin() { submitButton.perform(grey_tap()) }
    func dismiss() { cancelButton.perform(grey_tap()) }
    func tapResetPassword() { forgotPasswordButton.perform(grey_tap()) }
}
