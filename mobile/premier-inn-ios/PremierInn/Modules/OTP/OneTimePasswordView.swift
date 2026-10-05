//
//  OneTimePasswordView.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 08/04/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation
import Formeka
import PassKit

class OneTimePasswordView: FormekaViewController {
    enum OTPRow: String {
        case banner
        case otpText
        case otpCode
        case submitButton
        case resendButton
    }

    var presenter: OneTimePasswordPresenterInput?
    var timer = Timer()

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
        self.view.backgroundColor = .BasePurple
        presenter?.viewIsReady()
    }
}

extension OneTimePasswordView: OneTimePasswordViewInput {
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
            guard let code = viewModel.values[OTPRow.otpCode.rawValue] as? String else { return }

            presenter?.codeSubmitted(code: code)
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
        guard let submitButtonRowIndex = viewModel?.indexPath(forRowNamed: OTPRow.submitButton.rawValue) else { return }

        guard let submitButtonCell = table.cellForRow(at: submitButtonRowIndex) as? FormekaSubmitButtonCell else { return }

        submitButtonCell.activityIndicator.stopAnimating()
        submitButtonCell.button.isEnabled = true
        submitButtonCell.button.setTitle(
            PILocalizedString("otpContinueButtonTitle", comment: "Reset password: submit button title"),
            for: .normal
        )
    }

    func resendButtonCountdown() {
        guard let submitButtonRowIndex = viewModel?.indexPath(forRowNamed: OTPRow.resendButton.rawValue) else { return }

        guard let submitButtonCell = table.cellForRow(at: submitButtonRowIndex) as? FormekaSubmitButtonCell else { return }

        submitButtonCell.button.isUserInteractionEnabled = false
        submitButtonCell.button.setTitle(PILocalizedString("otpPasscodeSentButtonTitle", comment: ""), for: .normal)


        timer = Timer.scheduledTimer(
            timeInterval: 5,
            target: self,
            selector: #selector(resendButtonTimerEnd),
            userInfo: nil,
            repeats: false
        )
    }

    func showAlert(with title: String, error: Error?) {
        showErrorAlertWith(title: title, error: error)
    }

    @objc func resendButtonTimerEnd() {
        guard let countdownIndex = viewModel?.indexPath(forRowNamed: OTPRow.resendButton.rawValue) else { return }

        guard let submitButtonCell = table.cellForRow(at: countdownIndex) as? FormekaSubmitButtonCell else { return }

        submitButtonCell.button.isUserInteractionEnabled = true
        submitButtonCell.button.setTitle(PILocalizedString("otpResendButtonTitle", comment: ""), for: .normal)
        submitButtonCell.button.setTitleColor(.BaseWhite, for: .normal)

        timer.invalidate()
    }

    func showAlert(withTitle title: String, message: String) {
        let controller = UIAlertController(title: title, message: message, preferredStyle: .alert)
        controller.addAction(UIAlertAction(title: "OK", style: .cancel, handler: { [unowned self] (_) in
            // Do something after error shown if needed
        }))

        present(controller, animated: true)
    }
}

extension OneTimePasswordView: FormekaSubmitButtonCellDelegate {
    func submitButtonDidTap(cell: FormekaSubmitButtonCell) {
        guard let indexPath = table.indexPath(for: cell) else { return }
        guard let row = viewModel?.row(at: indexPath) else { return }

        switch row.tag {
        case OTPRow.submitButton.rawValue:
        cell.button.setTitle(nil, for: .normal)
        cell.button.isEnabled = false
        cell.activityIndicator.color = .BasePurple
        cell.activityIndicator.startAnimating()
        presenter?.submitButtonTapped()

        case OTPRow.resendButton.rawValue:
        presenter?.resendButtonTapped()

        default:
        return
        }
    }
}
