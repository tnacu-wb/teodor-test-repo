//
//  BookingFlowRequests.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 14/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation
import Alamofire

extension GraphQL {
    func saveUpsellsToBooking(bookingDetails: BookingDetails) throws -> Resource<Bool> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.saveUpsellsToBookingMutation
        parameters[.variablesKey] = GraphQL.saveUpsellsToBookingVariables(bookingDetails: bookingDetails)

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary,
                  let dataDict = data["data"] as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard dataDict["saveReservation"] is String else { return false }

            // could return the basketReference here - although it's probably the same as what we have already
            return true
        }
    }

    func getTotalCostWithCityTax(bookingDetails: BookingDetails) throws -> Resource<Cost> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }
        guard let basketReference = bookingDetails.basketReference else { throw WebserviceError.missingUserSession }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.bookingConfirmationForTotalCostWithCityTax
        parameters[.variablesKey] = GraphQL.bookingConfirmationVariables(
            basketReference: basketReference,
            bookingChannel: bookingDetails.bookingChannel.rawValue
        )

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary,
                  let dataDict = data["data"] as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let bookingConfirmationDict = dataDict["bookingConfirmation"] as? PIDictionary else { return nil }

            BookingDetails.sharedInstance.operaBookingReference = bookingConfirmationDict["bookingReference"] as? String

            let mappedDictionary = TotalCostMapper.map(from: bookingConfirmationDict)
            // could return the basketReference here - although it's probably the same as what we have already
            return try Cost(dictionary: mappedDictionary)
        }
    }

    func holdBookingWithGuests(bookingDetails: BookingDetails, isCiolFlow: Bool, isRegCard: Bool) throws -> Resource<Bool> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.createReservationGuestMutation

        if isRegCard {
            parameters[.variablesKey] = try GraphQL.getCreateReservationGuestRegCardVariables(
                bookingDetails: bookingDetails,
                isCiolFlow: isCiolFlow
            )
        } else {
            parameters[.variablesKey] = try GraphQL.getCreateReservationGuestVariables(
                bookingDetails: bookingDetails,
                isCiolFlow: isCiolFlow
            )
        }

        let shouldNotSendToken = isRegCard || isCiolFlow

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default,
            shouldSendAuthToken: !shouldNotSendToken
        ) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDictionary = data[.dataKey] as? PIDictionary,
                  dataDictionary.isEmpty == false else { throw GraphQLError.missingData }
            guard let reservationGuestDictionary = dataDictionary["createReservationGuest"] as? PIDictionary
                else { throw ResponseParserError.keyNotFound("createReservationGuest") }
            guard reservationGuestDictionary["basketReference"] is String
                else { throw ResponseParserError.keyNotFound("basketReference") }

            return true
        }
    }

    func checkBasketStatus(basketReference: String?) throws -> Resource<BookingConfirmation> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        guard let basketReference = basketReference else { throw BookingError.missingSessionIdentifier }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.basketStatusQuery
        parameters[.variablesKey] = try GraphQL.getBasketInformationVariables(basketReference: basketReference)

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDictionary = data[.dataKey] as? PIDictionary,
                  dataDictionary.isEmpty == false else { throw GraphQLError.missingData }
            guard let basketDictionary = dataDictionary["basketStatus"] as? PIDictionary
                else { throw ResponseParserError.keyNotFound("basketStatus") }

            var mappedDictionary = PIDictionary()
            mappedDictionary["basketStatus"] = basketDictionary
            mappedDictionary["confirmationNumber"] = basketDictionary["basketReference"]

            let bookingConfirmationData = try JSONSerialization.data(
                withJSONObject: mappedDictionary,
                options: .prettyPrinted
            )

            return try JSONDecoder().decode(BookingConfirmation.self, from: bookingConfirmationData)
        }
    }

    func holdBooking(bookingDetails: BookingDetails, sensorData: String) throws -> Resource<String> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.createReservationMutation
        parameters[.variablesKey] = try GraphQL.getCreateReservationVariables(bookingDetails: bookingDetails)

        let headers: [String: String] = [Constants.akamaiSensorDataKey: sensorData]

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default,
            headers: headers,
            shouldSendAuthToken: true
        ) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDictionary = data[.dataKey] as? PIDictionary,
                  dataDictionary.isEmpty == false else { throw GraphQLError.missingData }
            guard let reservationDictionary = dataDictionary["createReservation"] as? PIDictionary
                else { throw ResponseParserError.keyNotFound("createReservation") }
            guard let confirmationId = reservationDictionary["basketReference"] as? String
                else { throw ResponseParserError.keyNotFound("basketReference") }

            return confirmationId
        }
    }

    func releaseBooking(basketReference: String, hotelId: String?) throws -> Resource<Bool> {
        guard let hotelId = hotelId else { throw GraphQLError.missingHotelCode }
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.releaseBookingMutation
        parameters[.variablesKey] = GraphQL.releaseBookingVariables(basketReference: basketReference, hotelId: hotelId)

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDictionary = data[.dataKey] as? PIDictionary,
                  dataDictionary.isEmpty == false else { throw GraphQLError.missingData }
            guard let releaseBookingDictionary = dataDictionary["cancelOnHoldReservation"] as? PIDictionary
                else { throw ResponseParserError.keyNotFound("cancelOnHoldReservation") }
            return releaseBookingDictionary["basketReference"] != nil
        }
    }
}
