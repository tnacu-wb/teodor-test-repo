//
//  ResetPasswordView.swift
//  PremierInn
//
//  Created by Nick Jones on 08/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import Formeka
import UIKit

protocol ResetPasswordViewInput {
    var tabBarIsHidden: Bool { get }
    var parentNavigationController: UINavigationController? { get }

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

class ResetPasswordView: FormekaViewController {
    enum ResetPasswordRow: String {
        case banner
        case email
        case submitButton
    }

    var presenter: ResetPasswordPresenterInput?

    override var screenName: String {
        presenter?.screenName ?? ""
    }

    override var screenType: String {
        presenter?.screenType ?? ""
    }

    init() {
        super.init(nibName: String(describing: FormekaViewController.self), bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        if table != nil {
            table.backgroundColor = .BaseWhite
            table.separatorColor = .ColourLD3
        }

        presenter?.viewIsReady()
    }
}

extension ResetPasswordView: ResetPasswordViewInput {
    var parentNavigationController: UINavigationController? {
        navigationController
    }

    var tabBarIsHidden: Bool {
        (presentingViewController as? UITabBarController)?.tabBar.isHidden ?? false
    }

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

            guard let emailAddress = viewModel.values[ResetPasswordRow.email.rawValue] as? String else { return }

            presenter?.userSubmitted(emailAddress: emailAddress)
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
        guard let submitButtonRowIndex = viewModel?.indexPath(forRowNamed: ResetPasswordRow.submitButton.rawValue)
            else { return }

        guard let submitButtonCell = table.cellForRow(at: submitButtonRowIndex) as? FormekaSubmitButtonCell else { return }

        submitButtonCell.activityIndicator.stopAnimating()
        submitButtonCell.button.isEnabled = true
        submitButtonCell.button.setTitle(
            PILocalizedString("resetPasswordSubmitButtonTitle", comment: "Reset password: submit button title"),
            for: .normal
        )
    }

    func showAlert(with title: String, error: Error?) {
        showErrorAlertWith(title: title, error: error)
    }

    func showAlert(withTitle title: String, message: String) {
        let controller = UIAlertController(title: title, message: message, preferredStyle: .alert)

        controller.addAction(UIAlertAction(title: "OK", style: .cancel, handler: { [unowned self] (_) in
            presenter?.userClosedSuccessAlert()
        }))

        present(controller, animated: true)
    }
}

extension ResetPasswordView: FormekaSubmitButtonCellDelegate {
    func submitButtonDidTap(cell: FormekaSubmitButtonCell) {
        cell.button.setTitle(nil, for: .normal)
        cell.button.isEnabled = false
        cell.activityIndicator.startAnimating()

        presenter?.submitButtonTapped()
    }
}
