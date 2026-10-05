//
//  CiolPaymentActionsResponseTests.swift
//  SimpleNetwork
//
//  Created by Rodrigues, Seymour (Contractor) on 08/06/2026.
//

import Testing
@testable import SimpleNetwork

struct CiolPaymentActionsResponseTests {

    // MARK: - cityTax tests

    @Test
    func testCityTaxWhenCreditCardAndCardOnFileArePresent() {
        // GIVEN
        let sut = CiolPaymentActionsResponse(
            displayPaymentPage: nil,
            paymentActions: [
                makeAction(type: .creditCard, amount: 10),
                makeAction(type: .cardOnFile, amount: 5)
            ]
        )

        // WHEN
        let result = sut.cityTax

        // THEN
        #expect(result?.amount.doubleValue == 10)
    }

    @Test
    func testCityTaxWhenOnlyCreditCardIsPresent() {
        // GIVEN
        let sut = CiolPaymentActionsResponse(
            displayPaymentPage: nil,
            paymentActions: [
                makeAction(type: .creditCard, amount: 10)
            ]
        )

        // WHEN
        let result = sut.cityTax

        // THEN
        #expect(result == nil)
    }

    @Test
    func testCityTaxWhenCreditCardAmountIsZero() {
        // GIVEN
        let sut = CiolPaymentActionsResponse(
            displayPaymentPage: nil,
            paymentActions: [
                makeAction(type: .creditCard, amount: 0),
                makeAction(type: .cardOnFile, amount: 5)
            ]
        )

        // WHEN
        let result = sut.cityTax

        // THEN
        #expect(result == nil)
    }

    // MARK: - outstandingBalance tests

    @Test
    func testOutstandingBalanceWhenCreditCardPresent() {
        // GIVEN
        let sut = CiolPaymentActionsResponse(
            displayPaymentPage: nil,
            paymentActions: [
                makeAction(type: .creditCard, amount: 20)
            ]
        )

        // WHEN
        let result = sut.outstandingBalance

        // THEN
        #expect(result?.amount.doubleValue == 20)
    }

    @Test
    func testOutstandingBalanceWhenAuthorizeCardAndCardOnFileArePresent() {
        // GIVEN
        let sut = CiolPaymentActionsResponse(
            displayPaymentPage: nil,
            paymentActions: [
                makeAction(type: .creditCard, amount: 20),
                makeAction(type: .cardOnFile, amount: 5)
            ]
        )

        // WHEN
        let result = sut.outstandingBalance

        // THEN this should be nil as .creditCard and .cardOnFile stipulates to cityTax
        #expect(result == nil)
    }

    // MARK: - shouldPerformBackgroundCharge tests

    @Test("Test for shouldPerformBackgroundCharge", arguments: [
        CiolPaymentActionsResponse.PaymentAction.ChargeType.authorizeCard,
        .cardOnFile,
        .creditCard,
        .noPayment
    ])
    func testShouldPerformBackgroundCharge(_ chargeType: CiolPaymentActionsResponse.PaymentAction.ChargeType) {
        // GIVEN
        let sut = CiolPaymentActionsResponse(
            displayPaymentPage: nil,
            paymentActions: [
                makeAction(type: chargeType, amount: 0)
            ]
        )

        // WHEN
        let result = sut.shouldPerformBackgroundCharge

        // THEN
        if chargeType == .cardOnFile {
            #expect(result == true)
        } else {
            #expect(result == false)
        }
    }
}

// MARK: - Helpers

private extension CiolPaymentActionsResponseTests {
    private func makeCost(_ value: Double) -> Cost {
        Cost(amount: value, currencyCode: "GBP")
    }

    private func makeAction(
        type: CiolPaymentActionsResponse.PaymentAction.ChargeType,
        amount: Double
    ) -> CiolPaymentActionsResponse.PaymentAction {
        CiolPaymentActionsResponse.PaymentAction(
            chargeType: type,
            price: makeCost(amount)
        )
    }
}
