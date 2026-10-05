//
//  Mapper+Availabilities.swift
//  SimpleNetwork
//
//  Created by Santa Gurung on 06/02/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import Foundation

public enum AvailabilitiesMapper {
    static func map(from input: [PIDictionary]) -> [PIDictionary] {
        var array = [PIDictionary]()

        for hotel in input {
            var hotelDict = PIDictionary()
            let hotelAvailability = hotel["hotelAvailability"] as? PIDictionary
            let hotelInformation = hotel["hotelInformation"] as? PIDictionary

            hotelDict["hotelCode"] = hotel["hotelId"]
            hotelDict["hotelBrand"] = hotelInformation?["brand"]
            hotelDict["available"] = hotelAvailability?["available"]
            hotelDict["limitedAvailability"] = hotelAvailability?["limitedAvailability"]
            hotelDict["distance"] = hotelAvailability?["distance"]
            hotelDict["lowestRoomRate"] = LowestRoomRateCreator.createCost(from: hotelAvailability)
            hotelDict["ratePlans"] = [
                ["classification": hotelAvailability?["cellCode"]]
            ]
            hotelDict["pmsSource"] = BookingSourcePMS(rawValue: (hotelAvailability?["pmsSource"] as? String ?? "")
                .capitalized)

            var hotelInfo = PIDictionary()
            hotelInfo["code"] = hotel["hotelId"]
            hotelInfo["brand"] = hotelInformation?["brand"]
            hotelInfo["name"] = hotel["name"]
            hotelInfo["map"] = CoordinatesMapper.map(from: hotelInformation?["coordinates"] as? PIDictionary)
            hotelInfo["images"] = ThumbnailImagesMapper.map(from: hotelInformation?["thumbnailImages"] as? [PIDictionary])
            hotelInfo["messagingFlag"] = MessagingFlagMapper.map(from: hotelInformation?["messagingFlag"] as? PIDictionary)
            if let hotelFacilities = hotelInformation?["hotelFacilities"] as? [Any] {
                hotelInfo["facilities"] = hotelFacilities.map { FacilitiesMapper.map(from: $0 as? PIDictionary ?? [:]) }
            }

            hotelDict["hotelInfo"] = hotelInfo

            array.append(hotelDict)
        }

        return array
    }
}

public enum LowestRoomRateCreator {
    static func createCost(from input: PIDictionary?) -> Cost? {
        guard let lowestRoomRateDict = input?["lowestRoomRate"] as? PIDictionary,
              let amount = lowestRoomRateDict["netTotal"] as? Double,
              let currencyCode = lowestRoomRateDict["currencyCode"] as? String else {
            return nil
        }
        let cost = Cost(amount: amount, currencyCode: currencyCode)
        return cost
    }
}

public enum CoordinatesMapper {
    static func map(from input: PIDictionary?) -> PIDictionary {
        var dict = PIDictionary()

        dict["latitude"] = input?["latitude"] as? Double
        dict["longitude"] = input?["longitude"] as? Double

        return dict
    }
}

public enum ThumbnailImagesMapper {
    static func map(from input: [PIDictionary]?) -> [PIDictionary] {
        var array = [PIDictionary]()

        guard let imagesArray = input else { return array }

        for image in imagesArray {
            var newImage = PIDictionary()
            newImage["fileReference"] = image["imageSrc"]
            newImage["tags"] = image["tags"].map { $0 }

            array.append(newImage)
        }
        return array
    }
}

public enum MessagingFlagMapper {
    static func map(from input: PIDictionary?) -> PIDictionary {
        var dict = PIDictionary()

        dict["flagText"] = input?["text"]
        dict["flagColor"] = input?["color"]

        return dict
    }
}
