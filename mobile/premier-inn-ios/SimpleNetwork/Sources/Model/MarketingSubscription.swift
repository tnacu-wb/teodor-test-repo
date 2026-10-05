//
//  MarketingSubscription.swift
//  SimpleNetwork
//
//  Created by Freddie Parks on 09/10/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation

public enum MarketingBrandCode: String {
    case premierInn = "PINN"
    case hub = "PHUB"
}

public struct BrandPermission: Codable {
    let brandCode: String?
    let optIn: Bool?
    let suppressMarketingCheckbox: Bool?

    public init(brandCode: String?, optIn: Bool?, suppressMarketingCheckbox: Bool?) {
        self.brandCode = brandCode
        self.optIn = optIn
        self.suppressMarketingCheckbox = suppressMarketingCheckbox
    }
}

public struct MarketingPreferences: Codable {
    let contactChannelId: String?
    let permissions: [BrandPermission]?

    public init(contactChannelId: String?, permissions: [BrandPermission]?) {
        self.contactChannelId = contactChannelId
        self.permissions = permissions
    }
}
