//
//  RequestsManager+GraphQL.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 01/06/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation

enum WalletServiceError: Error {
    case walletServiceNotSupported
}

enum BookingHoldError: Error {
    case bookingNotHold
}

extension RequestsManager {
    private enum DispatchQueueOption {
        case enter
        case leave
    }

    private func dispatchManager(dispatchGroup: DispatchGroup, option: DispatchQueueOption) {
        switch option {
        case .enter:
            dispatchGroup.enter()
        case .leave:
            dispatchGroup.leave()
        }
    }

    func hotelAvailability(
        hotelCode: String,
        hotelBrand: HotelBrand?,
        bookingDetails: BookingDetails,
        datesChangedByUserInHdp: Bool = false,
        discountCodeViewModel: HotelDetailsDiscountCodeViewModel? = nil,
        completion: @escaping (
            _ result: HotelAvailabilityResponse?,
            _ error: Error?
        ) -> Void
    ) {
        let callHotelAvailability = { [weak self] in
            self?.hotelAvailability(
                hotelCode: hotelCode,
                hotelBrand: hotelBrand,
                bookingDetails: bookingDetails,
                allowEmployeeOffer: SettingsManager.sharedInstance.allowEmployeeOfferFeature == true &&
                bookingDetails.bookingMode != .business
            ) { result, error in
                self?.dispatchManager(
                    dispatchGroup: DispatchGroupManager.sharedInstance.availabilityDispatchGroup,
                    option: .leave
                )
                completion(result, error)
            }
        }

        dispatchManager(dispatchGroup: DispatchGroupManager.sharedInstance.availabilityDispatchGroup, option: .enter)

        // Do not call promotionsInformation if App Incentive or Free breakfast is available
        // OR if user has entered a valid discount code via HDP discount screen
        // [CTECH-4557](https://whitbreadis.atlassian.net/browse/CTECH-4557)
        if SettingsManager.sharedInstance.isAppIncentiveAvailable ||
           SettingsManager.sharedInstance.isFreeBreakfastPromotionAvailable ||
           bookingDetails.userEnteredPromoCode != nil {
            callHotelAvailability()
            return
        }

        let promotionsInformationCriteria = PromotionsInformationCriteria(
            brand: hotelBrand ?? .premierInn,
            promotionCode: "",
            bookingDate: nil,
            stayStartDate: bookingDetails.criteria.arrivalDate,
            stayEndDate: bookingDetails.criteria.checkOutDate ?? Date(),
            basketReference: nil
        )

        getPromotionsInformation(criteria: promotionsInformationCriteria) { promotionsInformation, _ in
            SettingsManager.sharedInstance.siteWidePromotionContent = promotionsInformation?
                .showPromo == true ? SiteWidePromotionContent(
                    title: promotionsInformation?.appPromoBannerTitle,
                    subtitle: promotionsInformation?.appPromoBannerSubtitle,
                    promotionCode: promotionsInformation?.promotionCode,
                    urlString: promotionsInformation?.termsLink
                ) : nil

            if let promotionCode = promotionsInformation?.promotionCode {
                bookingDetails.siteWidePromoCode = promotionCode
                bookingDetails.promoKind = promotionsInformation?.promoKind
            } else {
                bookingDetails.siteWidePromoCode = nil
                bookingDetails.promoKind = nil
            }

            // Make the validateDiscountCode call if user has changed dates on HDP
            // AND if there is no site-wide promotion
            // AND if there is a cached previouslyValidatedDiscountCode
            // [CTECH-6102](https://whitbreadis.atlassian.net/browse/CTECH-6102)
            if let discountCodeViewModel, datesChangedByUserInHdp,
               promotionsInformation?.promotionCode == nil,
               discountCodeViewModel.previouslyValidatedDiscountCode != nil {
                discountCodeViewModel.validateDiscountCode { code, codeType  in
                    bookingDetails.userEnteredPromoCode = code
                    bookingDetails.promoKind = codeType
                    callHotelAvailability()
                }
            } else {
                callHotelAvailability()
            }
        }
    }

    func performHoldBooking(
        bookingDetails: BookingDetails,
        completion: @escaping (_ basketReference: String?, _ error: Error?) -> Void
    ) {
        dispatchManager(dispatchGroup: DispatchGroupManager.sharedInstance.holdBookingDispatchGroup, option: .enter)

		let sensorData = AkamaiProtection.sensorData

        holdBooking(bookingDetails: bookingDetails, sensorData: sensorData) { basketReference, error in
            if error == nil {
                BookingDetails.sharedInstance.basketReference = basketReference
                BookingDetails.sharedInstance.isBookingHold = true
            }

            self.dispatchManager(dispatchGroup: DispatchGroupManager.sharedInstance.holdBookingDispatchGroup, option: .leave)

            completion(basketReference, error)
        }
    }

    func performHoldBookingWithGuests(
        bookingDetails: BookingDetails,
        isCiolFlow: Bool,
        isRegCard: Bool = false,
        completion: @escaping (_ success: Bool, _ error: Error?) -> Void
    ) {
        guard BookingDetails.sharedInstance.isBookingHold == true else {
            completion(false, BookingHoldError.bookingNotHold)
            return
        }

        dispatchManager(dispatchGroup: DispatchGroupManager.sharedInstance.bookingFlowDispatchGroup, option: .enter)

        holdBookingWithGuests(
            bookingDetails: bookingDetails,
            isCiolFlow: isCiolFlow,
            isRegCard: isRegCard
        ) { success, error in
            self.dispatchManager(dispatchGroup: DispatchGroupManager.sharedInstance.bookingFlowDispatchGroup, option: .leave)
            completion(success, error)
        }
    }

    func saveUpsellsToBookingFlow(bookingDetails: BookingDetails, completion: @escaping (Bool, Error?) -> Void) {
        guard BookingDetails.sharedInstance.isBookingHold == true else {
            completion(false, nil)
            return
        }

        dispatchManager(dispatchGroup: DispatchGroupManager.sharedInstance.bookingFlowDispatchGroup, option: .enter)
        saveUpsellsToBooking(bookingDetails: bookingDetails) { success, error in
            self.dispatchManager(dispatchGroup: DispatchGroupManager.sharedInstance.bookingFlowDispatchGroup, option: .leave)
            completion(success, error)
        }
    }

    func loadWalletPass(
        with reservationDetails: ReservationDetails,
        isQRCodeEnabled: Bool,
        completion: @escaping (Data?, Error?) -> Void
    ) {
        getWalletPass(with: reservationDetails, excludeBarcode: !isQRCodeEnabled, completion: completion)
    }
}
