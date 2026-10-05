//
//  PaymentDetailsView+Formeka.swift
//  PremierInn
//
//  Created by Freddie Parks on 07/09/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Formeka
import UIKit

extension PaymentDetailsView {
    func formekaViewModel(with viewModel: PaymentDetailsViewModel) -> FormekaViewModel {
        var sections = [FormekaModelSection]()

        if let infoMessage = viewModel.infoMessage {
            sections.append(infoSection(with: infoMessage))
        }

        sections.append(confirmationSection(with: viewModel.confirmationViewModel))

        return FormekaViewModel(
            sections: sections
        )
    }

    private func infoSection(with infoMessage: String) -> FormekaModelSection {
        FormekaModelSection(
            header: nil,
            rows: [infoRow(with: infoMessage)],
            footer: footer(title: nil, height: 10, backgroundColor: .clear)
        )
    }

    private func confirmationSection(with confirmationViewModel: PaymentDetailsConfirmationViewModel)
        -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: BookingReviewTotalPriceCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.backgroundColor = .clear
            cell.contentView.backgroundColor = .clear

            cell.totalLabel.text = confirmationViewModel.totalLabel
            cell.totalPriceLabel.text = confirmationViewModel.total

            return cell
        }))

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: BookingReviewSubmitCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.backgroundColor = .clear
            cell.contentView.backgroundColor = .clear

            cell.continueButton.setTitle(confirmationViewModel.ctaTitle, for: .normal)
            cell.continueButton.addTarget(self, action: #selector(self.ctaDidTap), for: .touchUpInside)

            if let icon = confirmationViewModel.ctaIcon {
                if cell.continueButton.viewWithTag(999) == nil {
                    let imageView = UIImageView(image: icon)
                    imageView.frame = cell.continueButton.bounds.insetBy(dx: 20, dy: 0)
                    imageView.contentMode = .right
                    imageView.tintColor = .BaseWhite
                    imageView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
                    imageView.tag = 999
                    cell.continueButton.addSubview(imageView)
                }
            }

            return cell
        }))

        if let termsMessage = confirmationViewModel.termsMessage {
            rows.append(flexibleContentRow(with: termsMessage))
        }

        if let bookingsTermsMessage = confirmationViewModel.bookingManagementTermsMessage {
            rows.append(flexibleContentRow(with: bookingsTermsMessage))
        }

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
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

    private func flexibleContentRow(with attributedText: NSAttributedString) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.content.attributedText = attributedText
            cell.messageBottomConstraint.constant = 0
            cell.backgroundColor = .clear
            cell.contentView.backgroundColor = .clear

            return cell
        })
    }

    private func infoRow(with message: String) -> FormekaModelRow {
        FormekaModelRow(tag: ReviewAndBookRow.paymentAuthInfo.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath) else { return nil }

            let paragraphStyle = NSMutableParagraphStyle()
            paragraphStyle.lineSpacing = 4

            cell.bottomConstraint.constant = 10
            cell.topConstraint.constant = 10

            cell.content.text = message
            cell.content.textColor = .TintD1
            cell.content.font = .Body()
            cell.content.backgroundColor = .whiteTwo
            cell.icon.image = #imageLiteral(resourceName: "importantInfo")
            cell.icon.tintColor = .BasePurple

            cell.backgroundColor = .white

            return cell
        })
    }
}
