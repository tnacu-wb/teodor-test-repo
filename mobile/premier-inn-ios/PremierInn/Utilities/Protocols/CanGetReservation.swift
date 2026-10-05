//
//  CanGetReservation.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 14.03.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//


import Foundation
import SimpleNetwork

protocol ReservationProvider {
    func reservation(
        reservationDetails: ReservationDetails,
        hotelCode: String?,
        bookingDetails: BookingDetails?,
        completion: @escaping (Reservation?, Error?) -> Void
    )
}

protocol CanGetReservation {
    func getReservation(
        reservationDetails: ReservationDetails,
        provider: some ReservationProvider
    ) async throws -> Reservation

    func priceBreakdown(with cost: Cost) -> PreStayInteractor.CIOLPriceBreakdownViewModel
}

extension CanGetReservation {
    func getReservation(
        reservationDetails: ReservationDetails,
        provider: some ReservationProvider
    ) async throws -> Reservation {
        try await withCheckedThrowingContinuation { continuation in
            provider
                .reservation(
                    reservationDetails: reservationDetails,
                    hotelCode: nil,
                    bookingDetails: .init()
                ) { reservation, error in
                guard let reservation,
                      error == nil else {
                    continuation.resume(throwing: CIOLError.performPrestayChecks)
                    return
                }
                continuation.resume(with: .success(reservation))
            }
        }
    }

    func priceBreakdown(with cost: Cost) -> PreStayInteractor.CIOLPriceBreakdownViewModel {
        if cost.amount != 0 {
            let outstandingItem = PreStayInteractor.CIOLPriceBreakdownItemViewModel(
                name: PILocalizedString("preStayOutstandingBalance"),
                value: cost,
                quantity: 1
            )
            return PreStayInteractor.CIOLPriceBreakdownViewModel(
                ctaTitle: PILocalizedString("Continue"),
                totalValue: cost.localizedValue,
                items: [outstandingItem]
            )
        }
        return PreStayInteractor.CIOLPriceBreakdownViewModel(
            ctaTitle: PILocalizedString("Continue"),
            totalValue: "",
            items: []
        )
    }
}
