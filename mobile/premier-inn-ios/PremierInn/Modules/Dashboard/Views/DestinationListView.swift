//
//  DestinationListView.swift
//  PremierInn
//
//  Created by Santa Gurung on 21/11/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SwiftUI
import CoreLocation
import SimpleNetwork

struct DestinationListView: View {
    let title: String?
    let destinations: [DestinationCardViewModel]
    var onNavigateToSRP: ((Suggestion) -> Void)?
    private let padding: CGFloat = 16.0

    var body: some View {
        VStack(alignment: .leading) {
            // MARK: Title
            if let title = title {
                Text(title)
                    .font(Font(UIFont.Heading2_ExtraBold()))
                    .foregroundColor(Color(.BasePurple))
                    .accessibilityAddTraits(.isHeader)
                    .padding([.top, .leading], padding)
            }

            // MARK: Carousel
            ScrollView(.horizontal) {
                HStack(spacing: padding) {
                    ForEach(destinations) { destination in
                        DestinationCardView(destination: destination, onNavigateToSRP: onNavigateToSRP)
                    }
                }
                .padding(.horizontal, padding)
            }
            .scrollIndicators(.hidden)
        }
    }
}

struct DestinationCardView: View {
    let destination: DestinationCardViewModel
    var onNavigateToSRP: ((Suggestion) -> Void)?
    @State private var isPresenting = false
    @Environment(\.openURL) private var openURL

    private let sidePadding: CGFloat = 16.0
    private let cornerRadius: CGFloat = 8.0
    private let imageHeight: CGFloat = 174.0
    private let cellWidth: CGFloat = 320.0
    private let cellHeight: CGFloat = 294.0

    var body: some View {
        VStack(alignment: .leading) {
            // MARK: Top Image
            AsyncImage(url: destination.imageUrl) { image in
                image
                    .resizable()
                    .aspectRatio(contentMode: .fill)
            } placeholder: {
                Color(.ColourDL8)
            }
            .frame(width: cellWidth, height: imageHeight)
            .clipShape(UnevenRoundedRectangle(
                topLeadingRadius: cornerRadius,
                bottomLeadingRadius: 0,
                bottomTrailingRadius: 0,
                topTrailingRadius: cornerRadius
            ))
            // MARK: Capsule Tag
            .overlay(alignment: .topLeading) {
                if let tag = destination.tag,
                   tag.isNotEmpty {
                    ImageTagView(
                        tag: tag,
                        backgroundColor: Color(.Tint1),
                        textColor: Color(.BaseWhite)
                    )
                    .padding(8.0)
                }
            }

            Spacer()
                .frame(height: 16)

            // MARK: Title
            Text(destination.title)
                .lineLimit(1)
                .font(Font(UIFont.Heading2_Bold()))
                .foregroundStyle(Color(.BasePurple))
                .padding(.leading, sidePadding)

            Spacer()
                .frame(height: 4)

            // MARK: Description
            Text(destination.description ?? "")
                .lineLimit(3)
                .font(Font(UIFont.BodySmall()))
                .foregroundStyle(Color(.ColourDL1))
                .lineSpacing(5)
                .padding(.horizontal, sidePadding)

            Spacer()
        }
        .frame(width: cellWidth, height: cellHeight)
        .overlay {
            RoundedRectangle(cornerRadius: cornerRadius)
                .stroke(Color(.TintL3))
        }
        .onTapGesture {
            handleTap()
        }
        .sheet(isPresented: $isPresenting) {
            WebviewWithNavigationBar(url: destination.url, isSheetPresented: $isPresenting)
        }
    }

    // MARK: - Navigation Logic

    private func handleTap() {
        destination.trackDestinationTapped()

        // Priority 1: Navigate to SRP with coordinates if available
        if let suggestion = destination.createSearchSuggestion() {
            onNavigateToSRP?(suggestion)
            return
        }

        // Priority 2: Fallback to web link navigation (AC5)
        if destination.openLinkInApp == true {
            isPresenting = true
        } else if let url = destination.url {
            openURL(url)
        }
    }
}
