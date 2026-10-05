//
//  NoWhitespaceModifier.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 22/01/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SwiftUI

private struct NoWhitespaceModifier: ViewModifier {
    @Binding var text: String

    func body(content: Content) -> some View {
        content
            .onChange(of: text) { newValue in
                let filtered = newValue.filter { !$0.isWhitespace }

                if filtered != newValue {
                    text = filtered
                }
            }
    }
}

extension View {
    func noWhitespace(_ text: Binding<String>) -> some View {
        modifier(NoWhitespaceModifier(text: text))
    }
}
