//
//  HotelDetailsView+TripAdvisor.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 13/11/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Formeka
import UIKit

extension HotelDetailsViewController {
    func tripAdvisorSection(with viewModel: TripAdvisorViewModel) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        rows.append(tripAdvisorSummaryRow(with: viewModel))

        viewModel.subRatings.forEach { rows.append(subRatingRow(subRating: $0)) }

        viewModel.awards.forEach { rows.append(awardRow(award: $0)) }

        return FormekaModelSection(
            header: tripAdvisorHeader(),
            rows: rows,
            footer: simpleFooterView(backgroundColor: .whiteTwo)
        )
    }

    private func tripAdvisorHeader() -> FormekaModelHeaderFooter? {
        FormekaModelHeaderFooter(height: 50, viewSetup: { _, table in
            guard let header: TripAdvisorHeader = table.headerFooterView() else { return nil }

            header.contentView.backgroundColor = .white

            header.titleLabel.text = "TripAdvisor"
            header.titleLabel.font = UIFont.Heading3_Semibold()
            header.titleLabel.textAlignment = .center
            header.titleLabel.textColor = .BasePurple

            return header
        })
    }

    func tripAdvisorSummaryRow(with viewModel: TripAdvisorViewModel) -> FormekaModelRow {
        FormekaModelRow(tag: HotelDetailRow.tripAdvisorDetails.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: TripAdvisorSummaryCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.backgroundColor = .white
            cell.isUserInteractionEnabled = false
            cell.rating.attributedText = String(viewModel.rating).toAttributedString(kern: -1.0)
            cell.ratingDescription.text = viewModel.ratingDescription
            cell.numberOfReviews.text = NSNumber(value: viewModel.numberOfReviews).britishFormattedString() + " reviews"
            cell.ratingImage.image = viewModel.image

            return cell
        })
    }

    func subRatingRow(subRating: TripAdvisorSubRating) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: ActionIconCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.backgroundColor = .white
            cell.isUserInteractionEnabled = false
            cell.icon.image = subRating.image
            cell.icon.contentMode = .scaleAspectFit
            cell.title.text = PILocalizedString(subRating.name, comment: "")
            cell.title.font = UIFont.Body()
            cell.title.textColor = .TintD1
            cell.accessoryType = .none
            cell.accessoryView = nil
            cell.topConstraint.constant = 3
            cell.bottomConstraint.constant = 3
            cell.iconAspectRatio.isActive = false
            cell.iconWidth.constant = 80
            cell.iconHeight.constant = 16
            cell.iconTopConstraint.constant = 5

            return cell
        })
    }

    func awardRow(award: TripAdvisorAward) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: ActionIconCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.backgroundColor = .white
            cell.isUserInteractionEnabled = false
            if let iconURL = award.imageURL {
                cell.icon.setImage(with: iconURL)
            }

            cell.icon.contentMode = .scaleAspectFit
            cell.title.text = PILocalizedString(award.name, comment: "")
            cell.title.font = UIFont.Body()
            cell.title.textColor = .TintD1
            cell.accessoryType = .none
            cell.accessoryView = nil
            cell.topConstraint.constant = 12
            cell.bottomConstraint.constant = 12
            cell.iconAspectRatio.isActive = false
            cell.iconHeight.constant = 25
            cell.iconWidth.constant = cell.iconHeight.constant * CGFloat(29.0 / 23.0)

            return cell
        })
    }
}
