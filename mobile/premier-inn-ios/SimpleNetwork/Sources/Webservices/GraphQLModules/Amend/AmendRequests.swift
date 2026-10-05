//
//  AmendRequest.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 24/07/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import Foundation
import Alamofire

extension GraphQL {
    func copyBooking(reservationDetails: ReservationDetails) throws -> Resource<String> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()

        parameters[.queryKey] = GraphQL.copyBookingMutation
        parameters[.variablesKey] = GraphQL.copyBookingParameters(reservationDetails: reservationDetails)

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw ResponseParserError.keyNotFound("data") }
            guard let copyBookingDict = dataDict["copyBooking"] as? PIDictionary
                else { throw ResponseParserError.keyNotFound("copyBooking") }
            guard let newBasketReference = copyBookingDict["copyBasketReference"] as? String
                else { throw ResponseParserError.keyNotFound("copyBasketReference") }

            return newBasketReference
        }
    }

    func hotelAvailabilityForAmendBooking(
        withHotelCode hotelCode: String,
        bookingDetails: BookingDetails,
        brand: HotelBrand?,
        allowEmployeeOffer: Bool
    ) throws -> Resource<HotelAvailabilityResponse> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        if brand != nil {
            parameters[.queryKey] = GraphQL.availabilityForAddRoomQuery
        } else {
            parameters[.queryKey] = GraphQL.availabilityForAmendQuery
        }

        parameters[.variablesKey] = try GraphQL.getAvailabilityForAmendVariables(
            bookingDetails: bookingDetails,
            hotelCode: hotelCode,
            brand: brand,
            allowEmployeeOffer: allowEmployeeOffer
        )

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default,
            parse: GraphQL.hotelAvailabilityForAmendParser
        )
    }

    static func hotelAvailabilityForAmendParser(data: Any) throws -> HotelAvailabilityResponse {
        guard var data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
        guard let dataDict = data["data"] as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
        guard let availabilityDict = dataDict["hotelAvailability"] as? PIDictionary
            else { throw RequestsManagerError.unexpectedResponseError }

        data = AvailabilityMapper.map(from: availabilityDict, and: nil, roomClassArray: nil)

        let rates = (data["ratePlans"] as? [PIDictionary])?.map { Rate(dictionary: $0) } ?? []
        let roomTypesDict = dataDict["roomTypeInformation"] as? PIDictionary
        let roomTypes = roomTypesDict?["roomTypes"] as? [PIDictionary] ?? []
        let roomTypeContent: [RoomTypeInformation]? = {
            guard let roomTypeContentData = try? JSONSerialization.data(withJSONObject: roomTypes, options: .prettyPrinted)
                else { return nil }

            let decoder = JSONDecoder()
            return try? decoder.decode([RoomTypeInformation].self, from: roomTypeContentData)
        }()
        let prepaymentAllowed = data["prepaymentAllowed"] as? Bool ?? false
        let available = data["available"] as? Bool ?? false
        let paymentProvider = PaymentProvider.cccp // always 3CP in Opera

        return (rates, nil, prepaymentAllowed, available, false, nil, nil, paymentProvider, roomTypeContent, nil)
    }

    func amendBookingDates(
        temporaryReference: String,
        arrivalDate: Date,
        departureDate: Date,
        token: String
    ) throws -> Resource<String> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.amendBookingDatesMutation

        parameters[.variablesKey] = try GraphQL.amendBookingDatesVariables(
            temporaryReference: temporaryReference,
            arrivalDate: arrivalDate,
            departureDate: departureDate,
            token: token
        )

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data -> String? in
            guard let data = data as? PIDictionary, let dataDict = data["data"] as? PIDictionary,
                  let changeBookingDatesDict = dataDict["changeBookingDates"] as? PIDictionary
            else { throw RequestsManagerError.unexpectedResponseError }
            guard let temporaryReference = changeBookingDatesDict["tempBasket"] as? String
                else { throw ResponseParserError.keyNotFound("tempBasket") }

            return temporaryReference
        }
    }

    func addNewRoom(roomCriteria: AmendRoomCriteria) throws -> Resource<String> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()

        parameters[.queryKey] = GraphQL.addNewRoomMutation
        parameters[.variablesKey] = try GraphQL.addNewRoomVariables(roomCriteria: roomCriteria)

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw ResponseParserError.keyNotFound("data") }
            guard let responseDict = dataDict["addNewRoom"] as? PIDictionary
                else { throw ResponseParserError.keyNotFound("addNewRoom") }
            guard let temporaryReference = responseDict["tempBookingRef"] as? String
                else { throw ResponseParserError.keyNotFound("tempBookingRef") }

            return temporaryReference
        }
    }

    func removeRoom(roomCriteria: AmendRoomCriteria) throws -> Resource<String> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()

        parameters[.queryKey] = GraphQL.removeRoomMutation
        parameters[.variablesKey] = try GraphQL.removeRoomVariables(roomCriteria: roomCriteria)

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw ResponseParserError.keyNotFound("data") }
            guard let responseDict = dataDict["removeRoom"] as? PIDictionary
                else { throw ResponseParserError.keyNotFound("removeRoom") }
            guard let temporaryReference = responseDict["tempBookingRef"] as? String
                else { throw ResponseParserError.keyNotFound("tempBookingRef") }

            return temporaryReference
        }
    }

    func amendPackages(packagesDetails: AmendPackagesDetail) throws -> Resource<Bool> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()

        parameters[.queryKey] = GraphQL.amendPackagesMutation
        parameters[.variablesKey] = GraphQL.amendPackagesParameters(parameter: packagesDetails)

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw ResponseParserError.keyNotFound("data") }
            guard let temporaryReference = dataDict["updateReservationPackagesByReservation"] as? String
                else { throw ResponseParserError.keyNotFound("updateReservationPackagesByReservation") }

            return temporaryReference.contains(packagesDetails.basketReferenceId)
        }
    }

    func amendCiolPackages(amendInfo: CiolAmendInfo) throws -> Resource<Bool> {
        let path = String.graphQLPath

        guard let url = baseURL?.appendingPathComponent(path) else { throw WebserviceError.invalidPath(path) }

        var parameters = PIDictionary()

        parameters[.queryKey] = GraphQL.amendPackagesMutation
        parameters[.variablesKey] = GraphQL.amendCiolPackagesParameters(amendInfo: amendInfo)

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data[Constants.RequestCoding.data] as? PIDictionary
                else { throw ResponseParserError.keyNotFound(Constants.RequestCoding.data) }
            guard let temporaryReference = dataDict[CiolAmendInfo.Constants.query] as? String
                else { throw ResponseParserError.keyNotFound(CiolAmendInfo.Constants.query) }
            return temporaryReference.contains(amendInfo.basketReferenceId)
        }
    }

    func confirmAmendLogic(
        reservationDetails: ReservationDetails,
        tempBookingReference: String,
        selectedPaymentOption: String
    ) throws -> Resource<CCCPPaymentResponse> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }
        let environmentURL = getEnvironmentURL(host: apiHost)

        var parameters = PIDictionary()

        parameters[.queryKey] = GraphQL.confirmAmendLogicMutation
        parameters[.variablesKey] = GraphQL.confirmAmendLogicParameters(
            reservationDetails: reservationDetails,
            tempBookingReference: tempBookingReference,
            selectedPaymentOption: selectedPaymentOption,
            environment: environmentURL
        )

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw ResponseParserError.keyNotFound("data") }
            guard let confirmAmendLogicDict = dataDict["confirmAmendLogic"] as? PIDictionary
                else { throw ResponseParserError.keyNotFound("confirmAmendLogic") }
            guard let paymentDict = confirmAmendLogicDict["payment"] as? PIDictionary
                else { throw ResponseParserError.keyNotFound("payment") }

            do {
                let paymentData = try JSONSerialization.data(withJSONObject: paymentDict, options: .prettyPrinted)
                return try JSONDecoder().decode(CCCPPaymentResponse.self, from: paymentData)
            } catch {
                throw GraphQLError.decodeError
            }
        }
    }

    func amendEditRoom(amendEditDetails: AmendRoomCriteria) throws -> Resource<String> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()

        parameters[.queryKey] = GraphQL.amendEditRoomMutation
        parameters[.variablesKey] = try GraphQL.amendEditRoomParameters(amendEdit: amendEditDetails)

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw ResponseParserError.keyNotFound("data") }
            guard let amendEditRoom = dataDict["amendEditRoom"] as? PIDictionary
                else { throw ResponseParserError.keyNotFound("amendEditRoom") }
            guard let tempBookingRef = amendEditRoom["tempBookingRef"] as? String
                else { throw ResponseParserError.keyNotFound("tempBookingRef") }

            return tempBookingRef
        }
    }

    func reservationForAmend(reservationDetails: ReservationDetails, hotelCode: String?) throws -> Resource<Reservation> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        guard let token = reservationDetails.token, token.isEmpty != true else { throw GraphQLError.missingToken }
        guard let hotelCode = hotelCode else { throw GraphQLError.missingHotelCode }

        parameters[.queryKey] = GraphQL.bookingConfirmationWithManageBookingQuery
        parameters[.variablesKey] = GraphQL.bookingConfirmationVariablesForAmend(
            reservationDetails: reservationDetails,
            hotelCode: hotelCode
        )

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw GraphQLError.missingData }
            guard let bookingConfirmationDict = dataDict["bookingConfirmation"] as? PIDictionary
                else { throw ResponseParserError.keyNotFound("bookingConfirmation") }

            var reservationDict: PIDictionary

            var manageBookingDict = PIDictionary()
            if let manageBooking = dataDict["manageBooking"] as? PIDictionary {
                manageBookingDict = manageBooking
            }

            reservationDict = ReservationMapper.map(
                from: bookingConfirmationDict,
                basketReference: reservationDetails.reservationId,
                manageBooking: manageBookingDict,
                packagesDict: nil,
                isBusiness: reservationDetails.business
            )

            return try Reservation(dictionary: reservationDict, manageBookingOperaToken: reservationDetails.token)
        }
    }

    func amendSummary(reservationDetails: ReservationDetails, temporaryReference: String) throws -> Resource<AmendSummary> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }
        guard let token = reservationDetails.token, !token.isEmpty else { throw GraphQLError.missingToken }

        var parameters = PIDictionary()

        parameters[.queryKey] = GraphQL.amendSummaryQuery
        parameters[.variablesKey] = GraphQL.amendSummaryVariables(
            reservationDetails: reservationDetails,
            temporaryReference: temporaryReference
        )

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw GraphQLError.missingData }
            guard let amendSummaryDict = dataDict["amendSummary"] as? PIDictionary
                else { throw ResponseParserError.keyNotFound("amendSummary") }

            do {
                let amendSummaryData = try JSONSerialization.data(withJSONObject: amendSummaryDict, options: .prettyPrinted)
                let decoder = JSONDecoder()
                return try decoder.decode(AmendSummary.self, from: amendSummaryData)
            } catch {
                throw GraphQLError.decodeError
            }
        }
    }

    func bookingConfirmationWithAmendSummary(
        reservationDetails: ReservationDetails,
        originalBookingRef: String
    ) throws -> Resource<(
        Reservation,
        AmendSummary
    )> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()

        parameters[.queryKey] = GraphQL.bookingConfirmationWithAmendSummaryQuery
        parameters[.variablesKey] = GraphQL.bookingConfirmationWithAmendSummaryVariables(
            reservationDetails: reservationDetails,
            originalBasketRef: originalBookingRef
        )

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw GraphQLError.missingData }
            guard let bookingConfirmationDict = dataDict["bookingConfirmation"] as? PIDictionary
                else { throw ResponseParserError.keyNotFound("bookingConfirmation") }
            guard let amendSummaryDict = dataDict["amendSummary"] as? PIDictionary
                else { throw ResponseParserError.keyNotFound("amendSummary") }

            var reservationDict: PIDictionary

            reservationDict = ReservationMapper.map(
                from: bookingConfirmationDict,
                basketReference: reservationDetails.reservationId,
                manageBooking: nil,
                packagesDict: nil,
                isBusiness: reservationDetails.business
            )

            var totalCostDict = reservationDict["totalCost"] as? PIDictionary
            totalCostDict?["amount"] = amendSummaryDict["totalCost"]
            reservationDict["totalCost"] = totalCostDict
            let reservation = try Reservation(dictionary: reservationDict, manageBookingOperaToken: reservationDetails.token)

            let amendSummaryData = try JSONSerialization.data(withJSONObject: amendSummaryDict, options: .prettyPrinted)
            let decoder = JSONDecoder()
            let amendSummary = try decoder.decode(AmendSummary.self, from: amendSummaryData)

            return (reservation, amendSummary)
        }
    }

    func amendConfirmationPrices(
        tempBookingRef: String,
        originalBookingRef: String,
        token: String
    ) throws -> Resource<AmendConfirmationPrices> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()

        parameters[.queryKey] = GraphQL.amendConfirmationPricesQuery
        parameters[.variablesKey] = GraphQL.amendConfirmationPricesParameters(
            tempBookingRef: tempBookingRef,
            originalBookingRef: originalBookingRef,
            token: token
        )

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw GraphQLError.missingData }
            guard let pricesConfirmationDict = dataDict["amendConfirmationPrices"] as? PIDictionary
                else { throw ResponseParserError.keyNotFound("amendConfirmationPrices") }

            do {
                let pricesConfirmationData = try JSONSerialization.data(
                    withJSONObject: pricesConfirmationDict,
                    options: .prettyPrinted
                )
                let decoder = JSONDecoder()
                return try decoder.decode(AmendConfirmationPrices.self, from: pricesConfirmationData)
            } catch {
                throw GraphQLError.decodeError
            }
        }
    }
}
