//
//  BathroomSelectionView+Formeka.swift
//  PremierInn
//
//  Created by Nick Jones on 04/07/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Formeka
import UIKit

extension BathroomSelectionViewController {
    func tableViewModel(with viewModel: BathroomSelectionViewModel) -> FormekaViewModel {
        FormekaViewModel(sections: roomSections(with: viewModel))
    }
}

extension BathroomSelectionViewController {
    func roomSections(with viewModel: BathroomSelectionViewModel) -> [FormekaModelSection] {
        var sections = [FormekaModelSection]()

        if viewModel.roomOptionViewModels.filter({ $0.bathroomSelectionRequired == true }).isNotEmpty && viewModel
           .accessibleRoomImageURLs.isNotEmpty {
            sections.append(FormekaModelSection(
                header: nil,
                rows: [carouselImageRow(with: viewModel.accessibleRoomImageURLs)],
                footer: nil
            ))
        } else if viewModel.twinRoomImageURLs.isNotEmpty {
            sections.append(FormekaModelSection(
                header: nil,
                rows: [carouselImageRow(with: viewModel.twinRoomImageURLs)],
                footer: nil
            ))
        }

        for (index, optionViewModel) in viewModel.roomOptionViewModels.enumerated() {
            // When the room isn't an accessible or new twin one we can just show the customer a no selection required section
            if optionViewModel.bathroomSelectionRequired == false && optionViewModel.twinRoomSelectionRequired == false {
                sections.append(sectionForNoBathroomSelectionRequired(index: index, with: optionViewModel))
                continue
            }

            if optionViewModel.twinRoomSelectionRequired == true {
                sections.append(sectionForTwinRoomSelectionRequired(index: index, with: optionViewModel))
                continue
            }

            // 🦑 All eyes have opened 🦑
            // First we get our empty section
            var rowsForProtoSection = [FormekaModelRow]()
            // ---------------------------------------------
            // Give it a title
            rowsForProtoSection.append(titleRow(index: index, with: optionViewModel.roomAndSelectionRowViewModel))

            // ---------------------------------------------
            // Now we see which bathroom types we need to offer and build a radio row for each one
            if let bathroomOptions = optionViewModel.bathroomTypesCurrentlyAvailable?.compactMap({ bathroomOptionRow(
                withBathroomType: $0,
                bathroomTypeToSelect: optionViewModel.bathroomTypeToSelect,
                uniqueIdentifier: optionViewModel.identifier,
                roomNumber: index + 1,
                price: optionViewModel.roomPrice
            ) }) {
                rowsForProtoSection.append(contentsOf: bathroomOptions)
            }

            // Adding the new Twin cell inheritad from mealOptionRow from upsell

//            if let twinOption = optionViewModel.twin

            // Finally we need to check if there's a bathroom that's never available at this hotel
            /*🙋‍♀️ "What if there's more than one unavailable type?"
             ⣿⣿⣿⣿⣿⣿⣿⠿⢛⢛⡛⡻⢿⣿⣿⣿⣿⠟⠛⢛⠻⢿⣿⣿⣿⣿⣿⣿⣿⣿
             ⣿⣿⣿⣿⢟⢱⡔⡝⣜⣜⢜⢜⡲⡬⡉⢕⢆⢏⢎⢇⢇⣧⡉⠿⣿⣿⣿⣿⣿⣿
             ⣿⣿⡟⡱⣸⠸⢝⢅⢆⢖⣜⣲⣵⣴⣱⣈⡣⣋⢣⠭⣢⣒⣬⣕⣄⣝⡻⢿⣿⣿
             ⣿⠟⡜⣎⢎⢇⢇⣵⣷⣿⣿⡿⠛⠉⠉⠛⢿⣦⢵⣷⣿⣿⣿⠟⠛⠋⠓⢲⡝⣿
             ⢏⢰⢱⣞⢜⢵⣿⣿⣿⣿⣿⠁⠐⠄⠄⠄⠄⢹⣻⣿⣿⣿⠡⠄⠄⠄⠄⠄⠹⣺
             ⢕⢜⢕⢕⢵⠹⢿⣿⣿⣿⣿⡀⠸⠗⣀⠄⠄⣼⣻⣿⣿⣿⡀⢾⠆⣀⠄⠄⣰⢳
             ⡕⣝⢜⡕⣕⢝⣜⢙⢿⣿⣿⣷⣦⣤⣥⣤⣾⢟⠸⢿⣿⣿⣿⣦⣄⣉⣤⡴⢫⣾
             ⡪⡪⣪⢪⢎⢮⢪⡪⡲⢬⢩⢩⢩⠩⢍⡪⢔⢆⢏⡒⠮⠭⡙⡙⠭⢝⣨⣶⣿⣿
             ⡪⡪⡎⡮⡪⡎⡮⡪⣪⢣⢳⢱⢪⢝⢜⢜⢕⢝⢜⢎⢧⢸⢱⡹⡍⡆⢿⣿⣿⣿
             ⡪⡺⡸⡪⡺⣸⠪⠚⡘⠊⠓⠕⢧⢳⢹⡸⣱⢹⡸⡱⡱⡕⡵⡱⡕⣝⠜⢿⣿⣿
             ⡪⡺⡸⡪⡺⢐⢪⢑⢈⢁⢋⢊⠆⠲⠰⠬⡨⡡⣁⣉⠨⡈⡌⢥⢱⠐⢕⣼⣿⣿
             ⡪⣪⢣⢫⠪⢢⢅⢥⢡⢅⢅⣑⡨⡑⠅⠕⠔⠔⠄⠤⢨⠠⡰⠠⡂⣎⣼⣿⣿⣿
             ⠪⣪⡪⡣⡫⡢⡣⡣⡣⡣⡣⣣⢪⡪⡣⡣⡲⣑⡒⡎⡖⢒⣢⣥⣶⣿⣿⣿⣿⣿
             ⢁⢂⠲⠬⠩⣁⣙⢊⡓⠝⠎⠮⠮⠚⢎⡣⡳⠕⡉⣬⣶⣿⣿⣿⣿⣿⣿⣿⣿⣿
             ⢐⠐⢌⠐⠅⡂⠄⠄⢌⢉⠩⠡⡉⠍⠄⢄⠢⡁⡢⠠⠻⣿⣿⣿⣿⣿⣿⣿⣿⣿
             */
            if let bathroomTypeNeverAvailable = viewModel.bathroomTypeNeverAvailable {
                rowsForProtoSection.append(rowForHotelHavingNoBathrooms(ofType: bathroomTypeNeverAvailable))
            }

            // ---------------------------------------------
            // Now we see which bathroom type we need to inform the user are currently unavailable
            /* 🙋‍♀️ "What if there's more than one unavailable type?"
                    - As of V3.15 (10th July 2019) only two types of accessible bathroom are available and we've made the decision to design around this.
                    - Based on this we can only get to this screen if there are enough bathrooms for all rooms so it isn't possible for there to be more than one 🧠
            However, if there is a bathroom type that is never available we need to not add our "no more x rooms left" row as it will be completely redundant.
            The unavailable and never unavailable types should always be the same too
            */
            if let bathroomTypeCurentlyUnavailable = optionViewModel.bathroomTypeCurrentlyUnavailable {
                if viewModel.bathroomTypeNeverAvailable == nil {
                    rowsForProtoSection.append(rowForNoMoreBathrooms(
                        ofType: bathroomTypeCurentlyUnavailable,
                        roomNumber: index + 1
                    ))
                }
            }

            sections.append(FormekaModelSection(
                header: nil,
                rows: rowsForProtoSection,
                footer: simpleHeaderFooter(with: 10)
            ))
        }

        return sections
    }

    // =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*STRING HELPERS=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=

    private func rowForHotelHavingNoBathrooms(ofType unavailableBathroomType: BathroomType) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.icon.tintColor = .deepOrange

            cell.containerView.backgroundColor = .coral
            cell.containerView.layer.borderColor = UIColor.clear.cgColor

            cell.content.text = unavailableBathroomType == .LoweredBath ?
                PILocalizedString("loweredBathroomOptionNoFacilityMessage") :
                PILocalizedString("wetRoomOptionNoFacilityMessage")
            cell.content.textColor = .TintD1

            return cell
        })
    }

    private func rowForNoMoreBathrooms(ofType unavailableBathroomType: BathroomType, roomNumber: Int) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.icon.tintColor = .deepOrange

            cell.containerView.backgroundColor = .coral
            cell.containerView.layer.borderColor = UIColor.clear.cgColor

            cell.content.text = unavailableBathroomType == .LoweredBath ?
                PILocalizedString("loweredBathroomOptionUnavailableMessage") :
                PILocalizedString("wetRoomOptionUnavailableMessage")
            cell.content.textColor = .TintD1
            cell.content
                .accessibilityIdentifier = "noMore" + String(describing: unavailableBathroomType) +
                String(describing: roomNumber)

            return cell
        })
    }

    private func rowForNoMoreTwinRooms(ofType unavailableTwinRoomType: NewTwinRoomType, roomNumber: Int) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.icon.tintColor = .deepOrange

            cell.containerView.backgroundColor = .coral
            cell.containerView.layer.borderColor = UIColor.clear.cgColor

            cell.content.text = unavailableTwinRoomType == .TrueTwin ?
                PILocalizedString("trueTwinOptionUnavailableMessage") :
                PILocalizedString("premierInnTwinOptionUnavailableMessage")
            cell.content.textColor = .TintD1
            cell.content
                .accessibilityIdentifier = "noMore" + String(describing: unavailableTwinRoomType) +
                String(describing: roomNumber)

            return cell
        })
    }

    private func sectionForNoBathroomSelectionRequired(
        index: Int,
        with viewModel: RoomOptionViewModel
    ) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(titleRow(index: index, with: viewModel.roomAndSelectionRowViewModel, isNonAccessibleRoom: true))

        rows.append(FormekaModelRow(tag: "", cellSetup: { indexPath, _, table in
            guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.content.text = PILocalizedString("bathroomSelectionNotNeeded")
            return cell
        }))

//        rows.append(twinRoomOptionRow(with: viewModel.roomAndSelectionRowViewModel,
//                                      withBathroomType: .LoweredBath,
//                                      bathroomTypeToSelect: .LoweredBath,
//                                      uniqueIdentifier: viewModel.identifier,
//                                      roomNumber: 0,
//                                      hideRadioButton: false))


        return FormekaModelSection(
            header: nil,
            rows: rows,
            footer: simpleHeaderFooter(with: 10)
        )
    }

    private func sectionForTwinRoomSelectionRequired(
        index: Int,
        with optionViewModel: RoomOptionViewModel
    ) -> FormekaModelSection {
        var rowsForProtoSection = [FormekaModelRow]()

        rowsForProtoSection.append(titleRow(
            index: index,
            with: optionViewModel.roomAndSelectionRowViewModel,
            isNonAccessibleRoom: true
        ))

        if let twinOptions = optionViewModel.twinRoomTypesCurrentlyAvailable?.compactMap({ twinRoomOptionRow(
            with: $0.0,
            twinTypeToSelect: optionViewModel.twinRoomTypeToSelect,
            uniqueIdentifier: optionViewModel.identifier,
            roomNumber: index + 1,
            price: $0.1.localizedValue
        ) }) {
            rowsForProtoSection.append(contentsOf: twinOptions)
        }

        if let twinRoomTypeCurentlyUnavailable = optionViewModel.twinRoomTypeCurrentlyUnavailable {
            rowsForProtoSection.append(rowForNoMoreTwinRooms(ofType: twinRoomTypeCurentlyUnavailable, roomNumber: index + 1))
        }

        return FormekaModelSection(header: nil, rows: rowsForProtoSection, footer: simpleHeaderFooter(with: 10))
    }

    private func carouselImageRow(with images: [URL]) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: CarouselCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.urls = images
            cell.contentView.backgroundColor = .whiteTwo
            cell.collectionView.backgroundColor = .whiteTwo
            cell.messagingFlagLabel.isHidden = true

            return cell
        })
    }

    func bathroomOptionRow(
        withBathroomType bathroomType: BathroomType,
        bathroomTypeToSelect: BathroomType?,
        uniqueIdentifier: UUID,
        roomNumber: Int,
        hideRadioButton: Bool = false,
        price: String
    ) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { (indexPath, _, table) -> UITableViewCell? in
            guard let cell: BathroomOptionCell = table.dequeueCell(for: indexPath) else { return nil }

            let bathroomTitle = bathroomType == .LoweredBath ?
                PILocalizedString("loweredBathroomOptionTitle") :
                PILocalizedString("wetRoomBathroomOptionTitle")

            let bathroomDescription = bathroomType == .LoweredBath ?
                PILocalizedString("loweredBathroomOptionDescription") :
                PILocalizedString("wetRoomOptionDescription")

            cell.bathroomTitle.text = bathroomTitle
            cell.bathroomTitle
                .accessibilityIdentifier = String(describing: bathroomType) + "Title" + String(describing: roomNumber)
            cell.bathroomDescription.text = bathroomDescription

            cell.radioButton.isHidden = hideRadioButton
            cell.radioButton
                .accessibilityIdentifier = String(describing: bathroomType) + "RadioButton" + String(describing: roomNumber)

            cell.priceLabel.text = price + " " + PILocalizedString("bathroomPriceCell")

            if let bathroomTypeToSelect = bathroomTypeToSelect {
                cell.radioButton.isSelected = bathroomTypeToSelect == bathroomType
            }

            return cell
        }, didSelect: { [weak self] _, _ in
            self?.eventHandler?.selected(bathroomOfType: bathroomType, forRoomID: uniqueIdentifier)
        })
    }

    func twinRoomOptionRow(
        with twinType: NewTwinRoomType,
        twinTypeToSelect: NewTwinRoomType?,
        uniqueIdentifier: UUID,
        roomNumber: Int,
        hideRadioButton: Bool = false,
        price: String
    ) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { (indexPath, _, table) -> UITableViewCell? in
            guard let cell: BathroomOptionCell = table.dequeueCell(for: indexPath) else { return nil }

            let title = twinType == .TrueTwin ?
                PILocalizedString("trueTwinOptionTitle") :
                PILocalizedString("premierInnTwinOptionTitle")

            let description = twinType == .TrueTwin ?
                PILocalizedString("trueTwinOptionDescription") :
                PILocalizedString("premierInnTwinOptionDescription")

            cell.bathroomTitle.text = title
            cell.bathroomTitle
                .accessibilityIdentifier = String(describing: twinType) + "Title" + String(describing: roomNumber)
            cell.bathroomDescription.text = description

            cell.radioButton.isHidden = hideRadioButton
            cell.radioButton
                .accessibilityIdentifier = String(describing: twinType) + "RadioButton" + String(describing: roomNumber)

            cell.priceLabel.text = price + " " + PILocalizedString("bathroomPriceCell")

            if let twinTypeToSelect = twinTypeToSelect {
                cell.radioButton.isSelected = twinTypeToSelect == twinType
            }

            return cell
        }, didSelect: { [weak self] _, _ in
            self?.eventHandler?.selected(twinRoomOfType: twinType, forRoomID: uniqueIdentifier)
        })
    }

    private func findLettingTypeIndex(for lettingType: String) -> Int {
        SettingsManager.sharedInstance.twinRoomInfo.firstIndex(where: { $0["lettingType"] as? String == lettingType }) ?? 0
    }

    private var dottedSeparatorRow: FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: DottedSeparatorCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.bottomConstraint.constant = 0
            cell.topCconstraint.constant = 0

            return cell
        })
    }

    private func simpleHeaderFooter(with height: CGFloat) -> FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: height, viewSetup: { _, _ in
            let headerFooterView = UITableViewHeaderFooterView(frame: CGRect.zero)
            headerFooterView.contentView.backgroundColor = .clear

            return headerFooterView
        })
    }

    func titleRow(
        index: Int,
        with viewModel: RoomAndSelectionRowViewModel,
        isNonAccessibleRoom: Bool = false
    ) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: BathroomSelectionHeaderCell = table.dequeueCell(for: indexPath) else { return nil }

            if viewModel.otherRoomAvailabilityDescription != nil {
                cell.alternativeRoomBackground.isHidden = false
                cell.alternativeRoomLabel.isHidden = false
                cell.alternativeRoomLabel.text = viewModel.otherRoomAvailabilityDescription
                cell.changeRoomButton.setTitle(PILocalizedString("bathroomSelectionChangeButtonTitle"), for: .normal)
                cell.changeRoomButton.isHidden = false
            } else {
                cell.alternativeRoomLabel.isHidden = true
                cell.alternativeRoomBackground.isHidden = true
                cell.changeRoomButton.isHidden = true
            }

            cell.roomTypeLabel.text = viewModel.currentRoomDescription
            cell.roomCriteriaLabel.text = viewModel.roomCriteria
            cell.roomNumberLabel.text = viewModel.roomNumber
            cell.solidSeparator.backgroundColor = .TintL2

            cell.changeRoomButton.isEnabled = false

            cell.selectionStyle = .none

            cell.accessibleIcon.isHidden = isNonAccessibleRoom
            cell.roomTypeToIconConstraint.constant = isNonAccessibleRoom ? 0 : 32

            return cell
        }, didSelect: { [weak self] _, _ in
            guard viewModel.otherRoomAvailabilityDescription != nil else {
                NotificationFeedbackManager.shared.provideFeedback(for: .warning)
                return
            }

            NotificationFeedbackManager.shared.provideLightTapFeedback()
            self?.eventHandler?.tappedChange(withIndex: index)
        })
    }
}
