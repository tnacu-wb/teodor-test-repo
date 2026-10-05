//
//  BookingConfirmationInteractor+WalletPassFetchResult.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 21/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import PassKit

extension BookingConfirmationInteractor {
    // MARK: - WalletPassFetchResult

    enum WalletPassFetchResult: Equatable {
        case containsPass
        case fetchSuccessful(PKPass)
        case fetchFailed(Error?)
        case noReservationDetails
    }
}

// MARK: - Equatable

extension BookingConfirmationInteractor.WalletPassFetchResult {
    static func == (lhs: Self, rhs: Self) -> Bool {
        switch (lhs, rhs) {
        case (.containsPass, .containsPass),
            (.noReservationDetails, .noReservationDetails),
            (.fetchSuccessful, .fetchSuccessful),
            (.fetchFailed, .fetchFailed):
            return true

        default:
            return false
        }
    }
}
