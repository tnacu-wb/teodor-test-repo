//
//  MockPreStayDataProvider.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 08/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
@testable import PremierInn

final class MockPreStayDataProvider: PreStayDataProvider {
    private(set) var isConfirmPreCheckInOutCalled = false

    func getPackages(
        reservationId: String,
        bookingDetails: BookingDetails,
        hotelCode: String,
        bookingFlowId: String?,
        showMealInclusiveRate: Bool,
        completion: @escaping (([UpsellItem], [UpsellItem], CityTaxResponse,GoshPackage?)?, (any Error)?) -> Void
    ) { }
    
    func performHoldBookingWithGuests(
        bookingDetails: BookingDetails,
        isCiolFlow: Bool,
        isRegCard: Bool,
        completion: @escaping (Bool, (any Error)?) -> Void
    ) { }
    
    func confirmPreCheckInOut(
        basketReference: String,
        type: CiolRequestType,
        isCiol: Bool,
        completion: @escaping (ConfirmPreCheckInOut?, (any Error)?) -> Void
    ) {
        isConfirmPreCheckInOutCalled = true
        completion(nil, nil)
    }
    
    func getHotelPreferences(
        hotelCode: String,
        completion: @escaping ([HotelPreference]?, (any Error)?) -> Void
    ) { }
    
    func updateReservationPreferences(
        hotelCode: String,
        reservationIds: [String],
        preferencesCollections: [PreferencesCollection],
        completion: @escaping (PIDictionary?, (any Error)?) -> Void
    ) { }
    
    func authorizePayment() async throws -> CCCPPaymentProviderResponse? {
        nil
    }
    
    func attachFileToReservation(params: AuthorizationFileAttachmentParams) async throws -> StatusResult? {
        nil
    }
    
    func updatePreCheckInStatus(params: UpdatePrecheckInParams) async throws -> StatusResult? {
        nil
    }

    func reservation(
        reservationDetails: ReservationDetails,
        hotelCode: String?,
        bookingDetails: BookingDetails?,
        completion: @escaping (
            Reservation?,
            (any Error)?
        ) -> Void
    ) { }
   
    func ciolBackgroundCharge(
        basketReference: String,
        token: String,
        completion: @escaping (CiolBackgroundChargeResponse?, (any Error)?) -> Void
    ) { }
}
