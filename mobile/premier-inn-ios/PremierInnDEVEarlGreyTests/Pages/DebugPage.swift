//
//  DebugPage.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 16/04/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import XCTest
import EarlGrey

enum BartEnvironment {
    case alpha1
    case alpha2
}

class DebugPage {

    private let alpha1Option = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("UAT Alpha1"), grey_sufficientlyVisible()]))
    private let alpha2Option = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("UAT Alpha2"), grey_sufficientlyVisible()]))

    func select(environment: BartEnvironment) {
        switch environment {

        case .alpha1:
            alpha1Option.perform(grey_tap())

        case .alpha2:
            alpha2Option.perform(grey_tap())
        }
    }
}
