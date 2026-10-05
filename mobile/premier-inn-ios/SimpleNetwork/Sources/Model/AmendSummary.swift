//
//  AmendSummary.swift
//  SimpleNetwork
//
//  Created by Santa Gurung on 12/10/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import Foundation

public struct AmendSummary: Codable {
    public let totalCost: Float?
    public let balanceAuthorised: Float?
    public let paymentOptions: PaymentInterval?
    public let paymentCardDetails: PaymentCardDetails?
}

public struct PaymentInterval: Codable {
    public let payNow: Bool?
    public let payOnArrival: Bool?
}

public struct PaymentCardDetails: Codable {
    public let cardNumberMasked: String?
    public let expirationDate: String?
    public let cardHolderName: String?
    public let cardNumberLast4Digits: String?
    public let cardName: String?
    public let cardLogoSrc: String?

    public init(
        cardNumberMasked: String?,
        expirationDate: String?,
        cardHolderName: String?,
        cardNumberLast4Digits: String?,
        cardName: String?,
        cardLogoSrc: String?
    ) {
        self.cardNumberMasked = cardNumberMasked
        self.expirationDate = expirationDate
        self.cardHolderName = cardHolderName
        self.cardNumberLast4Digits = cardNumberLast4Digits
        self.cardName = cardName
        self.cardLogoSrc = cardLogoSrc
    }
}
