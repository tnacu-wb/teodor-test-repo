//
//  RequestsManager+UpdateCiolStatus.swift
//  SimpleNetwork
//
//  Created by Rodrigues, Seymour (Contractor) on 11/03/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

public extension RequestsManager {
    /// The purpose of this request is to update the Udfc20 criteria on the BE which updates Opera.
    /// The response received from BE has no use for mobile client.
    ///
    /// [CTECH-5851](https://whitbreadis.atlassian.net/browse/CTECH-5851)
    func updateCiolStatus(
        payload: UpdateCiolStatusPayload,
        completion: @escaping (_ response: UpdateCiolStatusResponse?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.updateCiolStatus(payload: payload)
            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }
}
