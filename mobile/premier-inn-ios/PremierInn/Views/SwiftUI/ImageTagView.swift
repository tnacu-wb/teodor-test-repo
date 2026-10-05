//
//  ImageTagView.swift
//  PremierInn
//
//  Created by Santa Gurung on 24/02/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import SwiftUI

struct ImageTagView: View {
    private let tag: String
    private let backgroundColor: Color
    private let textColor: Color
    private let font: UIFont
    private let borderColor: Color?
    private let borderWidth: CGFloat

    init(
        tag: String,
        backgroundColor: Color,
        textColor: Color,
        font: UIFont = UIFont.SubtextSmall_Bold(),
        borderColor: Color? = nil,
        borderWidth: CGFloat = 0
    ) {
        self.tag = tag
        self.backgroundColor = backgroundColor
        self.textColor = textColor
        self.font = font
        self.borderColor = borderColor
        self.borderWidth = borderWidth
    }

    var body: some View {
        Text(tag)
            .lineLimit(1)
            .font(font)
            .foregroundStyle(textColor)
            .padding(.horizontal, ViewConstants.Spacing.small)
            .padding(.vertical, ViewConstants.Spacing.xSmall)
            .background(backgroundColor)
            .clipShape(.capsule)
            .overlay(
                Capsule()
                    .stroke(borderColor ?? .clear, lineWidth: borderWidth)
            )
    }
}

#Preview {
    VStack {
        ImageTagView(
            tag: "FX20RU",
            backgroundColor: .gray,
            textColor: .primary
        )

        ImageTagView(
            tag: "New Hotel",
            backgroundColor: .clear,
            textColor: Color(.DarkPurple),
            font: .Body_Semibold(),
            borderColor: Color(.DarkPurple),
            borderWidth: 1
        )
    }
}
