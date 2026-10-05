//
//  LoginView+ViewModel.swift
//  PremierInn
//
//  Created by Marcello Mascia on 08/09/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//
import Formeka
import UIKit

extension LoginViewController {
	var viewModelSections: [FormekaModelSection] {
		var sections: [FormekaModelSection] = []

        sections.append(loginTypeSection())

        if presenter?.isBusinessLogin == true {
            sections.append(businessLogoSection())
        }

		if presenter?.shouldShowLoginError == true {
			sections.append(errorSection())
		}

		sections.append(formSection())

        return sections
	}

    private func loginTypeSection() -> FormekaModelSection {
        let row = FormekaModelRow(
        	tag: RoomTypeRow.roomTypesSegmentsCell.rawValue,
        	cellSetup: { [weak self] indexPath, _, table in
            guard let cell: LoginTypeCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.backgroundColor = .white

            cell.hiddenSeparatorLocations = [.top, .bottom]
            cell.eventHandler = self
            cell.personalAccountSelected = (self?.presenter?.isBusinessLogin ?? false) == false || self?.presenter?
            	.shouldOverrideDefaultingToBusinessTab ?? false

            return cell
        }
        )

        return FormekaModelSection(header: nil, rows: [row], footer: nil)
    }

    private func businessLogoSection() -> FormekaModelSection {
        let businessLogoRow = FormekaModelRow { indexPath, _, table in
            guard let cell: SimpleImageCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.iconView.image = UIImage(named: "piBusinessLogoPurple")
            cell.bottomConstraint.constant = 0
            cell.hiddenSeparatorLocations = [.bottom]

            return cell
        }
        return FormekaModelSection(header: nil, rows: [businessLogoRow], footer: nil)
    }

	private func formSection() -> FormekaModelSection {
		var rows = [FormekaModelRow]()
        rows.append(
            emailRow(
            	isBusiness: presenter?.isBusinessLogin ?? false,
            	returnTapped: { [unowned self] in
                    let cell: UITableViewCell? = viewModel?.cell(forRowNamed: LoginRow.password.rawValue, table: table)
                    cell?.becomeFirstResponder()

                    return true
                    // coming from splash screen means that the username should not be pre-populated
                },
            	value: (presenter?.hasComeFromSplashSscreen ?? false) == false ? presenter?.userName : nil
            )
        )
        rows.append(plainPasswordRow(returnTapped: { [unowned self] in
            submitButtonDidTap()

            return true
        }))

		rows.append(submitButtonRow())

        if presenter?.isBusinessLogin == true {
            rows.append(businessBookerInfoButtonRow())
        }

		if presenter?.shouldShowBiometricRow == true {
			rows.append(biometricButtonRow())
		}

		return FormekaModelSection(header: header(title: "", height: 40), rows: rows, footer: nil)
	}

	private func errorSection() -> FormekaModelSection {
		var rows: [FormekaModelRow] = []

        rows.append(
            FormekaModelRow(
            	tag: LoginRow.banner.rawValue,
            	cellSetup: { indexPath, _, table in
                    guard let cell: FormekaErrorBannerCell = table.dequeueCell(for: indexPath) else { return nil }

                    cell.backgroundColor = .whiteTwo
                    cell.message.text = PILocalizedString(
                    	"loginUsernamePasswordError",
                    	comment: "Login screen: email or password error message"
                    )
                    cell.message.textColor = .paleRed
                    cell.accessibilityIdentifier = AccessibilityIdentifiers.Login.bannerCellAcc
                    cell.message.font = .Body()

                    return cell
                },
            	didSelect: { [unowned self] _, _ in
                    presenter?.dismissLoginError()
                }
            )
        )

		return FormekaModelSection(header: nil, rows: rows, footer: nil)
	}

	private func submitButtonRow() -> FormekaModelRow {
		FormekaModelRow(tag: LoginRow.submit.rawValue, cellSetup: { [unowned self] indexPath, _, table in
			guard let cell: TwoButtonsCell = table.dequeueCell(for: indexPath) else { return nil }

			cell.delegate = self
			cell.hiddenSeparatorLocations = [.top, .bottom]
            cell.topButton.setTitle(
            	PILocalizedString("loginButtonTitle", comment: "Login screen: login button title"),
            	for: .normal
            )
            cell.topButton.accessibilityIdentifier = AccessibilityIdentifiers.Login.loginButton

            cell.bottomButton.setTitle(
            	PILocalizedString("loginForgottenTitle", comment: "Login screen: forgotten password button title"),
            	for: .normal
            )
            cell.bottomButton.accessibilityIdentifier = AccessibilityIdentifiers.Login.forgotPasswordButtonAcc

			return cell
		})
	}

    private func businessBookerInfoButtonRow() -> FormekaModelRow {
        FormekaModelRow(tag: LoginRow.businessBookerInfo.rawValue, cellSetup: { [unowned self] indexPath, _, table in
            guard let cell: AboutBusinessBookerButtonCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self

            cell.button.setTitle(
            	PILocalizedString(
            		"loginMoreAboutBusinessBookerButtonTitle",
            		comment: "Login screen: more about business booker button title"
            	),
            	for: .normal
            )
            cell.button.tintColor = UIColor.BasePurple
            cell.button.setTitleColor(UIColor.BasePurple, for: .normal)
            cell.hiddenSeparatorLocations = [.top, .bottom]

            cell.borderColor = UIColor.BasePurple

            return cell
        })
    }

	private func biometricButtonRow() -> FormekaModelRow {
		FormekaModelRow(tag: LoginRow.biometricAuth.rawValue, cellSetup: { [unowned self] indexPath, _, table in
			guard let cell: BiometricButtonCell = table.dequeueCell(for: indexPath) else { return nil }

			cell.delegate = self

            cell.button.setTitle(BiometricAuthenticationManager.biometryTypeAvailable.buttonTitle, for: .normal)
            cell.button.setImage(BiometricAuthenticationManager.biometryTypeAvailable.image, for: .normal)
			cell.button.tintColor = UIColor.whiteTwo
			cell.button.setTitleColor(UIColor.BasePurple, for: .normal)
			cell.hiddenSeparatorLocations = [.top, .bottom]

			return cell
		})
	}
}

extension LoginViewController: LoginTypeEventHandler {
    func selectedBusinessBooker() {
        presenter?.selectedBusinessBooker()
    }

    func selectedPersonalAccount() {
        presenter?.selectedPersonalAccount()
    }
}

extension LoginViewController: TwoButtonsCellDelegate {
	func topButtonDidTap(cell: TwoButtonsCell) {
		submitButtonDidTap()
	}

	func bottomButtonDidTap(cell: TwoButtonsCell) {
		let email = viewModel?.values[LoginRow.email.rawValue] as? String

        forgotPasswordButtonDidTap(email: email)
	}
}

extension LoginViewController: BiometricButtonCellDelegate {
	func buttonDidTap(cell: BiometricButtonCell) {
		biometricButtonDidTap()
	}
}

extension LoginViewController: AboutBusinessBookerButtonCellDelegate {
    func buttonDidTap(cell: AboutBusinessBookerButtonCell) {
        self.openAboutBusinessBookerExternalLink()
    }
}
