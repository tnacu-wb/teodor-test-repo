//
//  RequestsManager+ValidateDiscountCode.swift
//  SimpleNetwork
//
//  Created by Rodrigues, Seymour (Contractor) on 05/02/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

public protocol RequestManagerValidateDiscountCodeProtocol {
    func validateDiscountCode(
        criteria: PromotionsInformationCriteria,
        completion: @escaping (_ response: ValidateDiscountCodeResult?, _ error: Error?) -> Void
    )
}

extension RequestsManager: RequestManagerValidateDiscountCodeProtocol {
    public func validateDiscountCode(
        criteria: PromotionsInformationCriteria,
        completion: @escaping (_ response: ValidateDiscountCodeResult?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.validateDiscountCode(criteria: criteria)

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }
}
