//
//  BookingConfirmationPage.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 26/05/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import EarlGrey
import SimpleNetwork

open class GetLabelText {
    var text = ""
}

public func grey_getText(_ elementCopy: GetLabelText) -> GREYActionBlock {
    return GREYActionBlock.action(withName: "get text", constraints: grey_respondsToSelector(#selector(getter: UILabel.text))) { element, errorOrNil -> Bool in
        let elementObject = element as? NSObject
        let text = elementObject?.perform(#selector(getter: UILabel.text), with: nil)?.takeUnretainedValue() as? String
        elementCopy.text = text ?? ""
        return true
    }
}

struct BookingConfirmationPage {

    // MARK: - Elements

    let tableView = EarlGrey.selectElement(with: grey_accessibilityID("tableView"))
    let cancelButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Cancel"), grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!)]))
    let backButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Close"), grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!)]))
    let backButtonBookingDetails = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("My bookings"), grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!)]))
    let bookingConfirmationLabel = EarlGrey.selectElement(with: grey_accessibilityLabel("Thanks for your booking"))
    let manageBookingButton = EarlGrey.selectElement(with: grey_accessibilityLabel("Manage booking"))

    func rateLabel(with name: String) -> GREYInteraction{
        return EarlGrey.selectElement(with: grey_accessibilityLabel(name))
    }

    // MARK: - Actions

    func goBack() { backButton.perform(grey_tap()) }
    func goBackBookingsDetails() { backButtonBookingDetails.perform(grey_tap()) }

    func checkLiveBookingIsSuccessful() {

        GREYCondition(name: "check booking success and payment time") {

            var error: NSError?
            self.bookingConfirmationLabel.assert(grey_sufficientlyVisible(), error: &error)

            return error == nil
        }.wait(withTimeout: 300, pollInterval: 1)
    }

    func checkBookingIsSuccessful(for paymentOption: PaymentIntervalOption, with rateName: String?) {

        bookingConfirmationLabel.assert(grey_sufficientlyVisible())

        let element = GetLabelText()
        EarlGrey.selectElement(with: grey_accessibilityID("bookingConfirmationMessage")).perform(grey_getText(element))
        let paymentMessageExpected = paymentOption == .now ? "paid in full" : "paid on arrival"
        assert(element.text.contains(paymentMessageExpected))

        self.tableView.perform(grey_scrollInDirection(.down, 500))

        if let rateName = rateName {

            self.rateLabel(with: rateName).assert(grey_sufficientlyVisible())
        }
    }

    func selectManangeBooking() {

        GREYCondition(name: "scroll until manage booking is visible") {

            var error: NSError?

            self.manageBookingButton.usingSearch(grey_scrollInDirection(.down, 50), onElementWith: grey_accessibilityID("tableView")).perform(grey_tap(), error: &error)

            return error == nil

        }.wait(withTimeout: 5, pollInterval: 0.2)
    }
}
