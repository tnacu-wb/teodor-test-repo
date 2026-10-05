//
//  AmendConfirmationPrices.swift
//  SimpleNetworkTests
//
//  Created by Santa Gurung on 01/11/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import Foundation

public struct AmendConfirmationPrices: Codable {
    public let previousTotal: Float
    public let newTotalCost: Float
    public let outstandingBalance: Float
}
