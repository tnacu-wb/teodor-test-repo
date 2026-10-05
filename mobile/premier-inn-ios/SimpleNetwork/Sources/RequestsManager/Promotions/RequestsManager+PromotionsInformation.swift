//
//  RequestsManager+PromotionsInformation.swift
//  SimpleNetwork
//
//  Created by Rodrigues, Seymour (Contractor) on 05/02/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

public extension RequestsManager {
    func getPromotionsInformation(
        criteria: PromotionsInformationCriteria,
        completion: @escaping (_ response: PromotionsInformation?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.getPromotionsInformation(criteria: criteria)

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }
}
