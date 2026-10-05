//
//  KioskPassView.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 25/01/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SwiftUI
import PassKit
struct KioskPassView: View {
    @ObservedObject var viewModel: KioskViewModel
    @State private var showingSheet = false

    var body: some View {
        VStack {
            Spacer(minLength: 50)
            ZStack {
                Image(uiImage: viewModel.qrImage)
                    .interpolation(.none)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 300, height: 300)
            }
            VStack {
                Text(viewModel.summary)
                    .font(Font(UIFont.Body_Semibold()))
                    .padding()
                Text(viewModel.text)
                    .font(Font(UIFont.Body()))
                    .padding()
                appleWalletButton()
            }
            Spacer()
        }
        .toolbar {
            Button {
                showingSheet.toggle()
            } label: {
                Image(systemName: "info.circle")
                    .foregroundStyle(Color(viewModel.navigationTheme.iconTintColor))
            }
            .sheet(isPresented: $showingSheet) {
                KioskInfoView(showingSheet: $showingSheet)
            }
        }
        .fullScreenCover(
            isPresented: $viewModel.showPassView,
            onDismiss: {
                viewModel.getExistingPass()
            },
            content: {
                PassView(pkPass: viewModel.pass, bookingReference: viewModel.stay.identifier)
            }
        )
        .onAppear {
            viewModel.getExistingPass()
        }
        .alert(PILocalizedString("kioskPassAppleWalletFailed"), isPresented: $viewModel.appleWalletError) {
            Button(PILocalizedString("OK"), role: .cancel) {
                viewModel.appleWalletError = false
            }
        }
    }

    @Environment(\.openURL) var openURL
    @ViewBuilder
    func appleWalletButton() -> some View {
        switch viewModel.appleWalletState {
        case .passSaved:
            Button {
                guard let passUrl = viewModel.pass?.passURL else { return }
                openURL(passUrl)
            } label: {
                HStack {
                    Image(systemName: "checkmark.circle")
                    Text(viewModel.appleWalletExistsButtonTitle)
                }
            }.buttonStyle(PIAppleWalletButtonStyle())
                .frame(width: 290, height: 40)
            Spacer()
        case .passCanBeAdded:
            if #available(iOS 16.0, *) {
                AddPassToWalletButton(action: {
                    viewModel.fetchWalletPass()
                })
                .frame(width: 290, height: 40)
            } else {
                EmptyView()
            }
            Spacer()
        case .passHidden:
            EmptyView()
        }
    }
}
