//
//  AnalyticsBootstrap.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 08/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

struct AnalyticsBootstrap: BootstrapTask {
    private let analyticsManager: AnalyticsType
    private let appsFlyerManager: AppsFlyerManager
    private let adobeCampaignManager: AdobeCampaignManager

    init(
        analyticsManager: AnalyticsType = AnalyticsManager.shared,
        appsFlyerManager: AppsFlyerManager = .sharedInstance,
        adobeCampaignManager: AdobeCampaignManager = .shared
    ) {
        self.analyticsManager = analyticsManager
        self.appsFlyerManager = appsFlyerManager
        self.adobeCampaignManager = adobeCampaignManager
    }

    func run() {
        analyticsManager.setup { visitorId in
            appsFlyerManager.integrate(adobeCustomerId: visitorId)
        }

        adobeCampaignManager.setup()
    }
}
