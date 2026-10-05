//
//  StandardModalToolbarModifier.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 23/01/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SwiftUI

private struct StandardModalToolbarModifier: ViewModifier {
    @Environment(\.dismiss) private var dismiss

    let closeImageName: String
    let title: String
    let closeAccessibilityId: String
    let imageSize: CGFloat
    let closeAction: (() -> Void)?

    private let hapticsManager = NotificationFeedbackManager.shared

    func body(content: Content) -> some View {
        content
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Image(uiImage: UIImage(named: closeImageName)!)
                        .resizable()
                        .frame(width: imageSize, height: imageSize)
                        .onTapGesture {
                            hapticsManager.provideLightTapFeedback()
                            closeAction?()
                            dismiss()
                        }
                        .accessibilityIdentifier(closeAccessibilityId)
                }
            }
            .navigationTitle(title)
            .navigationBarTitleDisplayMode(.inline)
            .toolbarBackground(Color.clear, for: .navigationBar)
    }
}

extension View {
    func standardModalToolbar(
        closeImageName: String = "closeModal",
        title: String,
        closeAccessibilityId: String = AccessibilityIdentifiers.StandardModalToolbar.closeImage,
        imageSize: CGFloat = 24,
        closeAction: (() -> Void)? = nil
    ) -> some View {
        self.modifier(
            StandardModalToolbarModifier(
                closeImageName: closeImageName,
                title: title,
                closeAccessibilityId: closeAccessibilityId,
                imageSize: imageSize,
                closeAction: closeAction
            )
        )
    }
}
