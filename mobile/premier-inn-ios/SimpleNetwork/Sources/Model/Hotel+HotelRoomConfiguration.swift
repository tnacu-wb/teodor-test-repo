//
//  Hotel+HotelRoomConfiguration.swift
//  SimpleNetwork
//
//  Created by Freddie Parks on 21/06/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Foundation

public struct NavOption: Codable {
    public let facility: String?
}

public struct NewFacility: Codable {
    public let facilitiesTitle: String?
    public let navOptions: [NavOption]?
}

public struct TabBarGroup: Codable {
    public let groupId: String?
    public let groupTitle: String?
}

public struct TabBarItem: Codable {
    public let renditions: [String]?
    public let roomType: String?
    public let roomDescriptionText: String?
    public let roomDescriptionTextShort: String?
    public let roomInfoLabel: String?
    public let roomInfoText: String?
    public let roomTitle: String?
    public let newFacilities: [NewFacility]?
    public let room: String?
    public let additionalInfo: String?
}

public struct HotelRoomConfiguration: Codable {
    public let title: String?
    public let tabGroups: [TabBarGroup]?
    public let tabItems: [TabBarItem]?
}

public extension Hotel {
    class func hotelRoomConfiguration(with dictionary: PIDictionary?) -> HotelRoomConfiguration? {
        guard let dictionary = dictionary else { return nil }
        guard let data = try? JSONSerialization.data(withJSONObject: dictionary, options: .prettyPrinted) else { return nil }

        do {
            let decoder = JSONDecoder()
            return try decoder.decode(HotelRoomConfiguration.self, from: data)
        } catch {
            print(error)
            return nil
        }
    }
}
