//
//  VenueFullyBookedCellView.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 02/07/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SwiftUI

// MARK: - VenueFullyBookedCellView

struct VenueFullyBookedCellView: View {
    // MARK: - Properties

    private let viewModel: VenueFullyBookedCellViewModel
    private let onChangeDatesTap: () -> Void

    // MARK: - init

    init(
        dataProvider: VenueFullyBookedCellViewModelDataProviding,
        onChangeDatesTap: @escaping () -> Void
    ) {
        self.viewModel = .init(provider: dataProvider)
        self.onChangeDatesTap = onChangeDatesTap
    }

    // MARK: - body

    var body: some View {
        HStack {
            soldOutImageView
            nameAndChangeDateButtonView
        }
        .venueCellContainer()
    }
}

// MARK: - Subviews

// MARK: - Sold out image view

private extension VenueFullyBookedCellView {
    var soldOutImageView: some View {
        ZStack {
            RemoteImageView(url: viewModel.imageUrl)
                .frame(
                    width: Constants.imageWidth
                )
                .frame(maxHeight: .infinity)
                .opacity(ViewConstants.Opacity.low)
                .accessibilityIdentifier(
                    AccessibilityIdentifiers.SearchResults.noAvailabilityHotelImage
                )

            RoundedTextLabelView(
                text: PILocalizedString("hotelSoldOut"),
                accessibilityIdentifier: AccessibilityIdentifiers.SearchResults.noAvailabilitySoldOutLabel,
                font: .Heading4_Bold(),
                textColor: .TintD1,
                backgroundColor: .BaseWhite,
                cornerRadius: ViewConstants.CornerRadius.small,
                verticalPadding: ViewConstants.Spacing.small,
                horizontalPadding: ViewConstants.Spacing.small
            )
        }
    }
}

// MARK: - Name and change date button

private extension VenueFullyBookedCellView {
    var nameAndChangeDateButtonView: some View {
        VStack(
            alignment: .leading,
            spacing: ViewConstants.Spacing.none
        ) {
            nameView
            changeDatesButtonView
        }
        .padding(ViewConstants.Spacing.medium)
    }

    var nameView: some View {
        Text(viewModel.name)
            .foregroundColor(Color(.TintD1))
            .font(.Heading4_Semibold())
            .padding(.bottom, ViewConstants.Spacing.small)
            .accessibilityIdentifier(
                AccessibilityIdentifiers.SearchResults.noAvailabilityHotelName
            )
    }

    var changeDatesButtonView: some View {
        Button(PILocalizedString("hotelDetailsFullyBookedEditDates")) {
            generateLightFeedbackHaptic()
            onChangeDatesTap()
        }
        .buttonStyle(
            StandardButtonStyle(
                labelFont: .Heading4_Semibold(),
                labelColor: .BasePurple,
                backgroundColor: .BaseWhite,
                borderColor: .BasePurple,
                borderWidth: ViewConstants.BorderWidth.small
            )
        )
        .accessibilityIdentifier(AccessibilityIdentifiers.SearchResults.editDatesButton)
    }
}

// MARK: - Haptics

private extension VenueFullyBookedCellView {
    func generateLightFeedbackHaptic() {
        NotificationFeedbackManager.shared.provideLightTapFeedback()
    }
}

// MARK: - Constants

private extension VenueFullyBookedCellView {
    enum Constants {
        static let imageWidth: CGFloat = 120
    }
}

// MARK: - Preview

#if DEV

#Preview {
    List {
        VenueFullyBookedCellView(
            dataProvider: MockVenueFullyBookedCellViewModelDataProvider()
        ) { }

        VenueFullyBookedCellView(
            dataProvider: MockVenueFullyBookedCellViewModelDataProvider()
        ) { }
    }
}

#endif
