//
//  MockReservationsInteractorDataProvider.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 08/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
@testable import PremierInn

final class MockReservationsInteractorDataProvider: ReservationsInteractorDataProvider {

    var stubbedCiolPaymentActionsResponse: (CiolPaymentActionsResponse?, (any Error)?)?
    private(set) var isCiolPaymentActionsCalled = false
    
    func cancelConnections() { }
    
    func refreshStays(
        for user: SimpleNetwork.User?,
        shouldAttemptLogin: Bool,
        completion: @escaping (Bool?) -> Void
    ) { }
    
    func loadHotel(
        with hotelCode: String,
        completion: @escaping (SimpleNetwork.Hotel?, (any Error)?) -> Void
    ) { }
    
    func findBookingSource(
        findBookingDetails: SimpleNetwork.FindBookingDetails,
        completion: @escaping (SimpleNetwork.FindBookingSource?, (any Error)?) -> Void
    ) { }
    
    func reservation(
        reservationDetails: SimpleNetwork.ReservationDetails,
        hotelCode: String?,
        bookingDetails: SimpleNetwork.BookingDetails?,
        completion: @escaping (SimpleNetwork.Reservation?, (any Error)?) -> Void
    ) { }
    
    func updateCiolStatus(
        payload: SimpleNetwork.UpdateCiolStatusPayload,
        completion: @escaping (SimpleNetwork.UpdateCiolStatusResponse?, (any Error)?) -> Void
    ) { }
    
    func ciolPaymentActions(
        basketReference: String,
        completion: @escaping (CiolPaymentActionsResponse?, (any Error)?) -> Void
    ) {
        isCiolPaymentActionsCalled = true
        completion(stubbedCiolPaymentActionsResponse?.0, stubbedCiolPaymentActionsResponse?.1)
    }
    
    func getCategoryLabels(
        labelType: SimpleNetwork.LabelsConfig,
        completion: @escaping (SimpleNetwork.Result<SimpleNetwork.CategoryLabels>) -> Void
    ) { }
    
    
}
