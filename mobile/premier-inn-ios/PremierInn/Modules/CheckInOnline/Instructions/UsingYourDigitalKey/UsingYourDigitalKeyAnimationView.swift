//
//  UsingYourDigitalKeyAnimationView.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 08/04/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SwiftUI
import Lottie

// MARK: - UsingYourDigitalKeyAnimationView

struct UsingYourDigitalKeyAnimationView: View {
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationStack {
            GeometryReader { proxy in
                LottieView {
                    try await DotLottieFile.named("phoneToDoor")
                }
                .playing(loopMode: .loop)
                .resizable()
                .scaledToFill()
                .frame(
                    width: proxy.size.width * proxy.size.lottieWidthMultiplier,
                    height: proxy.size.height
                )
                .frame(width: proxy.size.width, height: proxy.size.height)
                .clipped()
            }
            .ignoresSafeArea()
            .standardModalToolbar(title: "")
            .toolbarBackground(.hidden, for: .navigationBar)
        }
    }
}

// MARK: - Lottie width fix helpers

private extension CGSize {
    var needsLottieWidthFix: Bool {
        width <= 375 && height <= 667
    }

    var lottieWidthMultiplier: CGFloat {
        needsLottieWidthFix ? 1.8 : 1.0
    }
}

// MARK: - Preview

#Preview {
    UsingYourDigitalKeyAnimationView()
}
