//
//  AdobeCampaignManager.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 27.02.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation
import AEPCore
import AEPCampaignClassic
import UIKit

class AdobeCampaignManager {
    // MARK: - Singleton

    static let shared = AdobeCampaignManager()
    static let apnsTokenKey: String = "apnsTokenKey"
    static let fcmTokenKey: String = "fcmTokenKey"

    // MARK: - Properties

    var apnsTokenString: String? {
        apnsToken?.map { String(format: "%02.2hhx", $0) }.joined()
    }

    var apnsToken: Data? {
        get {
            UserDefaults.standard.data(forKey: AdobeCampaignManager.apnsTokenKey)
        }
        set {
            UserDefaults.standard.setValue(newValue, forKey: AdobeCampaignManager.apnsTokenKey)
        }
    }

    var fcmToken: String? {
        get {
            UserDefaults.standard.string(forKey: AdobeCampaignManager.fcmTokenKey)
        }
        set {
            UserDefaults.standard.setValue(newValue, forKey: AdobeCampaignManager.fcmTokenKey)
        }
    }


    // MARK: - Lifecycle

    func setup() {
        let extenstions = [CampaignClassic.self]

        UIApplication.shared.registerForRemoteNotifications()

        MobileCore.registerExtensions(extenstions, {
            MobileCore.configureWith(appId: AnalyticsConstants.appId)
#if DEV
            MobileCore.setLogLevel(.error)
#endif
        })
    }

    func register(userKey: String?) {
        var info = PIDictionary()
        info["loggedIn"] = true
        if let apnsToken = apnsToken,
           let contactChannelId = userKey {
            CampaignClassic.registerDevice(
                token: apnsToken,
                userKey: contactChannelId,
                additionalParameters: info
            )
        }
    }

    func unregister(userKey: String?) {
        var info = PIDictionary()
        info["loggedIn"] = false
        if let apnsToken = apnsToken,
           let contactChannelId = userKey {
            CampaignClassic.registerDevice(
                token: apnsToken,
                userKey: contactChannelId,
                additionalParameters: info
            )
        }
    }

    func trackNotificationClick(userInfo: PIDictionary) {
        CampaignClassic.trackNotificationClick(withUserInfo: userInfo)
    }
}
