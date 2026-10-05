//
//  RequestsManager+CiolBackgroundCharge.swift
//  SimpleNetwork
//
//  Created by Rodrigues, Seymour (Contractor) on 29/06/2026.
//

import Foundation

public extension RequestsManager {
    func ciolBackgroundCharge(
        basketReference: String,
        token: String,
        completion: @escaping (
            _ response: CiolBackgroundChargeResponse?,
            _ error: Error?
        ) -> Void
    ) {
        do {
            let resource = try Router
                .current
                .ciolBackgroundCharge(
                    basketReference: basketReference,
                    token: token
                )

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }
}
