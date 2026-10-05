//
//  PromotionPopoverView.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 01/09/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import SwiftUI

struct PromotionPopoverView: View {
    @Environment(\.dismiss) private var dismiss

    private let viewModel = PromotionPopoverViewModel()

    var body: some View {
        VStack(alignment: .center) {
            HStack {
                Spacer()
                Button {
                    dismiss()
                } label: {
                    Text(viewModel.dismissButtonTitle)
                        .foregroundStyle(Color(.BaseWhite))
                        .padding(EdgeInsets(top: 16, leading: 16, bottom: 16, trailing: 24))
                }
            }
            .padding(EdgeInsets(top: 16, leading: 0, bottom: 0, trailing: 0))

            Image(viewModel.imageView)
                .resizable()
                .aspectRatio(contentMode: .fit)
                .frame(width: 100, height: 100)
                .tint(Color(.DarkPurple))

            Spacer(minLength: 50)

            VStack(alignment: .center) {
                Text(viewModel.title)
                    .font(Font(UIFont.Heading1_ExtraBold(29)))
                    .foregroundStyle(Color(.BaseWhite))
                    .padding(EdgeInsets(top: 20, leading: 8, bottom: 8, trailing: 8))

                Text(viewModel.bookByDate)
                    .font(Font(UIFont.Heading3_Regular()))
                    .foregroundStyle(Color(.BaseWhite))

                RepresentedDiscountOfferView(
                    number: viewModel.offerNumber,
                    percentage: viewModel.offerPercentage,
                    off: viewModel.offerOff
                )
                    .frame(width: 220, height: 100)
                    .frame(maxWidth: .infinity, alignment: .center)

                Text(viewModel.offerDescription)
                    .padding(.bottom, 24)
                    .font(Font(UIFont.Heading1_ExtraBold(24)))
                    .foregroundStyle(Color(.Tint11))

                Button {
                    dismiss()
                } label: {
                    Text(viewModel.actionButtonTitle)
                        .frame(maxWidth: .infinity, minHeight: 56)
                        .background(Color(.BaseWhite))
                        .tint(Color(.BasePurple))
                        .font(Font(UIFont.Button1()))
                        .clipShape(.rect(cornerRadius: 4))
                }

                Text(viewModel.disclaimer)
                    .padding(.top, 24)
                    .font(Font(UIFont.SubtextSmall()))
                    .foregroundStyle(Color(.BaseWhite))
                Text(viewModel.termsAndConditions)
                    .underline()
                    .padding(.bottom, 50)
                    .font(Font(UIFont.SubtextSmall()))
                    .foregroundStyle(Color(.BaseWhite))
                    .onTapGesture {
                        if let vc = hostingController {
                            viewModel.termsAndConditionsDidTap(viewController: vc)
                        }
                    }
            }
            .frame(maxHeight: .infinity)
            .padding(.leading, 32)
            .padding(.trailing, 32)
            .background(CurvedTopShape()
                .fill(Color(.BasePurple))
            )
        }
        .background(Color(.DarkPurple))
        .ignoresSafeArea()
    }
}

struct CurvedTopShape: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()

        // Start at bottom-left
        path.move(to: CGPoint(x: rect.minX, y: rect.maxY))
        // Left edge
        path.addLine(to: CGPoint(x: rect.minX, y: rect.minY))
        // Top edge with curve
        path.addQuadCurve(
            to: CGPoint(x: rect.maxX, y: rect.minY),
            control: CGPoint(x: rect.midX, y: rect.minY - 50) // adjust -40 for more/less curve
        )
        // Right edge
        path.addLine(to: CGPoint(x: rect.maxX, y: rect.maxY))
        // Close path
        path.closeSubpath()

        return path
    }
}

#Preview {
    PromotionPopoverView()
}
