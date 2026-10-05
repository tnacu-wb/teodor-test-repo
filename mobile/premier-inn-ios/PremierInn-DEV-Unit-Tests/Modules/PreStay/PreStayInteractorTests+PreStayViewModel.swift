//
//  PreStayInteractorTests+PreStayViewModel.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 10/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

extension PreStayInteractorTests {
    // MARK: - isAddressPostcodeValid tests

    func testPreStayViewModelIsAddressPostcodeValidForNonThirdParty() {
        // GIVEN interactor
        interactor = .init(
            preStayInputParams: makePreStayParams(
                stay: stay,
                isDirect: true
            )
        )

        // WHEN
        let result = interactor.viewModel.isAddressPostcodeValid

        // THEN
        XCTAssertTrue(result)
    }

    func testPreStayViewModelIsAddressPostcodeValidForThirdPartyWhenValidUkPostcode() {
        // GIVEN interactor
        interactor = .init(
            preStayInputParams: makePreStayParams(
                stay: stay,
                isDirect: false,
                address: ukAddressValidPostcode
            )
        )

        // WHEN
        let result = interactor.viewModel.isAddressPostcodeValid

        // THEN
        XCTAssertTrue(result)
    }

    func testPreStayViewModelIsAddressPostcodeValidForThirdPartyWhenInValidUkPostcode() {
        // GIVEN interactor
        interactor = .init(
            preStayInputParams: makePreStayParams(
                stay: stay,
                isDirect: false,
                address: ukAddressInvalidPostcode
            )
        )

        // WHEN
        let result = interactor.viewModel.isAddressPostcodeValid

        // THEN
        XCTAssertFalse(result)
    }

    func testPreStayViewModelIsAddressPostcodeValidForThirdPartyWhenValidGermanPostcode() {
        // GIVEN interactor
        interactor = .init(
            preStayInputParams: makePreStayParams(
                stay: stay,
                isDirect: false,
                address: germanyAddressValidPostcode
            )
        )

        // WHEN
        let result = interactor.viewModel.isAddressPostcodeValid

        // THEN
        XCTAssertTrue(result)
    }

    func testPreStayViewModelIsAddressPostcodeValidForThirdPartyWhenInValidGermanPostcode() {
        // GIVEN interactor
        interactor = .init(
            preStayInputParams: makePreStayParams(
                stay: stay,
                isDirect: false,
                address: germanyAddressInvalidPostcode
            )
        )

        // WHEN
        let result = interactor.viewModel.isAddressPostcodeValid

        // THEN
        XCTAssertFalse(result)
    }

    func testPreStayViewModelIsAddressPostcodeValidForThirdPartyWhenPostcodeIsNil() {
        // GIVEN interactor
        interactor = .init(
            preStayInputParams: makePreStayParams(
                stay: stay,
                isDirect: false,
                address: nil
            )
        )

        // WHEN
        let result = interactor.viewModel.isAddressPostcodeValid

        // THEN
        XCTAssertFalse(result)
    }

}

private extension PreStayInteractorTests {
    var ukAddressValidPostcode: Address {
        try! Address(
            dictionary: [
                "addressline1": "Royal Victoria Dock",
                "addressline2": "2 Festoon Way",
                "addressline3": "London",
                "countryCode": "GB",
                "postcode": "E16 1SJ"
            ]
        )
    }

    var ukAddressInvalidPostcode: Address {
        try! Address(
            dictionary: [
                "addressline1": "Royal Victoria Dock",
                "addressline2": "2 Festoon Way",
                "addressline3": "London",
                "countryCode": "GB",
                "postcode": "12345"
            ]
        )
    }

    var germanyAddressValidPostcode: Address {
        try! Address(
            dictionary: [
                "addressline1": "Bayern Munich street",
                "addressline2": "next to the stadium where bayern plays",
                "addressline3": "Munich",
                "countryCode": "D",
                "postcode": "10115"
            ]
        )
    }

    var germanyAddressInvalidPostcode: Address {
        try! Address(
            dictionary: [
                "addressline1": "Bayern Munich street",
                "addressline2": "next to the stadium where bayern plays",
                "addressline3": "Munich",
                "countryCode": "D",
                "postcode": "TW4 5HJ"
            ]
        )
    }
}
