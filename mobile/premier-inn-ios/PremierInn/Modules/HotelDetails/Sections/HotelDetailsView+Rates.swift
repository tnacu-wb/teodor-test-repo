//
//  Formeka+RateSections.swift
//  PremierInn
//
//  Created by Nick Jones on 15/03/2019.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import Formeka
import UIKit

class RateCell: UITableViewCell {
    @IBOutlet weak var rateContainer: RoundedCornersView!
    @IBOutlet weak var rateName: UILabel! {
        didSet {
            rateName.font = .Heading3_Bold()
        }
    }
    @IBOutlet weak var price: UILabel! {
        didSet {
            price.font = .Heading3_Bold()
        }
    }
    @IBOutlet weak var rateDescription: UILabel! {
        didSet {
            rateDescription.font = .BodySmall()
        }
    }

    @IBOutlet weak var discountedRateLabel: UILabel! {
        didSet {
            discountedRateLabel.font = .Heading3_Bold()
            discountedRateLabel.textColor = .TintD1
        }
    }

    @IBOutlet weak var rateButton: RoundedCornersButton! {
        didSet {
            rateButton.backgroundColor = UIColor.Tint1
            rateButton.titleLabel?.font = .Button2()
        }
    }
    @IBOutlet weak var criteria: UILabel! {
        didSet {
            criteria.font = .BodySmall()
        }
    }
    @IBOutlet weak var businessRateLabel: UILabel! {
        didSet {
            businessRateLabel.textAlignment = .right
        }
    }

    func updateContent(withRateViewModel viewModel: RateCellViewModel) {
        contentView.backgroundColor = .BaseWhite

        rateContainer.layer.borderWidth = 1
        rateContainer.layer.borderColor = UIColor.TintL3.cgColor

        rateButton.setTitle(viewModel.rateButtonTitle, for: .normal)
        rateButton.setTitleColor(.BaseWhite, for: .normal)
        rateButton.layer.masksToBounds = false
        rateButton.layer.shadowOpacity = 1
        rateButton.layer.shadowRadius = 0
        rateButton.layer.shadowOffset = .zero

        rateName.attributedText = viewModel.rateName
        rateName.textColor = UIColor.ColourDL1

        rateDescription.text = viewModel.rateDescription
        rateDescription.textColor = UIColor.ColourDL2

        criteria.text = viewModel.criteriaDescription
        criteria.textColor = UIColor.ColourDL2

        price.attributedText = viewModel.priceText
        price.textColor = UIColor.ColourDL1

        // non-discounted
        discountedRateLabel.isHidden = viewModel.nonDiscountedRate == nil
        if let discountedRate = viewModel.nonDiscountedRate {
            discountedRateLabel.attributedText = discountedRate
            discountedRateLabel.textColor = UIColor.ColourDL2
            price.textColor = UIColor.Tint1
        } else {
            price.textColor = UIColor.ColourDL1
        }

        accessibilityHelper(with: viewModel.rateCellAccessibilityHelper)

        if let promotionTag = viewModel.promotionTag {
            businessRateLabel.attributedText = promotionTag
            businessRateLabel.font = .SubtextStrong()
            businessRateLabel.textColor = .Tint1
            businessRateLabel.backgroundColor = .Tint3
            businessRateLabel.layer.cornerRadius = 4
            businessRateLabel.layer.masksToBounds = true
            businessRateLabel.isHidden = false
        } else {
            businessRateLabel.text = nil
            businessRateLabel.isHidden = true
        }
    }
}

extension RateCell {
    private func accessibilityHelper(with accessibilityHelper: RateCellAccessibilityHelper?) {
        guard let accessibilityHelper = accessibilityHelper else { return }

        price.accessibilityIdentifier = accessibilityHelper.priceIdentifier
        criteria.accessibilityIdentifier = accessibilityHelper.daysIdentifier
        rateDescription.accessibilityIdentifier = accessibilityHelper.detailsIdentifier
        rateName.accessibilityIdentifier = accessibilityHelper.headerIdentifier
        rateButton.accessibilityIdentifier = accessibilityHelper.buttonIdentifier
    }
}


extension HotelDetailsViewController {
    func titleHeaderFooter(
        title: String,
        textAlignment: NSTextAlignment = .center,
        height: CGFloat = 50,
        font: UIFont,
        backgroundColour: UIColor = .white,
        andColour colour: UIColor = .BasePurple,
        action: (() -> Void)? = nil
    ) -> FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: 50, viewSetup: { _, table in
            guard let header: SimpleHeaderWithActionLabel = table.headerFooterView() else { return nil }

            header.contentView.backgroundColor = backgroundColour

            header.titleLabel.text = title
            header.titleLabel.font = font
            header.titleLabel.textAlignment = textAlignment
            header.titleLabel.textColor = colour
            header.titleLabel.accessibilityTraits.insert(.header)
            header.actionButton.isHidden = true

            guard let action = action else { return header }

            header.actionButton.isHidden = false
            let buttonTitle = PILocalizedString("findOutMore", comment: "Find more header button title")
            let attributedString = NSAttributedString(
                string: buttonTitle,
                attributes: [
                    .foregroundColor: UIColor.BasePurple,
                    .font: UIFont.Body()
                ]
            )
            header.actionButton.setAttributedTitle(attributedString, for: .normal)
            header.actionButton.actionTouchUp {
                action()
            }

            return header
        })
    }

    func rateSections(
        with rateSectionViewModel: RateSectionViewModel,
        andShowAction showAction: Bool = false
    ) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        if let roomsAvailable = rateSectionViewModel.urgencyMessagingRoomsAvailable {
            // Check Adobe AB testing variant for urgency messaging
            let urgencyMessagingGroupType = MVTManager.sharedInstance.urgencyMessagingGroupType

            if urgencyMessagingGroupType == .variant {
                rows.append(urgencyMessagingRow(numberOfRooms: roomsAvailable))
            }
        }

        for (index, viewModel) in rateSectionViewModel.rowViewModels.enumerated() {
            rows.append(rateRow(withIndex: index, viewModel: viewModel))
        }

        if rateSectionViewModel.shouldShowCityTaxRow {
            rows.append(cityTaxRow())
        }

        let action: (() -> Void)? = showAction ? { [weak self] in
            self?.eventHandler.showOurRoomsTapped(lettingType: rateSectionViewModel.rateRoomSectionLettingType)
            } : nil

        let title: String = rateSectionViewModel.title

        // show a header always except for when searching for accessible rooms
        let header = titleHeaderFooter(
            title: title,
            textAlignment: .left,
            height: 40,
            font: .Heading3_Semibold(),
            backgroundColour: .BaseWhite,
            andColour: .black,
            action: action
        )

        let footer = FormekaModelHeaderFooter(
            height: 32,
            viewSetup: { _, _ in
                SeparatorFooterView(lineHeight: 1)
            }
        )

        return FormekaModelSection(header: header, rows: rows, footer: footer)
    }

    private func rateRow(withIndex index: Int, viewModel: RateCellViewModel) -> FormekaModelRow {
        FormekaModelRow(tag: HotelDetailRow.rateCell.rawValue, cellSetup: { [weak self] indexPath, _, table in
            guard let cell: RateCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.updateContent(withRateViewModel: viewModel)
            cell.rateButton.isEnabled = true
            cell.rateButton.alpha = 1.0

            cell.rateButton.actionTouchUp {
                self?.eventHandler
                    .selectedRate(
                        withRateID: viewModel.uniqueRateID,
                        lettingType: viewModel.lettingType
                    ) // this needs rate and class
            }

            return cell
        })
    }

    func cityTaxRow() -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.content.text = PILocalizedString(
                "hotelDetailRatesIncludeCityTaxMessage",
                comment: "Hotel details: city tax included in rates"
            )
            cell.content.font = UIFont.Body()
            cell.content.textAlignment = .right
            cell.messageTopConstraint.constant = 12
            cell.messageBottomConstraint.constant = 0
            cell.backgroundColor = .BaseGrey

            return cell
        })
    }

    func urgencyMessagingRow(numberOfRooms: Int) -> FormekaModelRow {
        FormekaModelRow(tag: HotelDetailRow.hotelFewRoomsCell.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: HotelFewRoomsCell = table.dequeueCell(for: indexPath) else { return nil }
            let message = String.localizedStringWithFormat(
                NSLocalizedString("urgencyMessagingRoomsLeft", comment: ""),
                numberOfRooms
            )
            cell.configureWithIcon(message: message)
            return cell
        })
    }

    func substitutionWarningRow(withText text: NSAttributedString) -> FormekaModelSection {
        let row = FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.backgroundColor = .clear
            cell.content.attributedText = text
            cell.icon.tintColor = .BaseWhite
            cell.containerView.backgroundColor = .Tint2

            return cell
        })

        return FormekaModelSection(header: nil, rows: [row], footer: nil)
    }

    func simpleHeader(ofHeight height: CGFloat, backgroundColor: UIColor = .whiteTwo) -> FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: height, viewSetup: { _, _ in
            let footerView = UITableViewHeaderFooterView(frame: CGRect.zero)
            footerView.contentView.backgroundColor = backgroundColor

            return footerView
        })
    }
}

extension Rate {
    func businessLogicName(hotelBrand: HotelBrand, biggerRoomAvailable: Bool) -> NSAttributedString? {
        if let cellCode = cellCode, cellCode == "EMP01" {
            return NSMutableAttributedString(string: PILocalizedString(
                "employeeRatesDescription",
                comment: "Employee Rates Description"
            ))
        }

//        if ![.none, .bflex, .bflexp].contains(cellCode) {
//            return NSMutableAttributedString(string: cellCode.description)
//        }

        let rateText: String = {
            name(with: hotelBrand)
        }()

        let attributes: [NSAttributedString.Key: Any] = [.font: UIFont.Body()]
        let result = NSMutableAttributedString(string: rateText)

        if isBiggerRoom {
            if hotelBrand == .hub {
                result.style(
                    text: "(\((PILocalizedString("hotelDetailsBiggerRoom", comment: "Hotel details: bigger room").lowercased())))",
                    withAttributes: attributes
                )
            } else {
                result.style(
                    text: "(\((PILocalizedString("hotelDetailsPremiumRoom", comment: "Hotel details: premium room").lowercased())))",
                    withAttributes: attributes
                )
            }
        } else if biggerRoomAvailable {
            result.style(
                text: "(\((PILocalizedString("hotelDetailsStandardRoom", comment: "Hotel details: standard room").lowercased())))",
                withAttributes: attributes
            )
        }

        return result
    }

    func businessLogicDescription(with brand: HotelBrand? = .premierInn) -> String {
        longDescription(with: brand)
    }
}
