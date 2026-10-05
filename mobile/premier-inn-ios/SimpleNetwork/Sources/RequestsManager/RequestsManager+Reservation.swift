//
//  RequestsManager+Reservation.swift
//  PremierInn
//
//  Created by Marcello Mascia on 07/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation
import Alamofire
import UIKit

public typealias CheckInOnlineSessionResponse = (
    sessionID: String,
    upsellItemsAvailable: [UpsellItem],
    paymentAllowed: Bool,
    paymentRequired: Bool,
    outstandingPayment: Cost,
    cancelableText: String?
)

public typealias AddCheckInOnlineGuestDetailsParameters = (
    sessionID: String,
    confirmationNumber: String,
    asBusinessTrip: Bool,
    booker: User,
    roomsAndNextDestinations: [RoomAndNextDestinationModel]
)

public typealias FindBookingDetails = (reservationId: String, surname: String, arrivalDate: Date, business: Bool)

public extension RequestsManager {
    func cancelReservation(
        reservationDetails: ReservationDetails,
        hotelCode: String?,
        completion: @escaping (_ confirmation: PIDictionary?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.cancelReservation(reservationDetails: reservationDetails, hotelCode: hotelCode)

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func startCheckInOnlineSession(
        with params: StartCheckInRequestParameters,
        completion: @escaping (_ response: CheckInOnlineSessionResponse?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.startCheckInOnlineSession(with: params)

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func addCheckInOnlineGuestDetails(
        with params: AddCheckInOnlineGuestDetailsParameters,
        completion: @escaping ( _ success: Bool?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.addCheckInOnlineGuestDetails(with: params)

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func addCheckInOnlineUpsells(
        withSessionID sessionID: String,
        confirmationNumber: String,
        andUpsells upsells: [UpsellItem],
        completion: @escaping (_ response: CheckInOnlineAddUpsellsResponse?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.addCheckInOnlineUpsells(
                withSessionID: sessionID,
                confirmationNumber: confirmationNumber,
                andUpsells: upsells
            )
            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func closeCheckInOnlineSession(
        withSessionID sessionID: String,
        completion: @escaping ( _ success: Bool?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.closeCheckInOnlineSession(withSessionID: sessionID)

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func checkInOnlinePayment(
        with sessionId: String,
        confirmationNumber: String,
        paymentDetails: PaymentDetails,
        completion: @escaping (_ response: CheckInPaymentResponse?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.checkInOnlinePayment(
                with: sessionId,
                confirmationNumber: confirmationNumber,
                paymentDetails: paymentDetails
            )
            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func bookingInformation(
        with basketReference: String,
        isBusiness: Bool,
        completion: @escaping (_ response: BookingInformation?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.bookingInformation(basketReference: basketReference, isBusiness: isBusiness)
            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func updateReservationPreferences(
        hotelCode: String,
        reservationIds: [String],
        preferencesCollections: [PreferencesCollection],
        completion: @escaping (_ confirmation: PIDictionary?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.updateReservationPreferences(
                hotelCode: hotelCode,
                reservationIds: reservationIds,
                preferencesCollections: preferencesCollections
            )

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func confirmPreCheckInOut(
        basketReference: String,
        type: CiolRequestType,
        isCiol: Bool = false,
        completion: @escaping (_ response: ConfirmPreCheckInOut?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.confirmPreCheckInOut(
                basketReference: basketReference,
                type: type,
                isCiol: isCiol
            )
            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }
}
