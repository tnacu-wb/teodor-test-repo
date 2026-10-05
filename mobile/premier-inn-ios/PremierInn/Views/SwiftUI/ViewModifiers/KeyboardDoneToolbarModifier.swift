//
//  KeyboardDoneToolbarModifier.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 23/01/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SwiftUI

private struct KeyboardDoneToolbarModifier: ViewModifier {
    var isFocused: FocusState<Bool>.Binding
    let doneTitle: String

    func body(content: Content) -> some View {
        content
            .safeAreaInset(edge: .bottom) {
                if isFocused.wrappedValue {
                    HStack {
                        Spacer()
                        Button(doneTitle) { isFocused.wrappedValue = false }
                            .foregroundStyle(Color(uiColor: .BaseBlack))
                    }
                    .padding(.horizontal, 16)
                    .frame(height: 44)
                    .background(Material.bar)
                    .transition(.move(edge: .bottom).combined(with: .opacity))
                } else {
                    EmptyView()
                }
            }
    }
}

extension View {
    func keyboardDoneToolbar(
        isFocused: FocusState<Bool>.Binding,
        doneTitle: String = PILocalizedString("Done")
    ) -> some View {
        self.modifier(
            KeyboardDoneToolbarModifier(
                isFocused: isFocused,
                doneTitle: doneTitle
            )
        )
    }
}
