//
//  DefaultAnalyticsData.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 16/02/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

// MARK: - Protocol

protocol DefaultAnalyticsData {
    var userSession: UserSessionManagerProtocol { get }
    var environment: String { get }
    var loggedIn: LoggedInAnalytic { get }
    var timeZone: String { get }
    var language: String { get }
    var screenType: String { get }
    var userID: String { get }
    var time: String { get }

    func mergingDefaultValues(with dict: PIDictionary?, analytics: AnalyticsType) -> PIDictionary
}

// MARK: - Default

extension DefaultAnalyticsData {
    var userSession: UserSessionManagerProtocol { UserSessionManager.sharedInstance }

    var environment: String { AnalyticsConstants.environment }

    var loggedIn: LoggedInAnalytic { userSession.currentUser != nil ? .loggedIn : .notLoggedIn }

    var timeZone: String { TimeZone.current.description }

    var language: String { Locale.current.language.languageCode?.identifier ?? "n/a" }

    var screenType: String { "PI_DEV" }

    var userID: String { userSession.currentUser?.customerAccountId ?? "" }

    var time: String { Date().analyticsTimeFormat }

    func mergingDefaultValues(with dict: PIDictionary?, analytics: AnalyticsType) -> PIDictionary {
        analyticsDict(analytics: analytics)
            .merging(dict ?? [:], uniquingKeysWith: { (_, last) in last })
    }
}

// MARK: - Private Helpers

private extension DefaultAnalyticsData {
    func analyticsDict(analytics: AnalyticsType) -> PIDictionary {
        var dict = [
            PIAnalytics.Keys.environment: environment,
            PIAnalytics.Keys.userLogin: loggedIn.rawValue,
            PIAnalytics.Keys.timeZone: timeZone,
            PIAnalytics.Keys.language: language,
            PIAnalytics.Keys.screenType: screenType,
            PIAnalytics.Keys.userID: userID,
            PIAnalytics.Keys.time: time
        ]

        if let user = userSession.currentUser, let companyId = user.companyId, let accessLevel = user.accessLevel?.rawValue {
            dict[PIAnalytics.Keys.companyID] = companyId
            dict[PIAnalytics.Keys.businessUserLevel] = accessLevel
        }

        // Deep link tracking campaign properties
        var campaignAttribution = analytics.campaignAttribution
        if let campaign = campaignAttribution.remove() {
            dict[PIAnalytics.Keys.sCampaign] = campaign.cid
            dict[PIAnalytics.Keys.mckv] = campaign.mckv
            dict[PIAnalytics.Keys.etRid] = campaign.etRid
            dict[PIAnalytics.Keys.sFullURL] = campaign.fullURLString
            dict[PIAnalytics.Keys.sReferrer] = campaign.referrerURLString
        }

        return dict
    }
}
