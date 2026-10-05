//
//  Restrictions.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 22/07/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

public struct Restrictions: Decodable {
    public let maxRooms: Int
    public let maxRoomsAmend: Int
    public let maxNights: Int
    public let maxDepartureDateCount: Int
    public let channel: Channel

    enum CodingKeys: String, CodingKey {
        case maxRooms
        case maxRoomsAmend
        case maxNights
        case maxArrivalDate
        case channel
    }

    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        self.maxRooms = try container.decode(Int.self, forKey: .maxRooms)
        self.maxNights = try container.decode(Int.self, forKey: .maxNights)
        self.maxRoomsAmend = try container.decode(Int.self, forKey: .maxRoomsAmend)
        // +1 because we need to work out the max departure date as we receive the max arrival date
        let maxArrivalDateCount = try container.decode(Int.self, forKey: .maxArrivalDate)
        self.maxDepartureDateCount = maxArrivalDateCount + 1
        self.channel = try container.decode(Channel.self, forKey: .channel)
    }

    public init(maxRooms: Int, maxArrivalDateCount: Int, maxNights: Int, maxRoomsAmend: Int, channel: Channel) {
        self.maxRooms = maxRooms
        self.maxNights = maxNights
        // +1 because we need to work out the max departure date as we recieve the max arrival date
        self.maxDepartureDateCount = maxArrivalDateCount + 1
        self.channel = channel
        self.maxRoomsAmend = maxRoomsAmend
    }
}
