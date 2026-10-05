//
//  RoundedTextLabelView.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 02/07/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SwiftUI

struct RoundedTextLabelView: View {
    let text: String
    let accessibilityIdentifier: String
    let font: UIFont
    let textColor: UIColor
    let backgroundColor: UIColor
    let cornerRadius: CGFloat
    let verticalPadding: CGFloat
    let horizontalPadding: CGFloat

    var body: some View {
        Text(text)
            .font(Font(font))
            .foregroundStyle(Color(textColor))
            .padding(.vertical, verticalPadding)
            .padding(.horizontal, horizontalPadding)
            .background(Color(backgroundColor))
            .clipShape(RoundedRectangle(cornerRadius: cornerRadius))
            .accessibilityIdentifier(accessibilityIdentifier)
    }
}
