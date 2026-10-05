//
//  PaymentMethodsCardSection.swift
//  PremierInn
//
//  Created by Clint Mengolli on 18/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

struct PaymentMethodsCardSection {
    let cardName: String?
    let cardType: String?
    let cardHiddenNumber: String?
    let cardHolderName: String?
    let cardExpiration: String?
    let links: [PaymentMethodsLink]
    let usageDescription: String?
    let cardImageURL: URL?
    let selectable: Bool
    let selected: Bool
    let cardInfoMessage: String?
    let isBookingFlow: Bool
    let pibaMessaging: String?
    let isDeleteHidden: Bool
    let accessibilityLabel: String?
}
