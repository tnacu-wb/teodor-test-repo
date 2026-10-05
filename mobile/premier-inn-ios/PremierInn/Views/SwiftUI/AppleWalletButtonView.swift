//
//  AppleWalletButtonView.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 12/09/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SwiftUI

struct PIAppleWalletButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        PIAppleWalletButton(configuration: configuration)
    }
}

private struct PIAppleWalletButton: View {
    let configuration: ButtonStyleConfiguration

    var body: some View {
        configuration.label
                    .font(.title3)
                    .foregroundStyle(Color(uiColor: .Tint4))
                    .frame(maxWidth: .infinity)
                    .frame(height: 40)
                    .background(RoundedRectangle(cornerRadius: 5, style: .continuous).stroke(Color(.Tint4), lineWidth: 1))
                    .background(Color(uiColor: .Tint5))
    }
}
