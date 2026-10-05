//
//  ReviewAmendmentsPage.swift
//  PremierInnUITests
//
//  Created by Filippo Minelle on 11/11/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import EarlGrey
import SimpleNetwork
import XCTest

struct ReviewAmendmentsPage {

    // MARK: - Elements

    static let backButton = EarlGrey.selectElement(with: grey_allOf([grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!), grey_interactable(), grey_not(grey_accessibilityLabel("shield"))]))
    static let continentalBreakfastLabel = EarlGrey.selectElement(with: grey_accessibilityLabel("Continental Breakfast"))
    static let confirmChangesButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityID("confirmChangesButton"), grey_interactable()]))
    static let paymentMethodLabel = EarlGrey.selectElement(with: grey_accessibilityLabel("Payment method"))
    static let paymentAuthenticationLabel = EarlGrey.selectElement(with: grey_accessibilityLabel("Payment authentication. You will be redirected as your bank verifies your payment using 3D secure."))
}
