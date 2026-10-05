//
//  Mapper+Stay.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 07/07/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import Foundation

public enum StayMapper {
    public static func map(input: PIDictionary, isBusiness: Bool) -> PIDictionary {
        var dict = PIDictionary()

        dict["hotelCode"] = input["hotelCode"]
        dict["hotelName"] = input["hotelName"]
        dict["hotelCountry"] = input["hotelCountry"]
        dict["identifier"] = input["bookingReference"]
        dict["arrivalDate"] = input["arrivalDate"]
        dict["checkOutDate"] = input["departureDate"]
        dict["leadGuestSurname"] = input["leadGuestSurname"]
        dict["cancelled"] = input["cancelled"]
        dict["importType"] = ReservationImportType.account.rawValue
        dict["checkInOnline"] = input["checkInOnline"]
        dict["guestHistoryNumber"] = input["historyRecordNumber"]
        dict["amendable"] = input["amendable"]
        dict["cancelable"] = input["cancelable"]
        dict["leadGuest"] = input["leadGuest"]
        dict["business"] = input["business"]
        dict["totalCost"] = input["totalCost"]
        dict["cityTax"] = input["cityTax"]
        dict["rateClass"] = input["rateClass"]
        dict["prePaidAmount"] = input["prePaidAmount"]
        dict["noOfRooms"] = input["noOfRooms"]
        dict["checkedIn"] = input["checkedIn"] as? String == "true" ? true : false
        dict["checkInOnlineAvailable"] = input["isCheckInOnlineAvailable"]
        dict["isCheckOutOnlineAvailable"] = input["isCheckOutOnlineAvailable"]
        dict["basketStatus"] = input["basketStatus"]
		dict["isDigitalKey"] = input["isDigitalKeyEligible"]
        dict["isDirect"] = input["isDirect"]
        dict["bookingStatus"] = input["bookingStatus"]

        // This is missing from the schema for now
        // dict["hotelLatitude"] = "missingLat"
        // dict["hotelLongitude"] = "missingLong"

        return dict
    }
}
