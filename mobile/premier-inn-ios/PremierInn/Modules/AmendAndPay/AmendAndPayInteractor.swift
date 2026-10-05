//
//  AmendAndPayInteractor.swift
//  PremierInn
//
//  Created by Santa Gurung on 16/11/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation

protocol AmendAndPayDataProvider {
    func confirmAmendLogic(
        reservationDetails: ReservationDetails,
        tempBookingReference: String,
        selectedPaymentOption: String,
        completion: @escaping (_ response: CCCPPaymentResponse?, _ error: Error?) -> Void
    )
    func checkBasketStatus(
        basketReference: String?,
        completion: @escaping (_ status: BookingConfirmation?, _ error: Error?) -> Void
    )
}

extension RequestsManager: AmendAndPayDataProvider {}

struct AmendAndPayViewModel {
    let paymentMethodsViewModel: [AmendPaymentMethodViewModel]
    let totalCost: String
    let ctaButtonTitle: String
}

protocol AmendPaymentMethodViewModel {
    var id: String { get }
    var cardTypeName: String { get }
    var cardLogoURLs: [URL]? { get }
    var isSelected: Bool { get }
    var billingAddressText: String { get }
}

struct SameCardPaymentMethod: AmendPaymentMethodViewModel {
    let paymentCard: PaymentCardDetails
    let isSelected: Bool
    let reservation: Reservation

    init(paymentCard: PaymentCardDetails, isSelected: Bool, reservation: Reservation) {
        self.paymentCard = paymentCard
        self.isSelected = isSelected
        self.reservation = reservation
    }

    var id: String {
        "\(paymentCard.cardNumberMasked ?? "") \(paymentCard.expirationDate ?? "") \(paymentCard.cardHolderName ?? "") \(paymentCard.cardName ?? "")"
    }

    var cardTypeName: String {
        "\(paymentCard.cardName ?? "") (\(paymentCard.cardNumberLast4Digits ?? ""))"
    }

    var cardLogoURLs: [URL]? {
        guard let baseUrl = Constants.imageBaseUrl else { return nil }
        guard let cardSrc = paymentCard.cardLogoSrc else { return nil }

        let cardUrl = baseUrl.appendingPathComponent(cardSrc)
        return [cardUrl]
    }

    var cardHolderName: String? {
        paymentCard.cardHolderName
    }

    var expiryDate: String? {
        "\(PILocalizedString("paymentCardFutureExpiration")) \(paymentCard.expirationDate ?? "")"
    }

    var billingAddressText: String {
        "\(reservation.booker?.address?.line1 ?? ""), \(reservation.booker?.address?.postcode ?? "")"
    }
}

protocol AmendAndPayInteractorProtocol {
    var amendAndPayViewModel: AmendAndPayViewModel { get }
    var amendOperaDetails: AmendOperaDetails? { get }

    func amendBooking(completion: @escaping (Result<ThreeCiPageParams>) -> Void)
    func updatePaymentMethods(selectedPaymentMethodViewModel: AmendPaymentMethodViewModel) -> AmendAndPayViewModel
    func checkBasketStatus(completion: @escaping (Result<BookingConfirmation>) -> Void)
    func trackBookingConfirmation()
    func updateAmendedStay(bookingReference: String)
}

class AmendAndPayInteractor {
    var dataProvider: AmendAndPayDataProvider = RequestsManager()
    let amendAndPayViewModel: AmendAndPayViewModel
    let amendOperaDetails: AmendOperaDetails?
    let reservationDetails: ReservationDetails
    let amendUpdateModel: AmendUpdateModel
    let existingStay: Stay

    init(
        amendUpdateModel: AmendUpdateModel,
        amendAndPayViewModel: AmendAndPayViewModel,
        amendOperaDetails: AmendOperaDetails,
        reservationDetails: ReservationDetails,
        existingStay: Stay
    ) {
        self.amendUpdateModel = amendUpdateModel
        self.amendAndPayViewModel = amendAndPayViewModel
        self.amendOperaDetails = amendOperaDetails
        self.reservationDetails = reservationDetails
        self.existingStay = existingStay
    }
}

extension AmendAndPayInteractor: AmendAndPayInteractorProtocol {
    func amendBooking(completion: @escaping (Result<ThreeCiPageParams>) -> Void) {
        guard let tempBasketReference = amendOperaDetails?.temporaryBasketReference else {
            completion(.failure(error: AmendAndPayError.missingTemporaryReference))
            return
        }

        guard let paymentOptionSelected = amendOperaDetails?.paymentOptions?.paymentOptionSelected.cccpType else {
            completion(.failure(error: AmendAndPayError.missingSelectedPaymentMethod))
            return
        }

        dataProvider.confirmAmendLogic(
            reservationDetails: reservationDetails,
            tempBookingReference: tempBasketReference,
            selectedPaymentOption: paymentOptionSelected
        ) { [weak self] response, error in
                guard let self = self else {
                    return completion(.failure(error: AmendAndPayError.confirmAmendLogicFail))
                }
                if response?.status == .required {
                    do {
                        let params = try getThreeCIpageParams(response: response)
                        completion(.success(result: params))
                    } catch {
                        completion(.failure(error: error))
                    }
                } else {
                    completion(.failure(error: error ?? AmendAndPayError.confirmAmendLogicFail))
                }
            }
    }

    private func getThreeCIpageParams(response: CCCPPaymentResponse?) throws -> ThreeCiPageParams {
        guard let response = response else { throw CCCPaymentError.missingResponse }
        guard let htmlString = response.paymentRequiredDetails?.htmlString else { throw CCCPaymentError.missingProviderUrl }

        var trackingParams = response.trackingParams
        if let cardCode = BookingDetails.sharedInstance.primaryPaymentMethod?.card?.type.cardCode {
            trackingParams[PIAnalytics.Keys.cccCardSelected] = cardCode
        }

        let bookingDetails = BookingDetails.sharedInstance
        trackingParams[PIAnalytics.Keys.bfUserType] = bookingDetails.bookingMode == .leisure ? "Leisure" : "Business"

        trackingParams[PIAnalytics.Keys.cccPaymentTakenNow] = bookingDetails.paymentOption == .now
        trackingParams[PIAnalytics.Keys.productString] = bookingDetails.trackingProductString

        return ThreeCiPageParams(
            html: htmlString,
            trackingParams: trackingParams,
            allowedEvents: nil
        )
    }

    func updatePaymentMethods(selectedPaymentMethodViewModel: AmendPaymentMethodViewModel) -> AmendAndPayViewModel {
        var paymentModels: [AmendPaymentMethodViewModel] = []

        // Currently we just have same card payment method
        let sameCardModel = amendAndPayViewModel.paymentMethodsViewModel
            .compactMap { $0 as? SameCardPaymentMethod }
            .map {
                SameCardPaymentMethod(
                    paymentCard: $0.paymentCard,
                    isSelected: selectedPaymentMethodViewModel.id == $0.id,
                    reservation: $0.reservation
                )
            }

        paymentModels.append(contentsOf: sameCardModel)

        return AmendAndPayViewModel(
            paymentMethodsViewModel: paymentModels,
            totalCost: amendAndPayViewModel.totalCost,
            ctaButtonTitle: amendAndPayViewModel.ctaButtonTitle
        )
    }

    func checkBasketStatus(completion: @escaping (Result<BookingConfirmation>) -> Void) {
        guard let tempBasketReference = amendOperaDetails?.temporaryBasketReference else {
            completion(.failure(error: AmendAndPayError.missingTemporaryReference))
            return
        }

        dataProvider.checkBasketStatus(basketReference: tempBasketReference) { status, error in
            guard let status = status else {
                return completion(.failure(error: error ?? ReviewAndBookMakeBookingError.checkBasketGenericError))
            }
            completion(.success(result: status))
        }
    }

    func updateAmendedStay(bookingReference: String) {
        let reservationsManager = SimpleStorageManager<Stay>(dataSource: UserDefaults.standard)

        if let stay = reservationsManager.items.first(where: { $0.identifier == bookingReference }) {
            guard let checkoutDateString = amendUpdateModel.criteria.checkOutDate?.parameterString else { return }

            stay.numberOfRoooms = amendUpdateModel.criteria.rooms.count
            stay.arrivalDateString = amendUpdateModel.criteria.arrivalDate.parameterString
            stay.checkOutDateString = checkoutDateString

            _ = reservationsManager.update(with: [stay])
        }
    }

    func trackBookingConfirmation() {
        var data: PIDictionary = [:]

        data[PIAnalytics.Keys.amendExtrasBookingId] = amendOperaDetails?.bookingReference
        data[PIAnalytics.Keys.amendNightsChange] = amendUpdateModel.criteria.nights - existingStay.numberOfNights
        data[PIAnalytics.Keys.amendRoomsAmountChange] = amendUpdateModel.criteria.rooms.count - existingStay.numberOfRoooms
        data[PIAnalytics.Keys.amendRoomTypesDidChange] = roomTypesDidChange
        data[PIAnalytics.Keys.amendChanges] = changeDetails
        data[PIAnalytics.Keys.amendPayNow] = true

        AnalyticsManager.shared.trackAction(PIAnalytics.Action.amendComplete, userInfo: data)
    }

    private var changeDetails: String {
        amendUpdateModel.updates.compactMap {
            "\($0.title.string) \($0.description ?? "")"
        }.joined(separator: ", ")
    }

    private var roomTypesDidChange: Bool {
        for existingRoom in amendUpdateModel.reservation.rooms {
            if let updatedRoom = amendUpdateModel.criteria.rooms.first(where: { $0.roomId == existingRoom.roomId }),
               updatedRoom.type != existingRoom.type {
                return true
            }
        }
        return false
    }
}
