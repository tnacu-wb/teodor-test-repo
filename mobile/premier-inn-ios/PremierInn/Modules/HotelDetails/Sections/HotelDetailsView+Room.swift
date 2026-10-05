//
//  HotelDetailsView+Room.swift
//  PremierInn
//
//  Created by Nick Jones on 20/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Formeka

extension HotelDetailsViewController {
    func roomSection(with viewModel: RoomSectionViewModel) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        if viewModel.roomImageURLs.isNotEmpty {
            rows.append(FormekaModelRow(tag: HotelDetailRow.hotelRoomPhotosCell.rawValue, cellSetup: { indexPath, _, table in
                guard let cell: CarouselCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.urls = viewModel.roomImageURLs
                cell.messagingFlagLabel.isHidden = true

                return cell
            }))
        }

        rows.append(
            FormekaModelRow(
                tag: HotelDetailRow.roomSegmentsCell.rawValue,
                cellSetup: { [unowned self] indexPath, _, table in
                    guard let cell: HotelInformationSegmentsCell = table.dequeueCell(for: indexPath) else { return nil }

                    cell.selectorData = SegmentSelectorData(
                        segments: viewModel.hotelInformationViewModel.informationSegments,
                        selectedIndex: currentRoomSectionIndex
                    )
                    cell.updateUI(
                        backgroundColor: viewModel.hotelInformationViewModel.backgroundColour,
                        selectorColor: viewModel.hotelInformationViewModel.selectorColor,
                        normalTextAttributes: viewModel.hotelInformationViewModel.normalTextAttributes,
                        selectedTextAttributes: viewModel.hotelInformationViewModel.selectedTextAttributes
                    )

                    cell.segmentControl.accessibilityIdentifier = AccessibilityIdentifiers.HotelDetails.roomSegmentedControl
                    cell.delegate = self

                    return cell
                },
                cellWillDisplay: { cell, _, _ in
                    if let segmentCell = cell as? HotelInformationSegmentsCell {
                        segmentCell.segmentControlValueDidChange(cell)
                    }
                }
            )
        )

        rows.append(FormekaModelRow(
            tag: HotelDetailRow.roomContentCell.rawValue,
            cellSetup: { [unowned self] indexPath, _, table in
            guard let cell: RoomContentCell = table.dequeueCell(for: indexPath) else { return nil }

            let viewModels = viewModel.roomContentViewModels
            guard currentRoomSectionIndex < viewModels.endIndex else { return cell }

            let viewModel = viewModels[currentRoomSectionIndex]

            guard let attributedContent = viewModel.attributedContentDescription else {
                cell.contentLabel.text = viewModel.regularContentDescription
                return cell
            }

            cell.contentLabel.attributedText = attributedContent

            return cell
        }
        ))

        return FormekaModelSection(
            header: titleHeader(title: PILocalizedString(
                "hotelDetailsRoomSectionTitle",
                comment: "Hotel details: room section title"
            )),
            rows: rows,
            footer: simpleFooterView(backgroundColor: .whiteTwo)
        )
    }
}
