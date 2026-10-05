//
//  AmendReviewInteractor.swift
//  PremierInn
//
//  Created by Freddie Parks on 23/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation

enum AmendReviewUpsellsError: AmendError {
    case missingTemporaryReference
    case missingArrivalDate
    case missingDepartureDate
    case amendPackagesFailed
    case genericError
}

enum AmendReviewError: AmendError {
    case missingTemporaryReference
    case missingArrivalDate
    case missingDepartureDate
    case missingBookingReference
    case missingToken
    case genericError
    case missingAmendPNDetails
    case checkBasketStatusError
    case confirmAmendLogicFailed
}

enum AmendBookingError: AmendError {
    case missingReservation
    case missingResponse
    case missingPendingAmendId
}

struct AmendUpdateModel {
    let hotelImage: URL?
    let hotelName: String?
    let hotelBrand: HotelBrand?
    var updates: [AmendmentDetailsViewModel]
    var reservation: Reservation
    var rate: Rate
    var criteria: Criteria
    var breakfasts: [UpsellItem]
    var hotelCode: String
    var newTotalCost: Cost
    var isStayingForBusiness: Bool
    var showECILCORemovalMessage: Bool
}

protocol AmendReviewInteractorProtocol {
    var amendReviewViewModel: AmendReviewViewViewModel { get }
    var paymentRequired: Bool { get set }
    var amendOperaDetails: AmendOperaDetails? { get }

    func confirmChanges() throws
    func amendBooking(completion: @escaping (Result<Bool>) -> Void)
    func checkBasketStatus(completion: @escaping (Result<BookingConfirmation>) -> Void)
    func trackBookingConfirmation()
    func updateAmendedStay(bookingReference: String)
}

protocol AmendReviewInteractorOutput: AnyObject {
    func amendPayOnArrival()
    func goToAmendAndPayNowView(
        amendUpdateModel: AmendUpdateModel,
        amendAndPayViewModel: AmendAndPayViewModel,
        amendOperaDetails: AmendOperaDetails,
        reservationDetails: ReservationDetails,
        existingStay: Stay
    )
}

protocol AmendReviewDataProvider {
    func confirmAmendLogic(
        reservationDetails: ReservationDetails,
        tempBookingReference: String,
        selectedPaymentOption: String,
        completion: @escaping (_ response: CCCPPaymentResponse?, _ error: Error?) -> Void
    )
    func refreshStays(for user: User?, shouldAttemptLogin: Bool, completion: @escaping (Bool?) -> Void)
    func reservation(
        reservationDetails: ReservationDetails,
        hotelCode: String?,
        bookingDetails: BookingDetails?,
        completion: @escaping (Reservation?, Error?) -> Void
    )
    func checkBasketStatus(
        basketReference: String?,
        completion: @escaping (_ status: BookingConfirmation?, _ error: Error?) -> Void
    )
}

extension RequestsManager: AmendReviewDataProvider {}

class AmendReviewInteractor {
    private struct AmendInteractorReviewViewModel: AmendReviewViewViewModel {
        let hotelImageUrl: URL?
        let hotelName: String?
        let previousTotal: String
        let amendments: [AmendmentDetailsViewModel]
        let newTotal: String
        var newDatesDescription: String
        var guestsAndRoomsDescription: String
        let outstandingBalance: String
        let outstandingBalanceActionRequiredText: String
        let amendPaymentViewModel: AmendPaymentIntervalViewModel?
        var showECILCORemovalMessage: Bool
        let tripSummary: AmendReviewTripSummary
    }

    // MARK: - Properties

    private var existingStay: Stay
    private var updateModel: AmendUpdateModel
    private var analyticsUpdates: [AmendmentDetailsViewModel]
    var amendOperaDetails: AmendOperaDetails?

    private var hotelRequiresAuthentication: Bool?
    private var pendingAmendId: String?

    var amendReviewDataProvider: AmendReviewDataProvider = RequestsManager()

    var analyticsProperties: PIDictionary {
        var dict = AnalyticsManager.shared.analyticsProperties(stateType: PIAnalytics.StateTypes.myBookings)
        dict[PIAnalytics.Keys.amendBookingId] = existingStay.identifier

        return dict
    }

    var analytics: AnalyticsType = AnalyticsManager.shared
    weak var presenter: AmendReviewInteractorOutput?

    // MARK: - Lifecycle

    deinit {
        print("DEINIT: \(self)")
    }

    init(
        existingStay: Stay,
        updateModel: AmendUpdateModel,
        hotel: Hotel?,
        analyticsUpdates: [AmendmentDetailsViewModel],
        amendOperaDetails: AmendOperaDetails?
    ) {
        self.existingStay = existingStay
        self.updateModel = updateModel
        self.analyticsUpdates = analyticsUpdates
        self.amendOperaDetails = amendOperaDetails
    }

    private func priceChangeString(for cost: Cost?) -> String {
        guard let cost = cost else {
            let currencySymbol = NSLocale.current.currencySymbol ?? "£"
            return "\(currencySymbol)0.00"
        }

        return cost.amount.doubleValue < 0 ? cost.localizedValue : "+\(cost.localizedValue)"
    }

    func updateAmendedStay(bookingReference: String) {
        let reservationsManager = SimpleStorageManager<Stay>(dataSource: UserDefaults.standard)

        if let stay = reservationsManager.items.first(where: { $0.identifier == bookingReference }) {
            guard let checkoutDateString = updateModel.criteria.checkOutDate?.parameterString else { return }

            stay.numberOfRoooms = updateModel.criteria.rooms.count
            stay.arrivalDateString = updateModel.criteria.arrivalDate.parameterString
            stay.checkOutDateString = checkoutDateString

            _ = reservationsManager.update(with: [stay])
        }

        guard let currentUser = UserSessionManager.sharedInstance.currentUser else { return }

        amendReviewDataProvider.refreshStays(for: currentUser, shouldAttemptLogin: false) { _ in }
    }

    private func trackCompletion() {
        var data = analyticsProperties

        data[PIAnalytics.Keys.amendConfFoodRevenueChange] = foodRevenueChange.stringForAnalyticsCost
        data[PIAnalytics.Keys.amendConfRoomTypesDidChange] = roomTypesDidChange
        data[PIAnalytics.Keys.amendConfRoomRevenueChange] = roomsRevenueChange.stringForAnalyticsCost
        data[PIAnalytics.Keys.amendConfExtrasRevenueChange] = extrasRevenueChange.stringForAnalyticsCost
        data[PIAnalytics.Keys.amendConfRoomsAmountChange] = updateModel.criteria.rooms.count - existingStay.numberOfRoooms
        data[PIAnalytics.Keys.amendConfNightsChange] = updateModel.criteria.nights - existingStay.numberOfNights
        data[PIAnalytics.Keys.amendConfTotalRevenueChange] = (foodRevenueChange + roomsRevenueChange + extrasRevenueChange)
            .stringForAnalyticsCost
        data[PIAnalytics.Keys.amendConfChanges] = confChanges
        data[PIAnalytics.Keys.amendConfTotalRevenueOriginal] = (existingStay.totalCost?.amount.doubleValue ?? 0.0)
            .stringForAnalyticsCost
        data[PIAnalytics.Keys.amendConfTotalRevenue] = updateModel.newTotalCost.amount.doubleValue.stringForAnalyticsCost

        let stateName = PIAnalytics.StateNames.amendConfirmation
        analytics.trackState(stateName, data: data)
    }

    private var confChanges: String {
        (analyticsUpdates.map {
            var strings = [String]()
            strings.append($0.title.string.replacingOccurrences(of: ",", with: " "))
            if let description = $0.description { strings.append(description.replacingOccurrences(of: ",", with: " ")) }

            return strings.joined(separator: " ")
        }).joined(separator: ",").replacingOccurrences(of: "\n", with: " ")
    }

    private var foodRevenueChange: Double {
        guard let existingFoodTotal = updateModel.reservation.mealTotalCost else { return 0 }

        let newFoodTotal = updateModel.criteria.rooms.foodUpsellsCostAmount(
            for: updateModel.breakfasts,
            availableUpsells: updateModel.reservation.availableUpsells,
            nights: updateModel.criteria.nights
        )

        return NSDecimalNumber(value: newFoodTotal).subtracting(existingFoodTotal.amount).doubleValue
    }

    private var extrasRevenueChange: Double {
        let existingExtrasTotal = updateModel.reservation.rooms.nonFoodUpsellsCostAmount(
            for: updateModel.reservation.wifiUpsells,
            nights: 1
        )
        let newExtrasTotal = updateModel.criteria.rooms.nonFoodUpsellsCostAmount(
            for: updateModel.breakfasts,
            nights: updateModel.criteria.nights
        )

        return newExtrasTotal - existingExtrasTotal
    }

    private func mealsPriceForRoom(upsellItem: UpsellItem) -> Double {
        guard let roomId = upsellItem.roomId,
              let room = updateModel.criteria.rooms.first(where: { $0.roomId == roomId })
        else { return upsellItem.price.amount.doubleValue }

        let guestsCount = room.adults + (upsellItem.kidsHaveToPay ?? true ? room.children : 0)

        return upsellItem.price.amount.multiplying(by: NSDecimalNumber(value: guestsCount)).doubleValue
    }

    private var roomsRevenueChange: Double {
        guard let existingRoomTotal = updateModel.reservation.rooms.totalCostV1 else { return 0 }
        guard let updatedTotal = updateModel.rate.rooms?.totalCostV1 else { return 0 }

        return updatedTotal.amount.subtracting(existingRoomTotal.amount).doubleValue
    }

    private var roomTypesDidChange: Bool {
        for room in updateModel.reservation.rooms {
            if let updatedRoom = updateModel.criteria.rooms.first(where: { $0.roomId == room.roomId }) {
                if updatedRoom.type != room.type { return true }
            }
        }
        return false
    }
}

extension AmendReviewInteractor: AmendReviewInteractorProtocol {
    var amendReviewViewModel: AmendReviewViewViewModel {
        let outstandingBalanceString: String = {
            let currencySymbol = NSLocale.current.currencySymbol ?? "£"
            guard let existingTotalCost = existingStay.totalCost else { return "\(currencySymbol)0.00" }

            let existingStayPrepaidAmount = existingStay.prePaidAmount ?? Cost(
                amount: 0.0,
                currencyCode: existingTotalCost.currencyCode
            )

            let priceDifferenceAmount =
                updateModel.newTotalCost.amount.subtracting(existingStayPrepaidAmount.amount)
            let priceDifferenceCost = Cost(
                amount: priceDifferenceAmount.doubleValue,
                currencyCode: updateModel.newTotalCost.currencyCode,
                locale: updateModel.newTotalCost.locale
            )

            return priceDifferenceCost.localizedValue
        }()


        let outstandingBalanceText: String = {
            guard paymentRequired == false else { return PILocalizedString("To be paid now") }
            guard let existingStayTotalCost = existingStay.totalCost else { return "" }

            let existingStayPrepaidAmount = existingStay.prePaidAmount ?? Cost(
                amount: 0.0,
                currencyCode: existingStayTotalCost.currencyCode
            )

            if existingStayPrepaidAmount.amount.doubleValue == 0 {
                return PILocalizedString("amendReviewPaidOnArrivalLabel", comment: "")
            }

            let amountRemainingToBePaidOnExistingTotalCost = updateModel.newTotalCost.amount
                .subtracting(existingStayPrepaidAmount.amount)

            return amountRemainingToBePaidOnExistingTotalCost
                .doubleValue == 0 ? "" : amountRemainingToBePaidOnExistingTotalCost.doubleValue > 0 ? PILocalizedString(
                    "amendReviewPaidOnArrivalLabel",
                    comment: ""
                ) : PILocalizedString("To be refunded", comment: "")
        }()

        let amendPaymentIntervalViewModel: AmendPaymentIntervalViewModel? = {
            if let paymentOptions = amendOperaDetails?.paymentOptions {
                return AmendPaymentIntervalViewModel(amendPaymentOptions: paymentOptions)
            }

            return nil
        }()

        let tripSummary = AmendReviewTripSummary(
            tripSummaryTitle: PILocalizedString("bookingConfirmationTripTitle"),
            tripSummary: updateModel.criteria.amendReviewTripSummary,
            arrivingTitle: PILocalizedString("arrivingCellLabel"),
            departingTitle: PILocalizedString("leavingCellLabel"),
            arrivingDate: updateModel.criteria.amendReviewArriving,
            departingDate: updateModel.criteria.amendReviewDeparting,
            arrivingTime: existingStay.checkInTimeText(hotelBrand: updateModel.hotelBrand ?? .premierInn),
            departingTime: existingStay.checkOutTimeText(hotelBrand: updateModel.hotelBrand ?? .premierInn)
        )

        return AmendInteractorReviewViewModel(
            hotelImageUrl: updateModel.hotelImage,
            hotelName: updateModel.hotelName,
            previousTotal: existingStay.totalCost?.localizedValue ?? "",
            amendments: updateModel.updates,
            newTotal: updateModel.newTotalCost.localizedValue,
            newDatesDescription: updateModel.criteria.rateDatesSummary,
            guestsAndRoomsDescription: updateModel.criteria.reviewRoomsSummary,
            outstandingBalance: outstandingBalanceString,
            outstandingBalanceActionRequiredText: outstandingBalanceText,
            amendPaymentViewModel: amendPaymentIntervalViewModel,
            showECILCORemovalMessage: updateModel.showECILCORemovalMessage,
            tripSummary: tripSummary
        )
    }

    var paymentRequired: Bool {
        get {
            let userSelectedOption = amendOperaDetails?.paymentOptions?.paymentOptionSelected
            return userSelectedOption == .now
        }
        set {
            let selectedOption: PaymentIntervalOption = newValue ? .now : .later
            amendOperaDetails?.paymentOptions?.paymentOptionSelected = selectedOption
        }
    }

    func confirmChanges() throws {
        if paymentRequired == true {
            guard let paymentCard = amendReviewViewModel.amendPaymentViewModel?.amendPaymentOptions.paymentCardDetails,
                  let amendOperaDetails = amendOperaDetails else {
                throw AmendReviewError.missingAmendPNDetails
            }
            let outstandingTotal = amendReviewViewModel.outstandingBalance

            let sameCardViewModel = SameCardPaymentMethod(
                paymentCard: paymentCard,
                isSelected: true,
                reservation: updateModel.reservation
            )
            let amendAndPayViewModel = AmendAndPayViewModel(
                paymentMethodsViewModel: [sameCardViewModel],
                totalCost: outstandingTotal,
                ctaButtonTitle: PILocalizedString("reviewSubmitButton3CNewCardTitle")
            )
            let reservationDetails = try getReservationDetails()
            presenter?.goToAmendAndPayNowView(
                amendUpdateModel: updateModel,
                amendAndPayViewModel: amendAndPayViewModel,
                amendOperaDetails: amendOperaDetails,
                reservationDetails: reservationDetails,
                existingStay: existingStay
            )
        } else {
            presenter?.amendPayOnArrival()
        }
    }

    private func getReservationDetails() throws -> ReservationDetails {
        guard let arrivalDate = existingStay.arrivalDate else { throw AmendReviewError.missingArrivalDate }
        guard let reservationId = existingStay.operaBasketReference else { throw AmendReviewError.missingBookingReference }

        return ReservationDetails(
            reservationId: amendOperaDetails?.originalBasketReference ?? reservationId,
            surname: existingStay.lastName,
            arrivalDate: arrivalDate,
            business: existingStay.business,
            token: amendOperaDetails?.token
        )
    }

    func amendBooking(completion: @escaping (Result<Bool>) -> Void) {
        do {
            let reservationDetails = try getReservationDetails()

            guard let tempBasketReference = amendOperaDetails?.temporaryBasketReference,
                  let paymentOptionSelected = amendOperaDetails?.paymentOptions?.paymentOptionSelected.cccpType else {
                completion(.failure(error: AmendReviewError.missingTemporaryReference))
                return
            }
            amendReviewDataProvider.confirmAmendLogic(
                reservationDetails: reservationDetails,
                tempBookingReference: tempBasketReference,
                selectedPaymentOption: paymentOptionSelected
            ) { response, error in
                if response?.status != nil {
                    self.trackCompletion()
                    completion(.success(result: true))
                } else {
                    completion(.failure(error: error ?? AmendReviewError.confirmAmendLogicFailed))
                }
            }
        } catch {
            completion(.failure(error: error))
        }
    }

    func checkBasketStatus(completion: @escaping (Result<BookingConfirmation>) -> Void) {
        guard let tempBasketReference = amendOperaDetails?.temporaryBasketReference else {
            completion(.failure(error: AmendReviewError.missingTemporaryReference))
            return
        }
        amendReviewDataProvider.checkBasketStatus(basketReference: tempBasketReference) { status, error in
            guard let status = status else {
                return completion(.failure(error: error ?? AmendReviewError.checkBasketStatusError))
            }
            completion(.success(result: status))
        }
    }

    func trackBookingConfirmation() {
        var data: PIDictionary = [:]

        data[PIAnalytics.Keys.amendExtrasBookingId] = existingStay.identifier
        data[PIAnalytics.Keys.amendNightsChange] = updateModel.criteria.nights - existingStay.numberOfNights
        data[PIAnalytics.Keys.amendRoomsAmountChange] = updateModel.criteria.rooms.count - existingStay.numberOfRoooms
        data[PIAnalytics.Keys.amendRoomTypesDidChange] = roomTypesDidChange
        data[PIAnalytics.Keys.amendChanges] = confChanges
        data[PIAnalytics.Keys.amendPayNow] = false

        AnalyticsManager.shared.trackAction(PIAnalytics.Action.amendComplete, userInfo: data)
    }
}
