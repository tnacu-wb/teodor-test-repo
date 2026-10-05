//
//  PromoListView.swift
//  PremierInn
//
//  Created by Santa Gurung on 29/11/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SwiftUI

struct PromoListView: View {
    let items: [PromoCardViewModel]
    private let padding: Double = 16.0

    var body: some View {
        VStack(spacing: padding) {
            ForEach(items) { item in
                PromoView(item: item)
                    .frame(maxWidth: .infinity)
                    .padding(.horizontal, padding)
            }
        }
    }
}

struct PromoView: View {
    let item: PromoCardViewModel
    private let cornerRadius: Double = 8.0
    private let imageLength: Double = 120.0
    private let padding: Double = 16.0
    @Environment(\.openURL) private var openURL
    @State private var isSheetPresented = false

    var body: some View {
        HStack(spacing: 16) {
            // MARK: Image
            AsyncImage(url: item.imageUrl) { image in
                image
                    .resizable()
                    .aspectRatio(contentMode: .fill)
            } placeholder: {
                Color(.ColourDL8)
            }
            .frame(width: imageLength, height: imageLength)
            .clipShape(UnevenRoundedRectangle(
                topLeadingRadius: cornerRadius,
                bottomLeadingRadius: cornerRadius,
                bottomTrailingRadius: 0,
                topTrailingRadius: 0
            ))
            // MARK: Capsule Tag
            .overlay(alignment: .topLeading) {
                if let tag = item.tag,
                   tag.isNotEmpty {
                    ImageTagView(
                        tag: tag,
                        backgroundColor: Color(.BasePurple),
                        textColor: Color(.BaseWhite)
                    )
                    .padding(8.0)
                }
            }

            // MARK: Title and Description
            VStack(alignment: .leading, spacing: 8) {
                Text(item.title)
                    .lineLimit(2)
                    .font(Font(UIFont.Heading2_Bold()))
                    .foregroundStyle(Color(.BasePurple))

                if let description = item.description {
                    Text(description)
                        .lineLimit(3)
                        .font(Font(UIFont.BodySmall()))
                        .foregroundStyle(Color(.ColourDL1))
                        .lineSpacing(5)
                }
            }

            Spacer()
        }
        .frame(maxWidth: .infinity)
        .frame(height: imageLength)
        .overlay {
            RoundedRectangle(cornerRadius: cornerRadius)
                .stroke(Color(.TintL3))
        }
        .onTapGesture {
            if item.openLinkInApp == true {
                isSheetPresented = true
            } else if let url = item.url {
                openURL(url)
            }
            item.trackPromoTapped()
        }
        .sheet(isPresented: $isSheetPresented) {
            WebviewWithNavigationBar(url: item.url, isSheetPresented: $isSheetPresented)
        }
    }
}
