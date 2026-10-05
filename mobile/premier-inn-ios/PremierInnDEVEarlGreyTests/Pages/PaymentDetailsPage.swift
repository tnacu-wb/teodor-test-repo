//
//  PaymentDetailsPage.swift
//  PremierInn
//
//  Created by Freddie Parks on 15/09/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import XCTest
import EarlGrey

class PaymentDetailsPage {

    private let backButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Back"), grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!)]))
    private let paymentButton = EarlGrey.selectElement(with: grey_accessibilityLabel("Pay and check-in"))
    private let changeCardButton = EarlGrey.selectElement(with: grey_accessibilityLabel("Change")).atIndex(0)
    private let cardNumberInput = EarlGrey.selectElement(with: grey_accessibilityID("cardNumberAcc"))
    private func backButton(with label: String) -> GREYInteraction {
        return EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel(label), grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!)]))
    }

    func goBack(with backLabel: String = "Back") {
        backButton(with: backLabel).perform(grey_tap())
    }

    func tapChangeCard() {
        changeCardButton.perform(grey_tap())
    }

    func checkPaymentAllowed() {
        paymentButton.assert(grey_sufficientlyVisible())
    }

    func checkSaveCard(is visible: Bool) {

        EarlGrey.selectElement(with: grey_accessibilityLabel("Save card details for later")).assert(visible ? grey_sufficientlyVisible() : grey_notVisible())
    }
}
