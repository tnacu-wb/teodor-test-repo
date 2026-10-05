//
//  AmendDatesInteractor.swift
//  PremierInn
//
//  Created by Freddie Parks on 18/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation

typealias AmendStayDatesDetails = (
    criteria: Criteria,
    price: Cost,
    nightsRestriction: Bool,
    amendOperaDetails: AmendOperaDetails?,
    isBusiness: Bool,
    promotionDetails: AmendStayDatesPromotionDetails?,
    rulesToFollow: Restrictions
)
typealias ChangeDatesResponse = (available: Bool, priceDifference: Cost?)
typealias AmendStayDatesPromotionDetails = (promotionCode: String?, bannerMessage: String?)

enum AmendDatesError: AmendError {
    case bookingDetailsMissing
    case rateMissing
    case missingTemporaryReference
    case missingArrivalDate
    case missingDepartureDate
    case missingToken
    case amendDatesFailed(error: String)
    case notAllowedForPromotion(error: String)
    case getPromotionsInformationFailed
    case genericError

    var errorDescription: String? {
        switch self {
        case .notAllowedForPromotion(let error), .amendDatesFailed(let error):
            return error
        default:
            return String(describing: self)
        }
    }
}

enum AmendDatesAmendSummaryError: AmendError {
    case originalBasketReferenceMissing
    case surnameMissing
    case genericError

    var errorDescription: String? {
        PILocalizedString("somethingWentWrongMessage")
    }
}

protocol AmendDatesDataProvider {
    func amendDates(
        temporaryReference: String,
        arrivalDate: Date,
        departureDate: Date,
        token: String,
        completion: @escaping (_ temporaryBookingReference: String?, _ error: Error?) -> Void
    )
    func amendSummary(
        reservationDetails: ReservationDetails,
        tempBookingReference: String,
        completion: @escaping (_ response: AmendSummary?, _ error: Error?) -> Void
    )
    func getPromotionsInformation(
        criteria: PromotionsInformationCriteria,
        completion: @escaping (_ response: PromotionsInformation?, _ error: Error?) -> Void
    )
}

extension RequestsManager: AmendDatesDataProvider {}

protocol AmendDatesInteractorProtocol {
    var moveOnly: Bool { get }
    var existingNights: Int { get }
    var currentPriceDifference: Cost? { get }

    func duplicateOfExistingNights(nights: Int) -> Bool
    func duplicateOfExisting(date: Date, nights: Int) -> Bool
    func duplicateOfCurrentRange(date: Date, nights: Int) -> Bool
    func changeDates(to arrivalDate: Date, nights: Int, completion: @escaping (ChangeDatesResponse, Error?) -> Void)
}

struct AmendDatesConfirmViewModel: AlternateCalendarConfirmViewModel {
    let localizedPriceDifference: String
    let numberOfNights: Int
}

class AmendDatesInteractor {
    // MARK: - Properties

    private let existingStayDetails: AmendStayDatesDetails

    private(set) var nightsRestriction: Bool
    private var originalBookingDatesRange: BookingDateRange
    private var currentBookingDatesRange: BookingDateRange
    internal var currentPriceDifference: Cost?

    var amendDatesDataProvider: AmendDatesDataProvider = RequestsManager()
    var analytics: AnalyticsType = AnalyticsManager.shared

    // MARK: - Lifecycle

    deinit {
        print("DEINIT: \(self)")
    }

    init(with existingStayDetails: AmendStayDatesDetails, nightsRestriction: Bool = false) {
        self.existingStayDetails = existingStayDetails
        self.nightsRestriction = nightsRestriction
        self.originalBookingDatesRange = (existingStayDetails.criteria.arrivalDate, existingStayDetails.criteria.nights)
        self.currentBookingDatesRange = (existingStayDetails.criteria.arrivalDate, existingStayDetails.criteria.nights)
    }

    private func priceDifference(for newPrice: Float) -> Cost? {
        let newCost = Cost(
            amount: Double(newPrice),
            currencyCode: existingStayDetails.price.currencyCode,
            locale: existingStayDetails.price.locale
        )

        return newCost - existingStayDetails.price
    }
}

extension AmendDatesInteractor: AmendDatesInteractorProtocol {
    var moveOnly: Bool {
        nightsRestriction
    }

    var existingNights: Int {
        originalBookingDatesRange.nights
    }

    func duplicateOfExistingNights(nights: Int) -> Bool {
        let nightsDuplicate = originalBookingDatesRange.nights == nights

        if nightsDuplicate {
            let nightsAction = AmendTrackableAction.numberOfNightsChanged(originalBookingDatesRange.nights, nights).action
            analytics.trackAction(PIAnalytics.Action.amendChange, userInfo: [PIAnalytics.Action.amendChange: nightsAction])
        }

        return nightsDuplicate
    }

    func duplicateOfExisting(date: Date, nights: Int) -> Bool {
        let arrivalDateDuplicate = date.isOnTheSameDateAs(date: originalBookingDatesRange.arrivalDate)
        let nightsDuplicate = duplicateOfExistingNights(nights: nights)

        if arrivalDateDuplicate {
            let arrivalDateAction = AmendTrackableAction.arrivalDateChanged(date).action
            analytics.trackAction(
                PIAnalytics.Action.amendChange,
                userInfo: [PIAnalytics.Action.amendChange: arrivalDateAction]
            )
        }

        return arrivalDateDuplicate && nightsDuplicate
    }

    func duplicateOfCurrentRange(date: Date, nights: Int) -> Bool {
        let arrivalDateDuplicate = date.isOnTheSameDateAs(date: currentBookingDatesRange.arrivalDate)
        let nightsDuplicate = currentBookingDatesRange.nights == nights

        return arrivalDateDuplicate && nightsDuplicate
    }

    func changeDates(to arrivalDate: Date, nights: Int, completion: @escaping (ChangeDatesResponse, Error?) -> Void) {
        let bookingDetails = BookingDetails(numberOfNights: nights, arrivalDate: arrivalDate)

        // check if promotional booking, and if yes, getPromotionsInformation for the requested dates
        checkIfRequestedDatesAreAllowed(bookingDetails: bookingDetails) { error in
            // if not allowed, show respective error
            guard error == nil else { return completion((false, nil), error) }

            // if allowed, continue to change dates
            guard let temporaryBasketReference = self.existingStayDetails.amendOperaDetails?.temporaryBasketReference
                else { return completion(
                    (false, nil),
                    AmendDatesError.missingTemporaryReference
                ) }
            guard let departureDate = bookingDetails.criteria.checkOutDate else { return completion(
                (false, nil),
                AmendDatesError.missingDepartureDate
            ) }
            guard let token = self.existingStayDetails.amendOperaDetails?.token else { return completion(
                (false, nil),
                AmendDatesError.missingToken
            ) }

            self.amendDatesDataProvider.amendDates(
                temporaryReference: temporaryBasketReference,
                arrivalDate: bookingDetails.criteria.arrivalDate,
                departureDate: departureDate,
                token: token
            ) { responseTemporaryBasketReference, error in
                guard let responseTemporaryBasketReference = responseTemporaryBasketReference, error == nil,
                      responseTemporaryBasketReference == temporaryBasketReference else {
                    let selectedDatesNotAvailable = PILocalizedString("Your selected dates are not available")
                    let responseError = error?.localizedDescription
                    let fallbackError = responseError ?? selectedDatesNotAvailable
                    let isUnexpectedErrorMessage = (responseError ?? "") == RequestsManagerError.unexpectedResponseError
                        .localizedDescription
                    let amendDatesError = isUnexpectedErrorMessage ? selectedDatesNotAvailable : fallbackError

                    return completion((false, nil), AmendDatesError.amendDatesFailed(error: amendDatesError))
                }

                //  amendSummary call to get priceDifference
                guard let originalBookingReference = self.existingStayDetails.amendOperaDetails?.originalBasketReference
                    else { return completion(
                        (false, nil),
                        AmendDatesAmendSummaryError.originalBasketReferenceMissing
                    ) }
                guard let surname = self.existingStayDetails.amendOperaDetails?.bookerSurname else { return completion(
                    (false, nil),
                    AmendDatesAmendSummaryError.surnameMissing
                ) }

                let reservationDetails = ReservationDetails(
                    reservationId: originalBookingReference,
                    surname: surname,
                    arrivalDate: arrivalDate,
                    business: self.existingStayDetails.isBusiness == true,
                    token: token
                )

                self.amendDatesDataProvider.amendSummary(
                    reservationDetails: reservationDetails,
                    tempBookingReference: temporaryBasketReference
                ) { amendSummary, error in
                    guard let amendSummary = amendSummary, error == nil else { return completion(
                        (false, nil),
                        error ?? AmendDatesAmendSummaryError.genericError
                    ) }

                    // store the currently saved date range and price difference
                    self.currentBookingDatesRange = (arrivalDate, nights)

                    // To get the correct total cost for PIBA then we check the balance authorised is above 0.0, if so we use this value everything else will use total cost.
                    let totalCost = amendSummary.balanceAuthorised ?? 0.0 > 0.0 ? amendSummary
                        .balanceAuthorised : amendSummary.totalCost
                    self.currentPriceDifference = self.priceDifference(for: totalCost ?? 0.0)

                    completion((true, self.currentPriceDifference), nil)
                }
            }
        }
    }

    private func checkIfRequestedDatesAreAllowed(
        bookingDetails: BookingDetails,
        completion: @escaping (_ error: Error?) -> Void
    ) {
        guard let promotionCode = existingStayDetails.promotionDetails?.promotionCode else {
            completion(nil)
            return
        }

        guard let brand = existingStayDetails.amendOperaDetails?.brand,
              let arrivalDate = bookingDetails.arrivalDate,
              let endDate = bookingDetails.departureDate,
              let basketReference = existingStayDetails.amendOperaDetails?.originalBasketReference else {
            completion(nil)
            return
        }

        let criteria = PromotionsInformationCriteria(
            brand: brand,
            promotionCode: promotionCode,
            bookingDate: nil,
            stayStartDate: arrivalDate,
            stayEndDate: endDate,
            basketReference: basketReference
        )
        amendDatesDataProvider.getPromotionsInformation(criteria: criteria) { promotionsInformation, _ in
            guard let promotionsInformation else { return completion(AmendDatesError.getPromotionsInformationFailed) }
            guard promotionsInformation.isWithinPromoWindow else {
                return completion(AmendDatesError
                    .notAllowedForPromotion(error: promotionsInformation
                    .appPromoAmendMessage ?? PILocalizedString("somethingWentWrongMessage")))
            }

            completion(nil)
        }
    }
}
