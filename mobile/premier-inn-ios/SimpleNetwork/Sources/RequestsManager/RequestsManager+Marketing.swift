//
//  RequestsManager+Marketing.swift
//  SimpleNetwork
//
//  Created by Freddie Parks on 09/10/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation

public extension RequestsManager {
    func getMarketingPreferences(
        for emailAddress: String,
        and brands: MarketingBrandCode,
        isBusiness: Bool,
        completion: @escaping (_ data: MarketingPreferences?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.getMarketingPreferences(for: emailAddress, and: brands, isBusiness: isBusiness)

            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }

    func updateMarketingPreferences(
        for brands: [MarketingBrandCode],
        and emailAddress: String,
        optin: Bool,
        isoCountryCode: String,
        completion: @escaping (_ success: Bool, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.updateMarketingPreferences(
                brands: brands,
                emailAddress: emailAddress,
                optIn: optin,
                isoCountryCode: isoCountryCode
            )

            load(resource: resource) { result, error in
                completion(result ?? false, error)
            }
        } catch {
            completion(false, error)
        }
    }
}
