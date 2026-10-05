//
//  HotelDetailsView+AdditionalInfo.swift
//  PremierInn
//
//  Created by Nick Jones on 19/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import UIKit

extension HotelDetailsViewController {
    func additionalInfoSection(
        with viewModel: AdditionalInfoAndFacilitiesViewModel,
        shouldShowParkingInfo: Bool,
        and additionalRoomInfoLettingTypeToShow: String
    ) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        // hotel additional info cell
        rows.append(FormekaModelRow(
            tag: HotelDetailRow.hotelLocationInfoCell.rawValue,
            cellSetup: { [weak self] indexPath, _, table in
            guard let cell: HotelLocationInfoCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.subtitle?.setupLabel(
                text: viewModel.hotelDescription ?? "",
                font: .Body(),
                textAlignment: .left,
                textColor: .TintD1,
                numberOfLines: 3,
                lineBreak: .byTruncatingTail,
                accessibilityIdentifier: "subtitle"
            )

            if cell.directionsButton != nil {
                cell.directionsButton.removeFromSuperview()
            }

            cell.readMoreButton.actionTouchUp {
                self?.eventHandler.locationInformationButtonDidTap()
            }
            return cell
        }
        ))

        rows.append(contentsOf: tickedFacilitiesRows(with: viewModel))

        rows.append(FormekaModelRow(tag: HotelDetailRow.checkInOut.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: CheckInOutHDPCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.checkInValueLabel.text = viewModel.checkInDescription
            cell.checkOutValueLabel.text = viewModel.checkOutDescription

            return cell
        }))

        if viewModel.facilitiesViewModel != nil {
            rows.append(actionCell(
                icon: #imageLiteral(resourceName: "facilities"),
                title: PILocalizedString("facilitiesButtonTitle", comment: "About this hotel: facilities")
            ) { [weak self] _ in
                self?.eventHandler.hotelFacilitiesButtonDidTap()
            })
        }

        rows.append(actionCell(
            icon: #imageLiteral(resourceName: "roomTypes"),
            title: PILocalizedString("roomTypesButtonTitle", comment: "About this hotel: room types")
        ) { [weak self] _ in
            self?.eventHandler.showOurRoomsTapped(lettingType: additionalRoomInfoLettingTypeToShow)
        })

        if shouldShowParkingInfo {
            rows.append(actionCell(
                icon: #imageLiteral(resourceName: "parking"),
                title: PILocalizedString("parkingHotelButtonTitle", comment: "About this hotel: parking")
            ) { [weak self] _ in
                self?.eventHandler.parkingInfoDidTap()
            })
        }

        rows.append(actionCell(
            icon: #imageLiteral(resourceName: "iconDisabledPurple"),
            title: PILocalizedString(
                "disabledAccessInformationButtonTitle",
                comment: "About this hotel: disabled access information"
            )
        ) { [weak self] _ in
            self?.disabledAccessButtonDidTap()
        })

        let footer: FormekaModelHeaderFooter? = {
            FormekaModelHeaderFooter(height: 32, viewSetup: { _, _ in
                let footerView = SeparatorFooterView(lineHeight: 1)

                return footerView
            })
        }()

        return FormekaModelSection(
            header: titleHeader(
                title: viewModel.title,
                height: 32,
                font: .Heading2_Bold(),
                andColour: .TintD1,
                accessibilityIdentifier: AccessibilityIdentifiers.HotelDetails
                                                       .parkingDetails
            ),
            rows: rows,
            footer: footer
        )
    }

    // copied from ReservationsView
    func actionCell(
        icon: UIImage?,
        title: String?,
        hideSeparators: Bool = false,
        action: ((IndexPath) -> Void)?
    ) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: HDPInfoCellTableViewCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.backgroundColor = .white
            cell.iconImageView.image = icon
            cell.iconImageView.tintColor = .TintD1
            cell.titleLabel.text = title
            cell.titleLabel.textColor = .TintD1
            cell.titleLabel.font = .Heading4_Regular()

            cell.setHeight(56)
            return cell
        }, didSelect: { [weak self] indexPath, _ in
            self?.tableView.deselectRow(at: indexPath, animated: true)
            action?(indexPath)
        })
    }

    func facilityRow(facilityTitle: String) -> FormekaModelRow {
        FormekaModelRow(tag: HotelDetailRow.checkInOut.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: ActionIconCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.backgroundColor = .white
            cell.isUserInteractionEnabled = false
            cell.icon.image = UIImage(named: "tick") ?? UIImage(named: "UNKNOWN")
            cell.icon.contentMode = .center
            cell.title.text = PILocalizedString(facilityTitle, comment: "")
            cell.title.font = UIFont.Body()
            cell.title.textColor = .TintD1
            cell.accessoryType = .none
            cell.accessoryView = nil
            cell.topConstraint.constant = 10
            cell.bottomConstraint.constant = 10
            cell.iconWidth.constant = 20
            cell.iconAspectRatio.isActive = true
            cell.iconHeight.constant = 30
            cell.iconTopConstraint.constant = 11

            return cell
        })
    }

    func tickedFacilitiesRows(with viewModel: AdditionalInfoAndFacilitiesViewModel) -> [FormekaModelRow] {
        var rows: [FormekaModelRow] = []

        var prioritisedFacilitiesTitles: [String] = []

        if let parkingFacility = viewModel.facilitiesViewModel?.facilities
           .first(where: { ["COP", "CPP", "CPF"].contains($0.code) }) {
            prioritisedFacilitiesTitles.append(parkingFacility.legend)
        }

        // we need a custom title for restaurant and/or breakfast here
        if let restaurantBreakfastTitle = restaurantBreakfastFacility(with: viewModel) {
            prioritisedFacilitiesTitles.append(restaurantBreakfastTitle)
        }

        if let wifiFacility = viewModel.facilitiesViewModel?.facilities.first(where: { ["WIA", "HUW"].contains($0.code) }) {
            prioritisedFacilitiesTitles.append(wifiFacility.legend)
        }

        if let airconditionFacility = viewModel.facilitiesViewModel?.facilities
           .first(where: { ["ACO", "HAC"].contains($0.code) }) {
            prioritisedFacilitiesTitles.append(airconditionFacility.legend)
        }

        if let liftFacility = viewModel.facilitiesViewModel?.facilities.first(where: { ["LFT", "HUL"].contains($0.code) }) {
            prioritisedFacilitiesTitles.append(liftFacility.legend)
        }

        // show up to 3 facilities
        if prioritisedFacilitiesTitles.isNotEmpty {
            prioritisedFacilitiesTitles = Array(prioritisedFacilitiesTitles[0...min(
                2,
                prioritisedFacilitiesTitles.count - 1
            )])
        }

        prioritisedFacilitiesTitles.forEach { facilityTitle in
            rows.append(facilityRow(facilityTitle: facilityTitle))
        }

        return rows
    }

    func restaurantBreakfastFacility(with viewModel: AdditionalInfoAndFacilitiesViewModel) -> String? {
        var restaurantBreakfastTitle = ""

        if viewModel.facilitiesViewModel?.facilities.first(where: { ["RES", "HRS"].contains($0.code) }) != nil {
            restaurantBreakfastTitle.append(PILocalizedString("Restaurant", comment: "Restaurant"))
        }

        if viewModel.offersBreakfast || viewModel.facilitiesViewModel?.facilities.first(where: { $0.code == "ZBF" }) != nil {
            if restaurantBreakfastTitle.isNotEmpty { restaurantBreakfastTitle.append("/") }

            restaurantBreakfastTitle.append(PILocalizedString("Breakfast", comment: "Breakfast"))
        }

        if restaurantBreakfastTitle.isNotEmpty {
            restaurantBreakfastTitle.append(" ")
            restaurantBreakfastTitle.append(PILocalizedString("available", comment: "available"))

            return restaurantBreakfastTitle
        }

        return nil
    }
}
