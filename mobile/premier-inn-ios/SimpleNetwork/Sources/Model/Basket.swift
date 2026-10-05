//
//  Basket.swift
//  SimpleNetwork
//
//  Created by Georgios Aikaterinakis on 24/02/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import Foundation

public enum BasketStatus: String, Decodable {
    case complete = "COMPLETED"
    case pending = "PROCESSING"
    case payPending = "PAY_PENDING"
    case failed = "FAILED"
    case amended = "AMENDED"
    case amending = "AMENDING"
    case open = "OPEN"
    case amendFailed = "AMEND_FAILED"
    case preCheckedIn = "PRE_CHECKED_IN"
    case preCheckedOut = "PRE_CHECKED_OUT"
    case ciolFailed = "CIOL_FAILED"
}

public struct Basket: Decodable {
    public let basketStatus: BasketStatus?
    public let basketError: BasketStatusError?
}

public struct BasketStatusError: Decodable {
    public let code: String?
    public let description: String?
    // Unsure what type is can not find it in docs
//  public let type: String?
}
