//
//  RequestsManager+DatatransPayment.swift
//  PremierInn
//
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation

public extension RequestsManager {
    func initMobileSDKPayment(
        basketId: String,
        completion: @escaping (_ response: DatatransPaymentSessionResponse?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.initMobileSDKPayment(basketId: basketId)

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }
}
