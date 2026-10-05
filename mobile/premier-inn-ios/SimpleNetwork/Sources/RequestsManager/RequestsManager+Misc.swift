//
//  RequestsManager+Misc.swift
//  PremierInn
//
//  Created by Marcello Mascia on 24/11/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Foundation

public extension RequestsManager {
    func reservation(
        reservationDetails: ReservationDetails,
        hotelCode: String?,
        bookingDetails: BookingDetails?,
        completion: @escaping (_ confirmation: Reservation?, _ error: Error?) -> Void
    ) {
		do {
            let resource = try Router.current.reservation(
                reservationDetails: reservationDetails,
                hotelCode: hotelCode,
                bookingDetails: bookingDetails
            )

			load(resource: resource, completion: completion)
		} catch {
			completion(nil, error)
		}
	}

    func loadRemoteSuggestions(
        searchTerm: String,
        completion: @escaping (_ suggestions: [PISuggestion]?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.suggestions(searchTerm: searchTerm)

			load(resource: resource, timeout: 5, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func addressLookup(
        postCode: String,
        completion: @escaping (_ results: [AddressSummary]?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.addressLookup(postCode: postCode)

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func addressLookup(
        postCode: String,
        id: String,
        completion: @escaping (_ results: Address?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.addressLookup(postCode: postCode, id: id)

			load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func loadHotel(with hotelCode: String, completion: @escaping (_ data: Hotel?, _ error: Error?) -> Void) {
        do {
            let resource = try Router.current.getHotel(with: hotelCode)

			load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func getWalletPass(
        with reservationDetails: ReservationDetails,
        excludeBarcode: Bool,
        completion: @escaping (Data?, Error?) -> Void
    ) {
        do {
            let resource = try Router.current.getWalletPass(with: reservationDetails, excludeBarcode: excludeBarcode)

            loadData(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func getDoorKey(
        reservationDetails: ReservationDetails,
        deviceId: String,
        authenticationCode: String?,
        completion: @escaping (_ keyId: String?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.getDoorKey(
                reservationDetails: reservationDetails,
                deviceId: deviceId,
                authenticationCode: authenticationCode
            )

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func getCountries(completion: @escaping (_ countries: [Country]?, _ error: Error?) -> Void) {
        do {
            let resource = try Router.current.getCountries()

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func getRestrictions(completion: @escaping ([Restrictions]?, _ error: Error?) -> Void) {
        do {
            let resource = try Router.current.getRestrictions()

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func findBookingSource(
        findBookingDetails: FindBookingDetails,
        completion: @escaping (FindBookingSource?, Error?) -> Void
    ) {
        do {
            let resource = try Router.current.findBookingSource(findBookingDetails: findBookingDetails)
            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func getRatesInformation(
        ratePlans: [String],
        hotelCode: String,
        hotelBrand: HotelBrand?,
        completion: @escaping ([RateInformation]?, Error?) -> Void
    ) {
        do {
            let resource = try Router.current.ratesInformation(
                ratePlans: ratePlans,
                hotelCode: hotelCode,
                hotelBrand: hotelBrand
            )

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }
}

extension RequestsManager: DashboardRequestsProvider {
    public func getDashboardComponents(
        stay: Stay?,
        recentSearchesFlag: Bool,
        completion: @escaping (_ dashboardComponents: [DashboardComponent]?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.getDashboardComponents(
                surname: stay?.importName ?? stay?.leadGuestName,
                arrival: stay?.arrivalDate,
                reservationId: stay?.identifier,
                isBusiness: stay?.isBusinessTrip == true,
                recentSearchesFlag: recentSearchesFlag
            )
            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    public func getHomepageContent(
        channel: Channel,
        subchannel: String,
        language: String,
        country: String,
        completion: @escaping (HomepageAppsContent?, Error?) -> Void
    ) {
        do {
            let resource = try Router.current.homepageAppsContent(
                channel: channel,
                subchannel: subchannel,
                language: language,
                country: country
            )

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }
}
