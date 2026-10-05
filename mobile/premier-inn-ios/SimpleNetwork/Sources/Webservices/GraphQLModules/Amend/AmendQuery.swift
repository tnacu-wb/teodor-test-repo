//
//  AmendQuery.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 24/07/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import Foundation

extension GraphQL {
    static let copyBookingMutation =
    """
    mutation copyBooking($copyBookingCriteria: CopyBookingCriteria!) {
            copyBooking(copyBookingCriteria: $copyBookingCriteria) {
            copyBasketReference
        }
    }
    """

    static let availabilityForAmendQuery =
        "query hotelAvailability ($availabilitySearchCriteria: AvailabilitySearchCriteria!) {" +
        "\n" + availabilityQuery +
        "\n" +
        "}"

    static let availabilityForAddRoomQuery =
    """
    query hotelAvailability ($availabilitySearchCriteria: AvailabilitySearchCriteria!, $language: String!, $country: String!, $brand: String!) {
    \(availabilityQuery)
    \(roomTypeInformationQuery)
    }
    """

    static let amendBookingDatesMutation =
    """
    mutation changeBookingDates($amendStayDatesCriteria: AmendStayDatesCriteria!) {
        changeBookingDates(amendStayDatesCriteria: $amendStayDatesCriteria) {
            tempBasket
        }
    }
    """

    static let addNewRoomMutation =
    """
    mutation addNewRoom($addNewRoomCriteria: AddNewRoomCriteria!) {
        addNewRoom(addNewRoomCriteria: $addNewRoomCriteria) {
            tempBookingRef
        }
    }
    """

    static let removeRoomMutation =
    """
    mutation removeRoom($tempBookingRef: String!, $reservationId: String!, $token: String, $bookingChannel: BookingChannelCriteria!) {
        removeRoom(tempBookingRef: $tempBookingRef, reservationId: $reservationId, token: $token, bookingChannel: $bookingChannel) {
            tempBookingRef
        }
    }
    """

    static let amendPackagesMutation =
    """
    mutation updateReservationPackagesByReservation($updateReservationPackagesRequest: UpdateReservationPackagesRequest!) {
        updateReservationPackagesByReservation(updateReservationPackagesRequest: $updateReservationPackagesRequest)
    }
    """

    static let confirmAmendLogicMutation =
    """
    mutation confirmAmendLogic($confirmAmendLogicCriteria: ConfirmAmendLogicCriteria!) {
        confirmAmendLogic(confirmAmendLogicCriteria: $confirmAmendLogicCriteria) {
            payment {
                status
                paymentRequiredDetails {
                    paymentRedirect
                }
            }
        }
    }
    """

    static let amendEditRoomMutation =
    """
    mutation amendEditRoom($editRoomCriteria: EditRoomCriteria!) {
            amendEditRoom(editRoomCriteria: $editRoomCriteria) {
            tempBookingRef
        }
    }
    """

    static let amendSummaryQuery =
    """
    query amendSummary($originalBasketRef: String!, $copyBasketRef: String!, $token: String, $bookingChannel: BookingChannelCriteria!, $country: String!) {
        amendSummary(originalBasketRef: $originalBasketRef, copyBasketRef: $copyBasketRef, token: $token, bookingChannel: $bookingChannel, country: $country) {
            totalCost
            balanceAuthorised
            paymentOptions {
                payNow
                payOnArrival
            }
            paymentCardDetails {
                cardNumberMasked
                expirationDate
                cardHolderName
                cardNumberLast4Digits
                cardName
                cardLogoSrc
            }
        }
    }
    """

    static let amendConfirmationPricesQuery =
    """
    query amendConfirmationPrices($amendConfirmationPricesRequest: AmendConfirmationPricesRequest!) {
        amendConfirmationPrices(amendConfirmationPricesRequest: $amendConfirmationPricesRequest) {
            previousTotal
            newTotalCost
            outstandingBalance
        }
    }
    """

    static let bookingConfirmationWithAmendSummaryQuery =
    """
    query bookingConfirmationWithAmendSummary($basketReference: String!, $country: String!, $language: String!, $bookingChannel: String, $bookingChannelObject: BookingChannelCriteria!, $originalBasketRef: String!, $token: String) {
    \(bookingConfirmation)
    \(amendSummaryQueryComponent)
    }
    """

    static let amendSummaryQueryComponent =
    """
    amendSummary(originalBasketRef: $originalBasketRef, copyBasketRef: $basketReference, token: $token, bookingChannel: $bookingChannelObject, country: $country) {
        totalCost
        paymentOptions {
            payNow
            payOnArrival
        }
        paymentCardDetails {
            cardNumberMasked
            expirationDate
            cardHolderName
            cardNumberLast4Digits
            cardName
            cardLogoSrc
        }
    }
    """
}
