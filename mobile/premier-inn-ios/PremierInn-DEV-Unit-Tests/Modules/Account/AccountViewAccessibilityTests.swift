//
//  AccountViewAccessibilityTests.swift
//  PremierInnTests
//
//  Created by Santa Gurung on 20/03/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import XCTest
import Formeka
@testable import PremierInn

final class AccountViewAccessibilityTests: XCTestCase {

    func test_LogOutCell_Views_Are_AccessibilityElements() {

        let userComponent: AccountLogoutCell = AccountLogoutCell.fromNib()!
        let userLabel = userComponent.username!
        let emailLabel = userComponent.email!
        XCTAssertTrue(userLabel.isAccessibilityElement)
        XCTAssertTrue(emailLabel.isAccessibilityElement)
    }

    func test_Row_Views_Are_AccessibilityElements() {

        let simpleActionCell: SimpleActionCell = SimpleActionCell.fromNib()!
        let personalDetails = simpleActionCell.textLabel!
        XCTAssertTrue(personalDetails.isAccessibilityElement)
    }
}
