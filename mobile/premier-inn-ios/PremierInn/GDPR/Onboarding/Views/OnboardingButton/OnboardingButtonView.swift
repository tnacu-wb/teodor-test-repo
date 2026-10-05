//
//  OnboardingButtonView.swift
//  PremierInn
//
//  Created by Santa Gurung on 18/10/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SwiftUI

struct OnboardingButtonView: View {
    private typealias Styling = ViewConstants

    let button: OnboardingButtonType
    @State var isPresented: Bool = false

    var body: some View {
        Button {
            isPresented = true
        } label: {
            Text(button.title)
                .padding(Styling.Spacing.medium)
                .font(Font(UIFont.Heading3_Semibold()))
                .frame(maxWidth: .infinity)
                .foregroundStyle(button.foregroundColor)
                .background(button.backgroundColor)
                .cornerRadius(Styling.CornerRadius.xSmall)
                .overlay {
                    RoundedRectangle(cornerRadius: Styling.CornerRadius.xSmall)
                        .stroke(button.borderColor)
                }
        }
        .sheet(isPresented: $isPresented) {
            isPresented = false
        } content: {
            button.destination
        }
    }
}

#Preview {
    OnboardingButtonView(button: .login(onDismissOnboarding: {}))
}
