//
//  RoomTypesView+Formeka.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 07/06/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Formeka

extension RoomTypesView {
    func roomTypesViewModel(with viewModel: RoomTypesViewModel) -> FormekaViewModel {
        var sections = [FormekaModelSection]()

        let typesSection = roomTypesSection(with: viewModel)
        sections.append(typesSection)

        return FormekaViewModel(sections: sections)
    }

    func roomTypesSection(with viewModel: RoomTypesViewModel) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        // append room types segment cell
        rows.append(roomTypesSegmentRow(with: viewModel))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    func roomTypesSegmentRow(with viewModel: RoomTypesViewModel) -> FormekaModelRow {
        FormekaModelRow(
            tag: RoomTypeRow.roomTypesSegmentsCell.rawValue,
            cellSetup: { [unowned self] indexPath, _, table in
                guard let cell: HotelInformationSegmentsCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.selectorData = SegmentSelectorData(
                    segments: viewModel.roomTypeSegmentData.informationSegments,
                    selectedIndex: currentRoomTypeSectionIndex
                )
                cell.updateUI(
                    backgroundColor: viewModel.roomTypeSegmentData.backgroundColour,
                    selectorColor: viewModel.roomTypeSegmentData.selectorColor,
                    normalTextAttributes: viewModel.roomTypeSegmentData.normalTextAttributes,
                    selectedTextAttributes: viewModel.roomTypeSegmentData.selectedTextAttributes
                )

                cell.segmentControl.accessibilityIdentifier = AccessibilityIdentifiers.RoomTypes.roomTypesSegmentedControl
                cell.delegate = self

                return cell
            },
            cellWillDisplay: { cell, _, _ in
                if let segmentCell = cell as? HotelInformationSegmentsCell {
                    segmentCell.segmentControlValueDidChange(cell)
                }
        }
        )
    }
}
