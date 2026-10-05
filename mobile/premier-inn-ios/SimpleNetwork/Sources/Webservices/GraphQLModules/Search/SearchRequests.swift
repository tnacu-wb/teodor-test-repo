//
//  SearchRequests.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 14/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation
import Alamofire

extension GraphQL {
    func searchAvailabilities(
        bookingDetails: BookingDetails,
        suggestion: Suggestion,
        page: Int?,
        sorting: AvailabilitiesSorting,
        allowEmployeeOffer: Bool
    ) throws -> Resource<AvailabilitiesResponse> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.availabilitiesQuery
        parameters[.variablesKey] = GraphQL.getAvalabilitiesVariables(
            bookingDetails: bookingDetails,
            suggestion: suggestion,
            page: page,
            size: 40,
            sorting: sorting,
            allowEmployeeOffer: allowEmployeeOffer
        )

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw GraphQLError.missingData }
            guard let hotelAvailabilities = dataDict["hotelAvailabilities"] as? PIDictionary
                else { throw ResponseParserError.keyNotFound("hotelAvailabilities") }
            guard let total = hotelAvailabilities["total"] as? Int else { throw ResponseParserError.keyNotFound("total") }
            guard let pageNumber = hotelAvailabilities["page"] as? Int else { throw ResponseParserError.keyNotFound("page") }
            guard let multiHotelAvailabilities = hotelAvailabilities["multiHotelAvailabilities"] as? [PIDictionary]
                else { throw ResponseParserError.keyNotFound("multiHotelAvailabilities")}

            let mappedAvailabilities = AvailabilitiesMapper.map(from: multiHotelAvailabilities)

            let hotels = mappedAvailabilities.compactMap { try? Hotel(dictionary: $0) }

            return AvailabilitiesResponse(hotels: hotels, total: total, pageNumber: pageNumber, shouldPaginate: false)
        }
    }

    func hotelAvailability(
        hotelCode: String,
        hotelBrand: HotelBrand?,
        bookingDetails: BookingDetails,
        allowEmployeeOffer: Bool
    ) throws -> Resource<HotelAvailabilityResponse> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.availabilityAndPackagesQuery

        parameters[.variablesKey] = try GraphQL.getAvailabilityAndPackagesVariables(
            bookingDetails: bookingDetails,
            hotelCode: hotelCode,
            hotelBrand: hotelBrand,
            allowEmployeeOffer: allowEmployeeOffer
        )

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default,
            parse: GraphQL.hotelAvailabilityParser
        )
    }

    static func hotelAvailabilityParser(data: Any) throws -> HotelAvailabilityResponse {
        guard var data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
        guard let dataDict = data["data"] as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
        guard let availabilityDict = dataDict["hotelAvailability"] as? PIDictionary
            else { throw RequestsManagerError.unexpectedResponseError }

        let ratesInfoDict = dataDict["ratesInformation"] as? PIDictionary
        let ratesInfo = ratesInfoDict?["rateClassifications"] as? [PIDictionary] ?? []
        let ratesInfoDictBB = dataDict["ratesInformationBB"] as? PIDictionary
        let ratesInfoBB = ratesInfoDictBB?["rateClassifications"] as? [PIDictionary] ?? []
        let ratesInfoCombined = ratesInfo + ratesInfoBB

        let roomTypesDict = dataDict["roomTypeInformation"] as? PIDictionary
        let roomTypes = roomTypesDict?["roomTypes"] as? [PIDictionary] ?? []

        let globalConfigDict = dataDict["globalConfig"] as? PIDictionary
        let roomClassArray = globalConfigDict?["roomClassConfig"] as? [PIDictionary]

        data = AvailabilityMapper.map(from: availabilityDict, and: ratesInfoCombined, roomClassArray: roomClassArray)

        let rates = (data["ratePlans"] as? [PIDictionary])?.map { Rate(dictionary: $0) } ?? []
        let notesDictionaries = data["notes"] as? [PIDictionary]
        let notes = notesDictionaries?.compactMap { Note(dictionary: $0) }
        let prepaymentAllowed = data["prepaymentAllowed"] as? Bool ?? false
        let available = data["available"] as? Bool ?? false
        let limitedAvailability = data["limitedAvailability"] as? Bool ?? false
        let cnpAuthorisation: CNPAuthorisation? = {
            guard let cnpAuthDictionary = data["cnpAuthorisation"] as? PIDictionary else { return nil }
            guard let cnpdata = try? JSONSerialization.data(withJSONObject: cnpAuthDictionary, options: .prettyPrinted)
                else { return nil }

            let decoder = JSONDecoder()
            return try? decoder.decode(CNPAuthorisation.self, from: cnpdata)
        }()
        let ratesInfoContent: [RateInformation]? = {
            guard let ratesContentData = try? JSONSerialization.data(withJSONObject: ratesInfo, options: .prettyPrinted)
                else { return nil }

            let decoder = JSONDecoder()
            return try? decoder.decode([RateInformation].self, from: ratesContentData)
        }()
        let roomTypeContent: [RoomTypeInformation]? = {
            guard let roomTypeContentData = try? JSONSerialization.data(withJSONObject: roomTypes, options: .prettyPrinted)
                else { return nil }

            let decoder = JSONDecoder()
            return try? decoder.decode([RoomTypeInformation].self, from: roomTypeContentData)
        }()
        let cityTaxResponse: CityTaxResponse = (false, false)
        let paymentProvider = PaymentProvider
            .cccp // always 3CP in Opera // PaymentProvider(rawValue: data["paymentProvider"] as? String ?? "")

        return (
            rates,
            notes,
            prepaymentAllowed,
            available,
            limitedAvailability,
            cnpAuthorisation,
            cityTaxResponse,
            paymentProvider,
            roomTypeContent,
            ratesInfoContent
        )
    }
}
