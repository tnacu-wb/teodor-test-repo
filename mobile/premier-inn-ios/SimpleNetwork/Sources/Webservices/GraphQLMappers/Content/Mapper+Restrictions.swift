//
//  Mapper+Restrictions.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 05/05/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import Foundation

public enum RestrictionsMapper {
    public static func map(input: PIDictionary) -> [PIDictionary] {
        var dicts = [PIDictionary]()

        for channel in Channel.channelsWithRestrictions {
            var dict = PIDictionary()
            if let roomsLimitationDict = input["roomsLimitation\(channel.rawValue)"] as? PIDictionary,
               let maxRoomsLimDict = roomsLimitationDict["maxRoomsLim"] as? PIDictionary {
                dict["maxRooms"] = channel == .BB ? 1 : maxRoomsLimDict["maxRooms"]
                dict["maxRoomsAmend"] = channel == .BB ? 1 : maxRoomsLimDict["maxRoomsAmend"]
            }
            if let maxArrivalDateDict = input["arrivalDateLimitation\(channel.rawValue)"] as? PIDictionary {
                dict["maxArrivalDate"] = maxArrivalDateDict["maxArrivalDate"]
            }
            if let maxNightsDict = input["nightsLimitation\(channel.rawValue)"] as? PIDictionary {
                dict["maxNights"] = maxNightsDict["maxNights"]
            }
            dict["channel"] = channel.rawValue
            dicts.append(dict)
        }
        return dicts
    }
}
