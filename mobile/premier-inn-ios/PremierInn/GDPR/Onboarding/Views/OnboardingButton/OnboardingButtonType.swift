//
//  OnboardingButtonType.swift
//  PremierInn
//
//  Created by Santa Gurung on 18/10/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SwiftUI

enum OnboardingButtonType {
    case createAccount(onDismissOnboarding: () -> Void)
    case login(onDismissOnboarding: () -> Void)

    var title: String {
        switch self {
        case .createAccount: PILocalizedString("myAccountRegisterButtonTitle")
        case .login: PILocalizedString("loginButtonTitle")
        }
    }

    var foregroundColor: Color {
        switch self {
        case .createAccount: Color(.BaseWhite)
        case .login: Color(.BasePurple)
        }
    }

    var backgroundColor: Color {
        switch self {
        case .createAccount: Color(.Tint1)
        case .login: Color(.BaseWhite)
        }
    }

    var borderColor: Color {
        switch self {
        case .createAccount: Color(.Tint1)
        case .login: Color(.BasePurple)
        }
    }

    var destination: AnyView {
        switch self {
        case .createAccount(let onDismissOnboarding): AnyView(RegisterViewWrapper(onDismissOnboarding: onDismissOnboarding))
        case .login(let onDismissOnboarding): AnyView(LoginViewWrapper(onDismissOnboarding: onDismissOnboarding))
        }
    }
}
