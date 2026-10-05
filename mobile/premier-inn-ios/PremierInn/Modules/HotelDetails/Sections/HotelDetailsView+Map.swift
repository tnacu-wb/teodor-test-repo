//
//  HotelDetailsView+Map.swift
//  PremierInn
//
//  Created by Nick Jones on 20/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Formeka
import UIKit

extension HotelDetailsViewController {
    func mapSection(with viewModel: MapSectionViewModel) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []
        let rowTag = HotelDetailRow.hotelMapCell.rawValue

        rows.append(FormekaModelRow(tag: rowTag, cellSetup: { [weak self] indexPath, _, table in
            guard let cell: LocationDetailsCell = table.dequeueCell(for: indexPath) else { return nil }

            if let image = self?.cachedMapImage {
                cell.mapImageView.image = image
            } else {
                cell.mapImageView.alpha = 0

                cell.mapImageView.loadMapImage(with: viewModel.annotations) { [weak self] image in
                    self?.cachedMapImage = image

                    UIView.animate(withDuration: .ocd) {
                        cell.mapImageView.alpha = 1
                    }
                }
            }

            cell.didTapDirections = { [weak self] _ in
                self?.tappedMapForDirections()
            }

            cell.configure(with: viewModel)
            return cell
        }))
        return FormekaModelSection(
            header: nil,
            rows: rows,
            footer: simpleFooterView(backgroundColor: .BaseWhite, showLine: false)
        )
    }

    @objc private func tappedMapForDirections() {
        eventHandler.mapPreviewDidTap()
    }
}
