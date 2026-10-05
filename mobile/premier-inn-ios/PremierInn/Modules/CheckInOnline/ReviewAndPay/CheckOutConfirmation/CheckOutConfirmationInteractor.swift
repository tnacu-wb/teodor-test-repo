//
//  CiolConfirmationInteractor.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei (Cognizant) on 21.03.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation

struct CheckOutDetails {
    let bookerFirstName: String

    var confirmationMessage: String {
        String(format: PILocalizedString("ciolCheckOutTitleMessage"), bookerFirstName)
    }
}

class CheckOutConfirmationInteractor: CheckOutConfirmationInteractorProtocol {
    var checkOutDetails: CheckOutDetails

    init(checkOutDetails: CheckOutDetails) {
        self.checkOutDetails = checkOutDetails
    }
}
