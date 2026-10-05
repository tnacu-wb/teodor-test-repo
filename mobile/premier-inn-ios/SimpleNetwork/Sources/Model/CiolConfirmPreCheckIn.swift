//
//  CiolConfirmPreCheckIn.swift
//  SimpleNetwork
//
//  Created by Muresan, Andreea (Cognizant) on 16.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation

public struct ConfirmPreCheckInOut: Decodable {
    public let basketReference: String
    public let basketStatus: BasketStatus
    public let basketError: BasketStatusError?
}
