//
//  LoginView.swift
//  PremierInn
//
//  Created by Marcello Mascia on 04/09/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//
import Formeka
import SimpleNetwork
import UIKit

enum LoginRow: String {
    case banner
    case accountTypeSwitch
    case email
    case password
    case submit
    case biometricAuth
    case accountAndInfo
    case businessBookerInfo
}

protocol LoginViewInput: AnyObject {
    var parentNavigationController: UINavigationController? { get }

    func setup(withTitle: String)
    func reloadViewModel()
	func stopEditing()
	func focusFormRow(at: IndexPath)
	func presentAlertWith(title: String, error: Error?)
	func lock()
	func unlock()
	func askForBiometricActivation(completion: @escaping (BiometricAuthenticationStatus) -> Void)
	func completeSession()
    func updateSubmitButton(isLoading: Bool)
	func biometricAuthenticationDidFinish(credential: AuthCredentials)
	func submitButtonDidTap()
    func forgotPasswordButtonDidTap(email: String?)
    func update(with email: String)
    func promptUserAboutLoggingInAndLosingBookingProgress()
}

class LoginViewController: FormekaViewController {
    var presenter: LoginPresenterInput?
    var onDismissOnboarding: (() -> Void)?

    override var screenName: String {
        (presentingViewController as? UITabBarController)?.tabBar.isHidden ?? false ? PIAnalytics.StateNames
        	.login : PIAnalytics.StateNames.myPiLogin
    }
    override var screenType: String {
        (presentingViewController as? UITabBarController)?.tabBar.isHidden ?? false ? PIAnalytics.StateTypes
        	.bookingFlow : PIAnalytics.StateTypes.myPI
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        registerTableElements()

        presenter?.viewIsReady()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.setNavigationBarHidden(false, animated: animated)
    }

    private func registerTableElements() {
        if table != nil {
            table.registerCellNib(with: GDPRBannerHeaderRow.self)
            table.registerCellNib(with: FormekaTextFieldCell.self)
            table.registerCellNib(with: FormekaTextFieldPasswordCell.self)
            table.registerCellNib(with: TwoButtonsCell.self)
            table.registerCellNib(with: AccountTypeSwitchCell.self)
            table.registerCellNib(with: FormekaErrorBannerCell.self)
            table.registerCellNib(with: BiometricButtonCell.self)
            table.registerCellNib(with: AboutBusinessBookerButtonCell.self)
            table.registerCellNib(with: LoginTypeCell.self)
            table.registerCellNib(with: SimpleImageCell.self)
            table.registerHeaderFooterNib(with: FormekaHeader.self)
            table.registerHeaderFooterNib(with: FormekaIconFooter.self)
        }
    }
    @objc func cancelButtonDidTap() {
        presenter?.dismissLoginDidTap()
    }

	func biometricButtonDidTap() {
		presenter?.authenticateWithBiometric()
	}

	static func passwordVisibilityButton(withText text: String, textfield: UITextField) -> PasswordVisibilityButton {
        let button = PasswordVisibilityButton(frame: CGRect(x: 0, y: -350, width: 50, height: 20))
        button.titleColor = .BasePurple
        button.titleFont = UIFont.BodySmall()
        button.setTitle(text, for: .normal)
        button.textfield = textfield
        button.accessibilityIdentifier = "passwordVisibilityButtonAcc"

        return button
    }
}

extension LoginViewController: LoginViewInput {
    var parentNavigationController: UINavigationController? {
        navigationController
    }

    func setup(withTitle title: String) {
        self.title = title

        table.backgroundColor = .white
        table.separatorColor = .ColourLD3

        navigationItem.leftBarButtonItem = UIBarButtonItem(
        	barButtonSystemItem: .cancel,
        	target: self,
        	action: #selector(cancelButtonDidTap)
        )
        navigationItem.leftBarButtonItem?.accessibilityIdentifier = "cancelButton"
        navigationItem.leftBarButtonItem?.tintColor = .white
    }

	func stopEditing() {
		view.endEditing(true)
	}

	func focusFormRow(at indexPath: IndexPath) {
		scrollAndFocus(at: indexPath)
	}

	func presentAlertWith(title: String, error: Error?) {
		showErrorAlertWith(title: title, error: error)
	}

	func lock() {
		isLocked = true
	}

	func unlock() {
		isLocked = false
	}

    func promptUserAboutLoggingInAndLosingBookingProgress() {
        let alert = UIAlertController(
        	title: PILocalizedString("loginScreenBBPopupTitle"),
        	message: PILocalizedString("loginScreenBBPopupMessage"),
        	preferredStyle: .alert
        )

        alert.addAction(UIAlertAction(title: PILocalizedString("loginScreenBBPopupConfirm"), style: .default, handler: nil))

        present(alert, animated: true)
    }

    func askForBiometricActivation(completion: @escaping (BiometricAuthenticationStatus) -> Void) {
		let alert = UIAlertController(
			title: BiometricAuthenticationManager.biometryTypeAvailable.enableTitle,
			message: BiometricAuthenticationManager.biometryTypeAvailable.enableMessage,
			preferredStyle: .alert
		)

		alert
			.addAction(UIAlertAction(title: BiometricAuthenticationManager.biometryTypeAvailable.enableAction, style: .default) { _ in
			BiometricAuthenticationManager.status = .enabled
            self.dismiss(animated: true) {
                completion(.enabled)
            }
		})

		alert
			.addAction(UIAlertAction(title: BiometricAuthenticationManager.biometryTypeAvailable.cancelAction, style: .cancel) { _ in
			BiometricAuthenticationManager.status = .disabled
            self.dismiss(animated: true) {
                completion(.disabled)
            }
		})

		present(alert, animated: true)
	}

	func completeSession() {
        onDismissOnboarding?()
		dismiss(animated: true)
	}

    func updateSubmitButton(isLoading: Bool) {
		guard let cell: TwoButtonsCell = viewModel?.cell(forRowNamed: LoginRow.submit.rawValue, table: table) else { return }

        let title = isLoading ? nil : PILocalizedString("loginButtonTitle", comment: "Login screen: login button title")

        cell.topButton.setTitle(title, for: .normal)
        cell.topButton.isEnabled = !isLoading

        if isLoading {
            cell.activityIndicator.startAnimating()
        } else {
            cell.activityIndicator.stopAnimating()
        }
    }

    func reloadViewModel() {
        viewModel = FormekaViewModel(sections: viewModelSections)
        viewModel?.delegate = self

        if table != nil {
            table.delegate = viewModel
            table.dataSource = viewModel

            table.reloadData()
        }
    }

	func submitButtonDidTap() {
		presenter?.submitForm(viewModel: viewModel)
	}

	func biometricAuthenticationDidFinish(credential: AuthCredentials) {
		func fillTextField(withName name: String, value: String?) {
			guard let row = viewModel?.row(named: name) else { return }

			row.value = value

			guard let indexPath = viewModel?.indexPath(for: row) else { return }
			guard let cell = table.cellForRow(at: indexPath) as? FormekaTextFieldCell else { return }

			cell.textField.text = value
		}

		fillTextField(withName: LoginRow.email.rawValue, value: credential.username)
		fillTextField(withName: LoginRow.password.rawValue, value: credential.password)
	}

    func forgotPasswordButtonDidTap(email: String?) {
        presenter?.forgotPasswordDidTap(email: email)
    }

    func update(with email: String) {
        viewModel?.row(named: LoginRow.email.rawValue)?.value = email
        table.reloadData()
    }
}
