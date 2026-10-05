//
//  BusinessCardQuestionsPage.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Freddie Parks on 05/08/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import XCTest
import EarlGrey

class BusinessCardQuestionsPage {

    // MARK: - Elements

    private let backButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Back"), grey_kindOfClass(NSClassFromString("_UIButtonBarButton")!)]))
    private let firstInput = EarlGrey.selectElement(with: grey_accessibilityID("questionAndAnswer0Acc"))
    private let secondInput = EarlGrey.selectElement(with: grey_accessibilityID("questionAndAnswer1Acc"))
    private let continueButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityID("continueButton"), grey_kindOfClass(NSClassFromString("UIButton")!)]))

    // MARK: - Actions

    func goBack() { backButton.perform(grey_tap()) }
    func inputTestDetails() {
        firstInput.perform(grey_typeText("Hello"))
        secondInput.perform(grey_typeText("World"))
    }
    func tapContinue() { continueButton.perform(grey_tap()) }
}
