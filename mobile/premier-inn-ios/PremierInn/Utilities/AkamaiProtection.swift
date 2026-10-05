//
//  AkamaiProtection.swift
//  PremierInn
//
//  Created by Santa Gurung on 28/10/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import AkamaiBMP

enum AkamaiProtection {
    static func configureSDK(baseURLString: String?) {
        guard let appDelegate = UIApplication.shared.delegate as? AppDelegate else {
            printDev("No app delegate")
            return
        }

        guard let baseURLString else {
            printDev("No url to configure SDK with")
			AnalyticsManager.shared.track(errorName: PIAnalytics.Error.akamaiSDKNoUrl)
            return
        }

        CYFSDKMonitor.enableBackground()

        // Set init callback before SDK initialization
        CYFSDKMonitor.setInitSDKCallBack(delegate: appDelegate)

        // Initialize SDK
        CYFSDKMonitor.configureSDK(url: "\(baseURLString)")
        printDev("BASEURL - \(baseURLString)")
    }

	static var sensorData: String {
		CYFSDKMonitor.getSensorData()
	}
}

extension AppDelegate: CYFSDKInitializationDelegate {
    func onSDKInitSuccess() {
        printDev("AkamaiSDK init success")
    }

    func onSDKInitFailure(message: String) {
        printDev("AkamaiSDK init fail - \(message)")
		AnalyticsManager.shared.track(errorName: PIAnalytics.Error.akamaiSDKInitFail)
    }
}
