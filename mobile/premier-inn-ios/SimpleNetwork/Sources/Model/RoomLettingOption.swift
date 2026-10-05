//
//  RoomLettingOption.swift
//  SimpleNetwork
//
//  Created by Freddie Parks on 27/03/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation

public enum RoomLettingOptionType: String {
    case accessible = "ACCESSIBLE"
    case roomUpsell = "ROOM_UPSELL"
    case unknown
}

public enum SpecialRequest: String {
    case loweredBath = "LOWB"
    case wetRoom = "WETR"

    case twinTwoSingleBeds = "TW2S"
    case twinDoubleBedAndSofa = "TWDS"

    case unknown
}

public let bathroomSpecialRequests: [SpecialRequest] = [.loweredBath, .wetRoom]
public let twinRoomSpecialRequests: [SpecialRequest] = [.twinTwoSingleBeds, .twinDoubleBedAndSofa]

public enum OperaRoomTypeCode: String {
    case wetDouble = "WETDBL"
    case loweredDouble = "LOWDBL"
    case wetTwin = "WETTWN"
    case loweredTwin = "LOWTWN"

    case unknown
}

public struct RoomLettingOption: Codable {
    public var lettingType: String?
    public var silentSubstitution: Bool?
    public var nonWindow: Bool?
    public var totalCost: Cost?
    public var cityTax: Cost?
    public var dailyRates: [DailyRate]?
    public var availabilityStatus: String?
    public var cotAvailable: Bool?
    public var roomClass: String?
    public var roomClassOrder: Int?
    public var roomClassLabel: String?
    public var specialRequests: [String]?
    public var packageCode: String?
    public var packageAmount: Cost?
    public var baseRateAmount: Cost?
    // For handling bathroom types
    private var alternativeType: String?
    public var numberAvailable: Int?

    public var alternativeLettingType: RoomLettingOptionType {
        guard let alternativeType = alternativeType else { return .unknown }
        return RoomLettingOptionType(rawValue: alternativeType) ?? .unknown
    }
}
