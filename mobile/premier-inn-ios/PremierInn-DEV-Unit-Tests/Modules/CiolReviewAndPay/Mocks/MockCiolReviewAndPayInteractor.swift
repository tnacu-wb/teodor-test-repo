//
//  MockCiolReviewAndPayInteractor.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 30/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
@testable import SimpleNetwork
@testable import PremierInn

final class MockCiolReviewAndPayInteractor: CiolReviewAndPayInteractorProtocol {
    var paymentViewLayout: PremierInn.WebViewControllerLayout = .general
    
    var showBillingAddressFields: Bool = false
    
    var storedAddressModel = StoredAddressModel()

    var customAnalyticsParameters: PIDictionary?

    var bookingDetails = BookingDetails.sharedInstance

    var mockBackgroundChargeResult = false
    
    let stay = try! Stay(dictionary: ["hotelCode": "AVC241",
                                      "hotelName": "London",
                                      "identifier": "MVC",
                                      "arrivalDate": "Mon, 12",
                                      "checkOutDate": "Tue, 13"])

    lazy var viewModel = CiolReviewAndPayInteractor.ViewModel(
        bookingSummaryViewModel: PreStayInteractor.BookingSummaryCIOLViewModel(
            image: nil,
            hotelName: nil,
            duration: nil,
            summary: nil
        ),
        priceBreakdownViewModel: PreStayInteractor.CIOLPriceBreakdownViewModel(
            ctaTitle: "Continue",
            totalValue: "£313.96",
            items: [PreStayInteractor.CIOLPriceBreakdownItemViewModel(
                name: "Outstanding balance",
                value: Cost(
                    amount: 313.96,
                    currencyCode: "GBP"
                ),
                quantity: 1
            )]
        ),
        formattedBillingAddress: "",
        billingAddress: StoredAddressModel(),
        confirmationDetails: CiolConfirmationDetails(
            bookerFirstName: "Tester",
            hotelBrand: .premierInn,
            ciolStartFlow: .myBookings,
            hotelImage: nil,
            analyticsInfo: [:],
            stay: self.stay
        ),
        isBillingFieldOn: false,
        isGermanHotel: false,
        deRegCardPaymentInformationMessage: NSAttributedString(),
        paymentMethodPIBAUnavailable: CiolReviewAndPayInteractor.CIOLPIBAUnavailableViewModel(
            message: "PIBA Unavailable",
            shouldShow: true
        )
    ) as CiolReviewAndPayViewModel
    
    var isGetPaymentMethodsCalled = false
    var isStartCCPaymentCalled = false
    var isStartPayPalVaultCalled = false
    var isCheckBasketStatusCalled = false
    var isSelectedPaymentMethodUpdated = false
    var isUpdateStoredAddressCalled = false
    var isPIBACheckMethodCalled = false
    var isTrackContinueButtonAnalyticsCalled = false

    func getPaymentMethods(completion: @escaping (Result<PaymentMethodsResponse>) -> Void) {
        isGetPaymentMethodsCalled = true
    }
    
    func startCccPayment(with paypalNonce: String?, paypalDeviceData: String?, completion: @escaping (Result<CCCPPaymentResponse>) -> Void) throws {
        isStartCCPaymentCalled = true
    }
    
    func startPaypalVault(completion: @escaping (String?, String?, (any Error)?) -> Void) {
        isStartPayPalVaultCalled = true
    }

    func checkBasketStatus(transactionID: String, completion: @escaping (Result<BookingConfirmation>) -> Void) {
        isCheckBasketStatusCalled = true
    }

    func updateSelectedPaymentMethod(paymentViewModel: PaymentMethodViewModelType) {
        isSelectedPaymentMethodUpdated = true
    }

    func updateStoredAddress(with addressLine: AddressLineType) {
        isUpdateStoredAddressCalled = true
    }

    func checkPIBAPaymentMethodExists(paymentMethods: [PaymentOption]?) {
        isPIBACheckMethodCalled = true
    }

    func didPop() {}

    func failedPayment() {}
    
    func trackPriceBreakdownTapAnalytics() { }
    
    func trackContinueButtonAnalytics() {
        isTrackContinueButtonAnalyticsCalled = true
    }

    func setCccCardType(_ cardType: String?) { }

    func handleBackgroundChargeIfRequired(completion: @escaping (Bool) -> Void) {
        completion(mockBackgroundChargeResult)
    }
}
