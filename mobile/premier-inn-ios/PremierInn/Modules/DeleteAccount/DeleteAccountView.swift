//
//  DeleteAccountView.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 28/04/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation
import Formeka
import UIKit

protocol DeleteAccountViewInput {
    func updateScreenTitle(with screenTitle: String)
    func registerRows()
    func loadModelForView()
    func validateForm()
    func scrollToAndFocusRow(at indexPath: IndexPath)
    func makeRowFirstResponder(at indexPath: IndexPath)
    func showAlert(with title: String, error: Error?)
    func showAlert(withTitle title: String, message: String)
    func stopLoadingAnimation()
}

class DeleteAccountView: FormekaViewController {
    enum DeleteAccountRow: String {
        case text
        case password
        case toggle
        case submitButton
    }

    var presenter: DeleteAccountPresenterInput?

    init() {
        super.init(nibName: String(describing: FormekaViewController.self), bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        if table != nil {
            table.backgroundColor = .BaseGrey
            table.separatorColor = .ColourLD3
        }

        presenter?.viewIsReady()
    }
}

extension DeleteAccountView: DeleteAccountViewInput {
    func updateScreenTitle(with screenTitle: String) {
        title = screenTitle
    }

    func registerRows() {
        registerTableElements()
    }

    func loadModelForView() {
        loadViewModel()
    }

    func validateForm() {
        view.endEditing(true)

        guard let viewModel = viewModel else { return }

        do {
            try viewModel.validate()
                    deleteAccountAlert(
                        withTitle: PILocalizedString("deleteAccountGenericAlertTitle"),
                        message: PILocalizedString("deleteAlertDescription")
                    )
        } catch let error as RowValidatorError {
            presenter?.formSubmissionErrorOccured(at: viewModel.indexPath(for: error.row))
        } catch {
            presenter?.genericFormSubmissionErrorOccured(with: error)
        }
    }

    func scrollToAndFocusRow(at indexPath: IndexPath) {
        scrollAndFocus(at: indexPath)
    }

    func makeRowFirstResponder(at indexPath: IndexPath) {
        table.cellForRow(at: indexPath)?.becomeFirstResponder()
    }

    func stopLoadingAnimation() {
        guard let submitButtonRowIndex = viewModel?.indexPath(forRowNamed: DeleteAccountRow.submitButton.rawValue)
            else { return }

        guard let submitButtonCell = table.cellForRow(at: submitButtonRowIndex) as? TwoButtonsCell else { return }

        submitButtonCell.activityIndicator.stopAnimating()
        submitButtonCell.topButton.isEnabled = true
        submitButtonCell.topButton.setTitle(PILocalizedString("deleteAccountDeleteButton"), for: .normal)
    }

    func showLoadingAnimation() {
        guard let submitButtonRowIndex = viewModel?.indexPath(forRowNamed: DeleteAccountRow.submitButton.rawValue)
            else { return }

        guard let submitButtonCell = table.cellForRow(at: submitButtonRowIndex) as? TwoButtonsCell else { return }

        submitButtonCell.activityIndicator.startAnimating()
        submitButtonCell.topButton.isEnabled = false
        submitButtonCell.topButton.setTitle(nil, for: .normal)
    }

    func showAlert(with title: String, error: Error?) {
        showErrorAlertWith(title: title, error: error)
    }

    func showAlert(withTitle title: String, message: String) {
        let controller = UIAlertController(title: title, message: message, preferredStyle: .alert)
        controller.addAction(UIAlertAction(title: PILocalizedString("OK"), style: .cancel, handler: nil))

        present(controller, animated: true)
    }

    func deleteAccountAlert(withTitle title: String, message: String) {
        let controller = UIAlertController(title: title, message: message, preferredStyle: .alert)
        controller.addAction(UIAlertAction(title: PILocalizedString("deleteGuestAlertCancel"), style: .cancel, handler: nil))
        controller.addAction(UIAlertAction(
            title: PILocalizedString("deleteGuestAlertConfirm"),
            style: .destructive,
            handler: { [unowned self] (_) in
            guard let viewModel = viewModel else { return }

            guard let password = viewModel.values[DeleteAccountRow.password.rawValue] as? String else { return }

            presenter?.deleteAccount(password: password)
            showLoadingAnimation()
        }
        ))

        present(controller, animated: true)
    }
}

extension DeleteAccountView: TwoButtonsCellDelegate {
    func topButtonDidTap(cell: TwoButtonsCell) {
        presenter?.submitButtonTapped()
    }

    func bottomButtonDidTap(cell: TwoButtonsCell) {
        presenter?.forgotPasswordTapped()
    }
}
