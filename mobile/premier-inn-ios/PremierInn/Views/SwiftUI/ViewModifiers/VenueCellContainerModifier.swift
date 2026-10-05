//
//  VenueCellContainerModifier.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 03/07/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SwiftUI

private struct VenueCellContainerModifier: ViewModifier {
    func body(content: Content) -> some View {
        content
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color(.systemBackground))
            .cornerRadius(ViewConstants.CornerRadius.medium)
            .overlay(
                RoundedRectangle(
                    cornerRadius: ViewConstants.CornerRadius.medium
                )
                .stroke(
                    Color(.TintL2),
                    lineWidth: ViewConstants.BorderWidth.small
                )
            )
    }
}

extension View {
    func venueCellContainer() -> some View {
        modifier(VenueCellContainerModifier())
    }
}
