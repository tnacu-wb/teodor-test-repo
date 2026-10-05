//
//  AppDelegate+MessagingDelegate.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 11/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import FirebaseMessaging

// MARK: - MessagingDelegate

extension AppDelegate: MessagingDelegate {
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        messaging.subscribe(toTopic: FirebaseConstants.cloudMessaging.configTopic) { error in
            error == nil
            ? print("Subscribed to \(FirebaseConstants.cloudMessaging.configTopic) topic")
            : print("Failed to subscribe to \(FirebaseConstants.cloudMessaging.configTopic)")
        }

        AdobeCampaignManager.shared.fcmToken = fcmToken
        printDev("fcmToken: \(String(describing: fcmToken))")

        // send token to analytics
        AnalyticsManager.shared.trackAction(
            PIAnalytics.Action.pushIdSet,
            userInfo: [PIAnalytics.Keys.pushId: fcmToken ?? ""]
        )
    }

    // FCM direct channel is deprecated, please use APNs for downstream message handling.
}
