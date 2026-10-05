//
//  ChangePasswordView+Formeka.swift
//  PremierInn
//
//  Created by Freddie Parks on 14/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import UIKit

enum ChangePasswordRow: String {
    case existing
    case new
    case newConfirm
    case info
    case error
    case submit
}

extension ChangePasswordView {
    func viewModelSections() -> [FormekaModelSection] {
        var sections = [FormekaModelSection]()

        sections.append(passwordsSection())
        sections.append(submitSection())

        return sections
    }

    func updateTableError(error: String) {
        if let existingIndexPath = viewModel?.remove(rowNamed: ChangePasswordRow.error.rawValue) {
            table.deleteRows(at: [existingIndexPath], with: .automatic)
        }

        guard let indexPath = viewModel?.indexPath(forRowNamed: ChangePasswordRow.new.rawValue) else { return }
        let newIndexPath = IndexPath(row: indexPath.row + 1, section: indexPath.section)

        viewModel?.add(row: errorRow(for: table, error: error), at: newIndexPath)
        table.insertRows(at: [newIndexPath], with: .automatic)
    }

    func highlightInformationRow() {
        guard let infoRow: ErrorCell = viewModel?.cell(forRowNamed: ChangePasswordRow.info.rawValue, table: table)
            else { return }

        infoRow.errorLabel.textColor = .Tint8
        infoRow.errorLabel.shake()

        infoRow.errorImageView.tintColor = .Tint8
    }

    private func passwordsSection() -> FormekaModelSection {
        var rows = [FormekaModelRow]()
        rows.append(plainPasswordRow(
            tag: ChangePasswordRow.existing.rawValue,
            title: PILocalizedString("currentPasswordLabel", comment: "")
        ))
        rows.append(passwordRow(
            tag: ChangePasswordRow.new.rawValue,
            title: PILocalizedString("changePasswordNewLabel", comment: ""),
            regexsDict: SettingsManager.sharedInstance.passwordRegexs
        ))
        rows.append(plainPasswordRow(
            tag: ChangePasswordRow.newConfirm.rawValue,
            title: PILocalizedString("changePasswordNewConfirmLabel", comment: "")
        ))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func submitSection() -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(submitButtonRow(for: table))

        return FormekaModelSection(header: spacerHeaderFooter(height: 10), rows: rows, footer: nil)
    }

    private func passwordInfoRow() -> FormekaModelRow {
        FormekaModelRow(tag: ChangePasswordRow.info.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: ErrorCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.errorLabel.text = PILocalizedString("changePasswordInfoMessage", comment: "")
            cell.errorLabel.font = .BodySmall()
            cell.errorLabel.textColor = .TintD1

            cell.errorImageView.tintColor = .BasePurple

            cell.topConstraint.constant = 6
            cell.leftConstraint.constant = 10
            cell.bottomConstraint.constant = 18
            cell.errorView.layer.borderColor = UIColor.clear.cgColor

            return cell
        })
    }

    private func submitButtonRow(for table: UITableView?) -> FormekaModelRow {
        FormekaModelRow(tag: ChangePasswordRow.submit.rawValue, cellSetup: { [unowned self] indexPath, _, table in
            guard let cell: TwoButtonsCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self
            cell.backgroundColor = .clear
            cell.contentView.backgroundColor = .clear
            cell.topButton.setTitleColor(.white, for: .normal)
            cell.topButton.setTitle(PILocalizedString("changePasswordButtonTitle", comment: ""), for: .normal)
            cell.topButton.backgroundColor = .Tint1
            cell.topButton.titleLabel?.font = .Button1()

            cell.bottomButton.setTitle(
                PILocalizedString("loginForgottenTitle", comment: "Login screen: forgotten password button title"),
                for: .normal
            )
            cell.bottomButton.accessibilityIdentifier = AccessibilityIdentifiers.Login.forgotPasswordButtonAcc

            cell.hiddenSeparatorLocations = [.top, .bottom]

            return cell
        })
    }

    private func errorRow(for table: UITableView?, error: String) -> FormekaModelRow {
        FormekaModelRow(tag: ChangePasswordRow.error.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: ErrorCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.errorLabel.text = error
            cell.errorLabel.font = .BodySmall()
            cell.topConstraint.constant = 6
            cell.leftConstraint.constant = 10
            cell.bottomConstraint.constant = 6
            cell.errorView.layer.borderColor = UIColor.clear.cgColor

            return cell
        })
    }
}
