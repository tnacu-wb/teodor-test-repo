//
//  AmendParameters.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 24/07/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import Foundation

extension GraphQL {
    static func copyBookingParameters(reservationDetails: ReservationDetails) -> PIDictionary {
        var params = PIDictionary()

        params["originalBasketReference"] = reservationDetails.reservationId
        params["token"] = reservationDetails.token
        params["bookingChannel"] = getBookingChannel(business: reservationDetails.business)

        return ["copyBookingCriteria": params]
    }

    static func amendBookingDatesVariables(
        temporaryReference: String,
        arrivalDate: Date,
        departureDate: Date,
        token: String
    ) throws -> PIDictionary {
        var dict = PIDictionary()

        dict["tempBookingRef"] = temporaryReference
        dict["newStartDate"] = arrivalDate.parameterString
        dict["newEndDate"] = departureDate.parameterString
        dict["token"] = token
        // update with AmendDetails
        dict["bookingChannel"] = getBookingChannel(bookingDetails: BookingDetails.sharedInstance)

        return ["amendStayDatesCriteria": dict]
    }

    static func addNewRoomVariables(roomCriteria: AmendRoomCriteria) throws -> PIDictionary {
        guard let roomOccupancy = roomCriteria.roomOccupancy
            else { throw WebserviceError.missingRequiredValues("roomOccupancy") }
        guard let leadGuest = roomCriteria.leadGuest else { throw WebserviceError.missingRequiredValues("leadGuest") }
        guard let roomType = roomCriteria.roomType else { throw WebserviceError.missingRequiredValues("roomType") }

        var params = PIDictionary()

        params["bookingChannel"] = getBookingChannel(business: roomCriteria.isBusiness)
        params["tempBookingRef"] = roomCriteria.tempBookingRef
        params["roomOccupancy"] = getRoomOccupancyAmend(roomOccupancy: roomOccupancy)
        params["leadGuest"] = getLeadGuest(leadGuest: leadGuest)
        params["roomType"] = roomType
        params["token"] = roomCriteria.token
        params["specialRequests"] = roomCriteria.specialRequests

        return ["addNewRoomCriteria": params]
    }

    static func removeRoomVariables(roomCriteria: AmendRoomCriteria) throws -> PIDictionary {
        guard let reservationId = roomCriteria.reservationId
            else { throw WebserviceError.missingRequiredValues("reservationId") }

        var dict = PIDictionary()

        dict["tempBookingRef"] = roomCriteria.tempBookingRef
        dict["reservationId"] = reservationId
        dict["token"] = roomCriteria.token.addingPercentEncoding(withAllowedCharacters: .alphanumerics)
        dict["bookingChannel"] = getBookingChannel(business: roomCriteria.isBusiness)

        return dict
    }

    static func amendPackagesParameters(parameter: AmendPackagesDetail) -> PIDictionary {
        var params = PIDictionary()

        params["basketReferenceId"] = parameter.basketReferenceId
        params["hotelId"] = parameter.hotelId
        params["arrivalDate"] = parameter.arrivalDate.parameterString
        params["departureDate"] = parameter.departureDate.parameterString
        if let roomsSelections = parameter.roomsSelections, let rooms = parameter.rooms {
            params["roomsSelections"] = getPackagesSelectionsFor(selections: roomsSelections, rooms: rooms)
        }
        if let previousRoomsSelections = parameter.previousRoomsSelections, let rooms = parameter.rooms {
            params["previousRoomsSelections"] = getPackagesSelectionsFor(selections: previousRoomsSelections, rooms: rooms)
        }

        return ["updateReservationPackagesRequest": params]
    }

    static func amendCiolPackagesParameters(amendInfo: CiolAmendInfo) -> PIDictionary {
        var params = PIDictionary()
        params[CiolAmendInfo.Constants.basketReferenceId] = amendInfo.basketReferenceId
        params[CiolAmendInfo.Constants.hotelId] = amendInfo.hotelId
        params[CiolAmendInfo.Constants.arrivalDate] = amendInfo.arrivalDate.parameterString
        params[CiolAmendInfo.Constants.departureDate] = amendInfo.departureDate.parameterString
        params[CiolAmendInfo.Constants.roomsSelections] = getCiolPackagesSelections(
            rooms: amendInfo.roomsSelections,
            sendPackagesSelection: true
        )
        // Sending the packages in previousRoomsSelections will remove packages selcted during booking
        // we do not want this as of this moment
        params[CiolAmendInfo.Constants.previousRoomsSelections] = getCiolPackagesSelections(
            rooms: amendInfo.previousRoomsSelections,
            sendPackagesSelection: false
        )
        return [CiolAmendInfo.Constants.updateReservationPackagesRequest: params]
    }

    static func bookingConfirmationVariablesForAmend(
        reservationDetails: ReservationDetails,
        hotelCode: String
    ) -> PIDictionary {
        let bookingChannel = reservationDetails.business == true ? Channel.BB.rawValue : Channel.PI.rawValue
        var bookingConfirmationDict = bookingConfirmationVariables(
            basketReference: reservationDetails.reservationId,
            bookingChannel: bookingChannel
        )

        bookingConfirmationDict["cancelInformationCriteria"] = manageBookingVariablesForAmend(
            reservationDetails: reservationDetails,
            hotelCode: hotelCode
        )

        return bookingConfirmationDict
    }

    private static func manageBookingVariablesForAmend(
        reservationDetails: ReservationDetails,
        hotelCode: String
    ) -> PIDictionary {
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

    static func confirmAmendLogicParameters(
        reservationDetails: ReservationDetails,
        tempBookingReference: String,
        selectedPaymentOption: String,
        environment: String
    ) -> PIDictionary {
        var params = PIDictionary()

        params["tempBookingRef"] = tempBookingReference
        params["originalBookingRef"] = reservationDetails.reservationId
        params["token"] = reservationDetails.token
        params["bookingChannel"] = getBookingChannel(business: reservationDetails.business)
        params["paymentOptionSelected"] = selectedPaymentOption
        params["environment"] = environment

        return ["confirmAmendLogicCriteria": params]
    }

    static func amendEditRoomParameters(amendEdit: AmendRoomCriteria) throws -> PIDictionary {
        guard let reservationId = amendEdit.reservationId
            else { throw WebserviceError.missingRequiredValues("reservationId") }
        guard let roomOccupancy = amendEdit.roomOccupancy
            else { throw WebserviceError.missingRequiredValues("roomOccupancy") }
        guard let leadGuest = amendEdit.leadGuest else { throw WebserviceError.missingRequiredValues("leadGuest") }
        guard let roomType = amendEdit.roomType else { throw WebserviceError.missingRequiredValues("roomType") }

        var params = PIDictionary()

        params["tempBookingRef"] = amendEdit.tempBookingRef
        params["reservationId"] = reservationId
        params["token"] = amendEdit.token
        params["bookingChannel"] = getBookingChannel(business: amendEdit.isBusiness)
        params["roomOccupancy"] = getRoomOccupancyAmend(roomOccupancy: roomOccupancy)
        params["leadGuest"] = getLeadGuest(leadGuest: leadGuest)
        params["roomType"] = roomType

        return ["editRoomCriteria": params]
    }

    private static func getRoomOccupancyAmend(roomOccupancy: RoomOccupancyAmend) -> PIDictionary {
        var params = PIDictionary()

        params["adultsNumber"] = roomOccupancy.adultsNumber
        params["childrenNumber"] = roomOccupancy.childrenNumber
        if let cotRequired = roomOccupancy.cotRequired {
            params["cotRequired"] = cotRequired
        }

        return params
    }

    private static func getLeadGuest(leadGuest: LeadGuest) -> PIDictionary {
        var params = PIDictionary()

        params["title"] = leadGuest.title
        params["firstName"] = leadGuest.firstName
        params["lastName"] = leadGuest.lastName
        if let email = leadGuest.emailAddress {
            params["emailAddress"] = email
        }

        return params
    }

    private static func getPackagesSelectionsFor(selections: [UpsellItem], rooms: [Room]) -> [PIDictionary] {
        var roomSelection = [PIDictionary]()

        for room in rooms {
            var dict = PIDictionary()
            var packageSelection = [PIDictionary]()
            for upsell in selections where upsell.roomId == room.roomId {
                var package = PIDictionary()
                package["id"] = upsell.id

                if upsell.foodUpsell {
                    package["noOfSelections"] = upsell.adults
                } else {
                    package["noOfSelections"] = upsell.quantity
                }

                packageSelection.append(package)
            }

            // free children meals - do this once every room to avoid adding different/more free breakfasts in the same room
            if room.children > 0,
               let upsellThatGivesFreeBreakfast = selections
               .first(where: {
                    $0.roomId == room.roomId &&
                    $0.foodUpsell &&
                    $0.freeBreakfastTrigger == true &&
                    $0.freeBreakfastMaxPerMeal > 0 &&
                    $0.freeBreakfastCode != nil
                }) {
                packageSelection.append(freeBreakfastDictionary(for: room.children, and: upsellThatGivesFreeBreakfast))
            }

            dict["reservationId"] = room.roomId
            dict["packagesSelection"] = packageSelection
            roomSelection.append(dict)
        }

        return roomSelection
    }

    private static func getCiolPackagesSelections(
        rooms: [CiolUpsellRoomSelection],
        sendPackagesSelection: Bool
    ) -> [PIDictionary] {
        var roomSelection = [PIDictionary]()

        for room in rooms {
            var dict = PIDictionary()
            var packageSelection = [PIDictionary]()
            for selection in room.packagesSelection {
                var package = PIDictionary()
                package[CiolAmendInfo.Constants.noOfSelections] = selection.noOfSelections
                package[CiolAmendInfo.Constants.id] = selection.id
                packageSelection.append(package)
            }

            dict[CiolAmendInfo.Constants.reservationId] = room.reservationId
            dict[CiolAmendInfo.Constants.packagesSelection] = sendPackagesSelection ? packageSelection : []
            roomSelection.append(dict)
        }
        return roomSelection
    }

    public static func getAvailabilityForAmendVariables(
        bookingDetails: BookingDetails,
        hotelCode: String,
        brand: HotelBrand?,
        allowEmployeeOffer: Bool
    ) throws -> PIDictionary {
        var params = PIDictionary()

        params["availabilitySearchCriteria"] = getAvailabilityVariables(
            bookingDetails: bookingDetails,
            hotelCode: hotelCode,
            allowEmployeeOffer: allowEmployeeOffer
        )
        if let brand = brand {
            params["brand"] = brand.rawValue
            params["country"] = LanguageManager.supportedLanguage.countryCode
            params["language"] = LanguageManager.supportedLanguage.rawValue
        }

        return params
    }

    public static func amendSummaryVariables(
        reservationDetails: ReservationDetails,
        temporaryReference: String
    ) -> PIDictionary {
        var dict = PIDictionary()

        dict["originalBasketRef"] = reservationDetails.reservationId
        dict["copyBasketRef"] = temporaryReference
        dict["token"] = reservationDetails.token
        dict["bookingChannel"] = getBookingChannel(business: reservationDetails.business)
        dict["country"] = LanguageManager.supportedLanguage.countryCode

        return dict
    }

    static func amendConfirmationPricesParameters(
        tempBookingRef: String,
        originalBookingRef: String,
        token: String
    ) -> PIDictionary {
        var params = PIDictionary()

        params["tempBookingRef"] = tempBookingRef
        params["originalBookingRef"] = originalBookingRef
        params["token"] = token

        return ["amendConfirmationPricesRequest": params]
    }

    static func bookingConfirmationWithAmendSummaryVariables(
        reservationDetails: ReservationDetails,
        originalBasketRef: String
    ) -> PIDictionary {
        let bookingChannel = reservationDetails.business == true ? Channel.BB.rawValue : Channel.PI.rawValue
        var bookingConfirmationDict = bookingConfirmationVariables(
            basketReference: reservationDetails.reservationId,
            bookingChannel: bookingChannel
        )

        bookingConfirmationDict["originalBasketRef"] = originalBasketRef
        bookingConfirmationDict["copyBasketRef"] = reservationDetails.reservationId
        bookingConfirmationDict["token"] = reservationDetails.token
        bookingConfirmationDict["bookingChannelObject"] = getBookingChannel(business: reservationDetails.business)

        return bookingConfirmationDict
    }
}
