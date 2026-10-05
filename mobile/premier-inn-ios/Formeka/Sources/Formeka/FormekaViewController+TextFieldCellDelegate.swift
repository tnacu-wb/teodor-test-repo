//
//  FormekaViewController+TextFieldCellDelegate.swift
//  FormekaExample
//
//  Created by Marcello Mascia on 14/04/2018.
//  Copyright © 2018 Marcello Mascia. All rights reserved.
//

import UIKit

extension FormekaViewController: FormekaTextFieldCellDelegate {

	public func textFieldDidBeginEditing(cell: FormekaTextFieldCell) {

		guard let indexPath = table.indexPath(for: cell) else { return }

		cell.textField.inputAccessoryView = inputToolbar(withIndexPathToUse: indexPath)
        cell.valueChangedWithState?(ValidationError.passwordEmpty(""))
	}

	@objc open func textFieldDidUpdateContent(value: String, cell: FormekaTextFieldCell) {

		guard let indexPath = table.indexPath(for: cell) else { return }
		guard let row = viewModel?.row(at: indexPath) else { return }

		row.value = value
        
        do {
            try row.validateInline()

            if let passwordValidator = row.inlineValidators.first as? PasswordValidator, value.isEmpty == false {
                cell.valueChangedWithState?(ValidationError.passwordSuccess(passwordValidator.customSuccessValue))
            }
        } catch let error as RowValidatorError {
            let returnValue = error.error as! ValidationError
            cell.valueChangedWithState?(returnValue)
        } catch {}
        
        cell.errorMessage = row.error?.localizedDescription
        table.beginUpdates()
        table.endUpdates()

        cell.valueChanged?()
	}

    public func textFieldDidEndEditing(value: String, cell: FormekaTextFieldCell) {

		guard let indexPath = table.indexPath(for: cell) else { return }
		guard let row = viewModel?.row(at: indexPath) else { return }

        row.value = value

        do {
            try row.validateOnBlur()
        } catch let error as RowValidatorError {
            let returnValue = error.error as! ValidationError
            cell.valueEndChangeWithState?(returnValue)
        } catch {}
        
        cell.errorMessage = row.error?.localizedDescription

        DispatchQueue.main.async {
            
            self.table.beginUpdates()
            self.table.endUpdates()
        }
	}
}

extension FormekaViewController {

	private func textFieldCells(forSection section: Int) -> [FormekaTextFieldCell] {

		guard let viewModel = viewModel else { return [] }
		guard section < viewModel.sections.count else { return [] }

		return viewModel.sections[section].rows.compactMap { (modelRow) -> FormekaTextFieldCell? in

			guard let indexPath = viewModel.indexPath(for: modelRow) else { return nil }

			return table.cellForRow(at: indexPath) as? FormekaTextFieldCell
		}
	}

	private func isLastTextField(forIndex index: IndexPath) -> Bool {

		return table.cellForRow(at: index) == textFieldCells(forSection: index.section).last
	}

	private func isFirstTextField(forIndex index: IndexPath) -> Bool {

		return table.cellForRow(at: index) == textFieldCells(forSection: index.section).first
	}

	private func inputToolbar(withIndexPathToUse indexPath: IndexPath) -> UIToolbar {

		let toolbar = UIToolbar()
		toolbar.backgroundColor = .white
		toolbar.sizeToFit()

		let flexibleSpaceButton = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)

		let doneButton = UIBarButtonItem(title: NSLocalizedString("Done", comment: ""), style: .plain, target: self, action: #selector(donePressed))
		doneButton.tintColor = FormekaStyleManager.shared.toolbartTint

		toolbar.isUserInteractionEnabled = true

		if textFieldCells(forSection: indexPath.section).count == 1 {
			toolbar.setItems([flexibleSpaceButton, doneButton], animated: false)
			return toolbar
		}

		let fixedSpaceButton = UIBarButtonItem(barButtonSystemItem: .fixedSpace, target: nil, action: nil)

		let nextButton: UIBarButtonItem = {
			let button = UIBarButtonItem(title: NSLocalizedString("Next", comment: ""), style: .plain, target: self, action: #selector(nextPressed))
			button.tintColor = FormekaStyleManager.shared.toolbartTint
			button.isEnabled = !isLastTextField(forIndex: indexPath)

			return button
		}()

		let previousButton: UIBarButtonItem = {
			let button = UIBarButtonItem(title: NSLocalizedString("Previous", comment: ""), style: .plain, target: self, action: #selector(previousPressed))
			button.tintColor = FormekaStyleManager.shared.toolbartTint
			button.isEnabled = !isFirstTextField(forIndex: indexPath)

			return button
		}()

		toolbar.setItems([fixedSpaceButton, previousButton, fixedSpaceButton, nextButton, flexibleSpaceButton, doneButton], animated: false)

		return toolbar
	}

	@objc private func donePressed() {

		view.endEditing(true)
	}

	@objc private func previousPressed() {

		guard let activeCell = table.visibleCells.first(where: { $0.isFirstResponder }) else { return }
		guard let indexPath = table.indexPath(for: activeCell) else { return }
		guard indexPath.row - 1 >= 0 else { return }

		// TODO: Add logic to skip to previous section if we are at the beginning of current one
		table.cellForRow(at: IndexPath(row: indexPath.row - 1, section: indexPath.section))?.becomeFirstResponder()
	}

	@objc private func nextPressed() {

		guard let activeCell = table.visibleCells.first(where: { $0.isFirstResponder }) else { return }
		guard let indexPath = table.indexPath(for: activeCell) else { return }
		guard let viewModel = viewModel else { return }
		guard indexPath.row + 1 < viewModel.sections[indexPath.section].rows.count else { return }

		// TODO: Add logic to skip to next section if we are at the end of current one
		table.cellForRow(at: IndexPath(row: indexPath.row + 1, section: indexPath.section))?.becomeFirstResponder()
	}
}
