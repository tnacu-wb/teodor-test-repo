//
//  StandardTextFieldView.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 22/01/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SwiftUI

// MARK: - typealias

private typealias AccessibilityID = AccessibilityIdentifiers.StandardTextField

// MARK: - StandardTextFieldView

struct StandardTextFieldView: View {
    // MARK: - Properties

    @Binding private var viewState: ViewState?
    @Binding private var text: String
    private let placeholderText: String
    private let onTextChanged: ((String, String) -> Void)?

    private let placeholderFont: Font
    private let textFieldFont: Font
    private let infoTextFont: Font

    private let shouldShowTopDivider: Bool
    private let shouldShowBottomDivider: Bool

    private let placeholderTextAccessibilityId: String
    private let inputFieldAccessibilityId: String
    private let infoTextAccessibilityId: String

    init(
        viewState: Binding<ViewState?>,
        text: Binding<String>,
        placeholderText: String,
        placeholderFont: Font = Font(UIFont.BodySmall()),
        textFieldFont: Font = Font(UIFont.BodySmall()),
        infoTextFont: Font = Font(UIFont.BodySmall()),
        shouldShowTopDivider: Bool = true,
        shouldShowBottomDivider: Bool = true,
        placeholderTextAccessibilityId: String = AccessibilityID.placeholderText,
        inputFieldAccessibilityId: String = AccessibilityID.inputField,
        infoTextAccessibilityId: String = AccessibilityID.infoText,
        onTextChanged: ((String, String) -> Void)? = nil
    ) {
        self._viewState = viewState
        self._text = text
        self.placeholderText = placeholderText
        self.placeholderFont = placeholderFont
        self.textFieldFont = textFieldFont
        self.infoTextFont = infoTextFont
        self.shouldShowTopDivider = shouldShowTopDivider
        self.shouldShowBottomDivider = shouldShowBottomDivider
        self.placeholderTextAccessibilityId = placeholderTextAccessibilityId
        self.inputFieldAccessibilityId = inputFieldAccessibilityId
        self.infoTextAccessibilityId = infoTextAccessibilityId
        self.onTextChanged = onTextChanged
    }

    // MARK: - body

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            if shouldShowTopDivider {
                Divider()
                    .padding(.bottom, 25)
            }

            HStack(alignment: .center, spacing: 0) {
                placeholderTextView
                TextField("", text: textBinding)
                    .autocorrectionDisabled()
                    .font(textFieldFont)
                    .accessibilityIdentifier(inputFieldAccessibilityId)
            }
            .padding(.horizontal, 16)

            infoTextView
                .padding(.top, 15)
                .padding(.horizontal, 16)

            if shouldShowBottomDivider {
                Divider()
                    .padding(.top, 25)
            }
        }
        .contentShape(.rect)
    }
}

// MARK: - ViewState

extension StandardTextFieldView {
    enum ViewState: Equatable {
        case success(String), error(String)
    }
}

// MARK: - Subviews

private extension StandardTextFieldView {
    var textBinding: Binding<String> {
        Binding(
            get: { text },
            set: { newValue in
                let previousValue = text
                text = newValue
                onTextChanged?(newValue, previousValue)
            }
        )
    }

    var placeholderTextView: some View {
        Text(placeholderText)
            .font(placeholderFont)
            .foregroundStyle(Color(uiColor: .textFieldPlaceholder))
            .frame(width: 115, alignment: .leading)
            .padding(.trailing, 10)
            .accessibilityIdentifier(placeholderTextAccessibilityId)
    }

    @ViewBuilder
    var infoTextView: some View {
        if let viewState {
            switch viewState {
            case .error(let string),
                 .success(let string):
                Text(string)
                    .font(infoTextFont)
                    .foregroundStyle(infoTextColor)
                    .multilineTextAlignment(.leading)
                    .lineLimit(nil)
                    .animation(.easeOut(duration: 0.3), value: viewState)
                    .accessibilityIdentifier(infoTextAccessibilityId)
            }
        }
    }

    var infoTextColor: Color {
        switch viewState {
        case .success: Color(uiColor: .Tint4)
        case .error: Color(uiColor: .Tint8)
        case .none: .clear
        }
    }
}
