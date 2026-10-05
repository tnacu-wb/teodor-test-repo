//
//  GDPRView.swift
//  PremierInn
//
//  Created by Santa Gurung on 15/10/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SwiftUI

struct GDPRView: View {
    let onDismiss: () -> Void
    private let viewModel = GDPRViewModel()

    var body: some View {
        NavigationStack {
            VStack(alignment: .leading) {
                Spacer()

                // MARK: Title
                Text(viewModel.title)
                    .font(Font(UIFont.Heading1_Bold_custom(40)))
                    .foregroundStyle(Color(.BasePurple))
                    .padding(.bottom, 16)

                // MARK: Description
                Text(viewModel.description)
                    .font(Font(UIFont.Body_Medium()))
                    .foregroundStyle(Color(.TintD1))
                    .lineSpacing(5)

                Spacer()
                    .frame(height: 16)

                // MARK: TechWeUse and Privacy policy buttons
                List(viewModel.listRows) { row in
                    ListView(option: row)
                        .listRowSeparator(.hidden)
                        .listRowInsets(EdgeInsets(top: 0, leading: 6, bottom: 0, trailing: 6))
                        .listRowBackground(Color.clear)
                }
                .frame(height: 112)
                .scrollDisabled(true)
                .listStyle(.inset)

                Spacer()
                    .frame(height: 32)

                // MARK: Accept GDPR button
                GDPRAcceptRow(onDismiss: onDismiss, viewModel: viewModel)

                Spacer()
                    .frame(height: 32)
            }
            .padding(32)
            .onAppear {
                viewModel.trackAnalytics(analyticsManager: AnalyticsManager.shared)
            }
        }
    }
}

// MARK: Struct for TechWeUse and Privacy policy button

private struct ListView: View {
    let option: GDPROption

    var body: some View {
        NavigationLink {
            option.destination
                .navigationTitle(option.title)
        } label: {
            HStack {
                option.iconImage
                    .foregroundStyle(Color(.BasePurple))
                    .frame(width: 20, height: 20)
                    .padding(.trailing, 20)
                Text(option.title)
                    .foregroundStyle(Color(.BasePurple))
                    .font(Font(UIFont.Body_Medium()))
                Spacer()
            }
            .padding(16)
        }
    }
}

// MARK: Struct for Accept GDPR button

private struct GDPRAcceptRow: View {
    let onDismiss: () -> Void
    let viewModel: GDPRViewModel

    var body: some View {
        NavigationLink {
            OnboardingView(onDismiss: onDismiss)
        } label: {
            Text(viewModel.acceptButtonLabel)
                .padding(16)
                .font(Font(UIFont.Heading3_Semibold()))
                .frame(maxWidth: .infinity)
                .foregroundStyle(Color(.BaseWhite))
                .background(Color(.Tint1))
                .cornerRadius(4)
        }
        .simultaneousGesture(TapGesture().onEnded {
            viewModel.acceptGDPRChanges()
        })
    }
}

#Preview {
    GDPRView(onDismiss: {})
}
