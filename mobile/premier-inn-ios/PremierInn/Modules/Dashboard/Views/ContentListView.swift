//
//  ContentListView.swift
//  PremierInn
//
//  Created by Santa Gurung on 26/11/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SwiftUI

struct ContentListView: View {
    let items: [ContentCardViewModel]
    private let padding: CGFloat = 16.0

    var body: some View {
        VStack(alignment: .leading) {
            ScrollView(.horizontal) {
                HStack(spacing: padding) {
                    ForEach(items) { content in
                        ContentCardView(content: content)
                    }
                }
                .padding(.horizontal, padding)
            }
            .scrollIndicators(.hidden)
        }
    }
}

struct ContentCardView: View {
    let content: ContentCardViewModel
    private let imageHeight: Double = 360.0
    private let padding: Double = 16.0
    private let cornerRadius: Double = 8.0
    private let cellWidth: CGFloat = 320.0
    @State private var isPresenting = false
    @Environment(\.openURL) private var openURL

    var body: some View {
        VStack(alignment: .leading) {
            // MARK: Image
            AsyncImage(url: content.imageUrl) { image in
                image
                    .resizable()
                    .aspectRatio(contentMode: .fill)
            } placeholder: {
                Color(.ColourDL8)
            }
            .frame(width: cellWidth, height: imageHeight)
            // MARK: Capsule Tag
            .overlay(alignment: .topLeading) {
                if let tag = content.tag,
                   tag.isNotEmpty {
                    ImageTagView(
                        tag: tag,
                        backgroundColor: Color(.Tint1),
                        textColor: Color(.BaseWhite)
                    )
                    .padding(ViewConstants.Spacing.small)
                }
            }
            .overlay(alignment: .bottomLeading) {
                ZStack(alignment: .bottomLeading) {
                    // MARK: Gradient over the texts
                    LinearGradient(
                        gradient: Gradient(colors: [
                            Color(.BaseBlack).opacity(0.0),
                            Color(.BaseBlack).opacity(0.3),
                            Color(.BaseBlack).opacity(0.6)
                        ]),
                        startPoint: .top,
                        endPoint: .bottom
                    )
                    .frame(width: cellWidth)

                    VStack(alignment: .leading, spacing: 10) {
                        // MARK: Title
                        Text(content.title ?? "")
                            .lineLimit(1)
                            .font(Font(UIFont.Heading2_Bold()))
                            .foregroundStyle(Color(.BaseWhite))
                            .padding(.leading, padding)

                        // MARK: Description
                        Text(content.description ?? "")
                            .lineLimit(3)
                            .font(Font(UIFont.BodySmall()))
                            .foregroundStyle(Color(.BaseWhite))
                            .lineSpacing(5)
                            .padding(.horizontal, padding)
                    }
                    .padding(.bottom, padding)
                }
            }
            .clipShape(UnevenRoundedRectangle(
                topLeadingRadius: cornerRadius,
                bottomLeadingRadius: cornerRadius,
                bottomTrailingRadius: cornerRadius,
                topTrailingRadius: cornerRadius
            ))
        }
        .contentShape(UnevenRoundedRectangle(
            topLeadingRadius: cornerRadius,
            bottomLeadingRadius: cornerRadius,
            bottomTrailingRadius: cornerRadius,
            topTrailingRadius: cornerRadius
        ))
        .onTapGesture {
            if content.openLinkInApp == true {
                isPresenting = true
            } else if let url = content.url {
                openURL(url)
            }
            content.trackContentTapped()
        }
        .sheet(isPresented: $isPresenting) {
            WebviewWithNavigationBar(url: content.url, isSheetPresented: $isPresenting)
        }
    }
}
