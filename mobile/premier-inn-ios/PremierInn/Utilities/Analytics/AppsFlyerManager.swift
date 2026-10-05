//
//  AppsFlyerManager.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 09/01/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Foundation
import AppsFlyerLib

enum AppsFlyerConstants {
    static let devKey = "fBLb5YoVLGfgmMTxfY5RNU"
#if DEV
    static let appId = "id1135399469"
#else
    static let appId = "id602110169"
#endif
}

class AppsFlyerManager {
    static let sharedInstance: AppsFlyerManager = AppsFlyerManager()

    private let appsFlyer: AppsFlyerLib = AppsFlyerLib.shared()

    // custom event names
    private static let AFEventStartCiol: String = "af_ciol_start"
    private static let AFEventCompleteCiol: String = "af_ciol_complete"

    func setUp(delegate: AppDelegate) {
        appsFlyer.appsFlyerDevKey = AppsFlyerConstants.devKey
        appsFlyer.appleAppID = AppsFlyerConstants.appId

#if DEV
        // debug logs
        appsFlyer.isDebug = true
#endif

        appsFlyer.deepLinkDelegate = delegate
    }

    func integrate(adobeCustomerId: String?) {
        appsFlyer.customerUserID = adobeCustomerId
    }

    func start() {
        appsFlyer.start()
    }

    func trackBookingConfirmation(parameters: PIDictionary) {
        guard let reference = parameters[AppsFlyerAnalytics.Parameter.bookingReference],
              let revenue = parameters[AppsFlyerAnalytics.Parameter.revenue],
              let currency = parameters[AppsFlyerAnalytics.Parameter.currency],
              let hotelCode = parameters[AppsFlyerAnalytics.Parameter.hotelCode] else { return }

        appsFlyer.logEvent(name: AFEventPurchase, values: [
            AFEventParamOrderId: reference,
            AFEventParamRevenue: revenue,
            AFEventParamContentType: AppsFlyerAnalytics.Parameter.hotel,
            AFEventParamCurrency: currency,
            AFEventParamContentId: hotelCode
        ])
    }

    func trackStartCiol(reference: String, hotelCode: String) {
        appsFlyer.logEvent(name: AppsFlyerManager.AFEventStartCiol, values: [
            AFEventParamOrderId: reference,
            AFEventParamContentId: hotelCode
        ])
    }

    func trackCiolComplete(reference: String, hotelCode: String) {
        appsFlyer.logEvent(name: AppsFlyerManager.AFEventCompleteCiol, values: [
            AFEventParamOrderId: reference,
            AFEventParamContentId: hotelCode
        ])
    }
}

extension AppDelegate: DeepLinkDelegate {
    func didResolveDeepLink(_ result: DeepLinkResult) {
        switch result.status {
        case .notFound:
            printDev("[AFSDK] Deep link not found")
            return
        case .failure:
            printDev("Error %@", result.error!)
            return
        case .found:
            printDev("[AFSDK] Deep link found")
        }

        guard let deepLinkObj = result.deepLink else {
            printDev("[AFSDK] Could not extract deep link object")
            return
        }

        if deepLinkObj.isDeferred == true {
            printDev("[AFSDK] This is a deferred deep link")
        } else {
            printDev("[AFSDK] This is a direct deep link")
        }

        let value = deepLinkObj.deeplinkValue
        let referrer = deepLinkObj.clickHTTPReferrer
        printDev("[AFSDK] value = \(String(describing: value))")

//        let secondaryValue = deepLinkObj.clickEvent["deep_link_sub1"] as? String

        DispatchGroupManager.sharedInstance.appShortcutsDispatchGroup.notify(queue: .main) {
            var appShortcut: AppShortcut?

            if let value,
               let url = URL(string: value),
               let shortcut = LinkHandler.appShortcut(for: url, referrer: referrer) {
                appShortcut = shortcut
            } else if let link = deepLinkObj.clickEvent["link"] as? String,
                      let url = URL(string: link),
                      let shortcut = LinkHandler.appShortcut(for: url, referrer: referrer) {
                appShortcut = shortcut
            }

            guard let appShortcut else { return }

            self.handleShortcut(appShortcut)
        }
    }
}

// MARK: - DeepLinking

extension AppDelegate {
    func handleShortcut(_ shortcut: AppShortcut?) {
        guard let shortcut else { return }

        LinkHandler.sharedInstance.activeAppShortcut = shortcut
    }
}
