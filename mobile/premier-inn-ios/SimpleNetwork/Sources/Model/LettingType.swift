//
//  LettingType.swift
//  SimpleNetwork
//
//  Created by Georgios Aikaterinakis on 26/06/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Foundation

public enum LettingType: String, Codable {
    case single = "SB" // this is an assumption
    case double = "DB"
    case twin = "TBT"
    case family = "TB"
    case accessible = "AB"
    case premierPlus = "RB"
    case businessRooms = "PB"

    public var name: String {
        switch self {
        case .single:
            return "Single"
        case .double:
            return "Double"
        case .twin:
            return "Twin"
        case .family:
            return "Family"
        case .accessible:
            return "Accessible"
        case .premierPlus:
            return "Premier Plus"
        case .businessRooms:
            return "Business"
        }
    }

    public var categoryName: String {
        switch self {
        case .single, .double, .twin, .family:
            return "Standard"
        case .accessible:
            return "Accessible"
        case .premierPlus:
            return "Premier Plus"
        case .businessRooms:
            return "Business"
        }
    }
}

public extension LettingType {
    init(roomType: RoomType) {
        switch roomType {
        case .single:
            self = .single
        case .double:
            self = .double
        case .twin:
            self = .twin
        case .family:
            self = .family
        case .accessible:
            self = .accessible
        }
    }
}
