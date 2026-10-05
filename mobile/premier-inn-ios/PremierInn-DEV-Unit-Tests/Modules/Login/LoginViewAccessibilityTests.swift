//
//  LoginViewAccessibilityTests.swift
//  PremierInnTests
//
//  Created by Santa Gurung on 20/03/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import XCTest
import Formeka
@testable import PremierInn

final class LoginViewAccessibilityTests: XCTestCase {

    func test_Views_Are_AccessibilityElements() {

        // Personal Account and Business Booker switch tab
        let leisureBBSwitcher: LoginTypeCell = LoginTypeCell.fromNib()!
        let personalAccount = leisureBBSwitcher.personalAccountButton!
        let bbAccount = leisureBBSwitcher.businessBookerButton!
        XCTAssertTrue(personalAccount.isAccessibilityElement)
        XCTAssertTrue(bbAccount.isAccessibilityElement)

        // Login and Forgot password button
        let twoButtonsCell: TwoButtonsCell = TwoButtonsCell.fromNib()!
        let loginButton = twoButtonsCell.topButton!
        let forgotPasswordButton = twoButtonsCell.bottomButton!
        XCTAssertTrue(loginButton.isAccessibilityElement)
        XCTAssertTrue(forgotPasswordButton.isAccessibilityElement)

        // Business booker button
        let businessBookerCell: AboutBusinessBookerButtonCell = AboutBusinessBookerButtonCell.fromNib()!
        let bbButton = businessBookerCell.button!
        XCTAssertTrue(bbButton.isAccessibilityElement)

        // Login with face ID button
        let biometricCell: BiometricButtonCell = BiometricButtonCell.fromNib()!
        let loginWithFaceIDButton = biometricCell.button!
        XCTAssertTrue(loginWithFaceIDButton.isAccessibilityElement)

    }
}
