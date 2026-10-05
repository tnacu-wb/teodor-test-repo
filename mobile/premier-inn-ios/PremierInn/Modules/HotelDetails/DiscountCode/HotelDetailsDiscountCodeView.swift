//
//  HotelDetailsDiscountCodeView.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 22/01/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SwiftUI

struct HotelDetailsDiscountCodeView: View {
    @Environment(\.dismiss) var dismiss
    @FocusState private var isFocused

    @ObservedObject var viewModel: HotelDetailsDiscountCodeViewModel

    var body: some View {
        NavigationStack {
            VStack(alignment: .leading, spacing: 0) {
                textField
                if viewModel.isDiscountCodeValid {
                    voucherTagView
                }
            }
            .frame(maxHeight: .infinity, alignment: .top)
            .frame(maxWidth: .infinity)
            .safeAreaInset(edge: .bottom) { ctaButton }
            .standardModalToolbar(
                title: PILocalizedString("hotelDetailsDiscountCodeTitle")
            )
            .keyboardDoneToolbar(isFocused: $isFocused)
            .contentShape(Rectangle())
            .onTapGesture { isFocused = false }
            .allowsHitTesting(!viewModel.isLoading)
        }
        .onChange(of: viewModel.textFieldViewState) { handleFeedbackHaptic(for: $0) }
        .onChange(of: viewModel.dismissModal) { handleDismiss($0) }
        .onAppear {
            viewModel.trackScreenLoad()
            if !viewModel.isDiscountCodeValid { isFocused = true }
        }
        .onDisappear { viewModel.handleModalClosed() }
    }
}

// MARK: - Subviews

private extension HotelDetailsDiscountCodeView {
    var textField: some View {
        StandardTextFieldView(
            viewState: $viewModel.textFieldViewState,
            text: $viewModel.discountCodeText,
            placeholderText: PILocalizedString("hotelDetailsDiscountCodeTextFieldPlaceholder"),
            onTextChanged: { newValue, previousValue in
                viewModel.handleTextInputNotification(newValue: newValue, previousValue: previousValue)
            }
        )
        .focused($isFocused)
        .noWhitespace($viewModel.discountCodeText)
        .textInputAutocapitalization(.characters)
        .onTapGesture { isFocused = true }
    }

    var ctaButton: some View {
        Button(viewModel.ctaText) {
            Task { @MainActor in
                generateLightFeedbackHaptic()
                isFocused = false
                await viewModel.ctaButtonTapped()
            }
        }
        .buttonStyle(StandardButtonStyle(isLoading: viewModel.isLoading))
        .padding(.bottom, 15)
        .padding(.horizontal, 26)
        .disabled(viewModel.isCtaButtonDisabled)
        .accessibilityIdentifier(AccessibilityIdentifiers.DiscountCode.applyButton)
    }

    var voucherTagView: some View {
        VoucherTagView(
            title: viewModel.validDiscountCode ?? String(),
            closeAction: {
                generateLightFeedbackHaptic()
                viewModel.tapVoucherTagCloseButton()
            }
        )
        .padding(.leading, 16)
        .padding(.top, 15)
    }
}

// MARK: - Event handlers

private extension HotelDetailsDiscountCodeView {
    func handleDismiss(_ shouldDismiss: Bool) {
        if shouldDismiss {
            dismiss()
            viewModel.dismissModal = false
        }
    }
}

// MARK: - Haptics

private extension HotelDetailsDiscountCodeView {
    func generateLightFeedbackHaptic() {
        NotificationFeedbackManager.shared.provideLightTapFeedback()
    }

    func generateErrorFeedbackHaptic() {
        NotificationFeedbackManager.shared.provideFeedback(for: .error)
    }

    func generateSuccessFeedbackHaptic() {
        NotificationFeedbackManager.shared.provideFeedback(for: .success)
    }

    func handleFeedbackHaptic(for viewState: StandardTextFieldView.ViewState?) {
        switch viewState {
        case .success: generateSuccessFeedbackHaptic()
        case .error: generateErrorFeedbackHaptic()
        case .none: break
        }
    }
}
