//
//  DeleteAccountView+ViewModel.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 28/04/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Formeka
import UIKit

extension DeleteAccountView {
    func loadViewModel() {
        viewModel = FormekaViewModel(sections: viewModelSections())
        viewModel?.delegate = self

        table.delegate = viewModel
        table.dataSource = viewModel
        table.backgroundColor = .BaseWhite

        table.reloadData()
    }

    func registerTableElements() {
        table.registerCellNib(with: TwoButtonsCell.self)
        table.registerCellNib(with: FormekaFreeTextCell.self)
        table.registerCellNib(with: FormekaErrorBannerCell.self)
        table.registerCellNib(with: FormekaTextFieldPasswordCell.self)
        table.registerCellNib(with: TermsAndConditionsCell.self)
        table.registerHeaderFooterNib(with: FormekaHeader.self)
        table.registerHeaderFooterNib(with: FormekaIconFooter.self)
        table.registerCellNib(with: SwitchCell.self)
    }

    private func viewModelSections() -> [FormekaModelSection] {
        var sections: [FormekaModelSection] = []

        sections.append(deleteAccountTextCell())
        sections.append(passwordsSection())
        sections.append(toggleSection())
        sections.append(submitSection())


        return sections
    }

    private func deleteAccountTextCell() -> FormekaModelSection {
        var rows = [FormekaModelRow]()
        let row = FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: FormekaFreeTextCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.message.text = PILocalizedString("deleteAccountDescriptionLabel")
            cell.message.font = .Body_Medium()
            cell.hiddenSeparatorLocations = [.bottom, .top]
            cell.topConstraint.constant = 0
            cell.bottomConstraint.constant = 10
            cell.message.textAlignment = .left
            return cell
        })
        rows.append(row)
        return FormekaModelSection(header: spacerHeaderFooter(height: 30), rows: rows, footer: nil)
    }

    private func toggleSection() -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        let row = FormekaModelRow(
            tag: RegisterRow.termsAndConditions.rawValue,
            onBlurValidators: [DeleteAccountToggleValidator()],
            cellSetup: { indexPath, row, table in
            guard let cell: TermsAndConditionsCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.toggled = { value in
                row.value = value
            }

            let attributedString = NSMutableAttributedString(
                string: PILocalizedString("deleteAccountConfirmationLabel"),
                attributes: [NSAttributedString.Key.font: UIFont.Body_Medium()]
            )
            cell.message.attributedText = attributedString
            cell.message.isUserInteractionEnabled = true

            cell.toggleSwitch.onTintColor = .Tint1
            cell.toggleSwitch.isOn = row.value as? Bool ?? false

            cell.errorLabel?.textColor = .Tint8
            cell.errorLabel?.font = UIFont.BodySmall()
            cell.errorMessage = row.error?.localizedDescription

            cell.contentView.backgroundColor = .ColourLD1

            return cell
        }
        )
        rows.append(row)

        return FormekaModelSection(header: nil, rows: rows, footer: spacerHeaderFooter(height: 13))
    }

    private func passwordsSection() -> FormekaModelSection {
        var rows = [FormekaModelRow]()
        rows.append(plainPasswordRow(
            tag: DeleteAccountRow.password.rawValue,
            title: PILocalizedString("currentPasswordLabel")
        ))

        return FormekaModelSection(header: spacerHeaderFooter(height: 30), rows: rows, footer: nil)
    }

    private func submitSection() -> FormekaModelSection {
        let row = FormekaModelRow(
            tag: DeleteAccountRow.submitButton.rawValue,
            cellSetup: { [weak self] indexPath, _, table in
                guard let cell: TwoButtonsCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.topButton.setTitle(PILocalizedString("deleteAccountDeleteButton"), for: .normal)
                cell.topButton.backgroundColor = .Tint1
                cell.topButton.setTitleColor(.BaseWhite, for: .normal)
                cell.topButton.titleLabel?.font = UIFont.Button1()

                cell.bottomButton.setTitle(PILocalizedString("loginForgottenTitle"), for: .normal)

                cell.contentView.backgroundColor = .BaseWhite
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
