//
//  RoomCriteriaPage.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Georgios Aikaterinakis on 15/07/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import EarlGrey

struct RoomCriteriaPage {

    // MARK: - Elements

    static let doneButton = EarlGrey.selectElement(with: grey_accessibilityID("doneButton"))
    static let doubleRoom = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Double room")]))
    static let accessibleRoom = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Accessible room")]))

    // MARK: - Actions

    static func tapDone() { doneButton.perform(grey_tap()) }

    // TODO: thing can be extended to allow all options, but we will need to change the IDs as it won't work for multiple rooms in the criteria
    static func changeDoubleRoomToAccessible() {

        doubleRoom.perform(grey_tap())
        accessibleRoom.perform(grey_tap())
    }

    // MARK: Validations
}
