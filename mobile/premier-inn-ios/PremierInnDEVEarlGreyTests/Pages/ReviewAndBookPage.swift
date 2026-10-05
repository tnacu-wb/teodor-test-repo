//
//  ReviewAndBookPage.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 15/05/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import EarlGrey
import SimpleNetwork
import XCTest

struct ReviewAndBookPage {

    // MARK: - Elements

    let backButton = EarlGrey.selectElement(with: grey_allOf([grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!), grey_interactable(), grey_not(grey_accessibilityLabel("shield"))]))
    let continueButtonAcc = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityID("continueButtonAcc"), grey_interactable()]))
    let changePaymentMethodButtonAcc = EarlGrey.selectElement(with: grey_accessibilityID("paymentMethodChangeButtonAcc"))
    let registerAcceptTCsAcc = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityID("registerAcceptT&CsAcc"), grey_interactable()]))
    let payOnArrivalOption = EarlGrey.selectElement(with: grey_accessibilityLabel("Pay on arrival"))
    let payNowOption = EarlGrey.selectElement(with: grey_accessibilityLabel("Pay now"))
    let tableView = EarlGrey.selectElement(with: grey_accessibilityID("tableView"))
    let cvvInput = EarlGrey.selectElement(with: grey_accessibilityID("reviewPageCVVNumberAcc"))
    let paymentErrorLabel = EarlGrey.selectElement(with: grey_accessibilityID("errorAlertTitle"))
    let paymentErrorCTA = EarlGrey.selectElement(with: grey_accessibilityLabel("Try booking again"))
    let timeoutNewRateLabel = EarlGrey.selectElement(with: grey_accessibilityLabel("Your booking price has changed"))
    let timeoutNewRateAcceptCTA = EarlGrey.selectElement(with: grey_accessibilityLabel("Continue with new price"))

    func rateLabel(with rate: String) -> GREYInteraction {
        return EarlGrey.selectElement(with: grey_accessibilityLabel("Hotel stay: \(rate) (1 night, 1 double room)"))
    }
    func breakfastLabel(with breakfast: String) -> GREYInteraction {
        return EarlGrey.selectElement(with: grey_accessibilityLabel("\(breakfast) (1 guest, 1 night)"))
    }
    func totalLabel(with total: String) -> GREYInteraction {
        return EarlGrey.selectElement(with: grey_accessibilityLabel(total)).atIndex(0)
    }
    func cardNumberLabel(ending: String) -> GREYInteraction {
        return EarlGrey.selectElement(with: grey_accessibilityLabel("Ending in \(ending)"))
    }

    // MARK: - Actions

    func waitToLoad() {

        GREYCondition(name: "Wait for the page to load") {

            var error: NSError?

            self.backButton.assert(grey_sufficientlyVisible(), error: &error)

            return error == nil

        }.wait(withTimeout: 10, pollInterval: 0.5)
    }

    func goBack() {

        GREYCondition(name: "Wait for the Back button to appear") {

            var error: NSError?

            self.backButton.perform(grey_tap(), error: &error)

            return error == nil

        }.wait(withTimeout: 10, pollInterval: 0.5)
    }

    func tapContinue() {

        GREYCondition(name: "scroll until Continue is visible") {

            var error: NSError?

            self.continueButtonAcc.usingSearch(grey_scrollInDirection(.down, 200), onElementWith: grey_accessibilityID("tableView")).perform(grey_tap(), error: &error)

            return error == nil

        }.wait(withTimeout: 5, pollInterval: 0.5)
    }

    func changePaymentMethod() {

        GREYCondition(name: "scroll until Continue is visible") {

            var error: NSError?

            self.changePaymentMethodButtonAcc.usingSearch(grey_scrollInDirection(.down, 50), onElementWith: grey_accessibilityID("tableView")).perform(grey_tap(), error: &error)

            return error == nil

        }.wait(withTimeout: 5, pollInterval: 0.5)
    }

    func acceptTerms() {

        GREYCondition(name: "scroll until Continue is visible") {

            var error: NSError?

            self.registerAcceptTCsAcc.usingSearch(grey_scrollInDirection(.down, 200), onElementWith: grey_accessibilityID("tableView")).perform(grey_turnSwitchOn(true), error: &error)

            return error == nil

        }.wait(withTimeout: 5, pollInterval: 0.5)
    }

    func scrollDown(amount: CGFloat = 300) {
        tableView.perform(grey_scrollInDirection(.down, amount))
    }

    func selectPayment(interval option: PaymentIntervalOption) {

        GREYCondition(name: "scroll until Payment time is visible") {

            var error: NSError?

            let interaction: GREYInteraction = option == .now ? self.payNowOption : self.payOnArrivalOption
            let paymentIntervalElement = interaction.usingSearch(grey_scrollInDirection(.down, 200), onElementWith: grey_accessibilityID("tableView")).atIndex(0)
            paymentIntervalElement.perform(grey_tap(), error: &error)

            return error == nil
        }.wait(withTimeout: 5, pollInterval: 0.5)
    }

    func enterCVV(_ number: String) {

        cvvInput.perform(grey_typeText(number))
    }

    func check(rate: String, termsMessage: String? = nil, extras: String? = nil, total: String) {

        rateLabel(with: rate).assert(grey_sufficientlyVisible())
        totalLabel(with: total).assert(grey_sufficientlyVisible())

        if let termsMessage = termsMessage {
            EarlGrey.selectElement(with: grey_accessibilityLabel(termsMessage)).assert(grey_sufficientlyVisible())
        }

        if let extras = extras {
            breakfastLabel(with: extras).assert(grey_sufficientlyVisible())
        }
    }

    private func check(for guestWithName: String) {

        GREYCondition(name: "scroll until guest is visible") {

            var error: NSError?

            EarlGrey.selectElement(with: grey_accessibilityLabel(guestWithName)).usingSearch(grey_scrollInDirection(.down, 200), onElementWith: grey_accessibilityID("tableView")).assert(grey_sufficientlyVisible(), error: &error)

            return error == nil

        }.wait(withTimeout: 5, pollInterval: 0.5)
    }

    func check(booker: String) {

        check(for: booker)
    }

    func check(roomGuest: String) {

        check(for: roomGuest)
    }

    func checkErrorShowing() {

        paymentErrorLabel.assert(grey_sufficientlyVisible())
    }

    func dismissError() {

        paymentErrorCTA.perform(grey_tap())
    }

    func checkTimeoutNewRateShowing() {

        timeoutNewRateLabel.assert(grey_sufficientlyVisible())
    }

    func acceptNewRate() {

        timeoutNewRateAcceptCTA.perform(grey_tap())
    }

    func checkPayingWithCardNumber(withLastFourDigits ending: String) {

        GREYCondition(name: "check booking success and payment time") {

            var error: NSError?
            self.cardNumberLabel(ending: ending).assert(grey_sufficientlyVisible(), error: &error)

            return error == nil
        }.wait(withTimeout: 5, pollInterval: 1)
    }
}
