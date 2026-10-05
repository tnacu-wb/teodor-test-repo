//
//  PaymentDetails.swift
//  SimpleNetwork
//
//  Created by Freddie Parks on 01/04/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Foundation

public struct PaymentDetails {
    public let card: PaymentCard?
    public let address: Address?
    public let useStoredCard: Bool
    public let confirmationEmailAddress: String

    public var cvv: String // incase we need to clear from memoire

    public init(
        card: PaymentCard? = nil,
        address: Address? = nil,
        useStoredCard: Bool,
        confirmationEmailAddress: String,
        cvv: String
    ) {
        self.card = card
        self.address = address
        self.useStoredCard = useStoredCard
        self.confirmationEmailAddress = confirmationEmailAddress
        self.cvv = cvv
    }
}
