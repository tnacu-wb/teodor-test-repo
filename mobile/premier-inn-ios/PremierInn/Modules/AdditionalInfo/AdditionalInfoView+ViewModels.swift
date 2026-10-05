//
//  AdditionalInfoView+ViewModels.swift
//  PremierInn
//
//  Created by Freddie Parks on 13/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

class HotelFacilitiesAndRoomFeaturesViewModel: NSObject, UITableViewDataSource {
    private let facilitiesTitle: String
    private let facilityDescriptions: [String]

    private let roomFeaturesTitle: String
    private let roomFeatureDescriptions: [String]

    init(
        withFacilitiesTitle facilitiesTitle: String,
        facilityDescriptions: [String],
        roomFeaturesTitle: String,
        andRoomFeatureDescriptions roomFeatureDescriptions: [String]
    ) {
        self.facilitiesTitle = facilitiesTitle
        self.facilityDescriptions = facilityDescriptions
        self.roomFeaturesTitle = roomFeaturesTitle
        self.roomFeatureDescriptions = roomFeatureDescriptions
    }

    func numberOfSections(in tableView: UITableView) -> Int { 2 }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        section == 0 ? facilityDescriptions.count + 2 : roomFeatureDescriptions.count + 2
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let endIndex = tableView.numberOfRows(inSection: indexPath.section) - 1

        switch indexPath.row {
        case 0:
            if let cell: FlexibleTextContentCell = tableView.dequeueCell(for: indexPath) {
                cell.content.textColor = .TintD1
                cell.content.font = UIFont.Heading3_Bold()
                cell.content.text = indexPath.section == 0 ? facilitiesTitle : roomFeaturesTitle
                cell.content.accessibilityIdentifier = indexPath.section == 0 ? AccessibilityIdentifiers.AdditionalInfo
                    .hotelFacilitiesHeader : AccessibilityIdentifiers.AdditionalInfo.roomFeaturesHeader
                cell.padding = UIEdgeInsets(top: 0, left: 28, bottom: 15, right: 28)

                return cell
            }
        case endIndex:
            if let cell: HotelDetailSeparatorCell = tableView.dequeueCell(for: indexPath) {
                cell.proportionalWidthConstraint
                    .constant = (tableView.frame.size.width - (tableView.frame.size.width * 0.15)) - 64
                cell.topMargin.constant = 25
                cell.bottomMargin.constant = 35
                return cell
            }
        default:

            guard let cell: ActionIconCell = tableView.dequeueCell(for: indexPath) else { return UITableViewCell() }

            let arrayOfTextToUse = indexPath.section == 0 ? facilityDescriptions : roomFeatureDescriptions

            let indexToUse = indexPath.row - 1
            guard indexToUse <= arrayOfTextToUse.endIndex else { return UITableViewCell() }

            cell.backgroundColor = .BaseWhite
            cell.isUserInteractionEnabled = false
            cell.icon.image = UIImage(named: "tick") ?? UIImage(named: "UNKNOWN")
            cell.title.text = arrayOfTextToUse[indexToUse]
            cell.title.font = UIFont.Body()
            cell.title.textColor = .TintD1
            cell.accessoryType = .none
            cell.accessoryView = nil
            cell.topConstraint.constant = 10
            cell.bottomConstraint.constant = 10
            cell.iconWidth.constant = 20

            return cell
        }
        return UITableViewCell()
    }
}

class HotelInfoViewModel: NSObject, UITableViewDataSource {
    private let notes: [String]

    init(with notes: [String]) {
        self.notes = notes
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        notes.count
    }

    func numberOfSections(in tableView: UITableView) -> Int {
        1
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell: HotelNotesCell = tableView.dequeueCell(for: indexPath) {
            cell.icon.tintColor = .BasePurple
            cell.noteLabel.text = notes[indexPath.row]
            return cell
        }
        return UITableViewCell()
    }
}

class HotelLocationInfoViewModel: NSObject, UITableViewDataSource {
    private let hotelName: String
    private let hotelDescription: String
    private let hotelDirections: String

    init(with hotelName: String, description: String, and directions: String) {
        self.hotelName = hotelName
        self.hotelDescription = description
        self.hotelDirections = directions
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        3
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        switch indexPath.row {
        case 0:
            if let cell: HotelTitleCell = tableView.dequeueCell(for: indexPath) {
                cell.hotelName.text = hotelName
                cell.hotelName.font = .Heading3_Bold()
                cell.hotelName.accessibilityIdentifier = AccessibilityIdentifiers.AdditionalInfo.hotelInfoName

                return cell
            }

        case 1:
            if let cell: HotelDescriptionCell = tableView.dequeueCell(for: indexPath) {
                cell.hotelDescription.attributedText = NSAttributedString.attributedStringWith(
                    text: hotelDescription,
                    lineSpacing: 7,
                    font: UIFont.Body(),
                    textColor: .TintD1,
                    textAlignment: .justified
                )
                cell.hotelDescription.accessibilityIdentifier = AccessibilityIdentifiers.AdditionalInfo
                    .hotelInfoAreaDescription

                return cell
            }

        case 2:
            if let cell: HotelDetailItemCell = tableView.dequeueCell(for: indexPath) {
                cell.hotelDetailTitle.text = PILocalizedString("directionCellTitle", comment: "Directions cell title")
                cell.hotelDetailTitle.accessibilityIdentifier = AccessibilityIdentifiers.AdditionalInfo
                    .hotelInfoDirectionsHeader

                cell.hotelDetailDescription.attributedText = NSAttributedString.attributedStringWith(
                    text: hotelDirections,
                    lineSpacing: 7,
                    font: UIFont.Body(),
                    textColor: .TintD1
                )
                cell.hotelDetailDescription.accessibilityIdentifier = AccessibilityIdentifiers.AdditionalInfo
                    .hotelInfoDirectionsDescription

                return cell
            }

        default:
            break
        }

        return UITableViewCell()
    }
}

class HotelParkingInfoViewModel: NSObject, UITableViewDataSource {
    private let parkingDetails: String

    init(parkingDetails: String) {
        self.parkingDetails = parkingDetails
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        1
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell: HotelDetailItemCell = tableView.dequeueCell(for: indexPath) {
            cell.hotelDetailTitle.isHidden = true
            cell.hotelDetailDescription.setupLabel(
                text: parkingDetails,
                font: .Body(),
                lineHeightMultiple: 1.23,
                textColor: .TintD1,
                accessibilityIdentifier: AccessibilityIdentifiers.ParkingInfo
                                                   .parkingInfoDescription
            )
            return cell
        }
        return UITableViewCell()
    }
}
