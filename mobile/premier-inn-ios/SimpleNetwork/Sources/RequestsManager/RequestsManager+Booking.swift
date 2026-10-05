//
//  RequestsManager+Booking.swift
//  PremierInn
//
//  Created by Marcello Mascia on 29/11/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Alamofire
import Foundation

public enum BookingError: Error {
	case missingSessionIdentifier
	case unexpectedSessionIdentifier
	case missingRate
	case missingHotel
	case missingRateRooms
	case missingBooker
	case missingBookerEmail
	case missingBookerContactNumber
	case missingPaymentCard
	case missingLeadGuest
	case missingBookerAddress
	case missingCardAddress
    case missingHotelCode
}

public extension RequestsManager {
    func holdBooking(
        bookingDetails: BookingDetails,
        sensorData: String,
        completion: @escaping (_ sessionIdentifier: String?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.holdBooking(bookingDetails: bookingDetails, sensorData: sensorData)

			load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func getTotalCostWithCityTax(
        bookingDetails: BookingDetails,
        completion: @escaping (_ totalCost: Cost?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.getTotalCostWithCityTax(bookingDetails: bookingDetails)

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func holdBookingWithGuests(
        bookingDetails: BookingDetails,
        isCiolFlow: Bool,
        isRegCard: Bool,
        completion: @escaping (_ success: Bool, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.holdBookingWithGuests(
                bookingDetails: bookingDetails,
                isCiolFlow: isCiolFlow,
                isRegCard: isRegCard
            )

            load(resource: resource) { result, error in
                completion(result ?? false, error)
            }
        } catch {
            completion(false, error)
        }
    }

    func getHotelPreferences(
        hotelCode: String,
        completion: @escaping (_ hotelPreference: [HotelPreference]?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.getHotelPreferences(hotelCode: hotelCode)

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func releaseBooking(basketReference: String?, hotelId: String?, completion: @escaping (Bool, Error?) -> Void) {
        do {
            guard let basketReference = basketReference else { throw BookingError.missingSessionIdentifier }

            let resource = try Router.current.releaseBooking(basketReference: basketReference, hotelId: hotelId)

            load(resource: resource) { result, error in
                completion(result ?? false, error)
            }
        } catch {
            completion(false, error)
        }
    }

    func paymentMethods(
        bookingDetails: BookingDetails,
        hotel: Hotel,
        rate: Rate,
        user: User?,
        isCiol: Bool = false,
        completion: @escaping (PaymentMethodsResponse?, Error?) -> Void
    ) {
        do {
            let resource = try Router.current.paymentMethods(
                for: bookingDetails,
                hotel: hotel,
                rate: rate,
                user: user,
                isCiol: isCiol
            )

            load(resource: resource) { result, error in
                completion(result, error)
            }
        } catch {
            completion(nil, error)
        }
    }

    func checkBasketStatus(
        basketReference: String?,
        completion: @escaping (_ status: BookingConfirmation?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.checkBasketStatus(basketReference: basketReference)

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func headerInformation(completion: @escaping (_ headerInformation: HeaderInformation?, _ error: Error?) -> Void) {
        do {
            let resource = try Router.current.headerInformation()

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func saveUpsellsToBooking(bookingDetails: BookingDetails, completion: @escaping (Bool, Error?) -> Void) {
        do {
            let resource = try Router.current.saveUpsellsToBooking(bookingDetails: bookingDetails)

            load(resource: resource) { result, error in
                completion(result ?? false, error)
            }
        } catch {
            completion(false, error)
        }
    }

    func getCategoryLabels(labelType: LabelsConfig, completion: @escaping (Result<CategoryLabels>) -> Void) {
        do {
            let resource = try Router.current.getCategoryLabels(labelType: labelType)
            load(resource: resource) { response, error in
                guard let error else {
                    if let response {
                        completion(.success(result: response))
                    } else {
                        completion(.failure(error: RequestsManagerError.unexpectedResponseError))
                    }
                    return
                }
                completion(.failure(error: error))
            }
        } catch {
            completion(.failure(error: error))
        }
    }

    func resendInvoiceEmail(reservation: Reservation, completion: @escaping (Bool) -> Void) {
        do {
            let resource = try Router.current.resendInvoiceEmail(reservation: reservation)

            load(resource: resource) { result, _ in
                completion(result ?? false)
            }
        } catch {
            completion(false)
        }
    }
}
