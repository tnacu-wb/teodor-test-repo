//
//  HotelDetailsDiscountCodeUseCaseFactoryTests.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 06/02/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Testing
@testable import PremierInn

struct HotelDetailsDiscountCodeUseCaseFactoryTests {

    private var sut: HotelDetailsDiscountCodeUseCaseFactory!

    @Test
    mutating func testValidateDiscountCode() {
        // GIVEN sut
        sut = .init()

        // WHEN `getValidateDiscountCodeUseCase` is called
        let result = sut.getValidateDiscountCodeUseCase(promotionCode: "")

        // THEN
        #expect(result is ValidateDiscountCodeUseCase, "Should be of type `ValidateDiscountCodeUseCase`")
    }
}
