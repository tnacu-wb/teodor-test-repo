//
//  PersonalDetailsPage.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 11/05/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import EarlGrey

struct PersonalDetailsPage {

    // MARK: - Elements

    let backButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Back"), grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!)]))
    let backToMyAccountButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("My account"), grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!)]))
    let tableView = EarlGrey.selectElement(with: grey_accessibilityID("tableView"))

    let chooseTitle = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityID("Title"), grey_interactable()]))
    let firstNameBookerAcc = EarlGrey.selectElement(with: grey_accessibilityID("firstNameBookerAcc"))
    let lastNameBookerAcc = EarlGrey.selectElement(with: grey_accessibilityID("lastNameBookerAcc"))

    //let chooseTitleRoom1 = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityID("Title"), grey_interactable()]))
    let firstNameRoom1Acc = EarlGrey.selectElement(with: grey_accessibilityID("firstName0Acc"))
    let lastNameRoom1Acc = EarlGrey.selectElement(with: grey_accessibilityID("lastName0Acc"))

    let contactNumberBookerAcc = EarlGrey.selectElement(with: grey_accessibilityID("contactNumberBookerAcc"))
    let emailAddressBookerAcc = EarlGrey.selectElement(with: grey_accessibilityID("emailAddressBookerAcc"))
    let addAddressManually = EarlGrey.selectElement(with: grey_accessibilityID("addAddressManually"))
    let countryAcc = EarlGrey.selectElement(with: grey_accessibilityID("countryAcc"))
    let postCodeAcc = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityID("postCodeAcc"), grey_interactable()]))
    let postCodeFindButton = EarlGrey.selectElement(with: grey_allOf([ grey_accessibilityLabel("Find"), grey_interactable()]))
    let addressLine1Acc = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityID("addressLine1Acc"), grey_interactable()]))
    let addressLine2Acc = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityID("addressLine2Acc"), grey_interactable()]))
    let addressLine3Acc = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityID("addressLine3Acc"), grey_interactable()]))
    let yourDetailsPageContinueToPaymentButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityID("yourDetailsPageContinueToPaymentButton"), grey_interactable()]))
    let bookerNotStayerToggle = EarlGrey.selectElement(with: grey_accessibilityID("bookingNotStayingRadioButton"))
    let marketingToggle = EarlGrey.selectElement(with: grey_accessibilityID("yourDetailsMarketingSwitch"))

    // MARK: - Actions

    func goBack() { backButton.perform(grey_tap()) }
    func goBackToMyAccount() { backToMyAccountButton.perform(grey_tap()) }
    func scrollDown(_ amount: CGFloat) { tableView.perform(grey_scrollInDirection(.down, amount)) }
    func scrollDown() { tableView.perform(grey_scrollInDirection(.down, 500)) }
    func tapAddAddressManually() { addAddressManually.perform(grey_tap()) }
    func tapContinue() {

        GREYCondition(name: "scroll until Continue is visible") {

            var error: NSError?

            self.yourDetailsPageContinueToPaymentButton.usingSearch(grey_scrollInDirection(.down, 200), onElementWith: grey_accessibilityID("tableView")).perform(grey_tap(), error: &error)

            return error == nil

        }.wait(withTimeout: 5, pollInterval: 0.5)
    }

    func enterTestPersonalDetails() {

        chooseTitle.perform(grey_tap())
        EarlGrey.selectElement(with: grey_accessibilityLabel("Mr")).perform(grey_tap())
        firstNameBookerAcc.perform(grey_typeText("Test"))
        lastNameBookerAcc.perform(grey_typeText("Test"))
        contactNumberBookerAcc.perform(grey_typeText("07512345678"))
        emailAddressBookerAcc.perform(grey_typeText("test@test.com"))
        scrollDown()
        postCodeAcc.perform(grey_typeText("SW1 2AB"))
        addAddressManually.perform(grey_tap())
        addressLine1Acc.perform(grey_typeText("line 1"))
        tableView.perform(grey_scrollInDirection(.down, 100))
    }

    func toggleBookerNotStayer() {

        GREYCondition(name: "scroll until booker switch is visible") {

            var error: NSError?

            self.bookerNotStayerToggle.usingSearch(grey_scrollInDirection(.down, 200), onElementWith: grey_accessibilityID("tableView")).perform(grey_tap(), error: &error)

            return error == nil

        }.wait(withTimeout: 5, pollInterval: 0.5)
    }

    func enterTestGuestDetails() {

        GREYCondition(name: "scroll until title is visible") {

            var error: NSError?

            self.chooseTitle.usingSearch(grey_scrollInDirection(.down, 200), onElementWith: grey_accessibilityID("tableView")).perform(grey_tap(), error: &error)

            return error == nil

        }.wait(withTimeout: 5, pollInterval: 0.5)

        EarlGrey.selectElement(with: grey_accessibilityLabel("Mr")).perform(grey_tap())
        firstNameRoom1Acc.perform(grey_typeText("Testes"))
        lastNameRoom1Acc.perform(grey_typeText("Testes"))
    }

    func findPostcode(_ postcode: String) {

        postCodeAcc.perform(grey_typeText(postcode))
        postCodeFindButton.perform(grey_tap())
    }

    func checkFieldsArePopulatedWithAddress(postcode: String, line1: String, line2: String, line3: String) {

        postCodeAcc.assert(grey_textFieldValue(postcode))
        addressLine1Acc.assert(grey_textFieldValue(line1))
        addressLine2Acc.assert(grey_textFieldValue(line2))
        addressLine3Acc.assert(grey_textFieldValue(line3))
    }

    func requiredValuesErrors() {

        EarlGrey.selectElement(with: grey_accessibilityLabel("Please enter a valid title")).assert(grey_sufficientlyVisible())
        EarlGrey.selectElement(with: grey_accessibilityLabel("Please enter a valid first name")).assert(grey_sufficientlyVisible())
        EarlGrey.selectElement(with: grey_accessibilityLabel("Please enter a valid last name")).assert(grey_sufficientlyVisible())
        EarlGrey.selectElement(with: grey_accessibilityLabel("Please enter a valid phone number")).assert(grey_sufficientlyVisible())
        EarlGrey.selectElement(with: grey_accessibilityLabel("Please enter a valid email address")).assert(grey_sufficientlyVisible())

        scrollDown()

        EarlGrey.selectElement(with: grey_accessibilityLabel("Please enter a valid postcode")).assert(grey_sufficientlyVisible())

    }

    func checkMarketingSwitch(on: Bool = true) {

        GREYCondition(name: "scroll until marketing is visible") {

            var error: NSError?

            self.marketingToggle.usingSearch(grey_scrollInDirection(.down, 200), onElementWith: grey_accessibilityID("tableView")).assert(grey_switchWithOnState(on), error: &error)

            return error == nil

        }.wait(withTimeout: 5, pollInterval: 0.5)
    }
}
