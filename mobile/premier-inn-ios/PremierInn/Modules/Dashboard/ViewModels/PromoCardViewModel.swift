//
//  PromoCardViewModel.swift
//  PremierInn
//
//  Created by Santa Gurung on 29/11/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SwiftUI

struct PromoCardViewModel: Identifiable {
    let id = UUID()
    let imageName: String
    let title: String
    let description: String?
    let tag: String?
    let url: URL?
    let openLinkInApp: Bool?
    let analyticsKey: String?
    let order: Int

    var imageUrl: URL? {
        Constants.imageBaseUrl?.appendingPathComponent(imageName)
    }

    func trackPromoTapped() {
        guard let analyticsKey else { return }
        AnalyticsManager.shared.trackAction(analyticsKey, userInfo: [
            PIAnalytics.Keys.dashboardCardClick: true,
            PIAnalytics.Keys.dashboardCardAction: analyticsKey
        ])
    }
}
