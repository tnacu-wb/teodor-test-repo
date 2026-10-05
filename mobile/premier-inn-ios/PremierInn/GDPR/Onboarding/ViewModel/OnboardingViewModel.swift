//
//  OnboardingViewModel.swift
//  PremierInn
//
//  Created by Santa Gurung on 18/10/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

struct OnboardingViewModel {
    let title = PILocalizedString("onboardingTitle")
    let description = PILocalizedString("onboardingDescription")
    let createAccount = PILocalizedString("myAccountRegisterButtonTitle")
    let login = PILocalizedString("loginButtonTitle")
    let continueWithout = PILocalizedString("continueWithout")
    let onboardingList: [String] = [
        PILocalizedString("onboardingStepOne"),
        PILocalizedString("onboardingStepTwo"),
        PILocalizedString("onboardingStepThree")
    ]

    func trackAnalytics(analyticsManager: AnalyticsType) {
        let data: [String: Any] = [
            PIAnalytics.Keys.environment: AnalyticsConstants.environment,
            PIAnalytics.Keys.timeZone: TimeZone.current.description,
            PIAnalytics.Keys.language: Locale.current.language.languageCode?.identifier ?? "n/a",
            PIAnalytics.Keys.screenType: PIAnalytics.StateTypes.GDPR
        ]

        analyticsManager.trackState(PIAnalytics.StateNames.onboardingView, data: data)
    }
}
