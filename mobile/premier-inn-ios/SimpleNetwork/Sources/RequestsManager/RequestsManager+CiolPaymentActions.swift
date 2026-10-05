//
//  RequestsManager+CiolPaymentActions.swift
//  SimpleNetwork
//
//  Created by Rodrigues, Seymour (Contractor) on 26/05/2026.
//

import Foundation

public extension RequestsManager {
    func ciolPaymentActions(
        basketReference: String,
        completion: @escaping (
            _ response: CiolPaymentActionsResponse?,
            _ error: Error?
        ) -> Void
    ) {
        do {
            let resource = try Router
                .current
                .ciolPaymentActions(basketReference: basketReference)

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }
}
