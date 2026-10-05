//
//  MockRequestManagerValidateDiscountCodeService.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 06/02/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

struct MockRequestManagerValidateDiscountCodeService: RequestManagerValidateDiscountCodeProtocol {
    private let result: ValidateDiscountCodeResult?
    private let error: Error?

    init(result: ValidateDiscountCodeResult?, error: Error?) {
        self.result = result
        self.error = error
    }

    func validateDiscountCode(
        criteria: PromotionsInformationCriteria,
        completion: @escaping (ValidateDiscountCodeResult?, (any Error)?) -> Void
    ) {
        completion(result, error)
    }
}
