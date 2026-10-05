//
//  AmendAndPayPresenterTests.swift
//  PremierInnTests
//
//  Created by Santa Gurung on 20/11/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

final class AmendAndPayPresenterTests: XCTestCase {

    var presenter: AmendAndPayPresenter!
    var interactor: AmendAndPayInteractorSpy!

    override func setUp() {
        interactor = AmendAndPayInteractorSpy()
        presenter = AmendAndPayPresenter()
        presenter.interactor = interactor
    }

    override func tearDown() {
        interactor = nil
        presenter = nil
    }

    func testCTAButtonDidTap() {
        XCTAssertFalse(interactor.amendBookingCalled)
        presenter.ctaButtonDidTap()
        XCTAssertTrue(interactor.amendBookingCalled)
    }

    func testDidSelectPaymentMethod() {
        XCTAssertFalse(interactor.updatePaymentMethodsCalled)
        presenter.didSelectPaymentMethod(selectedPaymentMethodViewModel: interactor.amendAndPayViewModel.paymentMethodsViewModel.first!)
        XCTAssertTrue(interactor.updatePaymentMethodsCalled)
    }

    func testCheckBasketStatusCalled() {
        XCTAssertFalse(interactor.checkBasketStatusCalled)
        presenter.checkBasketStatus(transactionID: "") { _ in
            XCTAssertTrue(self.interactor.checkBasketStatusCalled)
        }
    }
}

final class AmendAndPayInteractorSpy: AmendAndPayInteractorProtocol {

    var amendOperaDetails: AmendOperaDetails?
    
    var amendAndPayViewModel: AmendAndPayViewModel = {

        let paymentCard = PaymentCardDetails(
            cardNumberMasked: "XXXXXXXXXXXX1103",
            expirationDate: "2029-01-31",
            cardHolderName: "",
            cardNumberLast4Digits: "1103",
            cardName: "Visa Debit",
            cardLogoSrc: "/content/dam/global/booking/Visa_Debit.jpg"
        )
        let reservation = try! Reservation(dictionary: MiscTests.reservationDictionary)

        let paymentMethodViewModel = SameCardPaymentMethod(paymentCard: paymentCard, isSelected: true, reservation: reservation)

        let amendAndPayViewModel = AmendAndPayViewModel(
            paymentMethodsViewModel: [paymentMethodViewModel], 
            totalCost: "300",
            ctaButtonTitle: "Enter Card Details"
        )
        return amendAndPayViewModel
    }()

    var amendBookingCalled = false
    var updatePaymentMethodsCalled = false
    var trackBookingConfirmationCalled = false
    var checkBasketStatusCalled = false

    func updatePaymentMethods(selectedPaymentMethodViewModel: AmendPaymentMethodViewModel) -> AmendAndPayViewModel {
        updatePaymentMethodsCalled = true
        return amendAndPayViewModel
    }

    func amendBooking(completion: @escaping (Result<ThreeCiPageParams>) -> Void) {
        amendBookingCalled = true
    }

    func checkBasketStatus(completion: @escaping (Result<BookingConfirmation>) -> Void) {
        checkBasketStatusCalled = true
    }

    func trackBookingConfirmation() {
        trackBookingConfirmationCalled = true
    }

    func bookingNotification(withEmail emailAddress: String, paymentOption: PaymentIntervalOption) {
        // Intentional empty
    }

    func updateAmendedStay(bookingReference: String) {
        
    }
}
