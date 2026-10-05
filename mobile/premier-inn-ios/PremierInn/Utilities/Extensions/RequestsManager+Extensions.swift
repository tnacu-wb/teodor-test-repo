//
//  RequestsManager+Extensions.swift
//  PremierInn
//
//  Created by Marcello Mascia on 29/08/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation
import UIKit

enum HotelAvailabilityError: Error {
    case missingHotelCode
    case hotelNotFound
}

enum MigrationServiceError: Error {
    case fallbackToBePresented
    case bbNotAvailableBannerToBePresented
}

extension RequestsManager {
    func loadInfoForHotel(
        withCode hotelCode: String?,
        hotelBrand: HotelBrand?,
        existingAvailability: HotelAvailabilityResponse?,
        andShouldCheckAvailability shouldCheckAvailability: Bool = true,
        datesChangedByUserInHdp: Bool = false,
        discountCodeViewModel: HotelDetailsDiscountCodeViewModel? = nil,
        completion: @escaping (Result<Hotel>) -> Void
    ) {
        guard let hotelCode = hotelCode else {
            completion(.failure(error: HotelAvailabilityError.missingHotelCode))
            return
        }

        var fetchedHotel: Hotel?
        var fetchedAvailability: HotelAvailabilityResponse? = existingAvailability
        var fetchedRatesContent: [RateInformation]?
        var fetchedError: Error?

        let dispatchGroup = DispatchGroup()

        dispatchGroup.enter()
        loadHotel(with: hotelCode) { hotel, error in
            fetchedError = error
            fetchedHotel = hotel

            dispatchGroup.leave()
        }

        dispatchGroup.enter()

        if shouldCheckAvailability &&
           !(BookingDetails.sharedInstance.bookingMode == .business && SettingsManager.sharedInstance
            .shouldOperaShowFallBackForBB) {
            if BookingDetails.sharedInstance.criteria.arrivalDate.isInThePast {
                BookingDetails.sharedInstance.criteria.arrivalDate = Date()
            }

            dispatchGroup.enter()
            self.hotelAvailability(
                hotelCode: hotelCode,
                hotelBrand: hotelBrand,
                bookingDetails: BookingDetails.sharedInstance,
                datesChangedByUserInHdp: datesChangedByUserInHdp,
                discountCodeViewModel: discountCodeViewModel
            ) { response, error in
                fetchedError = error
                fetchedAvailability = response

                // extra ratesInformation call only for rates whose content we don't have
                if let ratesContent = response?.ratesContent,
                   let ratePlans = response?.rates.ratesWithoutContent(ratesContent: ratesContent)
                   .compactMap({ $0.classification }), ratePlans.isNotEmpty {
                    dispatchGroup.enter()
                    self
                        .getRatesInformation(
                            ratePlans: ratePlans,
                            hotelCode: hotelCode,
                            hotelBrand: hotelBrand
                        ) { ratesInfoResponse, error in
                        fetchedError = error
                        fetchedRatesContent = ratesInfoResponse

                        dispatchGroup.leave()
                    }
                }

                dispatchGroup.leave()
            }
        }

        dispatchGroup.leave()

        dispatchGroup.notify(queue: .main) {
            if let fetchedError = fetchedError {
                return completion(.failure(error: fetchedError))
            }

            BookingDetails.sharedInstance.operaConfirmedTotalCost = nil

            if let hotel = fetchedHotel {
                hotel.update(with: fetchedAvailability, ratesContent: fetchedRatesContent)

                completion(.success(result: hotel))
            } else {
                completion(.failure(error: HotelAvailabilityError.hotelNotFound))
            }
        }
    }

    func refreshStays(for user: User?, shouldAttemptLogin: Bool, completion: @escaping (Bool?) -> Void) {
        guard user?.emailAddress != nil else {
            RequestsManager.removeStays(accountOnly: true)
            return
        }

        getStays { summaries, error in
            guard let summaries = summaries, error == nil else {
                NotificationCenter.default.post(name: .staysDidChange, object: nil)
                return completion(false)
            }

            let reservationsManager = SimpleStorageManager<Stay>(dataSource: UserDefaults.standard)
            let untrackedSummaries = summaries
                .filter { summary in reservationsManager.items.first(where: { $0 == summary }) == nil }

            let trackedSummaries = summaries.difference(from: untrackedSummaries)

            // Update the digital key identifier for untracked stays from local storage
            for untrackedSummary in untrackedSummaries {
                self.updateDigitalKeyIdentifier(for: untrackedSummary)
            }
            _ = reservationsManager.update(with: untrackedSummaries)

            // We should update arrival date incase it was amended on another platform - this is required to make the GET reservation request.
            var updatedStays: [Stay] = []
            for trackedSummary in trackedSummaries {
                guard let arrivalDate = trackedSummary.arrivalDate else { continue }
                guard let stay = reservationsManager.items.first(where: { $0 == trackedSummary }) else { continue }

                // We only update arrival date and not cancelled/checkedIn etc because we have seen these to be incorrect in the stays request in the past. Also overwrite the import type as account
                stay.importType = ReservationImportType.account.rawValue
                stay.basketStatus = trackedSummary.basketStatus
                stay.arrivalDateString = arrivalDate.parameterString
                stay.isCheckInOnlineAvailable = trackedSummary.isCheckInOnlineAvailable
                stay.cancelled = trackedSummary.cancelled
                // Update the digital key identifier for untracked stays from local storage
                self.updateDigitalKeyIdentifier(for: trackedSummary)
                updatedStays.append(stay)
            }
            _ = reservationsManager.update(with: updatedStays)

            // don't getReservation for all untrackedReservations

            NotificationCenter.default.post(name: .staysDidChange, object: nil)
            ReservationsListViewController.shareDataWithTodayWidget()
        }
    }

    func updateDigitalKeyIdentifier(for stay: Stay) {
        if let localIdentifier = LocalReservationManager.shared
            .getDigitalKeyIdentifierForReservation(stay.identifier) {
            stay.digitalKeyIdentifier = localIdentifier
        }
    }

    public static func removeStays(accountOnly: Bool = false) {
        let reservationsManager = SimpleStorageManager<Stay>(dataSource: UserDefaults.standard)
        let reservationSummaries = reservationsManager.items

        // Store the digital key identifiers locally before removing all the local stays
        let staysWithKeyIdentifier = reservationSummaries
            .filter { $0.digitalKeyIdentifier?.isNotEmpty ?? false }
            .compactMap { StayWithKeyIdentifier(stay: $0) }

        LocalReservationManager.shared.updateDigitalKeyIdentifiers(with: staysWithKeyIdentifier)

        for stay in reservationSummaries
            where accountOnly == false || stay.importType == ReservationImportType.account.rawValue {
            _ = reservationsManager.remove(stay)
        }

        NotificationCenter.default.post(name: .reservationSummariesDidChange, object: nil)
        NotificationCenter.default.post(name: .staysDidChange, object: nil)

        ReservationsListViewController.shareDataWithTodayWidget()
    }

    func autoLogin(completion: @escaping (User?) -> Void) {
        let savedBusiness = UserDefaults.standard.bool(forKey: .storedBusinessKey)
        let usernameKey: String = { savedBusiness ? .storedBusinessUsernameKey : .storedUsernameKey }()
        guard let emailAddress = UserDefaults.standard.string(forKey: usernameKey) else { return }
        guard let credentials = User.storedCredentials(for: emailAddress, business: savedBusiness) else { return }

        login(
            withUsername: credentials.username,
            password: credentials.password,
            isBusiness: credentials.business
        ) { result in
            switch result {
            case .success:

                self.getUser(userId: emailAddress, isBusiness: credentials.business) { result in
                    DispatchQueue.main.async {
                        switch result.handle() {
                        case .sessionExpired?:
                            print("Session expired nontheless")
                        case .generic(let error)?:
                            print("Autologin failed: " + error.localizedDescription)
                        case .none:
                            break
                        }

                        completion(UserSessionManager.sharedInstance.currentUser)
                    }
                }

            case .failure:
                AnalyticsManager.shared.track(errorName: PIAnalytics.Error.autoLoginError)
                completion(nil)
            }
        }
    }

    // swiftlint:disable:next function_body_length
    public func loadResultsWith(
        suggestion: Suggestion,
        sorting: AvailabilitiesSorting = .distance,
        completion: @escaping (
            _ availabilitiesResponse: AvailabilitiesResponse?,
            _ hotelAvailability: HotelAvailabilityResponse?,
            _ error: Error?,
            _ unavailableHotelCode: String?
        ) -> Void
    ) {
        func searchAvailabilities(unavailableHotelCode: String?) {
            // wait for both calls to finish
            let dispatchGroup = DispatchGroup()

            dispatchGroup.enter()

            let brand: HotelBrand = {
                if let brand = suggestion.brand { return brand }

                let isSuggestionInGermany = suggestion.title?.lowercased()
                    .contains(Constants.germanyTextInSuggestion) == true

                return isSuggestionInGermany ? .premierInnGermany : .premierInn
            }()

            let criteria = PromotionsInformationCriteria(
                brand: brand,
                promotionCode: "",
                bookingDate: nil,
                stayStartDate: BookingDetails.sharedInstance.criteria.arrivalDate,
                stayEndDate: BookingDetails.sharedInstance.criteria.checkOutDate ?? Date(),
                basketReference: nil
            )
            self.getPromotionsInformation(criteria: criteria) { promotionsInformation, _ in
				SettingsManager.sharedInstance.siteWidePromotionContent = promotionsInformation?
				    .showPromo == true ? SiteWidePromotionContent(
				        title: promotionsInformation?.appPromoBannerTitle,
				        subtitle: promotionsInformation?.appPromoBannerSubtitle,
				        promotionCode: promotionsInformation?.promotionCode,
				        urlString: promotionsInformation?.termsLink
				    ) : nil

                dispatchGroup.leave()
            }

            dispatchGroup.enter()
            self.searchAvailabilities(
                bookingDetails: BookingDetails.sharedInstance,
                suggestion: suggestion,
                page: 1,
                sorting: sorting,
                allowEmployeeOffer: SettingsManager.sharedInstance.allowEmployeeOfferFeature == true
            ) { response, error in
                AnalyticsManager.shared.log(
                    event: FirebaseAnalytics.Event.graphQLAvailabilities,
                    parameters: ["success": response != nil]
                )

                dispatchGroup.leave()

                dispatchGroup.notify(queue: .main) {
                    handleAvailabilitiesResponse(
                        unavailableHotelCode: unavailableHotelCode,
                        response: response,
                        error: error,
                        completion: completion
                    )
                }
            }
        }

        func handleAvailabilitiesResponse(
            unavailableHotelCode: String?,
            response: AvailabilitiesResponse?,
            error: Error?,
            completion: @escaping (
                _ availabilitiesResponse: AvailabilitiesResponse?,
                _ hotelAvailability: HotelAvailabilityResponse?,
                _ error: Error?,
                _ unavailableHotelCode: String?
            ) -> Void
        ) {
            if let error = error {
                AnalyticsManager.shared.track(error: error, name: PIAnalytics.Error.remoteAvailabilitiesLoadError)
            }

            if let response = response {
                completion(response, nil, nil, unavailableHotelCode)
            } else {
                completion(nil, nil, error, nil)
            }
        }

        func searchAvailability(hotelCode: String, hotelBrand: HotelBrand) {
            self.hotelAvailability(
                hotelCode: hotelCode,
                hotelBrand: hotelBrand,
                bookingDetails: BookingDetails.sharedInstance
            ) { result, error in
                if let error = error {
                    AnalyticsManager.shared.track(error: error, name: PIAnalytics.Error.remoteAvailabilityLoadError)
                }

                if BookingDetails.sharedInstance.bookingMode == .business && SettingsManager.sharedInstance
                   .shouldOperaShowFallBackForBB {
                    completion(nil, nil, MigrationServiceError.bbNotAvailableBannerToBePresented, nil)
                    return
                }

                if let result = result, result.rates.isNotEmpty {
                    completion(nil, result, nil, nil)
                } else {
                    searchAvailabilities(unavailableHotelCode: hotelCode)
                }
            }
        }

        if suggestion.isHotel, let hotelCode = suggestion.identifier, let hotelBrand = suggestion.brand {
            if SettingsManager.sharedInstance.shouldOperaRedirectToWeb {
                completion(nil, nil, MigrationServiceError.fallbackToBePresented, nil)
                return
            } else {
                searchAvailability(hotelCode: hotelCode, hotelBrand: hotelBrand)
            }
        } else {
            searchAvailabilities(unavailableHotelCode: nil)
        }
    }

    func performSearch(
        with suggestion: Suggestion,
        criteria: Criteria,
        completion: @escaping (_ resultController: UIViewController?, _ error: Error?) -> Void
    ) {
        func getControllerFor(
            manyAvailabilitiesResponse: AvailabilitiesResponse?,
            singleAvailabilityResponse: HotelAvailabilityResponse?,
            location: Suggestion,
            unavailableHotelCode: String?,
            error: Error?
        ) -> UIViewController? {
            if let response = manyAvailabilitiesResponse {
                return MapListContainerRouter.buildMapListContainerView(
                    with: response,
                    unavailableHotelCode: unavailableHotelCode,
                    suggestion: location
                )
            } else if let response = singleAvailabilityResponse, let hotelCode = location.identifier {
                // If Opera and BB then do some change here for the exisitingAvailability and bookingAllowed here to show the new UI for BB
                let controller = HotelDetailsModule.build(
                    withCode: hotelCode,
                    hotelBrand: suggestion.brand,
                    existingAvailability: response,
                    bookingAllowed: true,
                    andSuggestion: suggestion
                )

                return controller
            } else if let error = error as? MigrationServiceError, error == .bbNotAvailableBannerToBePresented,
                      let hotelCode = location.identifier {
                let controller = HotelDetailsModule.build(
                    withCode: hotelCode,
                    hotelBrand: suggestion.brand,
                    existingAvailability: nil,
                    bookingAllowed: false,
                    andSuggestion: suggestion
                )

                return controller
            } else if let error = error as? MigrationServiceError, error == .fallbackToBePresented {
                let alertController = AlertManager.fallbackToWebsitePopup()

                return alertController
            }

            return nil
        }

        BookingDetails.sharedInstance.criteria = criteria

        switch suggestion.type {
        case .location:
            guard suggestion.coordinate.isValid else {
                guard let title = suggestion.title else { return }

                loadRemoteSuggestions(searchTerm: title) { (suggestions, _) in
                    guard let suggestion = suggestions?.first else { return }
                    guard suggestion.coordinate.isValid else { return }

                    self.performSearch(with: suggestion, criteria: criteria, completion: completion)
                }
                return
            }

            loadResultsWith(
                suggestion: suggestion
            ) { manyAvailabilitiesResponse, singleAvailabilityResponse, error, unavailableHotelCode  in
                guard let newController = getControllerFor(
                    manyAvailabilitiesResponse: manyAvailabilitiesResponse,
                    singleAvailabilityResponse: singleAvailabilityResponse,
                    location: suggestion,
                    unavailableHotelCode: unavailableHotelCode,
                    error: error
                ) else {
                    return completion(nil, error)
                }

                completion(newController, error)
            }

        case .place:
            loadResultsWith(
                suggestion: suggestion
            ) { manyAvailabilitiesResponse, singleAvailabilityResponse, error, unavailableHotelCode  in
                guard let newController = getControllerFor(
                    manyAvailabilitiesResponse: manyAvailabilitiesResponse,
                    singleAvailabilityResponse: singleAvailabilityResponse,
                    location: suggestion,
                    unavailableHotelCode: unavailableHotelCode,
                    error: error
                ) else {
                    return completion(nil, error)
                }

                completion(newController, error)
            }
        }
    }
}
