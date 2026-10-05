//
//  StandardButtonStyle.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 22/01/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SwiftUI

struct StandardButtonStyle: ButtonStyle {
    @Environment(\.isEnabled) private var isEnabled
    var isLoading: Bool = false
    var labelFont: UIFont = .Heading3_Semibold()
    var labelColor: UIColor = .BaseWhite

    var verticalPadding: CGFloat = ViewConstants.Spacing.small
    var cornerRadius: CGFloat = ViewConstants.CornerRadius.xSmall
    var backgroundColor: UIColor = .Tint1

    var borderColor: UIColor = .clear
    var borderWidth: CGFloat = ViewConstants.BorderWidth.none

    func makeBody(configuration: Configuration) -> some View {
        let pressed = configuration.isPressed

        ZStack {
            configuration.label
                .font(Font(labelFont))
                .foregroundColor(Color(labelColor))
                .opacity(
                    isLoading
                    ? ViewConstants.Opacity.transparent
                    : ViewConstants.Opacity.full
                )

            if isLoading {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: .white))
                    .scaleEffect(1.0)
            }
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, verticalPadding)
        .background(
            RoundedRectangle(cornerRadius: cornerRadius)
                .fill(Color(backgroundColor))
                .opacity(
                    !isEnabled
                    ? ViewConstants.Opacity.low
                    : (pressed ? ViewConstants.Opacity.medium : ViewConstants.Opacity.full)
                )
        )
        .overlay(
            RoundedRectangle(cornerRadius: cornerRadius)
                .stroke(Color(borderColor), lineWidth: borderWidth)
        )
        .contentShape(RoundedRectangle(cornerRadius: cornerRadius))
        .allowsHitTesting(!isLoading && isEnabled)
        .animation(.easeOut(duration: 0.15), value: pressed)
        .animation(.easeOut(duration: 0.15), value: isLoading)
    }
}
