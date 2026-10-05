//
//  Mapper+HotelInformation.swift
//  SimpleNetwork
//
//  Created by Santa Gurung on 07/06/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

public enum HotelInformationMapper {
    public static func map(from input: PIDictionary, disclaimer: PIDictionary?) -> PIDictionary {
        var dict = PIDictionary()

        dict["hotelCode"] = input["hotelId"]
        dict["brand"] = input["brand"]
        dict["name"] = input["name"]
        dict["county"] = input["county"]
        dict["headline"] = input["headline"]
        dict["guestDetails"] = input["guestDetails"]
        dict["parkingDescription"] = input["parkingDescription"]
        dict["hotelDirections"] = input["directions"]
        dict["hotelDescription"] = input["hotelDescription"]
        dict["ancillaryCloseout"] = input["ancillaryCloseout"]
        dict["disclaimer"] = disclaimer

        dict["map"] = input["coordinates"]
        if let address = input["address"] as? PIDictionary {
            dict["address"] = AddressMapper.map(from: address)
        }
        if let topSectionImages = input["topSectionImages"] as? [Any] {
            dict["images"] = topSectionImages.map { TopSectionImageMapper.map(from: $0 as? PIDictionary ?? [:]) }
        }
        if let messagingFlag = input["messagingFlag"] {
            dict["messagingFlag"] = MessagingFlagMapper.map(from: messagingFlag as? PIDictionary)
        }
        if let hotelFacilities = input["hotelFacilities"] as? [Any] {
            dict["facilities"] = hotelFacilities.map { FacilitiesMapper.map(from: $0 as? PIDictionary ?? [:]) }
        }
        if let roomConfiguration = input["roomConfiguration"] as? PIDictionary {
            dict["hotelRoomConfiguration"] = RoomTypesMapper.map(from: roomConfiguration)
        }
        if let restaurant = input["restaurant"] as? PIDictionary {
            dict["restaurant"] = RestaurantMapper.map(from: restaurant)
        }
        if let contactDetails = input["contactDetails"] as? PIDictionary {
            dict["contactDetails"] = ContactDetailsMapper.map(from: contactDetails)
        }
        if let announcement = input["announcement"] as? PIDictionary {
            dict["announcement"] = AnnouncementMapper.map(from: announcement)
        }
        if let infoItems = input["importantInfo"] as? PIDictionary {
            dict["notes"] = ImportantInfoMapper.map(from: infoItems)
        }

        dict["acceptedCreditCards"] = input["paymentCodeTypes"]
        dict["tripAdvisorDetails"] = input["tripAdvisorReviews"]

        return dict
    }
}

private struct ContactDetailsMapper: Mapper {
    public static func map(from input: PIDictionary) -> PIDictionary {
        var dict = PIDictionary()

        dict["phone"] = input["phone"]
        dict["hotelNationalPhone"] = input["hotelNationalPhone"]
        dict["email"] = input["email"]

        return dict
    }
}

private struct TopSectionImageMapper: Mapper {
    public static func map(from input: PIDictionary) -> PIDictionary {
        var dict = PIDictionary()

        dict["fileReference"] = input["imageSrc"]
        dict["tags"] = input["tags"]

        return dict
    }
}

struct FacilitiesMapper: Mapper {
    public static func map(from input: PIDictionary) -> PIDictionary {
        var dict = PIDictionary()

        dict["code"] = input["code"]
        dict["legend"] = input["name"]
        dict["description"] = input["description"]

        return dict
    }
}

private struct RestaurantMapper: Mapper {
    public static func map(from input: PIDictionary) -> PIDictionary {
        var dict = PIDictionary()

        dict["name"] = input["name"]
        dict["description"] = input["description"]
        dict["image"] = input["logoSrc"]
        if let menus = input["menus"] as? [Any] {
            dict["menus"] = menus.map { RestaurantMenuItemMapper.map(from: $0 as? PIDictionary ?? [:]) }
        }

        return dict
    }
}

struct RestaurantMenuItemMapper: Mapper {
    public static func map(from input: PIDictionary) -> PIDictionary {
        var dict = PIDictionary()

        dict["name"] = input["name"]
        dict["description"] = input["description"]
        dict["disclaimer"] = input["disclaimer"]
        dict["path"] = input["menuSrc"]

        return dict
    }
}

private struct AddressMapper: Mapper {
    public static func map(from input: PIDictionary) -> PIDictionary {
        var dict = PIDictionary()

        dict["addressLine1"] = input["addressLine1"]
        dict["addressLine2"] = input["addressLine2"]
        dict["addressLine3"] = input["addressLine3"]
        dict["country"] = input["country"]
        dict["postCode"] = input["postalCode"]

        return dict
    }
}

private struct AnnouncementMapper: Mapper {
    public static func map(from input: PIDictionary) -> PIDictionary {
        var dict = input
        dict["showAnnouncement"] = input["showAnnouncement"] as? String == "true" ? true : false

        return dict
    }
}

private enum ImportantInfoMapper {
    static func map(from input: PIDictionary) -> [PIDictionary]? {
        let dict = input["infoItems"] as? [PIDictionary]
        return dict
    }
}
