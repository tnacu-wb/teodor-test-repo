//
//  BookingSummaryPresenter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 04/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork

struct BookingSummaryViewModel {
    let hotel: Hotel
    let arrivalDateText: String?
    let checkOutDateText: String?
    let summaryText: String?
    let rooms: [Room]?
    let roomsCountDescription: String?
    let nightsCountDescription: String?
    let totalCostText: String?
    let roomsCostText: String?
    let rateText: String?
    let mealModels: [BookingSummaryMealViewModel]
    let wifiModels: [BookingSummaryWifiViewModel]
    let extrasModels: [BookingSummaryExtrasViewModel]
    let shouldTotalShowCityTaxInfo: Bool
    var donationsAmount: Cost?
}

struct BookingSummaryMealViewModel {
    let meal: UpsellItem?
    let childrenCount: Int?
    let guestsCountDescription: String?
    let adultsCountDescription: String?
    let childrenCountDescription: String?
    let totalCostText: String?
}

struct BookingSummaryWifiViewModel {
    let wifi: UpsellItem?
    let numberOfRooms: Int
    let totalCostText: String?
}

struct BookingSummaryExtrasViewModel {
    let extraItem: UpsellItem?
    let numberOfRooms: Int
    let totalCostText: String?
}

protocol BookingSummaryPresenterProtocol {
    func viewIsReady()
}

class BookingSummaryPresenter {
    weak var view: BookingSummaryViewProtocol?
    var interactor: BookingSummaryInteractorProtocol?
}

extension BookingSummaryPresenter: BookingSummaryPresenterProtocol {
    func viewIsReady() {
        view?.setup(withTitle: PILocalizedString("bookingSummaryScreenTitle", comment: "Booking summary screen title"))

        view?.toggleActivity(isOn: true)

        interactor?.load { (reservation, bookingDetails, hotel, error) in
            self.view?.toggleActivity(isOn: false)

            if let error = error {
                self.view?.showError(
                    title: PILocalizedString(
                        "bookingConfirmationLoadingErrorTitle",
                        comment: "Booking confirmation loading error title"
                    ),
                    message: error.localizedDescription
                )
                return
            }

            guard let hotel = hotel else {
                self.view?.showError(
                    title: PILocalizedString(
                        "bookingConfirmationLoadingErrorTitle",
                        comment: "Booking confirmation loading error title"
                    ),
                    message: PILocalizedString("bookingErrorMissingHotel", comment: "Booking error: missing hotel")
                )
                return
            }

            let bookingDetailsTotalCost: String? = {
                if let operaConfirmedTotalCost = bookingDetails?.totalCostOperaWithDonations {
                    return operaConfirmedTotalCost.localizedValue
                }

                return (bookingDetails?.cityTaxRequired ?? true) ? bookingDetails?.totalCostWithCityTaxAndExtras?
                    .localizedValue : bookingDetails?.totalCostWithoutCityTax?.localizedValue
            }()

            let viewModel = BookingSummaryViewModel(
                hotel: hotel,
                arrivalDateText: reservation?.arrivalDate?.fullDateFormat ?? bookingDetails?.criteria.arrivalDate
                .fullDateFormat,
                checkOutDateText: reservation?.checkOutDate?.fullDateFormat ?? bookingDetails?.criteria.checkOutDate?
                .fullDateFormat,
                summaryText: reservation?.guestsAndNightsSummary ?? bookingDetails?.criteria.guestsAndNightsSummary,
                rooms: reservation?.rooms ?? bookingDetails?.roomLettings,
                roomsCountDescription: reservation?.roomsCountDescription ?? bookingDetails?.criteria.roomsCountDescription,
                nightsCountDescription: reservation?.nightsCountDescription ?? bookingDetails?.criteria
                .nightsCountDescription,
                totalCostText: reservation?.totalCostText ?? bookingDetailsTotalCost,
                roomsCostText: reservation?.roomsTotalCost.localizedValue ?? bookingDetails?.roomCost?.localizedValue,
                rateText: reservation?.rate?.name ?? bookingDetails?.rate?.name(with: bookingDetails?.hotel?.brand),
                mealModels: self.interactor?.mealModels ?? [],
                wifiModels: self.interactor?.wifiModels ?? [],
                extrasModels: self.interactor?.extrasModels ?? [],
                // only show when we have a fetched totalCost (which is after they selected reason for stay)
                shouldTotalShowCityTaxInfo: bookingDetails?.operaConfirmedTotalCost != nil,
                donationsAmount: reservation?.selectedDonation ?? bookingDetails?.goshDonation
            )

            self.view?.showViewModel(with: viewModel)
        }
    }
}

private extension Reservation {
    var totalCostText: String? {
        if businessTrip { return totalCost?.localizedValue }

        guard let fullRoomcost = totalCost else { return nil }
        let cityTaxCost = rate?.rooms?.first?.options?.first?.cityTax ?? Cost(
            amount: 0,
            currencyCode: fullRoomcost.currencyCode
        )

        let totalCostMinusCityTax = Cost(
            amount: fullRoomcost.amount.doubleValue - cityTaxCost.amount.doubleValue,
            currencyCode: fullRoomcost.currencyCode
        )

        return totalCostMinusCityTax.localizedValue
    }
}

private extension BookingDetails {
    var totalCostText: String? {
        let shouldShowCityTaxInformation: Bool = rate?.cityTaxRequired ?? false && (purpose ?? .leisure) == .leisure ? hotel?
            .cityTaxForLeisure == true : hotel?.cityTaxForBusiness == true

        if shouldShowCityTaxInformation { return roomAndMealCost?.localizedValue }

        guard let fullRoomcost = roomAndMealCost else { return nil }

        let extrasCost = extrasTotalCost ?? Cost(amount: 0, currencyCode: fullRoomcost.currencyCode)
        // FIXME: incorrect rate cityTax
        let cityTaxCost = rate?.rooms?.first?.options?.first?.cityTax ?? Cost(
            amount: 0,
            currencyCode: fullRoomcost.currencyCode
        )

        let totalCostMinusCityTax = Cost(
            amount: fullRoomcost.amount.doubleValue + extrasCost.amount.doubleValue - cityTaxCost.amount.doubleValue,
            currencyCode: fullRoomcost.currencyCode
        )

        return totalCostMinusCityTax.localizedValue
    }
}
