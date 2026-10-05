//
//  Mapper+RoomType.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 06/06/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

public protocol ArrayMapper {
    static func arrayMap(from input: [PIDictionary]) -> [String]
}

public struct RoomTypesMapper: Mapper {
    public static func map(from input: PIDictionary) -> PIDictionary {
        var dict = PIDictionary()

        if let tabItems = input["tabItems"] as? [Any] {
            dict["tabItems"] = tabItems.map { TabItemsMapper.map(from: $0 as? PIDictionary ?? [:]) }
        }
        if let tabGroups = input["tabGroups"] as? [Any] {
            dict["tabGroups"] = tabGroups.map { TabGroupsMapper.map(from: $0 as? PIDictionary ?? [:]) }
        }

        return dict
    }
}

private struct TabItemsMapper: Mapper {
    public static func map(from input: PIDictionary) -> PIDictionary {
        var dict = PIDictionary()

        dict["room"] = input["roomType"]
        dict["roomType"] = input["roomType"]
        dict["roomTitle"] = input["roomName"]
        dict["roomDescriptionText"] = input["roomDescription"]

        if let facilities = input["facilities"] as? [Any] {
            dict["newFacilities"] = facilities.map { RoomTypeFacilitiesMapper.map(from: $0 as? PIDictionary ?? [:]) }
        }

        if let images = input["images"] as? [PIDictionary] {
            dict["renditions"] = images.map { ($0["imageSrc"] ?? "") }
        }

        return dict
    }
}

private struct TabGroupsMapper: Mapper {
    public static func map(from input: PIDictionary) -> PIDictionary {
        var dict = PIDictionary()

        dict["groupId"] = input["groupId"]
        dict["groupTitle"] = input["groupName"]

        return dict
    }
}

private struct RoomTypeFacilitiesMapper: Mapper {
    public static func map(from input: PIDictionary) -> PIDictionary {
        var dict = PIDictionary()
        // As the title in the request is null using this we can still show the description we have from the request and just show an empty title.
        dict["facilitiesTitle"] = input["name"] as? String ?? ""
        dict["navOptions"] = [RoomTypeNavOptionMapper.map(from: input)]

        return dict
    }
}

private struct RoomTypeNavOptionMapper: Mapper {
    public static func map(from input: PIDictionary) -> PIDictionary {
        var dict = PIDictionary()

        dict["facility"] = input["description"]

        return dict
    }
}
