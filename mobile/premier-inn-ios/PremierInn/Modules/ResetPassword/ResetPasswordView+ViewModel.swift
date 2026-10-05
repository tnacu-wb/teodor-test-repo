//
//  ResetPasswordView+ViewModel.swift
//  PremierInn
//
//  Created by Nick Jones on 08/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import UIKit

extension ResetPasswordView {
    func loadViewModel() {
        viewModel = FormekaViewModel(sections: viewModelSections())
        viewModel?.delegate = self

        table.delegate = viewModel
        table.dataSource = viewModel
        table.backgroundColor = .white

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

    private func viewModelSections() -> [FormekaModelSection] {
        var sections: [FormekaModelSection] = []

        if presenter?.shouldShowError ?? false {
            sections.append(errorSection())
        }
        sections.append(emailSection())
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

    private func emailSection() -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        let row = FormekaModelRow(
            tag: ResetPasswordRow.email.rawValue,
            onBlurValidators: [.required, .email],
            cellSetup: { [unowned self] indexPath, row, table in
                guard let cell: FormekaTextFieldCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.delegate = self

                cell.titleLabel.text = PILocalizedString("resetPasswordEmailLabel", comment: "Reset password: email label")
                cell.titleLabel.textColor = .ColourDL3
                cell.titleLabel.font = .BodySmall()

                cell.textField.text = row.value?.displayName
                cell.textField.keyboardType = .emailAddress
                cell.textField.textColor = .TintD1
                cell.textField.font = .Body()
                cell.textField.accessibilityIdentifier = AccessibilityIdentifiers.ResetPassword.emailTextField

                cell.errorLabel?.textColor = .Tint8
                cell.errorLabel?.font = .BodySmall()

                cell.accessibilityIdentifier = AccessibilityIdentifiers.ResetPassword.emailCell
                cell.errorMessage = row.error?.localizedDescription

                cell.textField.autocorrectionType = .yes
                cell.textField.textContentType = .emailAddress

                return cell
            }, didSelect: { [unowned self] indexPath, _ in
                presenter?.userTappedEmailAddressRow(at: indexPath)
            }
        )

        row.value = presenter?.emailAddress
        rows.append(titleRow(text: PILocalizedString("resetPasswordSectionTitle")))
        rows.append(row)

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func titleRow(text: String) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.content.textColor = .TintD1
            cell.content.font = .Body_Medium()
            cell.content.text = text
            cell.hiddenSeparatorLocations = [.top, .bottom]

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
            cell.button.backgroundColor = .Tint1
            cell.button.setTitleColor(.BaseWhite, for: .normal)
            cell.button.titleLabel?.font = UIFont.Button1()

            cell.contentView.backgroundColor = .white
            cell.delegate = self
            cell.selectionStyle = .none
            cell.hiddenSeparatorLocations = [.top, .bottom]

            return cell
        }
        )

        return FormekaModelSection(
            header: FormekaModelHeaderFooter(height: 24, viewSetup: { _, _ in
                UITableViewHeaderFooterView(frame: CGRect(x: 0, y: 0, width: 100, height: 24))
            }),
            rows: [row],
            footer: nil
        )
    }
}
