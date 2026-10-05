//
//  OnboardingView.swift
//  PremierInn
//
//  Created by Santa Gurung on 18/10/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SwiftUI

struct OnboardingView: View {
    private typealias Styling = ViewConstants

    let onDismiss: () -> Void
    private let viewModel = OnboardingViewModel()

    var body: some View {
        VStack(spacing: Styling.Spacing.none) {
            Spacer()
            VStack(alignment: .leading, spacing: Styling.Spacing.none) {
                titleView
                descriptionView
                informationView
                signInOptionsView
            }
            .padding(.horizontal, Styling.Spacing.large)
            .padding(.bottom, Styling.Spacing.medium)
        }
        .toolbar(.hidden)
        .onAppear {
            viewModel.trackAnalytics(analyticsManager: AnalyticsManager.shared)
        }
    }

    // MARK: - Subviews

    private var titleView: some View {
        Text(viewModel.title)
            .fixedSize(horizontal: false, vertical: true)
            .uiFont(.Heading1_Bold_custom(40))
            .foregroundStyle(Color(.BasePurple))
            .padding(.bottom, 40)
    }

    private var descriptionView: some View {
        Text(viewModel.description)
            .fixedSize(horizontal: false, vertical: true)
            .uiFont(.Heading3_Regular())
            .foregroundStyle(Color(.TintD1))
            .padding(.bottom, Styling.Spacing.medium)
    }

    private var informationView: some View {
        VStack(alignment: .leading) {
            ForEach(viewModel.onboardingList, id: \.self) { text in
                OnboardingInformationView(text: text, imageResourceName: "tick")
            }
        }
        .padding(.bottom, Styling.Spacing.xxLarge)
    }

    @ViewBuilder private var signInOptionsView: some View {
        let createAccountButton = OnboardingButtonType.createAccount(onDismissOnboarding: onDismiss)
        let loginButton = OnboardingButtonType.login(onDismissOnboarding: onDismiss)

        VStack(spacing: Styling.Spacing.small) {
            OnboardingButtonView(button: createAccountButton)
            OnboardingButtonView(button: loginButton)
            skipSignInButtonView
        }
    }

    private var skipSignInButtonView: some View {
        Button {
            onDismiss()
        } label: {
            Text(viewModel.continueWithout)
                .padding(Styling.Spacing.medium)
                .frame(maxWidth: .infinity)
                .uiFont(.Body_Medium())
                .foregroundStyle(Color(.BasePurple))
        }
        .padding(.top, Styling.Spacing.medium)
    }
}

// Could extract to Utilities file
private extension View {
    func uiFont(_ uiFont: UIFont) -> some View {
        self.font(Font(uiFont))
    }
}

#Preview {
    OnboardingView(onDismiss: {})
}
