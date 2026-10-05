//
//  HotelDetailsView+Location.swift
//  PremierInn
//
//  Created by Nick Jones on 20/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Formeka
import UIKit

extension HotelDetailsViewController {
    func locationSection(withLocationSectionViewModel viewModel: LocationSectionViewModel) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        rows.append(FormekaModelRow(
            tag: HotelDetailRow.hotelLocationInfoCell.rawValue,
            cellSetup: { [weak self] indexPath, _, table in
            guard let cell: HotelLocationInfoCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.subtitle?.attributedText = NSAttributedString.attributedStringWith(
                text: viewModel.hotelDescription,
                lineSpacing: 7,
                font: UIFont.Body(),
                textColor: .TintD1,
                textAlignment: .justified
            )
            cell.readMoreButton.actionTouchUp {
                self?.eventHandler.locationInformationButtonDidTap()
            }

            cell.readMoreButton.accessibilityIdentifier = AccessibilityIdentifiers.HotelDetails.hotelDetailsReadMoreButton

            return cell
        }
        ))

        return FormekaModelSection(header: nil, rows: rows, footer: simpleFooterView(backgroundColor: .whiteTwo))
    }
}
