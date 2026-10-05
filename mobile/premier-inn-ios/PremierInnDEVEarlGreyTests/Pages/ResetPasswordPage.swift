//
//  ResetPasswordPage.swift
//  PremierInnUITests
//
//  Created by Freddie Parks on 11/09/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import EarlGrey

struct ResetPasswordPage {

    private let backButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Log In"), grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!)]))
    private let errorBanner = EarlGrey.selectElement(with: grey_accessibilityID("bannerCellAcc"))
    private let emailTextField = EarlGrey.selectElement(with: grey_accessibilityID("emailTextField"))
    private let resetButton = EarlGrey.selectElement(with: grey_accessibilityLabel("Reset Password"))
    private let confirmButton = EarlGrey.selectElement(with: grey_accessibilityLabel("👌")).atIndex(0)

    func goBack() {
        backButton.perform(grey_tap())
    }

    func enterTestEmail(emailAddress: String) {

        emailTextField.perform(grey_clearText())
        emailTextField.perform(grey_typeText(emailAddress))
    }

    func tapResetButton() {

        resetButton.perform(grey_tap())
    }

    func checkPasswordReset(for emailAddress: String) {

        let successMessage = String(format: "If is this is a valid email, we have sent a link to %@ to reset your password", emailAddress)
        EarlGrey.selectElement(with: grey_accessibilityLabel(successMessage)).assert(grey_sufficientlyVisible())
    }

    func tapConfirmPasswordResetButton() {

        confirmButton.perform(grey_tap())
    }

    func checkErrorVisible() {

        GREYCondition(name: "Wait for error view") {

            var error: NSError?

            self.errorBanner.assert(grey_sufficientlyVisible(), error: &error)

            return error == nil

        }.wait(withTimeout: 10, pollInterval: 0.5)
    }
}
