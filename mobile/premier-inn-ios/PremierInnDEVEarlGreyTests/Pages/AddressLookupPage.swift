//
//  AddressLookupPage.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 13/05/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import EarlGrey

class AddressLookupPage {

    private let cancelButton = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Cancel"), grey_interactable()]))

    func goBack() { cancelButton.perform(grey_tap()) }

    func selectAddress(_ label: String) {

        EarlGrey.selectElement(with: grey_allOf([grey_text(label), grey_interactable()])).perform(grey_tap())
    }
}
