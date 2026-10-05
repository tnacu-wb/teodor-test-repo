//
//  PILoader.swift
//  PremierInn
//
//  Created by Badea, Bogdan (Cognizant) on 26.05.2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SwiftUI

struct PILoader: View {
    // MARK: - Constants

    private enum Constants {
        static let backgroundColor: Color = .black
        static let backgroundOpacity: Double = 0.2
        static let containerColor: Color = Color.white.opacity(0.95)
        static let progressViewColor: Color = .gray
        static let cornerRadius: CGFloat = 16
        static let containerSize: CGFloat = 90
        static let indicatorScale: CGFloat = 1.15
    }

    // MARK: - Content

    var body: some View {
        ZStack {
            Constants.backgroundColor
                .opacity(Constants.backgroundOpacity)
                .ignoresSafeArea()

            RoundedRectangle(cornerRadius: Constants.cornerRadius,
                             style: .circular)
            .fill(Constants.containerColor)
            .frame(width: Constants.containerSize,
                   height: Constants.containerSize)
            .overlay {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: Constants.progressViewColor))
                    .controlSize(.large)
                    .scaleEffect(Constants.indicatorScale)
            }
        }
        .ignoresSafeArea()
    }
}

// MARK: - ViewModifier

extension View {
    func showLoader(isPresented: Bool) -> some View {
        modifier(PILoaderModifier(isPresented: isPresented))
    }
}

private struct PILoaderModifier: ViewModifier {
    // MARK: - Properties

    let isPresented: Bool
    private let animationDuration: Double = 0.2
    private let transition: AnyTransition = .opacity

    // MARK: - Methods

    func body(content: Content) -> some View {
        ZStack {
            content

            if isPresented {
                PILoader()
                    .transition(transition)
            }
        }
        .animation(.easeInOut(duration: animationDuration),
                   value: isPresented)
    }
}

// MARK: - Preview

#Preview {
    VStack {
        Text("Screen content")
    }
    .frame(maxWidth: .infinity,
           maxHeight: .infinity)
    .showLoader(isPresented: true)
}
