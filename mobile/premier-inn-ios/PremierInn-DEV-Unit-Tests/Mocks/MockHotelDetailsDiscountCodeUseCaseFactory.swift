//
//  MockHotelDetailsDiscountCodeUseCaseFactory.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 06/02/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
@testable import PremierInn

struct MockHotelDetailsDiscountCodeUseCaseFactory: HotelDetailsDiscountCodeUseCaseFactoryType {
    private let mockedUseCase: ValidateDiscountCodeUseCaseType

    init(mockedUseCase: ValidateDiscountCodeUseCaseType) {
        self.mockedUseCase = mockedUseCase
    }

    func getValidateDiscountCodeUseCase(promotionCode: String) -> ValidateDiscountCodeUseCaseType {
        mockedUseCase
    }
}
