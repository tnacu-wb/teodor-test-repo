//
//  VenueCellBannerType.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 30/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SimpleNetwork

// MARK: - Banner Types

enum VenueCellBannerType {
    case hub
    case zip
    case agp
}

// MARK: - Hotel Brand Mapping

extension HotelBrand {
    var bannerType: VenueCellBannerType? {
        switch self {
        case .hub: .hub
        case .zip: .zip
        case .premierInn, .premierInnGermany: nil
        }
    }
}
