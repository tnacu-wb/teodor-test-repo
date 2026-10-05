//
//  Mapper+Availability.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 07/06/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

public enum AvailabilityMapper {
    public static func map(
        from input: PIDictionary,
        and ratesInfo: [PIDictionary]?,
        roomClassArray: [PIDictionary]?
    ) -> PIDictionary {
        var dict = PIDictionary()

        dict["hotelCode"] = input["hotelId"]
        dict["available"] = input["available"]
        dict["limitedAvailability"] = input["limitedAvailability"]
        dict["ratePlans"] = RatePlansMapper.map(from: input, and: ratesInfo, roomClassArray: roomClassArray)

        return dict
    }
}

public enum RatePlansMapper {
    public static func map(
        from input: PIDictionary,
        and ratesInfo: [PIDictionary]?,
        roomClassArray: [PIDictionary]?
    ) -> [PIDictionary] {
        var ratesDictionaries = [PIDictionary]()

        let rateClassifications = ratesInfo ?? []

        let rates = input["roomRates"] as? [PIDictionary] ?? [[:]]
        for aRate in rates {
            var aRateDictionary = PIDictionary()

            let roomTypes = aRate["roomTypes"] as? [PIDictionary]
            aRateDictionary["rooms"] = roomTypes?.compactMap { RoomTypeMapper.map(from: $0, roomClassArray: roomClassArray) }

            aRateDictionary["classification"] = aRate["ratePlanCode"]
            aRateDictionary["cellCode"] = "BFLEX"
            aRateDictionary["promotionCode"] = aRate["promotionCode"]

            if let cellCode = aRate["cellCode"] as? String {
                aRateDictionary["classification"] = cellCode
                aRateDictionary["cellCode"] = cellCode

                if cellCode == Constants.EmployeeOffer.rateCode {
                    aRateDictionary["classification"] = Constants.EmployeeOffer.rateClassification
                }
            }

            for rateClass in rateClassifications
                where rateClass["rateClassification"] as? String == aRateDictionary["classification"] as? String {
                // Hardcoding for BB - no longer doing anything
                aRateDictionary["cellCode"] = nil

                // these need to change once we know the brand
                aRateDictionary["name"] = rateClass["rateName"]
                aRateDictionary["description"] = rateClass["rateDescription"]
                aRateDictionary["order"] = rateClass["rateOrder"]
            }

            ratesDictionaries.append(aRateDictionary)
        }

        ratesDictionaries = ratesDictionaries.sorted { ($0["order"] as? String ?? "") < ($1["order"] as? String ?? "") }

        return ratesDictionaries
    }
}

public enum RoomTypeMapper {
    static func map(from input: PIDictionary, roomClassArray: [PIDictionary]?) -> PIDictionary {
        var aRoomDictionary = PIDictionary()

        aRoomDictionary["type"] = input["roomType"]
        aRoomDictionary["adults"] = input["adults"]
        aRoomDictionary["children"] = input["children"]
        aRoomDictionary["cotRequired"] = input["cotRequested"]
        aRoomDictionary["options"] = OptionsMapper.map(from: input, roomClassArray: roomClassArray)

        return aRoomDictionary
    }
}

public enum OptionsMapper {
    static func map(from input: PIDictionary, roomClassArray: [PIDictionary]?) -> [PIDictionary] {
        let roomOptions = input["rooms"] as? [PIDictionary] ?? [[:]]

        let roomOptionsDictionaries: [PIDictionary] = roomOptions.compactMap({ roomOption in
            var aRoomOptionDictionary = PIDictionary()

            let roomPriceBreakdown = roomOption["roomPriceBreakdown"] as? PIDictionary ?? [:]

            aRoomOptionDictionary["totalCost"] = [
                "amount": roomPriceBreakdown["totalNetAmount"],
                "currency": roomPriceBreakdown["currencyCode"]
            ]
            if let amount = roomPriceBreakdown["baseRateAmount"] as? Double {
                aRoomOptionDictionary["baseRateAmount"] = ["amount": amount, "currency": roomPriceBreakdown["currencyCode"]]
            }
            aRoomOptionDictionary["silentSubstitution"] = true // roomOption["silentSubstitution"]
            aRoomOptionDictionary["lettingType"] = roomOption["pmsRoomType"]
            aRoomOptionDictionary["roomClass"] = roomOption["roomClass"]
            aRoomOptionDictionary["specialRequests"] = roomOption["specialRequests"]
            aRoomOptionDictionary["cotAvailable"] = roomOption["cotAvailable"]
            aRoomOptionDictionary["numberAvailable"] = roomOption["numberOfRoomsAvailable"]

            let matchedRoomClass = roomClassArray?
                .first(where: { $0["code"] as? String == roomOption["roomClass"] as? String })
            aRoomOptionDictionary["roomClassOrder"] = matchedRoomClass?["order"]

            // optional twin room package
            aRoomOptionDictionary["packageCode"] = roomPriceBreakdown["packageCode"]
            if let packageAmount = roomPriceBreakdown["packageAmount"] as? Double {
                aRoomOptionDictionary["packageAmount"] = [
                    "amount": packageAmount,
                    "currency": roomPriceBreakdown["currencyCode"]
                ]
            }

            let dailyPrices = roomPriceBreakdown["dailyPrices"] as? [PIDictionary] ?? []
            aRoomOptionDictionary["dailyRates"] = dailyPrices.map { DailyRatesMapper.map(
                from: $0,
                currency: roomPriceBreakdown["currencyCode"] as? String
            ) }

            // city tax missing

            return aRoomOptionDictionary
        })

        return roomOptionsDictionaries
    }
}

public enum DailyRatesMapper {
    static func map(from input: PIDictionary, currency: String?) -> PIDictionary {
        var dailyRateDict = PIDictionary()

        dailyRateDict["date"] = input["date"]
        dailyRateDict["price"] = ["amount": input["netPrice"], "currency": currency ?? CostUnit.pound.rawValue]
        // missing city tax

        return dailyRateDict
    }
}
