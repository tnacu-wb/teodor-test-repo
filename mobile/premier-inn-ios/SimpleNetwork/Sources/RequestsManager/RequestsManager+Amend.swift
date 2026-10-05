//
//  RequestsManager+Amend.swift
//  SimpleNetwork
//
//  Created by Georgios Aikaterinakis on 26/07/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import Foundation

public extension RequestsManager {
    // starts Amend flow by copying the booking into a temporary basket
    func copyBooking(
        reservationDetails: ReservationDetails,
        completion: @escaping (_ response: String?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.copyBooking(reservationDetails: reservationDetails)
            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func amendDates(
        temporaryReference: String,
        arrivalDate: Date,
        departureDate: Date,
        token: String,
        completion: @escaping (_ temporaryBookingReference: String?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.amendBookingDates(
                temporaryReference: temporaryReference,
                arrivalDate: arrivalDate,
                departureDate: departureDate,
                token: token
            )

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func amendPackages(
        packagesDetails: AmendPackagesDetail,
        completion: @escaping (_ response: Bool?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.amendPackages(packagesDetails: packagesDetails)
            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func amendCiolPackages(
        amendInfo: CiolAmendInfo,
        completion: @escaping (_ response: Bool?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.amendCiolPackages(amendInfo: amendInfo)
            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func confirmAmendLogic(
        reservationDetails: ReservationDetails,
        tempBookingReference: String,
        selectedPaymentOption: String,
        completion: @escaping (_ response: CCCPPaymentResponse?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.confirmAmendLogic(
                reservationDetails: reservationDetails,
                tempBookingReference: tempBookingReference,
                selectedPaymentOption: selectedPaymentOption
            )
            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func getPackages(
        reservationId: String,
        bookingDetails: BookingDetails,
        hotelCode: String,
        bookingFlowId: String?,
        showMealInclusiveRate: Bool = false,
        completion: @escaping (_ response: ([UpsellItem], [UpsellItem], CityTaxResponse, GoshPackage?)?, _ error: Error?)
        -> Void
    ) {
        do {
            let resource = try Router.current.getPackages(
                reservationId: reservationId,
                bookingDetails: bookingDetails,
                hotelCode: hotelCode,
                bookingFlowId: bookingFlowId,
                showMealInclusiveRate: showMealInclusiveRate
            )

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func amendEditRoom(
        amendEditDetails: AmendRoomCriteria,
        completion: @escaping (_ response: String?, _ error: Error?) -> Void
    ) {
        do {
            let resource: Resource<String>?

            if amendEditDetails.reservationId != nil {
                resource = try Router.current.amendEditRoom(amendEditDetails: amendEditDetails)
            } else {
                resource = try Router.current.addNewRoom(roomCriteria: amendEditDetails)
            }

            if let resource = resource {
                load(resource: resource, completion: completion)
            }
        } catch {
            completion(nil, error)
        }
    }

    func amendRemoveRoom(
        amendRemoveDetails: AmendRoomCriteria,
        completion: @escaping (_ response: String?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.removeRoom(roomCriteria: amendRemoveDetails)

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func reservationForAmend(
        reservationDetails: ReservationDetails,
        hotelCode: String?,
        completion: @escaping (_ confirmation: Reservation?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.reservationForAmend(
                reservationDetails: reservationDetails,
                hotelCode: hotelCode
            )

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func amendSummary(
        reservationDetails: ReservationDetails,
        tempBookingReference: String,
        completion: @escaping (_ response: AmendSummary?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.amendSummary(
                reservationDetails: reservationDetails,
                temporaryReference: tempBookingReference
            )

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func amendConfirmationPrices(
        tempBookingRef: String,
        originalBookingRef: String,
        token: String,
        completion: @escaping (_ response: AmendConfirmationPrices?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.amendConfirmationPrices(
                tempBookingRef: tempBookingRef,
                originalBookingRef: originalBookingRef,
                token: token
            )

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func bookingConfirmationWithAmendSummary(
        reservationDetails: ReservationDetails,
        originalBookingRef: String,
        completion: @escaping (_ response: (Reservation?, AmendSummary?)?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.bookingConfirmationWithAmendSummary(
                reservationDetails: reservationDetails,
                originalBookingRef: originalBookingRef
            )

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }
}
