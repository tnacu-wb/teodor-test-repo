//
//  MockValidateDiscountCodeUseCase.swift
//  PremierInnTests
//
//  Created by Rodrigues, Seymour (Contractor) on 06/02/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
@testable import PremierInn

struct MockValidateDiscountCodeUseCase: ValidateDiscountCodeUseCaseType {
    private let mockResult: Result<ValidateDiscountCodeResult?>

    init(mockResult: Result<ValidateDiscountCodeResult?>) {
        self.mockResult = mockResult
    }

    func execute() async -> Result<ValidateDiscountCodeResult?> {
        mockResult
    }

    func execute(completion: @escaping (Result<ValidateDiscountCodeResult?>) -> Void) {
        completion(mockResult)
    }
}
