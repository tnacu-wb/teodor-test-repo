//
//  OneTimePasswordCaputure+ViewModel.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 01/09/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//
import Formeka
import UIKit

extension OTPEmailCaptureView {
	func loadViewModel(defaultEmail: String?) {
        viewModel = FormekaViewModel(sections: viewModelSections(defaultEmail: defaultEmail))
        viewModel?.delegate = self

        table.delegate = viewModel
        table.dataSource = viewModel
        table.backgroundColor = .BasePurple

        table.reloadData()
    }

    func registerTableElements() {
        table.registerCellNib(with: FlexibleTextContentCell.self)
        table.registerCellNib(with: FormekaTextFieldCell.self)
        table.registerCellNib(with: FormekaSubmitButtonCell.self)
        table.registerCellNib(with: FormekaErrorBannerCell.self)
        table.registerHeaderFooterNib(with: FormekaHeader.self)
        table.registerHeaderFooterNib(with: FormekaIconFooter.self)
    }

	private func viewModelSections(defaultEmail: String?) -> [FormekaModelSection] {
        var sections: [FormekaModelSection] = []

        if presenter?.shouldShowError ?? false {
            sections.append(errorSection())
        }
        sections.append(emailSection(defaultEmail: defaultEmail))
        sections.append(submitSection())

        return sections
    }

    private func errorSection() -> FormekaModelSection {
        FormekaModelSection(
            header: nil,
            rows: [
                FormekaModelRow(tag: ResetPasswordRow.banner.rawValue, cellSetup: { indexPath, _, table in
                    guard let cell: FormekaErrorBannerCell = table.dequeueCell(for: indexPath) else { return nil }

                    cell.backgroundColor = .whiteTwo

                    cell.message.text = PILocalizedString(
                        "resetPasswordErrorMessage",
                        comment: "Reset password: error message"
                    )
                    cell.message.textColor = .paleRed
                    cell.message.font = .Body()

                    cell.accessibilityIdentifier = "bannerCellAcc"

                    return cell
                })
            ],
            footer: nil
        )
    }

	private func emailSection(defaultEmail: String?) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        let row = FormekaModelRow(
            tag: ResetPasswordRow.email.rawValue,
            onBlurValidators: [.required, .email],
            cellSetup: { [unowned self] indexPath, row, table in
                guard let cell: FormekaTextFieldCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.delegate = self

                cell.titleLabel.text = PILocalizedString("resetPasswordEmailLabel", comment: "Reset password: email label")
                cell.titleLabel.textColor = .BaseWhite
                cell.titleLabel.font = .BodySmall()

                cell.textField.text = defaultEmail ?? row.value?.displayName
                cell.textField.keyboardType = .emailAddress
                cell.textField.textColor = .BaseWhite
                cell.textField.font = .Body()
                cell.textField.accessibilityIdentifier = AccessibilityIdentifiers.ResetPassword.emailTextField
				cell.textField.becomeFirstResponder()

                cell.errorLabel?.textColor = .Tint8
                cell.errorLabel?.font = .BodySmall()

                cell.accessibilityIdentifier = AccessibilityIdentifiers.ResetPassword.emailCell
                cell.errorMessage = row.error?.localizedDescription
				cell.errorBackground?.backgroundColor = .BaseGrey

                cell.textField.autocorrectionType = .yes
                cell.textField.textContentType = .emailAddress
                cell.backgroundColor = .BasePurple

                return cell
            }, didSelect: { [unowned self] indexPath, _ in
                presenter?.userTappedEmailAddressRow(at: indexPath)
        }
        )

		rows.append(emptyRow(
		    topHeight: 16,
		    bottomHeight: 16,
		    hiddenSeparators: [.top, .bottom],
		    backgroundColor: .BasePurple
		))
        rows.append(titleRow(
            text: PILocalizedString("verifyYourDetailsTitle"),
            font: .Heading1_ExtraBold(),
            hiddenSeparators: [.top, .bottom]
        ))
		rows.append(emptyRow(topHeight: 6, bottomHeight: 6, hiddenSeparators: [.top, .bottom], backgroundColor: .BasePurple))
		rows.append(titleRow(
		    text: PILocalizedString("verifyYourDetailsMessage"),
		    font: .Body_Medium(),
		    hiddenSeparators: [.top, .bottom]
		))
		rows.append(emptyRow(topHeight: 16, bottomHeight: 16, hiddenSeparators: [.top], backgroundColor: .BasePurple))
		rows.append(emptyRow(topHeight: 5, bottomHeight: 5, hiddenSeparators: [.top, .bottom], backgroundColor: .BasePurple))
        rows.append(row)

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func titleRow(
        text: String,
        font: UIFont,
        hiddenSeparators: [SimpleSeparatorsCell.SeparatorLocation]
    ) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

			cell.messageTopConstraint.constant = 0
			cell.messageBottomConstraint.constant = 0
            cell.content.textColor = .BaseWhite
            cell.content.font = font
            cell.content.text = text
            cell.content.textAlignment = .center
            cell.hiddenSeparatorLocations = hiddenSeparators
            cell.backgroundColor = .BasePurple

            return cell
        })
    }

    // View
    private func submitSection() -> FormekaModelSection {
        let row = FormekaModelRow(
            tag: ResetPasswordRow.submitButton.rawValue,
            cellSetup: { [unowned self] indexPath, _, table in
            guard let cell: FormekaSubmitButtonCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.button.setTitle(
                PILocalizedString("resetPasswordSubmitButtonTitle", comment: "Reset password: submit button title"),
                for: .normal
            )
            cell.button.backgroundColor = .BaseWhite
            cell.button.setTitleColor(.BasePurple, for: .normal)
            cell.button.titleLabel?.font = UIFont.Button1()
            cell.button.dtxCustomControlName("DigitalKey: Send Email")
            cell.contentView.backgroundColor = .BasePurple
            cell.delegate = self
            cell.selectionStyle = .none
            cell.hiddenSeparatorLocations = [.top, .bottom]

            return cell
        }
        )

        return FormekaModelSection(
            header: FormekaModelHeaderFooter(height: 8, viewSetup: { _, _ in
                UITableViewHeaderFooterView(frame: CGRect(x: 0, y: 0, width: 100, height: 8))
            }),
            rows: [row],
            footer: nil
        )
    }
}
