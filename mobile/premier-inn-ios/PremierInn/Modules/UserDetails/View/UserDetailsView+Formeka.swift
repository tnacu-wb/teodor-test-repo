//
//  UserDetailsView+Formeka.swift
//  PremierInn
//
//  Created by Marcello Mascia on 09/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import UIKit

struct GuestAccessibilityIdentifiers {
    let header: String
    let titleLabel: String
    let titleInput: String
    let firstNameLabel: String
    let firstNameInput: String
    let lastNameLabel: String
    let lastNameInput: String
    let emailLabel: String
    let emailInput: String
    let contactNumberLabel: String?
    let contactNumberInput: String?
}

struct UserMarketingModel {
    let description: NSAttributedString
    let isActive: Bool
}

struct TripPurposeMessageModel {
    let message: NSAttributedString
    let style: NotificationStyle
}

struct TripPurposeMessagesModel {
    let leisureMessages: [TripPurposeMessageModel]?
    let businessMessages: [TripPurposeMessageModel]?
}

struct UserSectionConf {
	let userTitle: String?
	let userFirstName: String?
	let userLastName: String?
	let userContactNumber: String?
	let userEmail: String?
	let userCountry: Country?
    let userPassport: Passport?
	let suffix: String
	let headerTitle: String
	let headerHeight: CGFloat
	let footerHeight: CGFloat
	let showCountry: Bool
	let emailIsOptional: Bool
    let shouldShowGdprWarning: Bool
    let accessibilityIdentifiers: GuestAccessibilityIdentifiers
}

extension UserDetailsViewController {
	private static func bookerConf(with conf: UserDetailsConfiguration) -> UserSectionConf {
		UserSectionConf(
			userTitle: conf.user?.title,
			userFirstName: conf.user?.firstName,
			userLastName: conf.user?.lastName,
			userContactNumber: conf.user?.contactNumber,
			userEmail: conf.user?.emailAddress,
			userCountry: conf.user?.country,
			userPassport: conf.user?.passport,
			suffix: Constants.bookerSuffix,
			headerTitle: conf.headerTitle,
			headerHeight: 70,
			footerHeight: 70,
			showCountry: conf.shouldShowCountry,
			emailIsOptional: false,
			shouldShowGdprWarning: false,
			accessibilityIdentifiers: GuestAccessibilityIdentifiers(
				header: AccessibilityIdentifiers.UserDetails.bookerHeader,
				titleLabel: AccessibilityIdentifiers.UserDetails.bookerTitleLabel,
				titleInput: AccessibilityIdentifiers.UserDetails.bookerTitleInput,
				firstNameLabel: AccessibilityIdentifiers.UserDetails.bookerFirstNameLabel,
				firstNameInput: AccessibilityIdentifiers.UserDetails.bookerFirstNameInput,
				lastNameLabel: AccessibilityIdentifiers.UserDetails.bookerLastNameLabel,
				lastNameInput: AccessibilityIdentifiers.UserDetails.bookerLastNameInput,
				emailLabel: AccessibilityIdentifiers.UserDetails.bookerEmailLabel,
				emailInput: AccessibilityIdentifiers.UserDetails.bookerEmailInput,
				contactNumberLabel: AccessibilityIdentifiers.UserDetails.bookerNumberLabel,
				contactNumberInput: AccessibilityIdentifiers.UserDetails.bookerNumberInput
			)
		)
	}

	private static func guestConf(with conf: RoomConfig) -> UserSectionConf {
		UserSectionConf(
			userTitle: conf.user?.title,
			userFirstName: conf.user?.firstName,
			userLastName: conf.user?.lastName,
			userContactNumber: conf.user?.contactNumber,
			userEmail: conf.user?.emailAddress,
			userCountry: conf.user?.country,
			userPassport: nil,
			suffix: String(describing: conf.roomIndex),
			headerTitle: conf.roomTitle,
			headerHeight: 70,
			footerHeight: conf.footerHeight,
			showCountry: false,
			emailIsOptional: !conf.isEmailRequired,
			shouldShowGdprWarning: true,
			accessibilityIdentifiers: GuestAccessibilityIdentifiers(
				header: String(format: AccessibilityIdentifiers.UserDetails.leadRoomHeaderFormat, conf.roomIndex),
				titleLabel: String(format: AccessibilityIdentifiers.UserDetails.leadTitleLabelFormat, conf.roomIndex),
				titleInput: AccessibilityIdentifiers.UserDetails.leadTitleInput,
				firstNameLabel: String(
					format: AccessibilityIdentifiers.UserDetails.leadFirstNameLabelFormat,
					conf.roomIndex
				),
				firstNameInput: AccessibilityIdentifiers.UserDetails.leadFirstNameInput,
				lastNameLabel: String(format: AccessibilityIdentifiers.UserDetails.leadLastNameLabelFormat, conf.roomIndex),
				lastNameInput: AccessibilityIdentifiers.UserDetails.leadLastNameInput,
				emailLabel: String(format: AccessibilityIdentifiers.UserDetails.leadEmailLabelFormat, conf.roomIndex),
				emailInput: AccessibilityIdentifiers.UserDetails.leadEmailInput,
				contactNumberLabel: nil,
				contactNumberInput: nil
			)
		)
	}

	func viewModelSections(conf: UserDetailsConfiguration) -> [FormekaModelSection] {
		var sections = [FormekaModelSection]()

		// Login button
		if conf.shouldShowLoginSection {
			sections.append(loginSection())
		}

		// User details
		sections.append(userSection(conf: UserDetailsViewController.bookerConf(with: conf), onChange: { [weak self] in
			self?.updateFirstGuestUI()
            self?.presenter?.userDetailsDidChange()
		}))

        if let view = addressSectionView {
            sections.append(view.addressSection(for: self))
        }

		// Booker is stayer
		if conf.shouldShowBookerStayer {
			sections.append(bookerIsStayerSection())
		}

		// Lead guests
		for roomConf in conf.roomConfigurations {
            guard roomConf.shouldShowRoomInfo else {
                continue
            }

            if roomConf.shouldSummarise {
                sections.append(
                    userSummarySection(
                    	user: roomConf.user,
                    	headerTitle: roomConf.roomTitle,
                    	footerTitle: nil,
                    	footerHeight: roomConf.footerHeight
                    )
                )
            } else {
                sections.append(
                    userSection(conf: UserDetailsViewController.guestConf(with: roomConf))
                )
            }
        }

		// Purpose
		if conf.shouldShowPurpose {
			sections.append(purposeSection())
		}

		// Car info
		if conf.shouldShowCarSection {
			sections.append(carSection(registration: conf.user?.carRegistration))
		}

        if let marketingModel = conf.marketingModel {
            sections.append(marketingSection(with: marketingModel))
        }

		// Submit
        let submitSection = submitSection(
        	backgroundColor: conf.submitButtonBackgroundColor,
        	foregroundColor: conf.submitButtonForegroundColor,
        	title: conf.submitButtonTitle,
        	showDeleteAccount: conf.shouldShowDeleteAccount
        )
        sections.append(submitSection)

		return sections
	}

	private func loginSection() -> FormekaModelSection {
		var rows = [FormekaModelRow]()

		rows.append(FormekaModelRow(cellSetup: { [weak self] indexPath, _, table in
			guard let cell: FormekaLoginRequestCell = table.dequeueCell(for: indexPath) else { return nil }

			cell.delegate = self

			cell.label.textColor = .ColourDL1
			cell.label.text = PILocalizedString("userDetailsLoginInvitationMessage")
            cell.label.font = UIFont.BodySmall()

			cell.loginButton.setTitle(PILocalizedString("userDetailsLoginButtonTitle"), for: .normal)
			cell.loginButton.backgroundColor = .BasePurple
			cell.loginButton.tintColor = .BaseWhite
            cell.loginButton.titleLabel?.font = UIFont.Button1()

            cell.contentView.backgroundColor = .ColourLD1

			return cell
		}))

		return FormekaModelSection(
			header: header(title: nil, height: CGFloat.leastNormalMagnitude),
			rows: rows,
			footer: footer(title: nil, height: 10)
		)
	}

	func userSection(conf: UserSectionConf, onChange: (() -> Void)? = nil) -> FormekaModelSection {
		var rows = [FormekaModelRow]()

        if conf.shouldShowGdprWarning {
            rows.append(FormekaModelRow(cellSetup: { [weak self] (indexPath, _, _) -> UITableViewCell? in
                guard let cell: FlexibleContentInformationCell = self?.table.dequeueCell(for: indexPath)
                	else { return UITableViewCell() }

                cell.hiddenSeparatorLocations = [.top, .bottom]
                cell.icon.image = #imageLiteral(resourceName: "padlock")
                cell.icon.tintColor = UIColor.BaseWhite

                let attributedString = NSMutableAttributedString(
                	string: PILocalizedString("sharingGuestDetailsPrivacyMessage"),
                	attributes: [
                		.font: UIFont.BodySmall(),
                		.foregroundColor: UIColor.BaseWhite
                	]
                )

                cell.content.attributedText = attributedString

                cell.contentView.backgroundColor = .ColourLD1
                cell.containerView.backgroundColor = .Tint2
                cell.containerView.layer.borderColor = UIColor.Tint2.cgColor
                cell.containerView.layer.cornerRadius = 0

                return cell
            }))
        }

        let localisedTitle = try? Title(title: conf.userTitle ?? "")
		rows.append(buttonRow(
			tag: Step1Row.salutation.rawValue + conf.suffix,
			title: PILocalizedString("userDetailsTitleLabel"),
			validators: [RequiredValidator(customErrorValue: PILocalizedString("userDetailsFormTitleErrorValue"))],
			value: localisedTitle?.localised ?? conf.userTitle,
			titleLabelAccessibilityIdentifier: conf.accessibilityIdentifiers.titleLabel,
			valueLabelAccessibilityIdentifier: conf.accessibilityIdentifiers.titleInput
		) { [weak self] indexPath, _ in
				self?.presenter?.salutationRowDidTap(indexPath: indexPath)
		})

        rows.append(firstNameTextFieldRow(conf: conf, onChange: onChange))
        rows.append(secondNameTextFieldRow(conf: conf, onChange: onChange))

		if conf.suffix == Constants.bookerSuffix {
			var numberTraits = FormekaTextFieldTraits()
			numberTraits.keyboardType = .phonePad
            numberTraits.textContentType = .telephoneNumber

            rows.append(
                textFieldRow(
                	name: Step1Row.contactNumber.rawValue + conf.suffix,
                	title: PILocalizedString("userDetailsContactNumberLabel"),
                	value: conf.userContactNumber,
                	traits: numberTraits,
                	inlineValidators: [.phoneNumber],
                	onBlurValidators: [.required, StringLengthValidator(range: 6...20)],
                	onChange: onChange,
                	titleLabelAccessibilityIdentifier: conf.accessibilityIdentifiers.contactNumberLabel,
                	textFieldAccessibilityIdentifier: conf.accessibilityIdentifiers.contactNumberInput,
                	maskField: true
                )
            )

            var emailTraits = FormekaTextFieldTraits()
            emailTraits.keyboardType = .default
            emailTraits.textContentType = .none
            emailTraits.placeholder = conf.emailIsOptional ? PILocalizedString("userDetailsOptional") : ""

            let onBlurValidators: [Validator] = conf.emailIsOptional ? [] : [.email, .required]

            rows.append(
                textFieldRow(
                	name: Step1Row.emailAddress.rawValue + conf.suffix,
                	title: PILocalizedString("userDetailsEmailLabel"),
                	value: conf.userEmail,
                	traits: emailTraits,
                	onBlurValidators: onBlurValidators,
                	onChange: onChange,
                	titleLabelAccessibilityIdentifier: conf.accessibilityIdentifiers.emailLabel,
                	textFieldAccessibilityIdentifier: conf.accessibilityIdentifiers.emailInput,
                	maskField: true,
                	onEndEditing: { [weak self] in
                        self?.handleEmailEndEditing(userSuffix: conf.suffix)
                }
                )
            )
		}

		rows.append(contentsOf: countryRows(conf: conf))

		return FormekaModelSection(
			header: header(
				title: conf.headerTitle,
				height: conf.headerHeight,
				accessibilityIdentifier: conf.accessibilityIdentifiers.header
			),
			rows: rows,
			footer: {
                footer(title: nil, height: 10)
			}()
		)
	}

	private func countryRows(conf: UserSectionConf) -> [FormekaModelRow] {
		var rows = [FormekaModelRow]()

		guard conf.showCountry else { return [] }

		rows.append(buttonRow(
			tag: Step1Row.nationality.rawValue + conf.suffix,
			title: PILocalizedString("userDetailsNationalityLabel"),
			validators: nil,
			value: conf.userCountry
		) { [weak self] indexPath, _ in
				self?.userNationalityAction(indexPath: indexPath)
		})

		if conf.userCountry?.passportRequired == true {
			var passportTraits = FormekaTextFieldTraits()
			passportTraits.keyboardType = .default

			rows.append(textFieldRow(
				name: Step1Row.passport.rawValue,
				title: PILocalizedString("userDetailsPassportLabel"),
				value: conf.userPassport?.number,
				traits: passportTraits,
				onBlurValidators: [.required],
				maskField: true
			))
		}

		return rows
	}

	private func bookerIsStayerSection() -> FormekaModelSection {
		var rows = [FormekaModelRow]()

		rows.append(FormekaModelRow(tag: Step1Row.bookerIsGuest.rawValue, cellSetup: { [weak self] indexPath, _, table in
			guard let cell: SwitchCell = table.dequeueCell(for: indexPath) else { return nil }

			cell.message.text = PILocalizedString("userDetailsBookerNotStayingMessage")
            cell.message.accessibilityIdentifier = AccessibilityIdentifiers.UserDetails.bookerStayerLabel
            cell.message.textColor = .ColourDL1

			cell.hiddenSeparatorLocations = [.top, .bottom]
			cell.toggleSwitch.isOn = self?.presenter?.bookerIsStaying == true ? false : true
            cell.toggleSwitch.accessibilityIdentifier = AccessibilityIdentifiers.UserDetails.bookerStayerSwitch
            cell.toggleSwitch.onTintColor = .Tint1

			cell.toggled = { [weak self] value in
				self?.presenter?.bookerIsStayingDidChange(isStaying: !value)
			}

            cell.contentView.backgroundColor = .ColourLD1

			return cell
		}))

        return FormekaModelSection(
        	header: header(
        		title: PILocalizedString("userDetailsBookerStayerSectionTitle"),
        		height: 70,
        		accessibilityIdentifier: AccessibilityIdentifiers.UserDetails.leadDetailSectionHeader
        	),
        	rows: rows,
        	footer: nil
        )
	}

    func userSummarySection(
    	user: User?,
    	headerTitle: String,
    	footerTitle: String?,
    	headerHeight: CGFloat = 50,
    	footerHeight: CGFloat
    ) -> FormekaModelSection {
		FormekaModelSection(
			header: header(title: headerTitle, height: headerHeight),
			rows: [staticGuestRow(guest: user)],
			footer: footer(title: footerTitle, height: footerHeight, backgroundColor: .ColourLD1)
		)
	}

	private func staticGuestRow(guest: User?) -> FormekaModelRow {
		let row = FormekaModelRow(tag: Step1Row.guestSummary.rawValue, cellSetup: { indexPath, row, table in
			guard let cell: SimpleSubtitleCell = table.dequeueCell(for: indexPath) else { return nil }

			let user = row.value as? User

			cell.title.text = user?.description
            cell.title.textColor = .ColourDL1

			cell.subtitle.text = user?.emailAddress
            cell.subtitle.textColor = .ColourDL1

			cell.hiddenSeparatorLocations = [.top, .bottom]

            cell.solidSeparator.backgroundColor = .clear
            cell.contentView.backgroundColor = .ColourLD1
            ContentsquareConfig.mask(view: cell)
			return cell
		})
		row.value = guest

		return row
	}

    private func purposeSection() -> FormekaModelSection {
        var rows: [FormekaModelRow] = []
        let rowTag = GuestDetailsRow.purpose.rawValue
        let validator = [RequiredSelectionValidator<TripPurposeSelections>(fieldName: PILocalizedString("tripPurpose"))]

        let row = FormekaModelRow(
        	tag: rowTag,
        	inlineValidators: validator,
        	cellSetup: { [weak self] indexPath, row, table in
                guard let self,
                      let cell: RadioButtonContainerCell = table.dequeueCell(for: indexPath) else {
                    return nil
                }

                cell.radioButtonView.currentSelection = row.value as? TripPurposeSelections
                cell.accessibilityIdentifier = AccessibilityIdentifiers.UserDetails.tripPurposeSelectorCell

                cell.radioButtonView.onSelectionChanged = { newSelection in
                    row.value = newSelection
                    self.presenter?.updatePurpose(newSelection)
                    row.error = nil
                    cell.errorMessage = nil
                    self.updateTableWithoutAnimation()
                }

                cell.errorMessage = row.error?.localizedDescription
                return cell
            }
        )

        rows.append(row)

        presenter?.purposeMessages?.forEach { message in
            rows.append(self.purposeInfoMessage(with: message))
        }

        return FormekaModelSection(
        	header: header(
        		title: PILocalizedString("userDetailsPurposeSectionTitle"),
        		height: 70,
        		accessibilityIdentifier: AccessibilityIdentifiers.UserDetails.tripPurposeMessage
        	),
        	rows: rows,
        	footer: nil
        )
    }

    private func refreshPurposeSection() {
        guard let indexPath = viewModel?.indexPath(forRowNamed: GuestDetailsRow.purpose.rawValue) else { return }
        viewModel?.remove(sectionAtIndex: indexPath.section)

        viewModel?.add(section: purposeSection(), index: indexPath.section)
        table.reloadSections([indexPath.section], with: .automatic)
    }

    private func purposeInfoMessage(with messageModel: TripPurposeMessageModel) -> FormekaModelRow {
        FormekaModelRow(
        	tag: GuestDetailsRow.taxInformationBox.rawValue,
        	cellSetup: { [weak self] (indexPath, _, _) -> UITableViewCell? in
                guard let cell: FlexibleContentInformationCell = self?.table.dequeueCell(for: indexPath) else {
                    return UITableViewCell()
                }

                cell.hiddenSeparatorLocations = [.top, .bottom]
                cell.icon.image = messageModel.style.icon
                cell.icon.image = cell.icon.image?.withRenderingMode(.alwaysTemplate)
                cell.icon.tintColor = messageModel.style.tint
                cell.topConstraint.constant = 2
                cell.bottomConstraint.constant = 10
                cell.containerView.backgroundColor = messageModel.style.background
                cell.containerView.layer.borderColor = messageModel.style.tint.withAlphaComponent(0.4).cgColor
                cell.containerView.layer.cornerRadius = 4

                cell.content.attributedText = messageModel.message

                return cell
            }
        )
    }

	private func carSection(registration: String?) -> FormekaModelSection {
		var rows = [FormekaModelRow]()

		var traits = FormekaTextFieldTraits()
		traits.keyboardType = .default
		traits.placeholder = PILocalizedString("userDetailsOptional", comment: "User details form: optional placeholder")

		rows.append(textFieldRow(
			name: Step1Row.carRegistration.rawValue,
			title: PILocalizedString("userDetailsCarRegistrationLabel", comment: "User details form: car registration label"),
			value: registration,
			traits: traits
		))

		return FormekaModelSection(
			header: header(
				title: PILocalizedString("userDetailsCarSectionTitle", comment: "User details form: car section title"),
				height: 70
			),
			rows: rows,
			footer: footer(title: nil, height: 10)
		)
	}

    func marketingSection(with model: UserMarketingModel) -> FormekaModelSection {
        let switchRow = FormekaModelRow(tag: Step1Row.marketing.rawValue, cellSetup: { indexPath, row, table in
            guard let cell: SwitchCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.message.attributedText = model.description
            cell.hiddenSeparatorLocations = [.top, .bottom]

            cell.toggleSwitch.isOn = row.value as? Bool ?? false

            cell.toggleSwitch.onTintColor = .Tint1
            cell.toggleSwitch.accessibilityIdentifier = AccessibilityIdentifiers.UserDetails.marketingSwitch
            cell.toggled = { value in
                row.value = value
            }

            cell.contentView.backgroundColor = .ColourLD1

            return cell
        })
        switchRow.value = model.isActive

        return FormekaModelSection(header: nil, rows: [switchRow], footer: footer(title: nil, height: 10))
    }

    private func submitSection(
    	backgroundColor: UIColor,
    	foregroundColor: UIColor,
    	title: String?,
    	showDeleteAccount: Bool
    ) -> FormekaModelSection {
        guard presenter != nil else { return FormekaModelSection(header: nil, rows: [], footer: nil) }

		var rows = [FormekaModelRow]()

        if showDeleteAccount {
            rows.append(submitSectionWithDelete(
            	backgroundColor: backgroundColor,
            	foregroundColor: foregroundColor,
            	title: title
            ))
        } else {
            rows.append(submitSectionWithoutDelete(
            	backgroundColor: backgroundColor,
            	foregroundColor: foregroundColor,
            	title: title
            ))
        }

        /* 🛡 GDPR Stuff 🛡 */
        rows.append(gdprFooterRow())
        /*=-=-=-=-=-=-=-=-=-=*/

		return FormekaModelSection(header: nil, rows: rows, footer: nil)
	}

    private func submitSectionWithoutDelete(
    	backgroundColor: UIColor,
    	foregroundColor: UIColor,
    	title: String?
    ) -> FormekaModelRow {
        let row = FormekaModelRow(
        	tag: Step1Row.submitButton.rawValue,
        	cellSetup: { [weak self] indexPath, _, table in
                guard let cell: FormekaSubmitButtonCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.button.backgroundColor = backgroundColor
                cell.button.setTitleColor(foregroundColor, for: .normal)
                cell.button.setTitle(title, for: .normal)
                cell.button.titleLabel?.font = UIFont.Heading3_Semibold()
                cell.button.accessibilityIdentifier = AccessibilityIdentifiers.UserDetails.continueButton

                cell.delegate = self
                cell.contentView.backgroundColor = .ColourLD6
                cell.hiddenSeparatorLocations = [.top, .bottom]

                return cell
            }
        )
        return row
    }

    private func submitSectionWithDelete(
    	backgroundColor: UIColor,
    	foregroundColor: UIColor,
    	title: String?
    ) -> FormekaModelRow {
        let row = FormekaModelRow(
        	tag: Step1Row.submitButton.rawValue,
        	cellSetup: { [weak self] indexPath, _, table in
                guard let cell: TwoButtonsCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.topButton.backgroundColor = backgroundColor
                cell.topButton.setTitleColor(foregroundColor, for: .normal)
                cell.topButton.setTitle(title, for: .normal)
                cell.topButton.titleLabel?.font = UIFont.Heading3_Semibold()
                cell.topButton.accessibilityIdentifier = AccessibilityIdentifiers.UserDetails.continueButton

                cell.bottomButton.setTitle(
                	PILocalizedString("userPreferenceDeleteAccount", comment: "User Preference Delete Account"),
                	for: .normal
                )

                cell.delegate = self
                cell.contentView.backgroundColor = .ColourLD6
                cell.hiddenSeparatorLocations = [.top, .bottom]

                return cell
            }
        )
        return row
    }

    private func userNationalityAction(indexPath: IndexPath) {
        let controller = ListViewController(
        	viewModel: CountriesListViewModel(countries: Country.countriesList),
        	invertedColours: true
        )
        controller.textFieldPlaceholder = PILocalizedString(
        	"searchCountryPlaceholder",
        	comment: "Search country input placeholder"
        )
        controller.shouldDelaySearchRequest = false
        controller.selectedObjectOutput = { [weak self] sender, object in
            sender.dismiss(animated: true)

            guard let country = object as? Country else { return }

            let countryRow = self?.viewModel?.row(named: Step1Row.nationality.rawValue + Constants.bookerSuffix)
            countryRow?.value = country

            if let indexPath = self?.viewModel?
               .indexPath(forRowNamed: Step1Row.nationality.rawValue + Constants.bookerSuffix) {
                self?.table.reloadRows(at: [indexPath], with: .automatic)
            }

            if country.passportRequired == true {
                var passportTraits = FormekaTextFieldTraits()
                passportTraits.keyboardType = .default

                guard self?.viewModel?.indexPath(forRowNamed: Step1Row.passport.rawValue) == nil else { return }

                guard let passportRow = self?.textFieldRow(
                	name: Step1Row.passport.rawValue,
                	title: PILocalizedString("userDetailsPassportLabel", comment: "User details form: user's passport label"),
                	value: nil,
                	traits: passportTraits,
                	onBlurValidators: [Validator.passport],
                	maxNumberOfCharacters: nil,
                	maskField: true
                ) else { return }

                let passportIndexPath = IndexPath(item: indexPath.item + 1, section: indexPath.section)
                self?.viewModel?.add(row: passportRow, at: passportIndexPath)
                self?.table.insertRows(at: [passportIndexPath], with: .automatic)
            } else {
                if let indexPath = self?.viewModel?.remove(rowNamed: Step1Row.passport.rawValue) {
                    self?.table.deleteRows(at: [indexPath], with: .automatic)
                }
            }
        }
        controller.cancelButtonDidTap = { sender in
            sender.dismiss(animated: true)
        }

        present(controller, animated: true)
    }
}

extension UserDetailsViewController: FormekaLoginRequestCellDelegate {
	func loginButtonDidTap() {
		presenter?.loginButtonDidTap()
	}
}

extension UserDetailsViewController: FormekaSegmentedControlCellDelegate, CompanyNameDelegate {
	func formekaSegmentedControlValueDidChange(cell: FormekaSegmentedControlCell) {
		guard let indexPath = table.indexPath(for: cell) else { return }
		guard let row = viewModel?.row(at: indexPath) else { return }

		switch row.tag {
		case GuestDetailsRow.addressType.rawValue + Constants.bookerSuffix:
			let addressType: AddressType = (cell.segmentedControl.selectedSegmentIndex == 0) ? .home : .commercial

			row.value = addressType

			// Add addition
			switch addressType {
			case .commercial:
				let newIndexPath = IndexPath(item: indexPath.row + 1, section: indexPath.section)
                let textFieldRow = textFieldRow(
                	name: GuestDetailsRow.companyName.rawValue,
                	title: PILocalizedString("userDetailsCompanyName", comment: "User details form: company name label"),
                	value: persistedCompanyName,
                	inlineValidators: [.companyName],
                	onBlurValidators: [.required, StringLengthValidator(range: 1...40)]
                )
				viewModel?.add(row: textFieldRow, at: newIndexPath)
				table.insertRows(at: [newIndexPath], with: .automatic)

			default:
                persistedCompanyName = viewModel?.row(named: GuestDetailsRow.companyName.rawValue)?.value as? String
				if let removedIndexPath = viewModel?.remove(rowNamed: GuestDetailsRow.companyName.rawValue) {
					table.deleteRows(at: [removedIndexPath], with: .automatic)
				}
			}

		default:
			break
		}
	}

    func removePersistedCompanyName() {
        persistedCompanyName = nil
    }
}

extension UserDetailsViewController: FormekaSubmitButtonCellDelegate {
	func submitButtonDidTap(cell: FormekaSubmitButtonCell) {
		presenter?.submitButtonDidTap()
	}
}

extension UserDetailsViewController: TwoButtonsCellDelegate {
    func topButtonDidTap(cell: TwoButtonsCell) {
        presenter?.submitButtonDidTap()
    }

    func bottomButtonDidTap(cell: TwoButtonsCell) {
        presenter?.didTapDeleteAccount()
    }
}

private extension UserDetailsViewController {
    private func firstNameTextFieldRow(
    	conf: UserSectionConf,
    	onChange: (() -> Void)? = nil
    ) -> FormekaModelRow {
        var firstNameTraits = FormekaTextFieldTraits()
        firstNameTraits.autocapitalizationType = .words
        firstNameTraits.textContentType = .givenName

        return textFieldRow(
        	name: Step1Row.firstName.rawValue + conf.suffix,
        	title: PILocalizedString("userDetailsFirstNameLabel" ),
        	value: conf.userFirstName,
        	traits: firstNameTraits,
        	inlineValidators: [NameValidator(customErrorValue: PILocalizedString("userDetailsFormFirstNameErrorValue"))],
        	onBlurValidators: [StringLengthValidator(range: 1...30)],
        	onChange: onChange,
        	titleLabelAccessibilityIdentifier: conf.accessibilityIdentifiers.firstNameLabel,
        	textFieldAccessibilityIdentifier: conf.accessibilityIdentifiers.firstNameInput,
        	maskField: true
        )
    }

    private func secondNameTextFieldRow(
    	conf: UserSectionConf,
    	onChange: (() -> Void)? = nil
    ) -> FormekaModelRow {
        var secondNameTraits = FormekaTextFieldTraits()
        secondNameTraits.autocapitalizationType = .words
        secondNameTraits.textContentType = .familyName

        return textFieldRow(
        	name: Step1Row.lastName.rawValue + conf.suffix,
        	title: PILocalizedString("userDetailsLastNameLabel"),
        	value: conf.userLastName,
        	traits: secondNameTraits,
        	inlineValidators: [NameValidator(customErrorValue: PILocalizedString("userDetailsFormLastNameErrorValue"))],
        	onBlurValidators: [StringLengthValidator(range: 1...30)],
        	onChange: onChange,
        	titleLabelAccessibilityIdentifier: conf.accessibilityIdentifiers.lastNameLabel,
        	textFieldAccessibilityIdentifier: conf.accessibilityIdentifiers.lastNameInput,
        	maskField: true
        )
    }
}
