//
//  BookingFlowQuery.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 14/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

extension GraphQL {
    static let createReservationGuestMutation =
    """
    mutation saveGuestDetails($createReservationGuestCriteria: ReservationGuestCriteria!){
        createReservationGuest(createReservationGuestCriteria: $createReservationGuestCriteria) {
            basketReference
        }
    }
    """

    static let saveUpsellsToBookingMutation =
    """
    mutation saveUpsellDetails($ancillariesCriteria: AncillariesCriteria!) {
        saveReservation(ancillariesCriteria: $ancillariesCriteria)
    }
    """

    static let basketConfirmationMutation =
    """
    mutation basketConfirmation($basketReference: String!) {
        basketConfirmation(basketReference: $basketReference) {
            reference,
            basketUri
        }
    }
    """

    static let createReservationMutation =
    """
    mutation holdBooking($createReservationCriteria: CreateReservationCriteria!) {
        createReservation(createReservationCriteria: $createReservationCriteria) {
            basketReference
        }
    }
    """

    static let releaseBookingMutation =
    """
    mutation releaseBooking($basketReference: String!, $hotelId: String!) {
        cancelOnHoldReservation(basketReference: $basketReference, hotelId: $hotelId) {
            basketReference
        }
    }
    """

    static let bookingConfirmationForTotalCostWithCityTax =
    """
    query bookingConfirmationTotalCost($basketReference: String!, $country: String!, $language: String!, $bookingChannel: String) {
        bookingConfirmation(basketReference: $basketReference, country: $country, language: $language, bookingChannel: $bookingChannel) {
            totalCost,
            currencyCode
            bookingReference
        }
    }
    """

    static let basketStatusQuery =
    """
    query basketStatus($basketReference: String!) {
        basketStatus(basketReference: $basketReference) {
            basketStatus
            basketReference
            basketError {
              code
              description
              type
            }
        }
    }
    """
}
