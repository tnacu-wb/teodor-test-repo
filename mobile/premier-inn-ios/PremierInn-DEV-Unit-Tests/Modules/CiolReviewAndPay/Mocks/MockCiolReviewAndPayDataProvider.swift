//
//  MockCiolReviewAndPayDataProvider.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 29/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
@testable import PremierInn

final class MockCiolReviewAndPayDataProvider: CiolReviewAndPayDataProvider {
    func paymentMethods(
        bookingDetails: BookingDetails,
        hotel: Hotel,
        rate: Rate,
        user: User?,
        isCiol: Bool,
        completion: @escaping (PaymentMethodsResponse?, (any Error)?) -> Void
    ) { }
    
    func cccpPayment(
        with paymentParams: CCCPPaymentParams,
        and stayDetails: any CCCPStayDetails,
        and sessionId: String?,
        and isCiol: Bool,
        sensorData: String,
        completion: @escaping (CCCPPaymentResponse?, (any Error)?) -> Void
    ) { }
    
    func checkBasketStatus(
        basketReference: String?,
        completion: @escaping (BookingConfirmation?, (any Error)?) -> Void
    ) { }
    
    func amendCiolPackages(
        amendInfo: CiolAmendInfo,
        completion: @escaping (Bool?, (any Error)?) -> Void
    ) { }
    
    func refreshStays(
        for user: User?,
        shouldAttemptLogin: Bool,
        completion: @escaping (Bool?) -> Void
    ) { }

    var mockedCiolBackgroundChargeResult: (CiolBackgroundChargeResponse?, (any Error)?)?
    private(set) var isCiolBackgroundChargeCalled: Bool = false
    func ciolBackgroundCharge(
        basketReference: String,
        token: String,
        completion: @escaping (CiolBackgroundChargeResponse?, (any Error)?) -> Void
    ) {
        isCiolBackgroundChargeCalled = true
        completion(mockedCiolBackgroundChargeResult?.0, mockedCiolBackgroundChargeResult?.1)
    }
    
    func confirmPreCheckInOut(
        basketReference: String,
        type: CiolRequestType,
        isCiol: Bool,
        completion: @escaping (ConfirmPreCheckInOut?, (any Error)?) -> Void
    ) { }
    
    func authorizePayment() async throws -> CCCPPaymentProviderResponse? {
        nil
    }
    
    func attachFileToReservation(
        params: AuthorizationFileAttachmentParams
    ) async throws -> StatusResult? {
        nil
    }
    
    func updatePreCheckInStatus(
        params: UpdatePrecheckInParams
    ) async throws -> StatusResult? {
        nil
    }
}
