//
//  VenueCellView.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 16/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SwiftUI

// MARK: - VenueCellView

struct VenueCellView: View {
    // MARK: - Properties

    /// Required for accessibility identifiers
    private let index: Int
    private let viewModel: VenueCellViewModel
    private let onImageCarouselTap: () -> Void

    // MARK: - init

    init(
        index: Int,
        dataProvider: VenueCellViewModelDataProviding,
        onImageCarouselTap: @escaping () -> Void
    ) {
        self.index = index
        self.viewModel = .init(dataProvider: dataProvider)
        self.onImageCarouselTap = onImageCarouselTap
    }

    // MARK: - body

    var body: some View {
        VStack(
            alignment: .leading,
            spacing: ViewConstants.Spacing.none
        ) {
            ZStack(alignment: .top) {
                imageCarouselView
                bannerView
            }
            titleView
            fromDistanceView
            capsuleGroupView
            parkingAndLowestCostView
        }
        .venueCellContainer()
    }
}

// MARK: - Subviews

// MARK: - Banner Views

private extension VenueCellView {
    @ViewBuilder
    var bannerView: some View {
        switch viewModel.bannerType {
        case .hub:
            hubBannerView
        case .zip:
            zipBannerView
        case .agp:
            EmptyView()
        case .none:
            EmptyView()
        }
    }

    var hubBannerView: some View {
        HStack(alignment: .center, spacing: ViewConstants.Spacing.xSmall) {
            Image(.hubLogo)
                .resizable()
                .scaledToFit()
                .frame(
                    width: ViewConstants.ImageSize.medium,
                    height: ViewConstants.ImageSize.medium
                )
                .padding(.leading, ViewConstants.Spacing.medium)

            Image(.byPremierInn)
                .resizable()
                .scaledToFit()
                .frame(
                    width: ViewConstants.ImageSize.xLarge,
                    height: ViewConstants.ImageSize.xSmall
                )
                .padding(.leading, ViewConstants.Spacing.xSmall)
                .padding(.top, ViewConstants.Spacing.xSmall)

            Spacer()
        }
        .frame(maxWidth: .infinity)
        .frame(height: Constants.bannerHeight)
        .background(Color(.HubPrimary))
    }

    var zipBannerView: some View {
        HStack(alignment: .center) {
            Image(.zipLogo)
                .resizable()
                .scaledToFit()
                .frame(
                    width: ViewConstants.ImageSize.medium,
                    height: ViewConstants.ImageSize.medium
                )
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.leading, ViewConstants.Spacing.medium)
                .padding(.top, ViewConstants.Spacing.xSmall)
        }
        .frame(maxWidth: .infinity)
        .frame(height: Constants.bannerHeight)
        .background(Color(.ZipPrimary))
    }
}

// MARK: - Image carousel, title and from distance views

private extension VenueCellView {
    var imageCarouselView: some View {
        RemoteImageCarouselViewWrapper(
            urls: viewModel.primaryImages,
            onTap: { onImageCarouselTap() }
        )
        .frame(height: Constants.imageCarouselHeight)
        .accessibilityIdentifier(
            String(
                format: AccessibilityIdentifiers.SearchResults.imageFormat,
                index
            )
        )
    }

    var titleView: some View {
        HStack {
            Text(viewModel.formattedNameString)
                .foregroundStyle(Color(.TintD1))
                .font(.Heading2_Semibold())

            if let lastRoomsString = viewModel.lastFewRoomsString {
                Text(lastRoomsString)
                    .foregroundStyle(Color(.deepOrange))
                    .font(.BodySmall())
            }
        }
        .padding(.top, ViewConstants.Spacing.medium)
        .padding(.horizontal, ViewConstants.Spacing.medium)
        .accessibilityIdentifier(String(
            format: AccessibilityIdentifiers.SearchResults.hotelNameFormat,
            index
        ))
    }

    @ViewBuilder
    var fromDistanceView: some View {
        if let fromHotelDistance = viewModel.fromHotelDistanceString {
            (
                Text(fromHotelDistance)
                    .font(.Heading4_Semibold())

                +

                Text(PILocalizedString("hotelDetailsDistanceSuffix"))
                    .font(.Body())
            )
            .foregroundStyle(Color(.TintD1))
            .padding(.top, ViewConstants.Spacing.medium)
            .padding(.horizontal, ViewConstants.Spacing.medium)
        }
    }
}

// MARK: - Capsule views

private extension VenueCellView {
    @ViewBuilder
    var capsuleGroupView: some View {
        if viewModel.shouldShowCapsulesGroup {
            Group {
                discountAppliedCapsuleView
                messageAndPremierPlusCapsuleStackView
            }
            .padding(.horizontal, ViewConstants.Spacing.medium)
        }
    }

    @ViewBuilder
    var discountAppliedCapsuleView: some View {
        if viewModel.shouldShowDiscountAppliedCapsule {
            ImageTagView(
                tag: PILocalizedString("discountAppliedBanner"),
                backgroundColor: Color(.deepTeal),
                textColor: Color(.BaseWhite),
                font: .SubtextStrong()
            )
            .padding(.top, ViewConstants.Spacing.small)
            .accessibilityIdentifier(
                String(
                    format: AccessibilityIdentifiers.SearchResults.discountAppliedBannerFormat,
                    index
                )
            )
        }
    }

    @ViewBuilder
    var messageAndPremierPlusCapsuleStackView: some View {
        if viewModel.shouldShowMessagePremierPlusCapsuleStack {
            HStack(
                alignment: .top,
                spacing: ViewConstants.Spacing.none
            ) {
                messageCapsuleView
                premierPlusCapsuleView
            }
            .padding(.top, ViewConstants.Spacing.small)
        }
    }

    @ViewBuilder
    var messageCapsuleView: some View {
        if let message = viewModel.capsuleMessageString {
            ImageTagView(
                tag: message,
                backgroundColor: .clear,
                textColor: Color(.DarkPurple),
                font: .SubtextStrong(),
                borderColor: Color(.BasePurple),
                borderWidth: ViewConstants.BorderWidth.small
            )
            .padding(.trailing, ViewConstants.Spacing.xSmall)
        }
    }

    @ViewBuilder
    var premierPlusCapsuleView: some View {
        if let premierPlusString = viewModel.premierPlusRoomsString {
            ImageTagView(
                tag: premierPlusString,
                backgroundColor: .clear,
                textColor: Color(.deepTeal),
                font: .SubtextStrong(),
                borderColor: Color(.deepTeal),
                borderWidth: ViewConstants.BorderWidth.small
            )
        }
    }
}

// MARK: - Parking and lowest cost views

private extension VenueCellView {
    @ViewBuilder
    var parkingAndLowestCostView: some View {
        HStack(alignment: .top, spacing: ViewConstants.Spacing.none) {
            parkingView
            Spacer()
            lowestCostView
        }
        .padding(.top, ViewConstants.Spacing.medium)
        .padding(.bottom, ViewConstants.Spacing.mediumLarge)
        .padding(.horizontal, ViewConstants.Spacing.medium)
    }

    @ViewBuilder
    var parkingView: some View {
        if let parkingImageName = viewModel.parkingImage,
           let parkingString = viewModel.parkingString {
            Image(parkingImageName)
                .renderingMode(.template)
                .resizable()
                .tint(Color(.TintD1))
                .frame(
                    width: ViewConstants.ImageSize.small,
                    height: ViewConstants.ImageSize.small
                )
                .padding(.trailing, ViewConstants.Spacing.xSmall)

            Text(parkingString)
                .foregroundStyle(Color(.TintD1))
                .font(.BodySmall())
                .lineLimit(2)
                .accessibilityIdentifier(String(
                    format: AccessibilityIdentifiers.SearchResults.parkingDetailFormat,
                    index
                ))
        }
    }

    @ViewBuilder
    var lowestCostView: some View {
        if let lowestCost = viewModel.lowestCostString {
            HStack(alignment: .bottom, spacing: 0) {
                     Text(PILocalizedString("ciolFrom"))
                         .font(.Body())
                         .accessibilityIdentifier(String(
                             format: AccessibilityIdentifiers.SearchResults.fromRateLabelFormat,
                             index
                         ))
                         .padding(.trailing, ViewConstants.Spacing.xSmall)

                     Text(lowestCost)
                         .font(.Heading3_Semibold())
                         .accessibilityIdentifier(String(
                             format: AccessibilityIdentifiers.SearchResults.fromRatePriceFormat,
                             index
                         ))
                 }
        }
    }
}

// MARK: ViewConstants

private extension VenueCellView {
    enum Constants {
        static let imageCarouselHeight: CGFloat = 252.5
        static let bannerHeight: CGFloat = 40
    }
}

// MARK: - Preview

#if DEV
import SimpleNetwork

#Preview {
    List {
        VenueCellView(
            index: 0,
            dataProvider: MockVenueCellViewModelDataProvider(
                brand: .hub,
                parkings: [.chargeableOnsite]
            ),
            onImageCarouselTap: { }
        )

        VenueCellView(
            index: 1,
            dataProvider: MockVenueCellViewModelDataProvider(
                name: "London Heathrow Airport",
                limitedAvailability: true
            ),
            onImageCarouselTap: { }
        )

        VenueCellView(
            index: 2,
            dataProvider: MockVenueCellViewModelDataProvider(
                name: "Staines Upon Thames",
                brand: .zip,
                limitedAvailability: true,
                cheapestRate: Rate(dictionary: [
                    "classification": SimpleNetwork.Constants.EmployeeOffer.rateCode
                ])
            ),
            onImageCarouselTap: { }
        )
    }
}

#endif
