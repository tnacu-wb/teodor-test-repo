//
//  MockBookingConfirmationInteractorOutput.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 21/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
@testable import SimpleNetwork
@testable import PremierInn

extension BookingConfirmationInteractorTests {
    final class MockBookingConfirmationInteractorOutput: BookingConfirmationInteractorOutput {
        var cancelConnectionsDidCall = false
        var loadHotelDidCall = false
        var loadWalletPassDidCall = false
        var categoryLabelsDidCall = false
        var confirmCheckOutInCalled = false
        var isUpdateCiolStatusCalled = false
        private(set) var isCiolPaymentsActionsCalled = false

        var loadWalletPassResult: (Data?, Error?)

        var ciolStatus: CiolStatus?

        var reservationResult: (Reservation?, Error?)?

        var getPackagesDidCall = false
        var getPackagesBookingDetails: BookingDetails?
        var getPackagesReservationId: String?
        var isThirdPartyBooking = false

        var stubbedCiolPaymentActionsResponse: (CiolPaymentActionsResponse?, (any Error)?)?
        
        func cancelConnections() {

            cancelConnectionsDidCall = true
        }

        func loadHotel(with hotelCode: String, completion: @escaping (Hotel?, Error?) -> Void) {

            loadHotelDidCall = true

            let hotel = try! Hotel(dictionary: [
                "name": "Hotel Name",
                "code": "LONLEI",
                "prepaymentAllowed": true,
                "address": ["postcode": "a", "addressline1": "a", "addressline2": "a", "addressline3": "a", "country": "a"],
                "images": [["fileReference": "/content/dam/pi/websites/hotelimages/gb/en/B/BRIPTI/BRIPTI 1.jpg"]],
                "contactDetails": ["hotelNationalPhone": "fakeNumber"]
                ])

            completion(hotel, nil)
        }

        func findBookingSource(findBookingDetails: FindBookingDetails, completion: @escaping (FindBookingSource?, Error?) -> Void) {
            let findBookingSourceStub = FindBookingSource(
                sourcePms: .opera,
                bookingReference: "BBER264250",
                token: "N8gzoF2Cmq96GBtbXlV5wRa3PTEZ+UdopdlD/ybTOE8ddi2lm",
                basketReference: "AKN-27ff4a81-3cc0-4d03-896c-6f0ada75e487",
                hotelId: "LINMIL",
                isThirdPartyBooking: false
            )
            completion(findBookingSourceStub, nil)
        }

        func reservation(reservationDetails: ReservationDetails, hotelCode: String?, bookingDetails: BookingDetails?, completion: @escaping (Reservation?, Error?) -> Void) {
            if let (reservation, error) = reservationResult {
                completion(reservation, error)
                return
            }

            let dictionary: [String: Any] = isThirdPartyBooking
                ? thirdPartyBookingReservationDetailsDictionary
                : reservationDetailsDictionary


            let reservationStub = try! Reservation(dictionary: dictionary)

            completion(reservationStub, nil)
        }

        func loadWalletPass(with reservationDetails: ReservationDetails, isQRCodeEnabled: Bool, completion: @escaping (Data?, Error?) -> Void) {

            loadWalletPassDidCall = true

            completion(loadWalletPassResult.0, loadWalletPassResult.1)
        }

        func startCheckInOnlineSession(with params: any SimpleNetwork.StartCheckInRequestParameters, completion: @escaping (SimpleNetwork.CheckInOnlineSessionResponse?, (any Error)?) -> Void) {
        }

        func getCategoryLabels(labelType: SimpleNetwork.LabelsConfig, completion: @escaping (SimpleNetwork.Result<SimpleNetwork.CategoryLabels>) -> Void) {
            categoryLabelsDidCall =  true
        }

        func getPackages(
            reservationId: String,
            bookingDetails: SimpleNetwork.BookingDetails,
            hotelCode: String,
            bookingFlowId: String?,
            showMealInclusiveRate: Bool,
            completion: @escaping (([SimpleNetwork.UpsellItem], [SimpleNetwork.UpsellItem], SimpleNetwork.CityTaxResponse, GoshPackage?)?, (any Error)?) -> Void
        ) {
            getPackagesDidCall = true
            getPackagesBookingDetails = bookingDetails
            getPackagesReservationId = reservationId
            completion(([], [], (cityTaxForLeisure: false, cityTaxForBusiness: false), nil), nil)
        }

        func resendInvoiceEmail(reservation: Reservation, completion: @escaping (Bool) -> Void) {}
        
        func confirmPreCheckInOut(basketReference: String, type: SimpleNetwork.CiolRequestType, isCiol _: Bool, completion: @escaping (SimpleNetwork.ConfirmPreCheckInOut?, (any Error)?) -> Void) {
            confirmCheckOutInCalled = true
        }

        func getPromotionsInformation(criteria: SimpleNetwork.PromotionsInformationCriteria, completion: @escaping (SimpleNetwork.PromotionsInformation?, (any Error)?) -> Void) {
            
        }

        func updateCiolStatus(payload: UpdateCiolStatusPayload, completion: @escaping (_ response: UpdateCiolStatusResponse?, _ error: Error?) -> Void) {
            ciolStatus = payload.ciolStatus
            isUpdateCiolStatusCalled = true
            completion(nil, nil)
        }

        func ciolPaymentActions(basketReference: String, completion: @escaping (CiolPaymentActionsResponse?, (any Error)?) -> Void) {
            isCiolPaymentsActionsCalled = true
            completion(stubbedCiolPaymentActionsResponse?.0, stubbedCiolPaymentActionsResponse?.1)
        }
    }
}

private extension BookingConfirmationInteractorTests.MockBookingConfirmationInteractorOutput {
    var reservationDetailsDictionary: [String: Any] {
        [
            "reservationDetails": [
                "hotelCode": "LINMIL",
                "hotelName": "Hotel de ville",
                "identifier": "1234",
                "checkOutDate": "2017-10-12",
                "confirmationNumber": "BBER264250",
                "arrivalDate": "2017-10-11",
                "departureDate": "2017-10-12",
                "booker": [:],
                "rooms": [["bookingStatus": "InHouse"]],
                "roomBreakdown": [["roomId": "12345"]],
                "operaBasketReference": "123456789"
            ]
        ]
    }

    var thirdPartyBookingReservationDetailsDictionary: [String: Any] {
        [
            "reservationDetails": [
                "hotelCode": "LINMIL",
                "hotelName": "Hotel de ville",
                "identifier": "1234",
                "checkOutDate": "2017-10-12",
                "confirmationNumber": "BBER264250",
                "arrivalDate": "2017-10-11",
                "departureDate": "2017-10-12",
                "booker": [:],
                "rooms": [["bookingStatus": "InHouse"]],
                "roomBreakdown": [["roomId": "12345"]],
                "isThirdPartyBooking": true,
                "operaBasketReference": "123456789"
            ]
        ]
    }
}
