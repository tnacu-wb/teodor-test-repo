//
//  KioskPassView.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 25/01/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SwiftUI

struct KioskInfoView: View {
    private let viewModel = KioskInfoViewModel()
    @Binding var showingSheet: Bool

    var body: some View {
        NavigationStack {
            VStack {
                Text(viewModel.about)
                    .font(Font(UIFont.Body()))
                    .padding(.top)
                    .padding(.leading, 5)
                    .padding(.trailing, 5)
                Text(viewModel.instructions)
                    .font(Font(UIFont.Body()))
                    .padding(.leading)
                    .padding(.trailing)
                viewModel.kioskImage
                    .resizable()
                    .cornerRadius(5)
                    .aspectRatio(contentMode: .fit)
                    .padding()
                Spacer()
            }
            .navigationTitle(viewModel.title)
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                Button {
                    showingSheet = false
                } label: {
                    Text(viewModel.doneButton)
                }
                .foregroundColor(Color(uiColor: UIColor.BaseBlack))
            }
        }
    }
}
