//
//  ValidateDiscountCodeUseCase.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 05/02/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SimpleNetwork

protocol ValidateDiscountCodeUseCaseType {
    func execute() async -> Result<ValidateDiscountCodeResult?>
    func execute(completion: @escaping (Result<ValidateDiscountCodeResult?>) -> Void)
}

struct ValidateDiscountCodeUseCase {
    private let criteria: PromotionsInformationCriteria
    private let service: RequestManagerValidateDiscountCodeProtocol

    init(
        criteria: PromotionsInformationCriteria,
        service: RequestManagerValidateDiscountCodeProtocol
    ) {
        self.criteria = criteria
        self.service = service
    }
}

extension ValidateDiscountCodeUseCase: ValidateDiscountCodeUseCaseType {
    func execute() async -> Result<ValidateDiscountCodeResult?> {
        await withCheckedContinuation { continuation in
            service.validateDiscountCode(criteria: criteria) { response, error in
                if let error {
                    continuation.resume(returning: .failure(error: error))
                } else {
                    continuation.resume(returning: .success(result: response))
                }
            }
        }
    }

    func execute(completion: @escaping (Result<ValidateDiscountCodeResult?>) -> Void) {
        service.validateDiscountCode(criteria: criteria) { response, error in
            if let error {
                completion(.failure(error: error))
            } else {
                completion(.success(result: response))
            }
        }
    }
}
