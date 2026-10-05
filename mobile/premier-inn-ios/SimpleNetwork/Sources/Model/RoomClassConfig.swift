//
//  RoomClassConfig.swift
//  SimpleNetwork
//
//  Created by Santa Gurung on 02/01/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

public struct RoomClassConfig: Decodable {
    public let roomClassConfig: [RoomClassOrder]
}

public struct RoomClassOrder: Decodable {
    public let code: String
    public let order: Int
}
