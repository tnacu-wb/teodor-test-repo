//
//  BookingInfoParameters.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 14/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

extension GraphQL {
    static func bookingConfirmationVariables(basketReference: String, bookingChannel: String?) -> PIDictionary {
        var params = PIDictionary()

        params["basketReference"] = basketReference
        params["country"] = LanguageManager.supportedLanguage.countryCode
        params["language"] = LanguageManager.supportedLanguage.rawValue
        if let bookingChannel = bookingChannel {
            params["bookingChannel"] = bookingChannel
        }

        return params
    }

    static func bookingConfirmationVariablesWithManageBooking(
        reservationDetails: ReservationDetails,
        hotelCode: String,
        bookingChannel: String?
    ) -> PIDictionary {
        var bookingConfirmationDict = bookingConfirmationVariables(
            basketReference: reservationDetails.reservationId,
            bookingChannel: bookingChannel
        )

        bookingConfirmationDict["cancelInformationCriteria"] = manageBookingVariables(
            reservationDetails: reservationDetails,
            hotelCode: hotelCode
        )

        return bookingConfirmationDict
    }

    static func getPackagesVariables(
        basketReference: String,
        hotelCode: String,
        bookingDetails: BookingDetails,
        bookingFlowId: String?,
        showMealInclusiveRate: Bool
    ) -> PIDictionary {
        var params = PIDictionary()

        params["hotelId"] = hotelCode
        params["startDate"] = bookingDetails.criteria.arrivalDate.parameterString
        params["endDate"] = bookingDetails.criteria.checkOutDate?.parameterString
        params["adultsNumber"] = bookingDetails.criteria.adultsCount
        params["childrenNumber"] = bookingDetails.criteria.childrenCount
        params["nightsNumber"] = bookingDetails.criteria.nights
        params["language"] = LanguageManager.supportedLanguage.rawValue
        params["country"] = LanguageManager.supportedLanguage.countryCode
        params["bookingFlowId"] = bookingFlowId
        params["basketReferenceId"] = basketReference
        params["channel"] = bookingDetails.bookingChannel.rawValue
        params["showMealInclusiveRate"] = showMealInclusiveRate

        return params
    }

    static func getBookingFlowCriteria(hotelCode: String, rateCode: String) -> PIDictionary {
        var params = PIDictionary()

        params["hotelId"] = hotelCode
        params["language"] = LanguageManager.supportedLanguage.rawValue
        params["country"] = LanguageManager.supportedLanguage.countryCode
        params["rateCode"] = rateCode

        return params
    }

    static func manageBookingVariables(reservationDetails: ReservationDetails, hotelCode: String) -> PIDictionary {
        var params = PIDictionary()

        let date = Date()
        // Need this unique date time for the query
        let iso8601DateFormatter = ISO8601DateFormatter()
        iso8601DateFormatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        let iso8601DateString = iso8601DateFormatter.string(from: date)

        params["userDateTime"] = iso8601DateString
        params["hotelId"] = hotelCode
        params["basketReference"] = reservationDetails.reservationId
        params["token"] = reservationDetails.token?.addingPercentEncoding(withAllowedCharacters: .alphanumerics)
        params["bookingChannel"] = getBookingChannel(business: reservationDetails.business)

        return params
    }

    static func cancelReservationVariables(reservationDetails: ReservationDetails, hotelCode: String) -> PIDictionary {
        var params = PIDictionary()

        params["cancellationCriteria"] = cancelReservationCriteria(
            reservationDetails: reservationDetails,
            hotelCode: hotelCode
        )

        return params
    }

    private static func cancelReservationCriteria(
        reservationDetails: ReservationDetails,
        hotelCode: String
    ) -> PIDictionary {
        var params = PIDictionary()

        params["basketReference"] = reservationDetails.reservationId
        params["hotelId"] = hotelCode
        params["token"] = reservationDetails.token

        return params
    }

    static func findBookingSourceVariables(findBookingDetails: FindBookingDetails) -> PIDictionary {
        var params = PIDictionary()

        params["resNo"] = findBookingDetails.reservationId
        params["lastName"] = findBookingDetails.surname
        params["arrivalDate"] = findBookingDetails.arrivalDate.parameterString
        params["country"] = LanguageManager.supportedLanguage.countryCode
        params["language"] = LanguageManager.supportedLanguage.rawValue
        params["bookingChannel"] = getBookingChannel(business: findBookingDetails.business)

        return ["findBookingCriteria": params]
    }

    public static func getStaysVariables(business: Bool) -> PIDictionary {
        var params = PIDictionary()

        params["business"] = business
        params["sortOrder"] = "DEFAULT"
        params["bookingChannel"] = getBookingChannel(business: business)
        params["includeCheckInBookings"] = true
        params["pageSize"] = Constants.maxStaysNumber

        return ["bookingHistoryRequest": params]
    }

    static func bookingInformationVariables(basketReference: String, isBusiness: Bool) -> PIDictionary {
        var params = PIDictionary()

        params["basketReference"] = basketReference
        params["country"] = LanguageManager.supportedLanguage.countryCode
        params["language"] = LanguageManager.supportedLanguage.rawValue
        // TODO: work out what this does and if we need for employee rate
        params["upgradeToEmployeeRate"] = false
        params["bookingChannelCriteria"] = getBookingChannel(business: isBusiness)

        return params
    }

    static func updateReservationPreferencesVariables(
        hotelId: String,
        reservationsIds: [String],
        preferences: [PreferencesCollection]
    ) -> PIDictionary {
        var params = [String: Any]()
        params["hotelId"] = hotelId
        params["reservationsIds"] = reservationsIds
        params["preferencesCollections"] = preferences.map {
            var dictionary = [String: Any]()
            dictionary["preferenceType"] = $0.preferenceType
            dictionary["preferences"] = $0.preferences.map { $0 }
            return dictionary
        }
        return params
    }
}
