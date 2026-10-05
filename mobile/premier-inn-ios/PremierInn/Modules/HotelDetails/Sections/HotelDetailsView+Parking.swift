//
//  HotelDetailsView+Parking.swift
//  PremierInn
//
//  Created by Nick Jones on 25/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Formeka
import UIKit

extension HotelDetailsViewController {
    func parkingSection(with viewModel: ParkingSectionViewModel) -> FormekaModelSection? {
        var rows: [FormekaModelRow] = []

        if viewModel.imageURLS.isNotEmpty {
            rows.append(FormekaModelRow(
                tag: HotelDetailRow.hotelParkingPhotosCell.rawValue,
                cellSetup: { indexPath, _, table in
                guard let cell: CarouselCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.urls = viewModel.imageURLS

                return cell
            }
            ))
        }

        if let parkingSummary = viewModel.parkingSummary {
            rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
                guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.content.textAlignment = .center
                cell.padding = UIEdgeInsets(
                    top: 15,
                    left: 20,
                    bottom: (viewModel.shouldShowLargeParkingSummaryCellBottomPadding ? 10 : 20),
                    right: 20
                )
                cell.content.attributedText = parkingSummary

                return cell
            }))

            rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
                guard let cell: HotelDetailSeparatorCell = table.dequeueCell(for: indexPath) else { return nil }

                return cell
            }))
        }

        if let parkingDescription = viewModel.parkingDescription {
            rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
                guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

//                let textAndRanges = parking.description(with: hotel.parkingDescription)

                cell.content.textAlignment = .center
                cell.padding = UIEdgeInsets(top: 5, left: 20, bottom: 20, right: 20)
                cell.content.attributedText = parkingDescription

                return cell
            }))
        } else if let hotelParkingDescription = viewModel.hotelParkingDescription {
            rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
                guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

                let text = hotelParkingDescription

                cell.content.textAlignment = .center
                cell.padding = UIEdgeInsets(top: 5, left: 20, bottom: 20, right: 20)
                cell.content.text = text

                return cell
            }))
        }

        return FormekaModelSection(
            header: titleHeader(
                title: viewModel.title,
                accessibilityIdentifier: AccessibilityIdentifiers.HotelDetails.parkingDetails
            ),
            rows: rows,
            footer: simpleFooterView(backgroundColor: .whiteTwo)
        )
    }
}
