//
//  BookingFlowParameters.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 14/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

extension GraphQL {
    static func saveUpsellsToBookingVariables(bookingDetails: BookingDetails) -> PIDictionary {
        var variables = PIDictionary()
        var params = PIDictionary()

        params["basketReferenceId"] = bookingDetails.basketReference
        params["hotelId"] = bookingDetails.hotel?.code
        params["arrivalDate"] = bookingDetails.criteria.arrivalDate.parameterString
        params["departureDate"] = bookingDetails.criteria.checkOutDate?.parameterString
        params["roomsSelections"] = getPackagesDictionaries(
            bookingDetails: bookingDetails,
            roomMealCombos: bookingDetails.roomMealCombos,
            roomExtraPackages: bookingDetails.roomExtraPackages
        )
        if bookingDetails.previousRoomMealCombos != nil || bookingDetails.previousRoomExtraPackages != nil {
            params["previousRoomsSelections"] = getPackagesDictionaries(
                bookingDetails: bookingDetails,
                roomMealCombos: bookingDetails.previousRoomMealCombos,
                roomExtraPackages: bookingDetails.previousRoomExtraPackages
            )
        }

        variables["ancillariesCriteria"] = params

        return variables
    }

    private static func getPackagesDictionaries(
        bookingDetails: BookingDetails,
        roomMealCombos: [RoomMealCombo]?,
        roomExtraPackages: [RoomMealCombo]?
    ) -> [PIDictionary] {
        var roomsPackages = [PIDictionary]()

        for (index, room) in bookingDetails.criteria.rooms.enumerated() {
            var oneRoomPackages = [PIDictionary]()
            var oneRoomPackagesSum = PIDictionary()
            oneRoomPackagesSum["packagesSelection"] = [PIDictionary]()

            // meals
            guard let roomMealCombos = roomMealCombos else { continue }
            for roomMealCombo in roomMealCombos {
                guard roomMealCombo.roomNumber == index else { continue }

                // ignore free breakfast promotion
                guard roomMealCombo.meal.id != UpsellItemOperaId.freeBreakfastPromotion.rawValue else { continue }

                oneRoomPackages.append(getUpsellSelectionValues(roomMealCombo: roomMealCombo))

                // free children meals
                if roomMealCombo.meal.freeBreakfastTrigger == true,
                   room.children > 0,
                   roomMealCombo.meal.freeBreakfastMaxPerMeal > 0,
                   let freeBreakfastCode = roomMealCombo.meal.freeBreakfastCode,
                   !freeBreakfastCode.isEmpty,
                   oneRoomPackages.contains(where: { $0["id"] as? String == freeBreakfastCode }) == false {
                    oneRoomPackages.append(freeBreakfastDictionary(for: room.children, and: roomMealCombo.meal))
                }
            }

            // extras

            if let extraPackages = roomExtraPackages?.filter({ $0.roomNumber == index }) {
                for extra in extraPackages {
                    oneRoomPackages.append(getUpsellSelectionValues(roomMealCombo: extra))
                }
            }

            oneRoomPackagesSum["packagesSelection"] = oneRoomPackages

            roomsPackages.append(oneRoomPackagesSum)
        }

        return roomsPackages
    }

    private static func getUpsellSelectionValues(roomMealCombo: RoomMealCombo) -> PIDictionary {
        var dict = PIDictionary()

        dict["id"] = roomMealCombo.meal.id
        dict["noOfSelections"] = roomMealCombo.quantity

        return dict
    }

    static func freeBreakfastDictionary(for children: Int, and upsell: UpsellItem) -> PIDictionary {
        var dictionary = PIDictionary()
        dictionary["id"] = upsell.freeBreakfastCode
        // if there are more children than the max, then only add the max allowed, otherwise add for all children
        dictionary["noOfSelections"] = children > upsell.freeBreakfastMaxPerMeal ? upsell.freeBreakfastMaxPerMeal : children

        return dictionary
    }

    static func getCreateReservationGuestVariables(bookingDetails: BookingDetails, isCiolFlow: Bool) throws -> PIDictionary {
        var params = PIDictionary()

        guard let hotelCode = bookingDetails.hotel?.code,
              !hotelCode.isEmpty,
              let basketReference = bookingDetails.basketReference
        else { throw RequestsManagerError.unexpectedResponseError }

        let criteria: [String: Any] = [
            "basketReference": basketReference,
            "hotelId": hotelCode,
            "reasonForStay": (bookingDetails.purpose ?? .leisure).operaReasonForStay,
            "booker": getBookerDictionary(bookingDetails: bookingDetails),
            "stayingGuests": getStayingGuestsArray(bookingDetails: bookingDetails, isCiolFlow: isCiolFlow)
        ]
        params["createReservationGuestCriteria"] = criteria

        return params
    }

    static func getCreateReservationGuestRegCardVariables(
        bookingDetails: BookingDetails,
        isCiolFlow: Bool
    ) throws -> PIDictionary {
        var params = PIDictionary()

        guard let hotelCode = bookingDetails.hotel?.code,
              !hotelCode.isEmpty,
              let basketReference = bookingDetails.basketReference
        else { throw RequestsManagerError.unexpectedResponseError }

        let criteria: [String: Any] = [
            "basketReference": basketReference,
            "hotelId": hotelCode,
            "preCheckIn": true,
            "reasonForStay": (bookingDetails.purpose ?? .leisure).operaReasonForStay,
            "booker": getBookerDictionary(bookingDetails: bookingDetails),
            "stayingGuests": getStayingGuestsRegCardArray(bookingDetails: bookingDetails, isCiolFlow: isCiolFlow)
        ]
        params["createReservationGuestCriteria"] = criteria

        return params
    }

    private static func getBookerDictionary(bookingDetails: BookingDetails) -> PIDictionary {
        var dict = PIDictionary()

        dict["title"] = bookingDetails.booker?.title
        dict["firstName"] = bookingDetails.booker?.firstName
        dict["lastName"] = bookingDetails.booker?.lastName
        dict["emailAddress"] = bookingDetails.booker?.emailAddress
        dict["mobile"] = bookingDetails.booker?.contactNumber
        dict["landline"] = bookingDetails.booker?.contactNumber
        dict["acceptFutureMailing"] = bookingDetails.marketingOptIn
        dict["address"] = getGuestAddress(user: bookingDetails.booker)

        return dict
    }

    private static func getStayingGuestsRegCardArray(bookingDetails: BookingDetails, isCiolFlow: Bool) -> [PIDictionary] {
        var stayingGuests = [PIDictionary]()

        // DE REG Card supports one room only
        guard let room = bookingDetails.criteria.rooms.first,
              let guestList = room.guestList else { return [] }
        stayingGuests = guestList.enumerated().compactMap { guest in
            var guestDict = PIDictionary()
            guestDict["stayingGuestDetails"] = getGuestDetails(guest: guest.element, isCiolFlow: isCiolFlow, isRegCard: true)
            guestDict["isAccompanyingGuest"] = guest.element.isAccompanyingGuest
            guestDict["sameAsBooker"] = bookingDetails.booker == guest.element && guest.offset == 0
            guestDict["reservationId"] = room.reservationId
            return guestDict
        }
        return stayingGuests
    }

    private static func getStayingGuestsArray(bookingDetails: BookingDetails, isCiolFlow: Bool) -> [PIDictionary] {
        var stayingGuests = [PIDictionary]()

        stayingGuests = bookingDetails.criteria.rooms.compactMap { room in
            var guestDict = PIDictionary()

            guestDict["sameAsBooker"] = bookingDetails.booker == room.leadGuest
            guestDict["stayingGuestDetails"] = getGuestDetails(
                guest: room.leadGuest,
                isCiolFlow: isCiolFlow,
                isRegCard: false
            )
            guestDict["accompanyingGuestDetails"] = getGuestDetails(
                guest: room.accompanyingGuest,
                isCiolFlow: isCiolFlow,
                isRegCard: false
            )

            return guestDict
        }
        return stayingGuests
    }

    private static func getGuestDetails(guest: User?, isCiolFlow: Bool, isRegCard: Bool) -> PIDictionary {
        var roomDict = PIDictionary()

        roomDict["title"] = guest?.title
        roomDict["firstName"] = guest?.firstName
        roomDict["lastName"] = guest?.lastName
        if isCiolFlow {
            roomDict["additionalDetails"] = getAdditionalGuestDetails(guest: guest)
        }
        if isRegCard {
            roomDict["profileId"] = guest?.profileId
            roomDict["address"] = getGuestAddress(user: guest, isRegCard: isRegCard)
        }

        return roomDict
    }

    private static func getAdditionalGuestDetails(guest: User?) -> PIDictionary {
        var roomDict = PIDictionary()

        roomDict["dob"] = guest?.outputDOB
        roomDict["passportNumber"] = guest?.passport?.number
        roomDict["nationality"] = guest?.country?.isoCode

        return roomDict
    }

    static func getGuestAddress(user: User?, isRegCard: Bool = false) -> PIDictionary {
        var guestAddressDict = PIDictionary()

        let address = user?.address

        guestAddressDict["addressType"] = address?.type?.rawValue
        if let companyName = address?.companyName {
            guestAddressDict["companyName"] = companyName
        }
        guestAddressDict["postalCode"] = address?.postcode
        guestAddressDict["addressLine1"] = address?.line1
        guestAddressDict["addressLine2"] = address?.line2
        guestAddressDict["addressLine3"] = address?.line3
        guestAddressDict["addressLine4"] = address?.line4
        guestAddressDict["countryCode"] = address?.country?.isoCode

        if isRegCard {
            guestAddressDict["cityName"] = address?.cityName
        }

        return guestAddressDict
    }

    static func getCreateReservationVariables(bookingDetails: BookingDetails) throws -> PIDictionary {
        var params = PIDictionary()
        let criteria: [String: Any] = [
            "reservations": try getReservationsArray(bookingDetails: bookingDetails),
            "bookingChannel": getBookingChannel(bookingDetails: bookingDetails)
        ]
        params["createReservationCriteria"] = criteria

        return params
    }

    static func releaseBookingVariables(basketReference: String, hotelId: String) -> PIDictionary {
        var params = PIDictionary()

        params["basketReference"] = basketReference
        params["hotelId"] = hotelId

        return params
    }

    private static func getReservationsArray(bookingDetails: BookingDetails) throws -> [PIDictionary] {
        guard let hotelCode = bookingDetails.hotel?.code,
              !hotelCode.isEmpty else { throw GraphQLError.missingHotelCode }

        let reservations: [PIDictionary]? = bookingDetails.roomLettings?.compactMap {
            var roomDict = PIDictionary()

            roomDict["hotelId"] = hotelCode
            roomDict["arrival"] = bookingDetails.criteria.arrivalDate.parameterString
            roomDict["departure"] = bookingDetails.criteria.checkOutDate?.parameterString
            roomDict["adultsNumber"] = $0.adults
            roomDict["childrenNumber"] = $0.children
            /// When `bookingDetails.shouldRequestCot` is true we request for cots
            /// When false, we intentionally skip requesting a cot to bypass backend
            /// validation, allowing the booking to proceed even when cots are unavailable.
            roomDict["cotRequired"] = bookingDetails.shouldRequestCot ? $0.cotRequired : false
            roomDict["roomRates"] = getRoomRates(room: $0, bookingDetails: bookingDetails)
            roomDict["reservationPackages"] = getReservationPackages(room: $0, bookingDetails: bookingDetails)

            return roomDict
        }

        return reservations ?? []
    }

    private static func getRoomRates(room: Room, bookingDetails: BookingDetails) -> PIDictionary {
        var roomDict = PIDictionary()

        let selectedRoomOption = room.options?.first(where: { $0.lettingType == room.lettingType }) ?? room.options?.first

        roomDict["startDate"] = bookingDetails.criteria.arrivalDate.parameterString
        roomDict["endDate"] = bookingDetails.criteria.checkOutDate?.parameterString
        roomDict["pmsRoomType"] = selectedRoomOption?.lettingType
        roomDict["ratePlanCode"] = bookingDetails.rate?.classification
        roomDict["specialRequests"] = selectedRoomOption?.specialRequests

        if let promotionCode = bookingDetails.rate?.promotionCode {
            roomDict["promotionCode"] = promotionCode

            if let promoKind = bookingDetails.promoKind {
                roomDict["promoKind"] = promoKind
            }
        }

        return roomDict
    }

    private static func getReservationPackages(room: Room, bookingDetails: BookingDetails) -> [PIDictionary]? {
        guard let endDate = bookingDetails.criteria.checkOutDate?.parameterString else { return nil }

        let selectedRoomOption = room.options?.first(where: { $0.lettingType == room.lettingType }) ?? room.options?.first

        guard let packageCode = selectedRoomOption?.packageCode,
              let unitPrice = selectedRoomOption?.packageAmount else { return nil }

        var dict = PIDictionary()

        dict["packageCode"] = packageCode
        dict["quantity"] = 1
        dict["unitPrice"] = unitPrice.amount.decimalValue
        dict["startDate"] = bookingDetails.criteria.arrivalDate.parameterString
        dict["endDate"] = endDate

        return [dict]
    }

    static func getBasketInformationVariables(basketReference: String) throws -> PIDictionary {
        var params = PIDictionary()
        params["basketReference"] = basketReference

        return params
    }

    static func getCategoryLabelsVariables(category: String, labels: [String]) -> [String: Any] {
        var params = [String: Any]()

        params["country"] = LanguageManager.supportedLanguage.countryCode
        params["language"] = LanguageManager.supportedLanguage.rawValue
        params["category"] = category
        params["labels"] = labels
        return params
    }

    static func getHotelPreferenceVariables(hotelCode: String) -> [String: Any] {
        var params = [String: Any]()

        params["hotelId"] = hotelCode
        params["language"] = LanguageManager.supportedLanguage.rawValue
        params["preferenceGroupsCodes"] = Constants.hotelEventsGroupCode
        return params
    }

    static func resendInvoiceEmailVariables(reservation: Reservation) -> PIDictionary {
        var params = PIDictionary()

        params["hotelId"] = reservation.hotelCode
        params["bookingReference"] = reservation.operaBasketReference
        params["invoiceRecordNumber"] = "0"
        params["bookingChannel"] = getBookingChannel(business: reservation.business)

        return ["resendInvoiceRequest": params]
    }
}
