//
//  PaymentPreference.swift
//  SimpleNetwork
//
//  Created by Marcello Mascia on 30/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation

public struct PaymentPreference {
    public var card: PaymentCard?
    var prepaymentRequired: Bool
    public var electronicInvoiceRequired: Bool

    public init(dict: PIDictionary?) {
        self.card = try? PaymentCard(dictionary: dict?["paymentCard"] as? PIDictionary)
        self.prepaymentRequired = dict?["prepaymentRequired"] as? Bool ?? false
        self.electronicInvoiceRequired = dict?["electronicInvoiceRequired"] as? Bool ?? false
    }
}
