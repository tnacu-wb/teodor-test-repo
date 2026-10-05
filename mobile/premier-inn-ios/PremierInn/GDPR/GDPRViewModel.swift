//
//  GDPRViewModel.swift
//  PremierInn
//
//  Created by Santa Gurung on 17/10/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SwiftUI
import SimpleNetwork

struct GDPROption: Identifiable {
    let id = UUID()
    let title: String
    let iconImage: Image
    let destination: AnyView
}

struct GDPRViewModel {
    let title = PILocalizedString("gdprInterstitialHeader")
    let description = PILocalizedString("privacyInterstitialMessage")
    let acceptButtonLabel = PILocalizedString("gdprAcceptButtonTitle")
    let listRows = [
        GDPROption(
            title: PILocalizedString("gdprDataPolicyRowTitle"),
            iconImage: Image("techWeUseIcon"),
            destination: AnyView(TechnologiesWeUseViewWrapper())
        ),
        GDPROption(
            title: PILocalizedString("gdprViewPrivacyPolicyButtonTitle"),
            iconImage: Image("padlock"),
            destination: AnyView(WKWebViewWrapper(url: Constants.privacyPolicyUrl))
        )
    ]

    func trackAnalytics(analyticsManager: AnalyticsType) {
        let data: [String: Any] = [
            PIAnalytics.Keys.environment: AnalyticsConstants.environment,
            PIAnalytics.Keys.userLogin: UserSessionManager.sharedInstance.currentUser != nil ? LoggedInAnalytic
            .loggedIn : LoggedInAnalytic.notLoggedIn,
            PIAnalytics.Keys.timeZone: TimeZone.current.description,
            PIAnalytics.Keys.language: Locale.current.language.languageCode?.identifier ?? "n/a",
            PIAnalytics.Keys.screenType: PIAnalytics.StateTypes.GDPR
        ]

        analyticsManager.trackState(PIAnalytics.StateNames.gdprInterstitial, data: data)
    }

    func acceptGDPRChanges() {
        if NSClassFromString("EarlGreyImpl") == nil {
            NotificationManager.shared.requestPermissions()
        }
        UserDefaults.standard.set(true, forKey: Constants.hasAcceptedGDPRChanges)
    }
}
