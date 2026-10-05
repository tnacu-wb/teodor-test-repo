//
//  StickyFooter.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 05/09/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import SwiftUI

struct StickyFooter: View {
    private let viewModel: StickyFooterViewModel

    init(viewModel: StickyFooterViewModel) {
        self.viewModel = viewModel
    }

    var body: some View {
        HStack(alignment: .top) {
            Image(viewModel.image)
                .resizable()
                .aspectRatio(contentMode: .fit)
                .frame(width: 40, height: 40)

            VStack(alignment: .leading) {
                Text(viewModel.title)
                    .padding(.bottom, 5)
                    .font(Font(UIFont.Heading2_ExtraBold()))
                    .foregroundStyle(Color(.Tint11))

                Text(viewModel.description)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .lineLimit(nil)
                    .multilineTextAlignment(.leading)
                    .fixedSize(horizontal: false, vertical: true)
                    .foregroundStyle(Color(.BaseWhite))
                    .onTapGesture(perform: viewModel.termsAndConditionsDidTap)
            }
            .padding(.leading, 15)
        }
        .padding(.leading, 20)
        .padding(.top, 20)
        .background(Color(.clear))
    }
}
