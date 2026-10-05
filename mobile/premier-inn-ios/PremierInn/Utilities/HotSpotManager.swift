//
//  HotSpotManager.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 29/11/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation
import NetworkExtension
import Network

enum HotSpotManager {
    static func connectToWifi(ssid: String) {
        let hotspotConfig = NEHotspotConfiguration(ssid: ssid)
        hotspotConfig.joinOnce = false
        AnalyticsManager.shared.log(event: FirebaseAnalytics.Event.wifiConnectionInitiated, parameters: ["ssid": ssid])
        NEHotspotConfigurationManager.shared.apply(hotspotConfig) { (error) in
            if let error = error {
                printDev("Error connecting to WiFi: \(error.localizedDescription)")
                trackFailedConnection(ssid: ssid, error: error)
                return
            } else {
                // I have observed that the wifi call can 'fail' and it still falls under connected, a hack around for this is to check if the user is connected to the internet after a couple of seconds. This is just for tracking purposes.
                DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                    checkWiFiConnection { isConnected in
                        if isConnected {
                            trackSuccessfulConnection(ssid: ssid)
                        } else {
                            trackFailedConnection(ssid: ssid)
                        }
                    }
                }
            }
        }
    }

    static func checkWiFiConnection(completion: @escaping (Bool) -> Void) {
        let monitor = NWPathMonitor()
        let queue = DispatchQueue.global(qos: .background)

        monitor.pathUpdateHandler = { path in
            if path.status == .satisfied && path.usesInterfaceType(.wifi) {
                completion(true)
            } else {
                completion(false)
            }
            monitor.cancel()
        }

        monitor.start(queue: queue)
    }


    private static func trackFailedConnection(ssid: String, error: Error? = nil) {
        AnalyticsManager.shared.log(
            event: FirebaseAnalytics.Event.wifiConnectionFailed,
            parameters: ["error": error?.localizedDescription ?? "", "ssid": ssid]
        )
        AnalyticsManager.shared.trackAction(
            PIAnalytics.Action.wifiConnectionFailed,
            userInfo: [PIAnalytics.Keys.inAppWifiConnected: false]
        )
    }

    private static func trackSuccessfulConnection(ssid: String) {
        AnalyticsManager.shared.log(event: FirebaseAnalytics.Event.wifiConnected, parameters: ["ssid": ssid])
        AnalyticsManager.shared.trackAction(
            PIAnalytics.Action.wifiConnected,
            userInfo: [PIAnalytics.Keys.inAppWifiConnected: true]
        )
    }
}
