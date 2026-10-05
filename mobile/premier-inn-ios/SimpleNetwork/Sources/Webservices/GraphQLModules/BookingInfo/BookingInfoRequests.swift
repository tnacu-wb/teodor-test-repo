//
//  BookingInfoRequests.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 14/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation
import Alamofire

extension GraphQL {
    func reservation(
        reservationDetails: ReservationDetails,
        hotelCode: String?,
        bookingDetails: BookingDetails?
    ) throws -> Resource<Reservation> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()

        if let hotelCode = hotelCode {
            guard let token = reservationDetails.token, token.isEmpty != true else { throw GraphQLError.missingToken }

            parameters[.queryKey] = GraphQL.bookingConfirmationWithManageBookingQuery
            parameters[.variablesKey] = GraphQL.bookingConfirmationVariablesWithManageBooking(
                reservationDetails: reservationDetails,
                hotelCode: hotelCode,
                bookingChannel: reservationDetails.business == true ? Channel.BB.rawValue : Channel.PI.rawValue
            )
        } else {
            parameters[.queryKey] = GraphQL.bookingConfirmationOnlyQuery
            parameters[.variablesKey] = GraphQL.bookingConfirmationVariables(
                basketReference: reservationDetails.reservationId,
                bookingChannel: bookingDetails?.bookingChannel.rawValue
            )
        }

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw GraphQLError.missingData }
            guard let bookingConfirmationDict = dataDict["bookingConfirmation"] as? PIDictionary
                else { throw ResponseParserError.keyNotFound("bookingConfirmation") }

            var reservationDict: PIDictionary

            if hotelCode != nil {
                var manageBookingDict = PIDictionary()
                if let manageBooking = dataDict["manageBooking"] as? PIDictionary {
                    manageBookingDict = manageBooking
                }

                var packages = PIDictionary()
                if let packagesDict = dataDict["packages"] as? PIDictionary {
                    packages = packagesDict
                }
                reservationDict = ReservationMapper.map(
                    from: bookingConfirmationDict,
                    basketReference: reservationDetails.reservationId,
                    manageBooking: manageBookingDict,
                    packagesDict: packages,
                    isBusiness: reservationDetails.business
                )
            } else {
                reservationDict = ReservationMapper.map(
                    from: bookingConfirmationDict,
                    basketReference: reservationDetails.reservationId,
                    manageBooking: nil,
                    packagesDict: nil,
                    isBusiness: reservationDetails.business
                )
            }

            return try Reservation(dictionary: reservationDict, manageBookingOperaToken: reservationDetails.token)
        }
    }

    func cancelReservation(reservationDetails: ReservationDetails, hotelCode: String?) throws -> Resource<PIDictionary> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }
        guard let hotelCode = hotelCode else { throw GraphQLError.missingHotelCode }
        var parameters = PIDictionary()

        parameters[.queryKey] = GraphQL.cancelReservationMutation
        parameters[.variablesKey] = GraphQL.cancelReservationVariables(
            reservationDetails: reservationDetails,
            hotelCode: hotelCode
        )

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary,
                  let cancelReservation = dataDict["cancelReservation"] as? PIDictionary
            else { throw GraphQLError.missingData }
            let mappedDict = CancelReservationMapper.map(input: cancelReservation)

            return mappedDict
        }
    }

    func findBookingSource(findBookingDetails: FindBookingDetails) throws -> Resource<FindBookingSource> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()

        parameters[.queryKey] = GraphQL.findBookingSourceQuery
        parameters[.variablesKey] = GraphQL.findBookingSourceVariables(findBookingDetails: findBookingDetails)

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw GraphQLError.missingData }
            guard let findBookingDict = dataDict["findBooking"] as? PIDictionary
                else { throw ResponseParserError.keyNotFound("findBooking") }

            do {
                let findBookingData = try JSONSerialization.data(withJSONObject: findBookingDict)
                let findBookingSource = try JSONDecoder().decode(FindBookingSource.self, from: findBookingData)
                return findBookingSource
            } catch {
                throw GraphQLError.decodeError
            }
        }
    }

    func getStays() throws -> Resource<[Stay]> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()

        let isBusiness = UserSessionManager.sharedInstance.currentUser?.isBusiness ?? false

        parameters[.queryKey] = GraphQL.getStaysQuery
        parameters[.variablesKey] = GraphQL.getStaysVariables(business: isBusiness)

        return resource(
            url: url,
            parameters: parameters,
            method: .post,
            encoding: JSONEncoding.default,
            shouldSendAuthToken: true
        ) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw GraphQLError.missingData }
            guard let bookingHistoryDict = dataDict["bookingHistory"] as? PIDictionary
                else { throw GraphQLError.missingData }
            guard let bookingsDict = bookingHistoryDict["bookings"] as? [PIDictionary]
                else { throw ResponseParserError.keyNotFound("bookings") }

            return bookingsDict.compactMap { dict in
                var detailsDict = dict

                if isBusiness {
                    detailsDict["business"] = true
                }

                let mappedDict = StayMapper.map(input: detailsDict, isBusiness: isBusiness)
                // Do mapping here
                return try? Stay(dictionary: mappedDict)
            }
        }
    }

    func getPackages(
        reservationId: String,
        bookingDetails: BookingDetails,
        hotelCode: String,
        bookingFlowId: String?,
        showMealInclusiveRate: Bool
    ) throws -> Resource<([UpsellItem], [UpsellItem], CityTaxResponse, GoshPackage?)> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        guard let bookingFlowId = bookingFlowId else { throw WebserviceError.missingRequiredValues("bookingFlowId")}

        var parameters = PIDictionary()

        parameters[.queryKey] = GraphQL.getPackagesStandaloneQuery
        parameters[.variablesKey] = [
            "packagesCriteria": GraphQL.getPackagesVariables(
                basketReference: reservationId,
                hotelCode: hotelCode,
                bookingDetails: bookingDetails,
                bookingFlowId: bookingFlowId,
                showMealInclusiveRate: showMealInclusiveRate
            ),
            "bookingFlowCriteria": GraphQL.getBookingFlowCriteria(
                hotelCode: hotelCode,
                rateCode: bookingDetails.rate?.classification ?? ""
            )
        ]

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw GraphQLError.missingData }
            guard let packagesDict = dataDict["packages"] as? PIDictionary
                else { throw ResponseParserError.keyNotFound("packages") }

            let availableUpsells = AvailableUpsellsMapper.map(from: packagesDict)?.compactMap({
                try? UpsellItem(dictionary: $0)
            }) ?? []

            let bookedUpsells = ReservationUpsellsMapper.map(from: packagesDict)?.compactMap({
                try? UpsellItem(dictionary: $0)
            }) ?? []

            let cityTaxFlags: CityTaxResponse = (
                packagesDict["hotelHasCityTaxForLeisure"] as? Bool,
                packagesDict["hotelHasCityTaxForBusiness"] as? Bool
            )

            var goshPackages: GoshPackage? {
                guard let dict = dataDict["donations"] as? PIDictionary else { return nil }
                guard let goshData = try? JSONSerialization.data(withJSONObject: dict) else { return nil }
                return try? JSONDecoder().decode(GoshPackage.self, from: goshData)
            }


            return (availableUpsells, bookedUpsells, cityTaxFlags, goshPackages)
        }
    }

    func bookingInformation(basketReference: String, isBusiness: Bool) throws -> Resource<BookingInformation> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }
        var parameters = PIDictionary()

        parameters[.queryKey] = GraphQL.bookingInformationOnlyQuery
        parameters[.variablesKey] = GraphQL.bookingInformationVariables(
            basketReference: basketReference,
            isBusiness: isBusiness
        )

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary,
                  let bookingInformation = dataDict["bookingInformation"] as? PIDictionary
            else { throw GraphQLError.missingData }
            do {
                let bookingInfoData = try JSONSerialization.data(withJSONObject: bookingInformation)
                let bookingInfo = try JSONDecoder().decode(BookingInformation.self, from: bookingInfoData)
                return bookingInfo
            } catch {
                throw GraphQLError.decodeError
            }
        }
    }

    func updateReservationPreferences(
        hotelCode: String,
        reservationIds: [String],
        preferencesCollections: [PreferencesCollection]
    ) throws -> Resource<PIDictionary> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }
        var parameters = PIDictionary()

        parameters[.queryKey] = GraphQL.updateReservationPreferencesMutation
        parameters[.variablesKey] = GraphQL.updateReservationPreferencesVariables(
            hotelId: hotelCode,
            reservationsIds: reservationIds,
            preferences: preferencesCollections
        )

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary,
                  let updatePreferences = dataDict["updateReservationPreferences"] as? String
            else { throw GraphQLError.missingData }
            let mappedDict = UpdateReservationPreferencesMapper.map(from: updatePreferences)

            return mappedDict
        }
    }

    func getHotelPreferences(hotelCode: String) throws -> Resource<[HotelPreference]> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }
        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.getHotelPreferences
        parameters[.variablesKey] = GraphQL.getHotelPreferenceVariables(hotelCode: hotelCode)

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary,
                  let getHotelPreferences = dataDict["getHotelPreferences"] as? PIDictionary
            else { throw GraphQLError.missingData }
            guard let hotelPreferences = getHotelPreferences["hotelPreferences"] as? [PIDictionary]
                else { throw GraphQLError.missingData }

            let availableHotelPreference = try hotelPreferences.compactMap({
                let hotelPreferenceData = try JSONSerialization.data(withJSONObject: $0)
                let hotelPreference = try JSONDecoder().decode(HotelPreference.self, from: hotelPreferenceData)
                return hotelPreference
            })
            return availableHotelPreference
        }
    }

    func resendInvoiceEmail(reservation: Reservation) throws -> Resource<Bool> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.resendInvoiceEmailMutation
        parameters[.variablesKey] = GraphQL.resendInvoiceEmailVariables(reservation: reservation)

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw GraphQLError.missingData }

            let resendInvoiceEmail = dataDict["resendInvoiceEmail"] as? String

            return resendInvoiceEmail != nil
        }
    }
}
