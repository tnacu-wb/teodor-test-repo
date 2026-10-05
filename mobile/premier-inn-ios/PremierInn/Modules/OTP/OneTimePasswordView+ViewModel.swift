//
//  OneTimePasswordView+ViewModel.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 08/04/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Formeka
import UIKit

extension OneTimePasswordView {
    func loadViewModel() {
        viewModel = FormekaViewModel(sections: viewModelSections())
        viewModel?.delegate = self

        table.delegate = viewModel
        table.dataSource = viewModel

        table.backgroundColor = .BasePurple
        table.reloadData()
    }

    func registerTableElements() {
		table.registerCellNib(with: FlexibleTextContentCell.self)
        table.registerCellNib(with: FormekaFreeTextCell.self)
        table.registerCellNib(with: FormekaSubmitButtonCell.self)
        table.registerCellNib(with: FormekaTextFieldCell.self)
        table.registerCellNib(with: FormekaErrorBannerCell.self)
        table.registerHeaderFooterNib(with: FormekaHeader.self)
        table.registerHeaderFooterNib(with: FormekaIconFooter.self)
    }

    private func viewModelSections() -> [FormekaModelSection] {
        var sections: [FormekaModelSection] = []

        sections.append(OTPTextCellTitle())
        sections.append(OTPTextCell())
        sections.append(passwordsSection())
        sections.append(submitSection())
        sections.append(resendCodeSection())

        return sections
    }

    private func errorSection(error: String) -> FormekaModelSection {
        var rows = [FormekaModelRow]()
        let errorRow = FormekaModelRow(
            tag: OTPRow.banner.rawValue,
            cellSetup: { indexPath, _, table in
                guard let cell: FormekaErrorBannerCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.backgroundColor = .BaseGrey
                cell.message.text = error
                cell.message.textColor = .Tint8
                cell.message.font = .Body()

                return cell
            }
        )
        rows.append(errorRow)
        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    func updateTableError(error: String?) {
        guard let error else {
            if let existingIndexPath = viewModel?.remove(rowNamed: OTPRow.banner.rawValue) {
                table.deleteRows(at: [existingIndexPath], with: .automatic)
            }
            return
        }

        if let existingIndexPath = viewModel?.remove(rowNamed: OTPRow.banner.rawValue) {
            table.deleteRows(at: [existingIndexPath], with: .automatic)
        }

        guard let indexPath = viewModel?.indexPath(forRowNamed: OTPRow.otpText.rawValue) else { return }
        let newIndexPath = IndexPath(row: indexPath.row - 1, section: indexPath.section)

        viewModel?.add(section: errorSection(error: error), index: newIndexPath.row)
        table.insertSections([newIndexPath.row], with: .automatic)
        stopLoadingAnimation()
    }

    private func OTPTextCellTitle() -> FormekaModelSection {
        var rows = [FormekaModelRow]()
        let row = FormekaModelRow(tag: OTPRow.otpText.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: FormekaFreeTextCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.backgroundColor = .BasePurple
            cell.message.text = PILocalizedString("otpEmailTitle", comment: "")
            cell.message.font = .Heading1_ExtraBold()
            cell.message.textColor = .BaseWhite

            cell.hiddenSeparatorLocations = [.bottom, .top]
            cell.topConstraint.constant = 0
            cell.bottomConstraint.constant = 0
            cell.message.textAlignment = .center
            return cell
        })
		rows.append(emptyRow(
		    topHeight: 16,
		    bottomHeight: 16,
		    hiddenSeparators: [.top, .bottom],
		    backgroundColor: .BasePurple
		))
        rows.append(row)
        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func OTPTextCell() -> FormekaModelSection {
        var rows = [FormekaModelRow]()
        let row = FormekaModelRow(tag: OTPRow.otpText.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: FormekaFreeTextCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.backgroundColor = .BasePurple
            cell.message.text = PILocalizedString("otpEmailMessage", comment: "")
            cell.message.font = .Body_Medium()
            cell.message.textColor = .BaseWhite

            cell.hiddenSeparatorLocations = [.bottom, .top]
            cell.topConstraint.constant = 0
            cell.bottomConstraint.constant = 0
            cell.message.textAlignment = .center
            return cell
        })
		rows.append(emptyRow(topHeight: 6, bottomHeight: 6, hiddenSeparators: [.top, .bottom], backgroundColor: .BasePurple))
        rows.append(row)
		rows.append(emptyRow(topHeight: 6, bottomHeight: 6, hiddenSeparators: [.top], backgroundColor: .BasePurple))
        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func passwordsSection() -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        var codeTraits = FormekaTextFieldTraits()
        codeTraits.keyboardType = .numberPad
        codeTraits.textContentType = .oneTimeCode

		rows.append(emptyRow(topHeight: 5, bottomHeight: 5, hiddenSeparators: [.top, .bottom], backgroundColor: .BasePurple))
		rows.append(textFieldRow(
		    name: OTPRow.otpCode.rawValue,
		    title: PILocalizedString("otpFieldLabel", comment: ""),
		    value: nil,
		    traits: codeTraits,
		    inlineValidators: [.numeric],
		    onBlurValidators: [.required],
		    onChange: { self.updateTableError(error: nil) },
		    backgroundColour: .BasePurple,
		    textColour: .BaseWhite,
		    textFieldColour: .BaseWhite,
		    errorBackgroundColor: .BaseGrey
		))


        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func submitSection() -> FormekaModelSection {
		var rows = [FormekaModelRow]()

        let row = FormekaModelRow(tag: OTPRow.submitButton.rawValue, cellSetup: { [unowned self] indexPath, _, table in
            guard let cell: FormekaSubmitButtonCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.button.setTitle(PILocalizedString("otpContinueButtonTitle", comment: ""), for: .normal)
            cell.button.backgroundColor = .BaseWhite
            cell.button.setTitleColor(.BasePurple, for: .normal)
            cell.button.titleLabel?.font = UIFont.Button1()
            cell.button.dtxCustomControlName("DigitalKey: Continue")
            cell.contentView.backgroundColor = .BasePurple
            cell.delegate = self
            cell.selectionStyle = .none
            cell.hiddenSeparatorLocations = [.top, .bottom]
			cell.bottomConstraint.constant = 0

            return cell
        })

		rows.append(emptyRow(topHeight: 5, bottomHeight: 5, hiddenSeparators: [.top, .bottom], backgroundColor: .BasePurple))
		rows.append(row)

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func resendCodeSection() -> FormekaModelSection {
        let row = FormekaModelRow(tag: OTPRow.resendButton.rawValue, cellSetup: { [unowned self] indexPath, _, table in
            guard let cell: FormekaSubmitButtonCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.button.setTitle(PILocalizedString("otpResendButtonTitle", comment: ""), for: .normal)
            cell.button.backgroundColor = .BasePurple
            cell.button.setTitleColor(.BaseWhite, for: .normal)
            cell.button.titleLabel?.font = UIFont.BodySmall()
            cell.button.dtxCustomControlName("DigitalKey: Resend Code")
            cell.button.layer.borderWidth = 1
            cell.button.layer.borderColor = UIColor.BasePurple.cgColor
            cell.contentView.backgroundColor = .BasePurple
            cell.delegate = self
            cell.selectionStyle = .none
            cell.hiddenSeparatorLocations = [.top, .bottom]
			cell.topConstraint.constant = 0
			cell.bottomConstraint.constant = 0
            return cell
        })

        return FormekaModelSection(header: nil, rows: [row], footer: nil)
    }
}
