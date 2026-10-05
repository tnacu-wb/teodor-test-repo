//
//  AppDelegate+Notifications.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 11/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit

// MARK: - UNUserNotificationCenterDelegate

extension AppDelegate: UNUserNotificationCenterDelegate {
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        completionHandler([UNNotificationPresentationOptions.list, UNNotificationPresentationOptions.banner])
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        let userInfo = PushPayloadParser.extract(from: response.notification.request.content.userInfo)
        let notification = LinkHandler.handleCustomPushPayload(userInfo: userInfo)

        LinkHandler.sharedInstance.activeAppShortcut = notification

        // extra ciol tracking to adobe classic?
        trackCampaignNotificationClick(userInfo: userInfo)

        completionHandler()
    }

    func trackCampaignNotificationClick(userInfo: PIDictionary) {
        AdobeCampaignManager.shared.trackNotificationClick(userInfo: userInfo)
    }

    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        AdobeCampaignManager.shared.apnsToken = deviceToken
    }

    func application(
        _ application: UIApplication,
        didReceiveRemoteNotification userInfo: [AnyHashable: Any],
        fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void
    ) {
        // If you are receiving a notification message while your app is in the background,
        // this callback will not be fired till the user taps on the notification launching the application.
        // TODO: Handle data of notification

        if userInfo.index(forKey: FirebaseConstants.notificationKeys.configState) != nil {
            printDev("Config set to stale")
            UserDefaults.standard.set(true, forKey: FirebaseConstants.localStorageKeys.configStale)
        }

        // With swizzling disabled you must let Messaging know about the message, for Analytics
        // Messaging.messaging().appDidReceiveMessage(userInfo)

        // Print full message.
        printDev(userInfo)

        let userInfoFiltered = PushPayloadParser.extract(from: userInfo)
        let notification = LinkHandler.handleCustomPushPayload(userInfo: userInfoFiltered)

        LinkHandler.sharedInstance.activeAppShortcut = notification

        completionHandler(UIBackgroundFetchResult.newData)
    }
}
