//
//  RequestsManager+HotelSearch.swift
//  PremierInn
//
//  Created by Marcello Mascia on 29/12/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Alamofire
import Foundation

public typealias CityTaxResponse = (cityTaxForLeisure: Bool?, cityTaxForBusiness: Bool?)

public typealias HotelAvailabilityResponse = (
    rates: [Rate],
    notes: [Note]?,
    prepaymentAllowed: Bool,
    available: Bool,
    limitedAvailability: Bool?,
    cnpAuthorisation: CNPAuthorisation?,
    cityTaxResponse: CityTaxResponse?,
    paymentProvider: PaymentProvider?,
    roomTypeContent: [RoomTypeInformation]?,
    ratesContent: [RateInformation]?
)

public struct AvailabilitiesResponse {
    public let hotels: [Hotel]
    let total: Int
    let pageNumber: Int
    let shouldPaginate: Bool
}

public enum AvailabilitiesSorting: String {
	case distance = "DISTANCE"
	case price = "PRICE"
}

public extension RequestsManager {
    func searchAvailabilities(
        bookingDetails: BookingDetails,
        suggestion: Suggestion,
        timeout: TimeInterval = 15,
        page: Int,
        sorting: AvailabilitiesSorting = .distance,
        allowEmployeeOffer: Bool,
        completion: @escaping (_ result: AvailabilitiesResponse?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.searchAvailabilities(
                bookingDetails: bookingDetails,
                suggestion: suggestion,
                page: page,
                sorting: sorting,
                allowEmployeeOffer: allowEmployeeOffer
            )

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func hotelAvailability(
        hotelCode: String,
        hotelBrand: HotelBrand?,
        bookingDetails: BookingDetails,
        allowEmployeeOffer: Bool,
        completion: @escaping (_ result: HotelAvailabilityResponse?, _ error: Error?) -> Void
    ) {
        func doAvailability() {
			do {
                let resource = try Router.current.hotelAvailability(
                    hotelCode: hotelCode,
                    hotelBrand: hotelBrand,
                    bookingDetails: bookingDetails,
                    allowEmployeeOffer: allowEmployeeOffer
                )

                load(resource: resource, completion: completion)
            } catch {
                completion(nil, error)
            }
        }

        guard let basketReference = bookingDetails.basketReference, bookingDetails.isBookingHold == true else {
            doAvailability()
            return
        }

        releaseBooking(basketReference: basketReference, hotelId: hotelCode) { _, _ in
            bookingDetails.bookingReleased()
            doAvailability()
        }
    }

    func hotelAvailabilityForAmendBooking(
        withHotelCode hotelCode: String,
        bookingDetails: BookingDetails,
        brand: HotelBrand?,
        allowEmployeeOffer: Bool,
        completion: @escaping (_ result: HotelAvailabilityResponse?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.hotelAvailabilityForAmendBooking(
                withHotelCode: hotelCode,
                bookingDetails: bookingDetails,
                brand: brand,
                allowEmployeeOffer: allowEmployeeOffer
            )

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func getHotelBySlug(slug: String, completion: @escaping (_ response: Hotel?, _ error: Error?) -> Void) {
        do {
            let resource = try Router.current.getHotelBySlug(slug: slug)
            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }
}
