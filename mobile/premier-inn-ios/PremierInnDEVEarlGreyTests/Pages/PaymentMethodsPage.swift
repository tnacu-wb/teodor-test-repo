//
//  PaymentMethodsPage.swift
//  PremierInnUITests
//
//  Created by Freddie Parks on 12/11/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import XCTest
import EarlGrey

class PaymentMethodsPage {

    // MARK: - Elements

    private let backButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("My account"), grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!)]))
    private let paymentButton = EarlGrey.selectElement(with: grey_accessibilityLabel("Pay and check-in"))
    private let changeCardButton = EarlGrey.selectElement(with: grey_accessibilityLabel("Change")).atIndex(0)
    private let cancelButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Cancel"), grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!)]))

    // MARK: - Actions

    func goBack() {
        backButton.perform(grey_tap())
    }

    func cancel() {
        cancelButton.perform(grey_tap())
    }

    // MARK: - Checks

    func checkCardInfo(showing message: String) {
        EarlGrey.selectElement(with: grey_accessibilityLabel(message)).assert(grey_sufficientlyVisible())
    }

    func checkDisabledCard(withCardName name: String) {
        EarlGrey.selectElement(with: grey_accessibilityID("paymentMethodCellAcc\(name)")).assert(grey_sufficientlyVisible())
        EarlGrey.selectElement(with: grey_accessibilityID("cardUsageLabelAcc\(name)")).assert(grey_sufficientlyVisible())
        EarlGrey.selectElement(with: grey_accessibilityID("radioButtonAcc\(name)")).assert(grey_notVisible())
    }

    func checkSelectableCard(withCardName name: String) {
        EarlGrey.selectElement(with: grey_accessibilityID("paymentMethodCellAcc\(name)")).assert(grey_sufficientlyVisible())
        EarlGrey.selectElement(with: grey_accessibilityID("cardUsageLabelAcc\(name)")).assert(grey_sufficientlyVisible())
        EarlGrey.selectElement(with: grey_accessibilityID("radioButtonAcc\(name)")).assert(grey_sufficientlyVisible())
    }
}
