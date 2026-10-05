//
//  OnboardingListView.swift
//  PremierInn
//
//  Created by Santa Gurung on 18/10/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SwiftUI

struct OnboardingInformationView: View {
    private typealias Styling = ViewConstants

    var text: String
    var imageResourceName: String

    var body: some View {
        HStack(alignment: .top, spacing: Styling.Spacing.medium) {
            Image(imageResourceName)
                .resizable()
                .frame(
                    width: Styling.ImageSize.medium,
                    height: Styling.ImageSize.medium
                )
                .foregroundStyle(Color(.BasePurple))
            Text(text)
                .fixedSize(horizontal: false, vertical: true)
                .font(Font(UIFont.Heading3_Regular()))
                .foregroundStyle(Color(.TintD1))
        }
        .padding(.vertical, Styling.Spacing.small)
    }
}

#Preview {
    OnboardingInformationView(
        text: "Hello World",
        imageResourceName: "tick"
    )
}
