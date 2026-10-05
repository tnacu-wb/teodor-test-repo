//
//  OneTimePasswordCaptureView.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 01/09/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//
import Foundation
import Formeka
import UIKit

protocol OTPEmailCaptureViewInput {
    var tabBarIsHidden: Bool { get }
    var parentNavigationController: UINavigationController? { get }
    var customAnalyticsParameters: PIDictionary? { get set }

    func updateScreenTitle(with screenTitle: String)
    func registerRows()
	func loadModelForView(defaultEmail: String?)
    func validateForm()
    func scrollToAndFocusRow(at indexPath: IndexPath)
    func makeRowFirstResponder(at indexPath: IndexPath)
    func showAlert(with title: String, error: Error?)
    func showAlert(withTitle title: String, message: String)
    func stopLoadingAnimation()
}

class OTPEmailCaptureView: FormekaViewController {
    enum ResetPasswordRow: String {
        case banner
        case email
        case submitButton
    }

    var presenter: OTPEmailCapturePresenterInput?

    override var screenName: String {
        presenter?.screenName ?? ""
    }

    override var screenType: String {
        presenter?.screenType ?? ""
    }

    override var customParameters: [String: Any]? { customAnalyticsParameters }

    var customAnalyticsParameters: PIDictionary?

    init() {
        super.init(nibName: String(describing: FormekaViewController.self), bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        if table != nil {
            table.backgroundColor = .BasePurple
            table.separatorColor = .BaseWhite
        }

        presenter?.viewIsReady()
    }
}

extension OTPEmailCaptureView: OTPEmailCaptureViewInput {
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

	func loadModelForView(defaultEmail: String?) {
        loadViewModel(defaultEmail: defaultEmail)
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
        }))

        present(controller, animated: true)
    }
}

extension OTPEmailCaptureView: FormekaSubmitButtonCellDelegate {
    func submitButtonDidTap(cell: FormekaSubmitButtonCell) {
        cell.button.setTitle(nil, for: .normal)
        cell.button.isEnabled = false
        cell.activityIndicator.color = .BasePurple
        cell.activityIndicator.startAnimating()

        presenter?.submitButtonTapped()
    }
}
