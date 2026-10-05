//
//  ChangePasswordView.swift
//  PremierInn
//
//  Created by Freddie Parks on 14/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import Formeka

protocol ChangePasswordViewProtocol: AnyObject {
    func loadViewModel()
    func setTitle(title: String)
    func showError(string: String)
    func invalidFormatPassword()
    func passwordChangeFailed(message: String)
    func errorOccuredWhenChangingPassword(error: Error)
}

class ChangePasswordView: FormekaViewController {
    var presenter: ChangePasswordPresenterProtocol?
    var activityIndicator: UIActivityIndicatorView?

    override var screenName: String {
        presenter?.changePasswordTracking.screenName ?? super.screenName
    }
    override var screenType: String {
        presenter?.changePasswordTracking.screenType ?? super.screenType
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        if table != nil {
            table.backgroundColor = .BaseWhite
            table.separatorColor = .ColourLD3

            registerTableElements()
        }

        activityIndicator = UIActivityIndicatorView(style: .medium)
        activityIndicator?.hidesWhenStopped = true
        activityIndicator?.center = table.center

        if let activityIndicator = activityIndicator {
            view.addSubview(activityIndicator)
        }

        presenter?.viewIsReady()
    }

    private func registerTableElements() {
        table.registerCellNib(with: FormekaTextFieldCell.self)
        table.registerCellNib(with: FormekaTextFieldPasswordCell.self)
        table.registerCellNib(with: TwoButtonsCell.self)
        table.registerCellNib(with: FormekaSubmitButtonCell.self)
        table.registerCellNib(with: ErrorCell.self)
    }

    private func showAlert(withMessage message: String, completion: @escaping () -> Void) {
        let alertController = UIAlertController(title: nil, message: message, preferredStyle: .alert)
        alertController.addAction(UIAlertAction(
            title: PILocalizedString("changePasswordCompleteAlertButtonTitle", comment: ""),
            style: .cancel,
            handler: { _ in
            completion()
        }
        ))

        present(alertController, animated: true)
    }

    private func updateViewBusy(busy: Bool) {
        guard let activityIndicator = activityIndicator else { return }
        activityIndicator.center = table.center
        activityIndicator.move(to: .front)

        if busy {
            activityIndicator.startAnimating()
        } else {
            activityIndicator.stopAnimating()
        }
        view.isUserInteractionEnabled = !busy
    }
}

extension ChangePasswordView: ChangePasswordViewProtocol {
    func loadViewModel() {
        viewModel = FormekaViewModel(sections: viewModelSections())
        viewModel?.delegate = self

        table?.delegate = viewModel
        table?.dataSource = viewModel
    }

    func setTitle(title: String) {
        navigationItem.title = title
    }

    func showError(string: String) {
        updateViewBusy(busy: false)
        updateTableError(error: string)
    }

    func invalidFormatPassword() {
        updateViewBusy(busy: false)
        highlightInformationRow()
    }

    func passwordChangeFailed(message: String) {
        updateViewBusy(busy: false)
        showAlert(withMessage: message) {}
    }

    func errorOccuredWhenChangingPassword(error: Error) {
        updateViewBusy(busy: false)
        updateTableError(error: error.localizedDescription)

        BARTDowntimeHandler.handle(error: error, withParentNavigationController: self.navigationController)
    }
    func forgotPasswordButtonDidTap(email: String?) {
        presenter?.forgotPasswordDidTap(email: email)
    }
}

extension ChangePasswordView: TwoButtonsCellDelegate {
    func bottomButtonDidTap(cell: TwoButtonsCell) {
        forgotPasswordButtonDidTap(email: nil)
    }

    func topButtonDidTap(cell: TwoButtonsCell) {
        do {
        try viewModel?.row(named: ChangePasswordRow.new.rawValue)?.validate()
        guard let existingPassword = viewModel?.row(named: ChangePasswordRow.existing.rawValue)?.value as? String
            else { return }
        guard let newPassword = viewModel?.row(named: ChangePasswordRow.new.rawValue)?.value as? String else { return }
        guard let newPasswordConfirm = viewModel?.row(named: ChangePasswordRow.newConfirm.rawValue)?.value as? String
            else { return }

        updateViewBusy(busy: true)
        presenter?.changePasswordButtonDidTap(
            existingPassword: existingPassword,
            newPassword: newPassword,
            newPasswordConfirm: newPasswordConfirm
        )
        } catch {
            return
        }
    }
}
