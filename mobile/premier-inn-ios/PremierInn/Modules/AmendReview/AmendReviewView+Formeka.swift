//
//  AmendReviewView+Formeka.swift
//  PremierInn
//
//  Created by Freddie Parks on 23/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import UIKit

struct AmendPaymentIntervalViewModel: PaymentIntervalViewModel {
    let amendPaymentOptions: AmendPaymentOptions

    var userCanChooseWhenToPay: Bool {
        amendPaymentOptions.payNow && amendPaymentOptions.payOnArrival
    }

    var title: String? {
        PILocalizedString("reviewPaymentSectionIntervalFlexibleTitle")
    }

    var interval: PaymentIntervalOption {
        amendPaymentOptions.paymentOptionSelected
    }

    var infoMessages: [PaymentInformationViewModel]? {
        if amendPaymentOptions.paymentOptionSelected == .now {
            return [AmendPaymentInfoViewModel(
                message: PILocalizedString("amendPayNow3CMessage"),
                style: .info
            )]
        } else {
            return nil
        }
    }
}

struct AmendPaymentInfoViewModel: PaymentInformationViewModel {
    var message: String
    var style: NotificationStyle
}

struct AmendReviewTripSummary {
    let tripSummaryTitle: String
    let tripSummary: String
    let arrivingTitle: String
    let departingTitle: String
    let arrivingDate: String
    let departingDate: String?
    let arrivingTime: String
    let departingTime: String
}

extension AmendReviewView {
    func viewModelSections(with reviewViewModel: AmendReviewViewViewModel) -> [FormekaModelSection] {
        var sections: [FormekaModelSection] = []

        if reviewViewModel.showECILCORemovalMessage {
            sections.append(eciLcoBannerSection())
        }

        sections.append(previousSection(with: reviewViewModel.previousTotal))
        sections.append(breakdownSection(with: reviewViewModel.amendments))
        sections.append(newSection(
            outstandingBalance: reviewViewModel.outstandingBalance,
            hotelName: reviewViewModel.hotelName,
            hotelImageURL: reviewViewModel.hotelImageUrl,
            tripSummary: reviewViewModel.tripSummary
        ))

        if let amendPaymentViewModel = reviewViewModel.amendPaymentViewModel,
           amendPaymentViewModel.userCanChooseWhenToPay {
            sections.append(paymentOptionsSection(with: amendPaymentViewModel))
        }

        return sections
    }

    private func eciLcoBannerSection() -> FormekaModelSection {
        let row = iconInfoRow(
            tag: AmendReviewTag.warning.rawValue,
            text: PILocalizedString("ecilcoRemovalMessage"),
            topPadding: 4,
            bottomPadding: 4,
            style: .alert
        )
        return FormekaModelSection(header: nil, rows: [row], footer: nil)
    }

    private func paymentOptionsSection(with viewModel: PaymentIntervalViewModel) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        rows.append(paymentToggleRow(initialValue: viewModel.interval))

        rows.append(contentsOf: viewModel.infoMessages?.compactMap {
            iconInfoRow(
                tag: ReviewAndBookRow.paymentTimeMessage3C.rawValue,
                text: $0.message,
                topPadding: 4,
                bottomPadding: 8,
                style: $0.style
            )
        } ?? [])

        return FormekaModelSection(
            header: actionHeader(withHeading: viewModel.title ?? ""),
            rows: rows,
            footer: nil
        )
    }

    func paymentToggleRow(initialValue: PaymentIntervalOption) -> FormekaModelRow {
        let row = FormekaModelRow(
            tag: ReviewAndBookRow.paymentOption.rawValue,
            cellSetup: { [weak self] indexPath, row, table in
            guard let cell: FormekaSegmentedControlCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.segmentedControl.tintColor = .gunMetal
            cell.segmentedControl.accessibilityIdentifier = AccessibilityIdentifiers.ReviewAndBook.chooseWhenToPaySegment
            cell.segmentedControl.setTitleTextAttributes(
                [NSAttributedString.Key.font: UIFont.Heading4_Semibold()],
                for: .normal
            )
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

    func actionHeader(withHeading heading: String) -> FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: 55, viewSetup: { _, table in
            guard let header: ActionableHeader = table.headerFooterView() else { return nil }
            header.heading.text = heading
            header.actionButton.isHidden = true
            return header
        })
    }

    private func previousSection(with previousTotal: String) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        rows.append(secondaryPriceRow(
            with: PILocalizedString("amendReviewPreviousTotalLabel", comment: ""),
            price: previousTotal
        ))
        rows.append(solidSeparator())

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func breakdownSection(with amendments: [AmendmentDetailsViewModel]) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        let changesMadeRowText = NSAttributedString(
            string: PILocalizedString("amendReviewYourChangesLabel", comment: ""),
            attributes: [NSAttributedString.Key.font: UIFont.Heading4_Semibold()]
        )

        rows.append(priceDetailRow(with: changesMadeRowText, price: nil))
        rows.append(contentsOf: (amendments.compactMap { priceDetailRow(with: $0.title, price: $0.description) }))
        rows.append(solidSeparator())

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func newSection(
        outstandingBalance: String,
        hotelName: String?,
        hotelImageURL: URL?,
        tripSummary: AmendReviewTripSummary
    ) -> FormekaModelSection {
        var rows: [FormekaModelRow] = []

        rows.append(secondaryPriceRow(with: PILocalizedString("amendReviewNewBookingSummaryLabel", comment: ""), price: ""))
        rows.append(hotelImageRow(title: hotelName, imageURL: hotelImageURL))

        rows.append(checkInOutRow(tripSummary: tripSummary))

        rows.append(primaryPriceRow(
            with: PILocalizedString("amendReviewBalanceOutstandingLabel", comment: ""),
            price: outstandingBalance
        ))
        rows.append(totalSubtextRow(text: outstandingBalanceActionRequired))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func hotelImageRow(title: String?, imageURL: URL?) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: BookingConfirmationHotelInfoCell = table.dequeueCell(for: indexPath) else { return nil }

            if let imageURL {
                cell.hotelImageView.setImage(with: imageURL)
            }
            cell.hotelNameLabel.text = title
            cell.hiddenSeparatorLocations = [.bottom]

            return cell
        })
    }

    private func checkInOutRow(tripSummary: AmendReviewTripSummary) -> FormekaModelRow {
        FormekaModelRow(tag: BookingConfirmationRowType.checkInOut.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: CheckInOutCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.tripTitleLabel.text = tripSummary.tripSummaryTitle
            cell.tripValueLabel.text = tripSummary.tripSummary
            cell.checkInLabel.text = tripSummary.arrivingTitle
            cell.checkOutLabel.text = tripSummary.departingTitle
            cell.checkInDateLabel.text = tripSummary.arrivingDate
            cell.checkInValueLabel.text = tripSummary.arrivingTime
            cell.checkOutDateLabel.text = tripSummary.departingDate
            cell.checkOutValueLabel.text = tripSummary.departingTime
            cell.hiddenSeparatorLocations = [.top, .bottom]

            return cell
        })
    }

    private func cvvInputRow(cvvModel: PaymentCardCVVModel) -> FormekaModelRow {
        var numberTraits = FormekaTextFieldTraits()
        numberTraits.keyboardType = .numberPad

        return FormekaModelRow(
            tag: ReviewAndBookRow.cvv.rawValue,
            title: PILocalizedString("security code", comment: ""),
            inlineValidators: [.required, .numeric],
            onBlurValidators: [CVVLengthValidator(length: cvvModel.cvvLength)],
            cellSetup: { [unowned self] indexPath, row, table in
                guard let cell: CVVInputCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.delegate = self

                cell.contentView.backgroundColor = .white
                cell.cvvTextField.accessibilityIdentifier = AccessibilityIdentifiers.ReviewAndBook.cvv
                cell.cvvTextField.text = row.value?.displayName
                cell.cvvTextField.textColor = .TintD1
                cell.cvvTextField.font = .Body()
                cell.cvvTextField.applyTraits(numberTraits)
                cell.cvvTextField.attributedPlaceholder = NSAttributedString(
                    string: PILocalizedString(
                        "reviewCardSecurityCodeAcronym",
                        comment: "Review and Book: card security code acronym"
                    ),
                    attributes: [.foregroundColor: UIColor.TintL1]
                )
                cell.errorLabel?.textColor = .Tint8
                cell.errorLabel?.font = .Body()
                cell.cvvInfoLabel.text = cvvModel.cvvInputHelperDescription

                cell.maxNumberOfCharacters = cvvModel.cvvLength

                cell.errorMessage = row.error?.localizedDescription
                cell.cvvTextField.layer.borderColor = row.error == nil ? UIColor.TintD2.cgColor : UIColor.Tint8.cgColor

                return cell
            }, cellWillDisplay: { cell, _, row in
                if let containerCell = cell as? CVVInputCell {
                    containerCell.cvvTextField.layer.borderColor = row.error == nil ? UIColor.TintD2.cgColor : UIColor.Tint8
                        .cgColor
                }
        }, didSelect: { [unowned self] indexPath, _ in
            table.cellForRow(at: indexPath)?.becomeFirstResponder()
        }
        )
    }

    private func priceDetailRow(with description: NSAttributedString, price: String?) -> FormekaModelRow {
        let price = NSAttributedString(
            string: price ?? "",
            attributes: [NSAttributedString.Key.font: UIFont.Heading4_Semibold()]
        )
        return priceRow(withDescription: description, andPrice: price)
    }

    private func secondaryPriceRow(with description: String, price: String) -> FormekaModelRow {
        let price = NSAttributedString(string: price, attributes: [NSAttributedString.Key.font: UIFont.Heading4_Semibold()])
        let description = NSAttributedString(
            string: description,
            attributes: [NSAttributedString.Key.font: UIFont.Heading4_Semibold()]
        )

        return priceRow(withDescription: description, andPrice: price)
    }

    private func primaryPriceRow(with description: String, price: String) -> FormekaModelRow {
        let price = NSAttributedString(string: price, attributes: [NSAttributedString.Key.font: UIFont.Heading1_Bold()])
        let description = NSAttributedString(
            string: description,
            attributes: [NSAttributedString.Key.font: UIFont.Heading4_Semibold()]
        )

        return priceRow(withDescription: description, andPrice: price)
    }

    private func priceRow(
        withDescription description: NSAttributedString,
        andPrice price: NSAttributedString
    ) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: BookingReviewAdditionsCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.titleLabel.textColor = .TintD1

            cell.titleLabel.attributedText = description

            cell.valueLabel.textColor = .TintD1
            cell.valueLabel.attributedText = price
            ContentsquareConfig.mask(view: cell)
            return cell
        })
    }

    func totalSubtextRow(text: String?) -> FormekaModelRow {
        FormekaModelRow(tag: AmendReviewTag.balanceOutstanding.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.content.text = text
            cell.padding.top = 0

            return cell
        })
    }

    private func solidSeparator(style: BottomBorderCell.PaddingStyle = .paddedGap(left: 16, right: -16)) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: BottomBorderCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.paddingStyle = style
            return cell
        })
    }

    private func paymentCardHeader(with heading: String, canChangeCard: Bool = false) -> FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: 55, viewSetup: { _, table in
            guard let header: ActionableHeader = table.headerFooterView() else { return nil }
            header.heading.text = heading
            header.actionButton.setTitle(PILocalizedString("actionableEditButtonTitle"), for: .normal)

            if canChangeCard {
                // Might be implemented if we give the user the choice to pay upfront
                // header.actionButton.actionTouchUp { self.eventHandler?.changeCardDidTap() }
                header.actionButton.isHidden = false
            } else {
                header.actionButton.isHidden = true
            }

            return header
        })
    }
}
