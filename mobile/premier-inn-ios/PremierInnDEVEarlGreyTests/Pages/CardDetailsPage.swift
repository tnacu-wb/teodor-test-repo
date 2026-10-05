//
//  CardDetailsPage.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 12/05/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import EarlGrey

class CardDetailsPage {

    private let tableView = EarlGrey.selectElement(with: grey_accessibilityID("tableView"))
    private let backButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Back"), grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!)]))

    private let cardholderNameAcc = EarlGrey.selectElement(with: grey_accessibilityID("cardholderNameAcc"))
    private let cardExpiryAcc = EarlGrey.selectElement(with: grey_accessibilityValue("mm/yy"))
    private let cardNumberAcc = EarlGrey.selectElement(with: grey_accessibilityID("cardNumberAcc"))
    private let cvvAcc = EarlGrey.selectElement(with: grey_accessibilityID("reviewPageCVVNumberAcc"))

    private let addAddressManually = EarlGrey.selectElement(with: grey_accessibilityID("addAddressManually"))
    private let postCodeAcc = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityID("postCodeAcc"), grey_interactable()]))
    private let addressLine1Acc = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityID("addressLine1Acc"), grey_interactable()]))

    private let continueButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityID("submitButtonIdentifier"), grey_interactable()]))
    private let payNowButton = EarlGrey.selectElement(with: grey_accessibilityLabel("Pay and check-in"))

    private let keyboardDoneButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Done"), grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!), grey_interactable()]))

    func goBack() { backButton.perform(grey_tap()) }
    private func scrollDown(_ amount: CGFloat) { tableView.perform(grey_scrollInDirection(.down, amount)) }
    private func scrollUp(_ amount: CGFloat) { tableView.perform(grey_scrollInDirection(.up, amount)) }

    func enterTestData(for cardNumber: String = "4444333322221111") {

        enterCardNumber(cardNumber)
        enterCardExpiryDate("0125")
        enterCardholderName("Mr T Test")
    }

    func enterMakePaymentTestData(for cardNumber: String = "4444333322221111") {

        enterTestData(for: cardNumber)
        enterCvv("456")

        scrollDown(10)

        postCodeAcc.perform(grey_typeText("EC1N 2TD"))
        addAddressManually.perform(grey_tap())
        addressLine1Acc.perform(grey_typeText("120 Shoehorn"))

        scrollUp(1)
    }

    func enterCardNumber(_ number: String) { cardNumberAcc.perform(grey_typeText(number)) }
    func enterCardExpiryDate(_ string: String) { cardExpiryAcc.perform(grey_typeText(string)) }
    func enterCardholderName(_ name: String) { cardholderNameAcc.perform(grey_typeText(name)) }
    func enterCvv(_ cvv: String) { cvvAcc.perform(grey_typeText(cvv)) }

    func checkInvalidCardError(isShown: Bool) {

        EarlGrey.selectElement(with: grey_accessibilityLabel("We do not accept this card type. Please use a different card.")).assert(isShown ? grey_sufficientlyVisible() : grey_notVisible())
    }

    func checkCancelableText(with string: String) {

        GREYCondition(name: "scroll until wifi is visible") {

            var error: NSError?

            EarlGrey.selectElement(with: grey_accessibilityLabel(string)).usingSearch(grey_scrollInDirection(.down, 200), onElementWith: grey_accessibilityID("tableView")).assert(grey_sufficientlyVisible(), error: &error)

            return error == nil

        }.wait(withTimeout: 5, pollInterval: 0.5)
    }

    func dismissKeyboard() { keyboardDoneButton.perform(grey_tap()) }
    func tapContinue() {
        dismissKeyboard()
        continueButton.perform(grey_tap())
    }
    func tapPayNowAndCheckIn() { payNowButton.perform(grey_tap()) }
}
