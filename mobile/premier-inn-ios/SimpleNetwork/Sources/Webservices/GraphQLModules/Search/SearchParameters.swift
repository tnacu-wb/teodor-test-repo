//
//  SearchParameters.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 14/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

extension GraphQL {
    static func getAvalabilitiesVariables(
        bookingDetails: BookingDetails,
        suggestion: Suggestion,
        page: Int?,
        size: Int,
        sorting: AvailabilitiesSorting = .distance,
        allowEmployeeOffer: Bool
    ) -> PIDictionary {
        var params = PIDictionary()

        var criteriaDict: PIDictionary = [
            "place": getPlace(suggestion: suggestion),
            "startDate": bookingDetails.criteria.arrivalDate.parameterString,
            "endDate": bookingDetails.criteria.checkOutDate?.parameterString ?? "",
            "rooms": getAvailabilitiesRooms(bookingDetails: bookingDetails),
            "channel": UserSessionManager.sharedInstance.currentUser?.isBusiness == true ? Channel.BB.rawValue : Channel.PI
            .rawValue,
            "oldWorldChannel": Constants.bookingChannel,
            "subChannel": Constants.bookingChannel,
            "country": LanguageManager.supportedLanguage.countryCode,
            "language": LanguageManager.supportedLanguage.rawValue,
            "sort": sorting.rawValue,
            "page": page ?? 1,
            "initialPageSize": size,
            "lazyLoadPageSize": size
        ]
        if let companyId = UserSessionManager.sharedInstance.currentUser?.companyId {
            criteriaDict["companyId"] = companyId
        }

        if allowEmployeeOffer && bookingDetails.employeeRatesEnabled && bookingDetails.bookingMode != .business {
            criteriaDict["ratePlanCodes"] = [Constants.EmployeeOffer.rateCode]
        }

        params["availabilitiesSearchCriteria"] = criteriaDict

        return params
    }

    private static func getPlace(suggestion: Suggestion) -> PIDictionary {
        var params = PIDictionary()

        params["location"] = suggestion.location
        params["locationFormat"] = suggestion.locationFormat
        params["radius"] = Constants.searchRadius
        params["radiusUnit"] = Constants.searchRadiusUnit

        return params
    }

    private static func getAvailabilitiesRooms(bookingDetails: BookingDetails) -> [PIDictionary] {
        var rooms = [PIDictionary]()

        for room in bookingDetails.criteria.rooms {
            var roomDict = PIDictionary()

            roomDict["type"] = room.type.code
            roomDict["adultsNumber"] = room.adults
            roomDict["childrenNumber"] = room.children

            rooms.append(roomDict)
        }
        return rooms
    }

    static func getAvailabilityAndPackagesVariables(
        bookingDetails: BookingDetails,
        hotelCode: String,
        hotelBrand: HotelBrand?,
        allowEmployeeOffer: Bool
    ) throws -> PIDictionary {
        var params = PIDictionary()

        params["availabilitySearchCriteria"] = getAvailabilityVariables(
            bookingDetails: bookingDetails,
            hotelCode: hotelCode,
            allowEmployeeOffer: allowEmployeeOffer
        )
        params["language"] = LanguageManager.supportedLanguage.rawValue
        params["country"] = LanguageManager.supportedLanguage.countryCode
        params["hotelId"] = hotelCode
        params["brand"] = hotelBrand?.rawValue.lowercased() ?? HotelBrand.premierInn.rawValue.lowercased()

        let channel = BookingDetails.sharedInstance.bookingMode == .business ? Channel.BB.rawValue : Channel.PI.rawValue
        params["channel"] = channel
        params["channelEnum"] = channel

        return params
    }

    static func getAvailabilityVariables(
        bookingDetails: BookingDetails,
        hotelCode: String,
        allowEmployeeOffer: Bool
    ) -> PIDictionary {
        var params = PIDictionary()

        params = [
            "arrival": bookingDetails.criteria.arrivalDate.parameterString,
            "departure": bookingDetails.criteria.checkOutDate?.parameterString ?? "",
            "rooms": getRoomDicitionaries(bookingDetails: bookingDetails),
            "bookingChannel": getBookingChannel(bookingDetails: bookingDetails),
            "hotel": [
                "identifier": hotelCode
            ]
        ]

        // need the operaCompanyId from the new JWT token
        if let operaCompanyId = UserSessionManager.sharedInstance.operaCompanyId {
            params["companyId"] = operaCompanyId
        }

        if allowEmployeeOffer && bookingDetails.employeeRatesEnabled {
            params["ratePlanCodes"] = [Constants.EmployeeOffer.rateCode]
        }

        if let promoCode = bookingDetails.appIncentivePromoCode ??
           bookingDetails.freeBreakfastPromoCode ??
           bookingDetails.siteWidePromoCode ??
           bookingDetails.userEnteredPromoCode {
            params["promotionCode"] = promoCode

            if let promoKind = bookingDetails.promoKind {
                params["promoKind"] = promoKind
            }
        }

        return params
    }

    private static func getRoomDicitionaries(bookingDetails: BookingDetails) -> [PIDictionary] {
        var rooms = [PIDictionary]()

        for room in bookingDetails.criteria.rooms {
            var roomDict = PIDictionary()

            roomDict["roomType"] = room.type.code
            roomDict["adultsNumber"] = room.adults
            roomDict["childrenNumber"] = room.children
            roomDict["cotRequired"] = room.cotRequired

            rooms.append(roomDict)
        }
        return rooms
    }
}
