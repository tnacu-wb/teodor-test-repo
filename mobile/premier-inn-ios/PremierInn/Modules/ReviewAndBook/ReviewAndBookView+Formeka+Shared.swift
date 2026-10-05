//
//  ReviewAndBookView+Formeka+Shared.swift
//  PremierInn
//
//  Created by Marcello Mascia on 08/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import UIKit

extension ReviewAndBookViewController {
    func dottedSeparatorRow(tag: String = ReviewAndBookRow.dottedSeparatorCell.rawValue) -> FormekaModelRow {
        FormekaModelRow(tag: tag, cellSetup: { indexPath, _, table in
            guard let cell: DottedSeparatorCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.hiddenSeparatorLocations = [.bottom, .top]

            return cell
        })
    }

    var sectionFooter: FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: 10, viewSetup: { _, _ in
            let footerView = UITableViewHeaderFooterView(frame: CGRect.zero)
            footerView.contentView.backgroundColor = .whiteTwo

            return footerView
        })
    }

    func actionHeader(
        withHeading heading: String,
        isActionButtonHidden: Bool = false,
        accessibilityIdentifier: String = "",
        action: @escaping (() -> Void)
    ) -> FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: 60, viewSetup: { _, table in
            guard let header: ActionableHeader = table.headerFooterView() else { return nil }
            header.leadingConstraint.constant = 16.0
            header.heading.text = heading
            header.heading.textColor = .BasePurple
            header.heading.font = .Heading2_Bold()
            header.heading.accessibilityTraits.insert(.header)
            header.actionButton.setTitle(
                PILocalizedString("reviewChangeButtonTitle", comment: "Review and Book: Change Button Title"),
                for: .normal
            )
            header.actionButton.actionTouchUp { action() }
            header.actionButton.isHidden = isActionButtonHidden
            header.actionButton.accessibilityIdentifier = accessibilityIdentifier + "ChangeButtonAcc"

            return header
        })
    }

    func errorRow(withText text: String) -> FormekaModelRow {
        FormekaModelRow(tag: ReviewAndBookRow.expiryError.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: ErrorCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.errorLabel.text = text
            cell.topConstraint.constant = 1
            cell.leftConstraint.constant = 15
            cell.bottomConstraint.constant = 5
            cell.rightConstraint.constant = 15

            return cell
        })
    }

    func simpleToggleCell(
        withTitle title: String,
        subTitle: String,
        isOn: Bool = false,
        toggle: ((Bool) -> Void)?,
        indexPath: IndexPath
    ) -> UITableViewCell? {
        guard let cell: SwitchCell = table.dequeueCell(for: indexPath) else { return nil }

        let attributedString = NSMutableAttributedString(
            string: title,
            attributes: [NSAttributedString.Key.font: UIFont.Heading4_Semibold()]
        )
        attributedString.append(NSAttributedString(
            string: "\n" + subTitle,
            attributes: [NSAttributedString.Key.font: UIFont.BodySmall()]
        ))

        cell.message.attributedText = attributedString
        cell.toggleSwitch.onTintColor = .Tint1
        cell.toggleSwitch.isOn = isOn
        cell.toggled = toggle

        return cell
    }

    func infoTextRow(
        withName name: String,
        andText text: String,
        imageOverride: UIImage? = nil,
        colourOverride: UIColor? = nil
    ) -> FormekaModelRow {
        let row = FormekaModelRow(tag: name, cellSetup: { indexPath, _, table in
            guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath)
                else { return UITableViewCell() }

            cell.icon.image = imageOverride ?? UIImage(named: "circledTick")
            cell.icon.contentMode = .scaleAspectFit
            cell.icon.tintColor = colourOverride ?? .BaseWhite
            cell.content.attributedText = NSAttributedString(string: text)
            cell.content.font = .Body()
            cell.content.textColor = .BaseWhite
            cell.containerView.backgroundColor = .Tint2
            cell.containerView.layer.borderColor = UIColor.Tint2.cgColor

            return cell
        })

        return row
    }

    func simpleTextCell(string: String, tagName: String) -> FormekaModelRow {
        let row = FormekaModelRow(tag: tagName) { indexPath, _, table in
            guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.contentContainer.backgroundColor = .BaseWhite
            cell.content.textColor = .TintD1
            cell.content.font = .Body()
            cell.padding = UIEdgeInsets(top: 0, left: 16, bottom: 15, right: 16)

            let paragraphStyle = NSMutableParagraphStyle()
            paragraphStyle.lineSpacing = 4

            let mutableAttributedString = NSMutableAttributedString(string: string)
            mutableAttributedString.addAttribute(
                .paragraphStyle,
                value: paragraphStyle,
                range: NSRange(location: 0, length: mutableAttributedString.string.count)
            )

            cell.content.attributedText = NSAttributedString(attributedString: mutableAttributedString)

            return cell
        }
        return row
    }

    func iconTextRow(name: String, text: String, icon: String, iconSize: CGSize, iconTint: UIColor) -> FormekaModelRow {
        FormekaModelRow(tag: name, cellSetup: { indexPath, _, table in
            guard let cell: HotelNotesCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.hiddenSeparatorLocations = [.top, .bottom]
            cell.noteLabel.text = text
            cell.noteLabel.font = .Body()
            cell.noteLabel.textColor = .TintD1
            cell.icon.image = UIImage(named: icon)
            cell.iconHeightConstraint.constant = iconSize.height
            cell.iconWidthConstraint.constant = iconSize.width
            cell.icon.tintColor = iconTint

            cell.backgroundColor = .clear

            return cell
        })
    }

    func paymentIntervalRow(initialValue: PaymentIntervalOption) -> FormekaModelRow {
        let row = FormekaModelRow(
            tag: ReviewAndBookRow.paymentOption.rawValue,
            cellSetup: { [unowned self] indexPath, row, table in
            guard let cell: FormekaSegmentedControlCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.segmentedControl.accessibilityIdentifier = AccessibilityIdentifiers.ReviewAndBook.chooseWhenToPaySegment
            cell.segmentedControl.setTitleTextAttributes([
                NSAttributedString.Key.font: UIFont.Heading4_Semibold(),
                .foregroundColor: UIColor.BasePurple
            ], for: .normal)
            cell.segmentedControl.setTitleTextAttributes([
                NSAttributedString.Key.font: UIFont.Heading4_Semibold(),
                .foregroundColor: UIColor.white
            ], for: .selected)
            cell.segmentedControl.setTitle(PaymentIntervalOption.later.localizedString, forSegmentAt: 0)
            cell.segmentedControl.setTitle(PaymentIntervalOption.now.localizedString, forSegmentAt: 1)

            cell.errorLabel?.textColor = .pinkishRed
            cell.errorLabel?.font = .Body()

            cell.delegate = self

            cell.hiddenSeparatorLocations = [.top, .bottom]
            cell.segmentedControl.selectedSegmentIndex = (row.value as? PaymentIntervalOption) == .later ? 0 : 1

            return cell
        }
        )
        row.value = initialValue

        return row
    }

    func donationRow(bookingDetails: BookingDetails) -> FormekaModelRow? {
           FormekaModelRow(tag: ReviewAndBookRow.donationSummary.rawValue, cellSetup: { indexPath, _, table in
               guard let cell: BookingReviewAdditionsCell = table.dequeueCell(for: indexPath) else { return nil }
               cell.hiddenSeparatorLocations = [.top, .bottom]
               cell.titleLabel.text = bookingDetails.goshDonation?.localizedValue == nil ? nil : PILocalizedString(
                   "bookingSummaryGoshLabel",
                   comment: "Booking summary GOSH label"
               )
               cell.valueLabel.text = bookingDetails.goshDonation?.localizedValue
               cell.backgroundColor = .BaseWhite

               return cell
           })
       }

    func goshSection(bookingDetails: BookingDetails) -> FormekaModelSection? {
        guard bookingDetails.shouldShowDonations else { return nil }
        guard let options = bookingDetails.goshOptions?.packagesOrdered,
              let firstOption = options[safe: 0] else { return nil }

        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(
            tag: ReviewAndBookRow.donation.rawValue,
            cellSetup: { [unowned self] indexPath, _, table in
            guard let cell: GoshCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self
            cell.descriptionLabel.text = bookingDetails.goshOptions?.description?.htmlStripped()
            if let imageSrc = bookingDetails.goshOptions?.imageSrc,
               let url = URL(string: Constants.creditCardImagesBaseUrl + imageSrc) {
                cell.goshImage.setImage(with: url)
            }
            cell.titleLabel.text = bookingDetails.goshOptions?.name
            cell.firstButton.setTitle(firstOption.cost.localizedValue, for: .normal)

            if let secondOption = options[safe: 1] {
                cell.secondButton.setTitle(secondOption.cost.localizedValue, for: .normal)
            } else {
                cell.secondButton.isHidden = true
            }

            if let thirdOption = options[safe: 2] {
                cell.thirdButton.setTitle(thirdOption.cost.localizedValue, for: .normal)
            } else {
                cell.thirdButton.isHidden = true
            }

            let selectedOption = options.firstIndex(where: { $0.cost == bookingDetails.goshDonation })
            let selectedButton = cell.buttonFromIndex(index: selectedOption)
            selectedButton.isSelected = true

            return cell
        }
        ))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
     }
}
