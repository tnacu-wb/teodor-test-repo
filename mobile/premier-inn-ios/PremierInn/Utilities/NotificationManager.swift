//
//  NotificationManager.swift
//  PremierInn
//
//  Created by Freddie Parks on 08/05/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation
import UIKit
import UserNotifications

enum PushNotificationContent: String {
    case banner
    case hotelDetails = "hotel_details"
    case locationSearch = "location_search"
    case ciolReady = "ciol"
    case leaveEasy = "leaveEasy"
}

protocol NotificationManaging {
    func checkNotificationPermission(completion: @escaping (Bool) -> Void)
    func openSettings()
}

final class NotificationManager: NotificationManaging {
    static let shared = NotificationManager()
    private let notificationCenter = UNUserNotificationCenter.current()

    private init() {}

    func showLocalNotification(
        withTitle title: String,
        body: String,
        delay: TimeInterval = TimeInterval(0)
    ) {
        let notification = UNMutableNotificationContent()
        notification.title = title
        notification.body = body

        // We should be able to delay trigger but UNTimeIntervalNotificationTrigger always crashes
        // let trigger = UNTimeIntervalNotificationTrigger(timeInterval: delay, repeats: false)

        let request = UNNotificationRequest(identifier: "bookingConfirmation", content: notification, trigger: nil)

        notificationCenter.add(request, withCompletionHandler: { _ in
            // I guess you could do something here if you care
        })
    }

    func requestPermissions() {
        notificationCenter.delegate = UIApplication.shared.delegate as? AppDelegate
        let authOptions: UNAuthorizationOptions = [.alert, .sound]
        notificationCenter.requestAuthorization(options: authOptions, completionHandler: { _, error in
            guard error == nil else { return }
            // I guess you could do something here if you care
        })

        UIApplication.shared.registerForRemoteNotifications()
    }

    func checkNotificationPermission(
        completion: @escaping (Bool) -> Void
    ) {
        notificationCenter.getNotificationSettings { settings in
            let isAllowed = settings.authorizationStatus == .authorized
            DispatchQueue.main.async {
                completion(isAllowed)
            }
        }
    }

    func openSettings() {
        if let url = URL(string: UIApplication.openSettingsURLString) {
            UIApplication.shared.open(url)
        }
    }
}
