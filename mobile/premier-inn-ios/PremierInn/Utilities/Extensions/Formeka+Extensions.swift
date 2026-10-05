//
//  Formeka+Extensions.swift
//  PremierInn
//
//  Created by Marcello Mascia on 22/07/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import UIKit

protocol PasswordRowDelegate: AnyObject {
    func valueChanged(result: ValidationError?)
}

enum FormekaError: LocalizedError {
    case formLocked

    var errorDescription: String? {
        switch self {
        case .formLocked:
            return PILocalizedString("formSubmissionLoadingMessage", comment: "Form submission loading message")
        }
    }
}

extension Bool: @retroactive FormekaValue {
    public var displayName: String { String(describing: self) }
}

extension Int: @retroactive FormekaValue {
    public var displayName: String { "\(self)" }
}

extension User: @retroactive FormekaValue {
    public var displayName: String {
        let localisedTitle = try? Title(title: title ?? "")
		return "\(localisedTitle?.localised ?? title ?? "") \(firstName ?? "") \(lastName ?? "")"
    }
}

extension UITextField {
    func applyTraits(_ traits: FormekaTextFieldTraits) {
        autocapitalizationType = traits.autocapitalizationType
        autocorrectionType = traits.autocorrectionType
        spellCheckingType = traits.spellCheckingType
        keyboardType = traits.keyboardType
        keyboardAppearance = traits.keyboardAppearance
        returnKeyType = traits.returnKeyType
        enablesReturnKeyAutomatically = traits.enablesReturnKeyAutomatically
        isSecureTextEntry = traits.secureTextEntry
        textContentType = traits.textContentType
    }
}

extension FormekaViewController {
	func emptyRow(
	    topHeight: CGFloat,
	    bottomHeight: CGFloat,
	    hiddenSeparators: [SimpleSeparatorsCell.SeparatorLocation],
	    backgroundColor: UIColor
	) -> FormekaModelRow {
		FormekaModelRow { indexPath, _, table in
			guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

			cell.contentView.backgroundColor = backgroundColor
			cell.content.text = nil
			cell.messageTopConstraint.constant = topHeight
			cell.messageBottomConstraint.constant = bottomHeight
			cell.hiddenSeparatorLocations = hiddenSeparators

			return cell
		}
	}

	func textFieldRow(
	    name: String,
	    title: String,
	    value: String?,
	    traits: FormekaTextFieldTraits = FormekaTextFieldTraits(),
	    inlineValidators: [Validator] = [],
	    onBlurValidators: [Validator] = [],
	    maxNumberOfCharacters: Int? = Int.max,
	    inputAccessoryView: UIView? = nil,
	    onChange: (() -> Void)? = nil,
	    titleLabelAccessibilityIdentifier: String? = nil,
	    textFieldAccessibilityIdentifier: String? = nil,
	    isReadOnly: Bool = false,
	    maskField: Bool = false,
	    backgroundColour: UIColor? = nil,
	    textColour: UIColor? = nil,
	    textFieldColour: UIColor? = nil,
	    errorBackgroundColor: UIColor? = nil,
	    onEndEditing: (() -> Void)? = nil
	) -> FormekaModelRow {
        let row = FormekaModelRow(
            tag: name,
            title: title.lowercased(),
            inlineValidators: inlineValidators,
            onBlurValidators: onBlurValidators,
            cellSetup: { [unowned self] indexPath, row, table in
                guard let cell: FormekaTextFieldCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.delegate = self
                cell.contentView.backgroundColor = backgroundColour ?? .ColourLD1

                cell.titleLabel.text = title
                cell.titleLabel.textColor = textColour ?? .ColourDL3
                cell.titleLabel.font = UIFont.BodySmall()
                cell.titleLabel.accessibilityIdentifier = titleLabelAccessibilityIdentifier
                cell.titleLabel.isAccessibilityElement = false

                cell.textField.accessibilityLabel = title
                cell.textField.text = row.value?.displayName
                cell.textField.textColor = textFieldColour ?? .ColourDL1
                cell.textField.font = UIFont.Body()
                cell.textField.accessibilityIdentifier = textFieldAccessibilityIdentifier
                cell.textField.applyTraits(traits)
                cell.textField.attributedPlaceholder = NSAttributedString(
                    string: traits.placeholder ?? "",
                    attributes: [.foregroundColor: UIColor.ColourDL3]
                )
                cell.textField.inputAccessoryView = inputAccessoryView
                cell.textField.accessibilityIdentifier = "\(name)Acc"
                cell.textField.isUserInteractionEnabled = !isReadOnly

                if maskField {
                    ContentsquareConfig.mask(view: cell.textField)
                }

                cell.errorLabel?.textColor = .Tint8
				cell.errorBackground?.backgroundColor = errorBackgroundColor
                cell.errorLabel?.font = UIFont.BodySmall()

                cell.maxNumberOfCharacters = maxNumberOfCharacters
                cell.valueChanged = onChange
                cell.valueEndChange = onEndEditing
                cell.errorMessage = row.error?.localizedDescription

                cell.accessibilityIdentifier = title

                return cell
            }, didSelect: { [unowned self] indexPath, _ in
                table.cellForRow(at: indexPath)?.becomeFirstResponder()
            }
        )

        row.value = value

        return row
    }

    func cardDateFieldRow(
        name: String,
        title: String,
        value: String?,
        traits: FormekaTextFieldTraits = FormekaTextFieldTraits(),
        inlineValidators: [Validator] = [],
        onBlurValidators: [Validator] = [],
        onChange: (() -> Void)? = nil
    ) -> FormekaModelRow {
        let row = FormekaModelRow(
            tag: name,
            title: title.lowercased(),
            inlineValidators: inlineValidators,
            onBlurValidators: onBlurValidators,
            cellSetup: { [unowned self] indexPath, row, table in
                guard let cell: FormekaCardDateCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.delegate = self

                cell.titleLabel.text = title
                cell.titleLabel.textColor = .ColourDL3
                cell.titleLabel.font = UIFont.BodySmall()

                cell.textField.text = row.value?.displayName
                cell.textField.textColor = .ColourDL1
                cell.textField.font = UIFont.Body()

                cell.textField.applyTraits(traits)
                cell.textField.attributedPlaceholder = NSAttributedString(
                    string: traits.placeholder ?? "",
                    attributes: [.foregroundColor: UIColor.ColourDL3]
                )

                cell.errorLabel?.textColor = .Tint8
                cell.errorLabel?.font = UIFont.BodySmall()

                cell.valueChanged = onChange
                cell.errorMessage = row.error?.localizedDescription

                return cell
            }, didSelect: { [unowned self] indexPath, _ in
                table.cellForRow(at: indexPath)?.becomeFirstResponder()
            }
        )
        row.value = value

        return row
    }

    func buttonRow(
        tag: String,
        title: String,
        validators: [Validator]?,
        value: FormekaValue?,
        titleLabelAccessibilityIdentifier: String? = nil,
        valueLabelAccessibilityIdentifier: String? = nil,
        action: TableRowCellSelection?
    ) -> FormekaModelRow {
        let row = FormekaModelRow(
            tag: tag,
            title: title,
            inlineValidators: validators ?? [],
            cellSetup: { indexPath, row, table in
                guard let cell: FormekaButtonCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.titleLabel.text = title
                cell.titleLabel.textColor = UIColor.ColourDL3
                cell.titleLabel.font = UIFont.BodySmall()
                cell.titleLabel.accessibilityIdentifier = titleLabelAccessibilityIdentifier
                cell.titleLabel.accessibilityTraits = .button

                cell.valueLabel.text = row.value?.displayName
                cell.valueLabel.textColor = UIColor.ColourDL1
                cell.valueLabel.font = UIFont.Body()
                cell.valueLabel.accessibilityIdentifier = valueLabelAccessibilityIdentifier ?? "\(tag)Acc"

                cell.errorLabel?.textColor = .Tint8
                cell.errorLabel?.font = UIFont.BodySmall()

                cell.errorMessage = row.error?.localizedDescription

                cell.contentView.backgroundColor = .ColourLD1

                cell.accessibilityIdentifier = title

                return cell
            },
            didSelect: action
        )
        row.value = value

        return row
    }

    func emailRow(isBusiness: Bool, returnTapped: (() -> Bool)? = nil, value: FormekaValue? = nil) -> FormekaModelRow {
        let row = FormekaModelRow(
            tag: LoginRow.email.rawValue,
            onBlurValidators: [.email, .required],
            cellSetup: { [weak self] indexPath, row, table in
                guard let cell: FormekaTextFieldCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.delegate = self

                cell.titleLabel
                    .text = isBusiness ? PILocalizedString("loginEmailLabelBusiness") :
                    PILocalizedString("loginEmailLabelPersonal")
                cell.titleLabel.textColor = .ColourDL3
                cell.titleLabel.font = UIFont.BodySmall()

                cell.textField.text = row.value?.displayName
                cell.textField.keyboardType = .emailAddress
                cell.textField.textColor = .ColourDL1
                cell.textField.font = UIFont.Body()
                cell.textField.isSecureTextEntry = false
                cell.textField.textContentType = .username

                cell.errorLabel?.textColor = .Tint8
                cell.errorLabel?.font = UIFont.BodySmall()

                cell.accessibilityIdentifier = "emailCellAcc"
                cell.errorMessage = row.error?.localizedDescription
                cell.returnTapped = returnTapped
                cell.returnTapped = {
                    let cell: UITableViewCell? = self?.viewModel?.cell(forRowNamed: LoginRow.password.rawValue, table: table)
                    cell?.becomeFirstResponder()

                    return true
                }

                cell.textField.autocorrectionType = .no
                cell.textField.textContentType = .emailAddress

                return cell
            }, didSelect: { [unowned self] indexPath, _ in
                table.cellForRow(at: indexPath)?.becomeFirstResponder()
            }
        )

        row.value = value
        return row
    }

    func plainPasswordRow(
        tag: String = LoginRow.password.rawValue,
        title: String = PILocalizedString("loginPasswordLabel"),
        returnTapped: (() -> Bool)? = nil
    ) -> FormekaModelRow {
        FormekaModelRow(
            tag: tag,
            onBlurValidators: [RequiredValidator(customErrorValue: PILocalizedString("password"))],
            cellSetup: { [unowned self] indexPath, row, table in
                guard let cell: FormekaTextFieldPasswordCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.delegate = self

                cell.titleLabel.text = title
                cell.titleLabel.textColor = .ColourDL3
                cell.titleLabel.font = .BodySmall()

                cell.textField.text = row.value?.displayName
                cell.textField.isSecureTextEntry = true
                cell.textField.adjustsFontSizeToFitWidth = false
                cell.textField.textColor = .TintD1
                cell.textField.font = .Body()
                cell.textField.accessibilityIdentifier = (tag + "PasswordTextField")

                cell.errorLabel?.textColor = .Tint8
                cell.errorLabel?.font = .Body()

                cell.setShowButton()
                cell.accessibilityIdentifier = (tag + "PasswordCellAcc")
                cell.errorMessage = row.error?.localizedDescription

                cell.returnTapped = returnTapped

                return cell
            },
            didSelect: { [unowned self] indexPath, _ in
                table?.cellForRow(at: indexPath)?.becomeFirstResponder()
            }
        )
    }

    func passwordRow(
        tag: String = LoginRow.password.rawValue,
        title: String = PILocalizedString("loginPasswordLabel"),
        regexsDict: [String: String]?,
        required: Bool = true,
        delegate: PasswordRowDelegate? = nil,
        hiddenSeparators: [SimpleSeparatorsCell.SeparatorLocation] = [],
        returnTapped: (() -> Bool)? = nil
    ) -> FormekaModelRow {
        FormekaModelRow(
            tag: tag,
            inlineValidators: [PasswordValidator(
                regexsDict: regexsDict,
                customSuccessValue: PILocalizedString("Acceptable password"),
                isRequired: required
            )],
            onBlurValidators: [PasswordValidator(
                regexsDict: regexsDict,
                customSuccessValue: PILocalizedString("Acceptable password"),
                isRequired: required
            )],
            cellSetup: { [unowned self] indexPath, row, table in
            guard let cell: FormekaTextFieldPasswordCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self
            cell.hiddenSeparatorLocations = hiddenSeparators

            cell.titleLabel.text = title
            cell.titleLabel.textColor = .ColourDL3
            cell.titleLabel.font = UIFont.BodySmall()

            cell.textField.text = row.value?.displayName
            cell.textField.isSecureTextEntry = true
            cell.textField.adjustsFontSizeToFitWidth = false
            cell.textField.textColor = .ColourDL1
            cell.textField.font = UIFont.Body()
            cell.textField.keyboardType = .default
            cell.textField.accessibilityIdentifier = "passwordCellAcc"

            cell.textField.textContentType = .password
            let descriptor = "required: upper; required: lower; required: digit; minlength: 8; allowed: upper, lower, digit;"
            cell.textField.passwordRules = UITextInputPasswordRules(descriptor: descriptor)
            cell.errorLabel?.textColor = .TintL1
            cell.errorLabel?.font = UIFont.BodySmall()

            cell.setShowButton()

            cell.returnTapped = returnTapped

            cell.valueChangedWithState = { state in
                switch state {
                case .passwordError:
                    cell.errorLabel?.textColor = .TintL1
                case .passwordEmpty:
                    table.beginUpdates()
                    cell.errorLabel?.textColor = .TintL1
                    if let regexsDict = regexsDict {
                        let message = String(
                            format: PILocalizedString("Must include %@", comment: ""),
                            regexsDict.compactMap { $0.value }.joined(separator: ", ")
                        )

                        cell.errorMessage = message
                        cell.errorLabel?.text = message
                        table.endUpdates()
                    }
                case .passwordSuccess:
                    cell.errorLabel?.textColor = .Tint4
                default:
                    cell.errorLabel?.textColor = .TintL1
                }
                delegate?.valueChanged(result: state)
            }

            cell.valueEndChangeWithState = { state in
                cell.errorLabel?.textColor = FormekaViewController.colour(for: state)
                delegate?.valueChanged(result: state)
            }
            return cell
        },
            didSelect: { [unowned self] indexPath, _ in
            table.cellForRow(at: indexPath)?.becomeFirstResponder()
        }
        )
    }

    private class func colour(for validationError: ValidationError?) -> UIColor {
        switch validationError {
        case .passwordError:
            return .Tint8
        case .passwordEmpty:
            return .TintL1
        case .passwordSuccess:
            return .Tint4
        default:
            return .TintL1
        }
    }

    func header(
        title: String?,
        height: CGFloat,
        backgroundColor: UIColor = .ColourLD1,
        accessibilityIdentifier: String? = nil
    ) -> FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: height, viewSetup: { _, table in
            let view: FormekaHeader? = table.headerFooterView()

            view?.titleLabel.text = title
            view?.titleLabel.textColor = .BasePurple
            view?.titleLabel.font = UIFont.Heading2_Bold()
            view?.titleLabel?.accessibilityIdentifier = accessibilityIdentifier
            view?.titleLabel?.accessibilityTraits.insert(.header)
            view?.contentView.backgroundColor = backgroundColor

            return view
        })
    }

    func footer(title: String?, height: CGFloat, backgroundColor: UIColor = .ColourLD1) -> FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: height, viewSetup: { _, table in
            let view: FormekaIconFooter? = table.headerFooterView()

            view?.titleLabel.text = title
            view?.titleLabel.textColor = .ColourDL1
            view?.titleLabel.font = UIFont.Subtext()

            view?.imageView.image = #imageLiteral(resourceName: "UNKNOWN")
            view?.imageView.isHidden = title == nil ? true : false
            view?.imageView.tintColor = .ColourDL5

            view?.grayAreaView.isHidden = title == nil ? true : false
            view?.grayAreaView.backgroundColor = .ColourLD1

            view?.lineView.backgroundColor = .TintL3

            view?.contentView.backgroundColor = title == nil ? .clear : .ColourLD1

            return view
        })
    }

    func cvvRequiredForBusinessCardRow(for accessLevel: AccessLevel? = .stayer) -> FormekaModelRow {
        FormekaModelRow(tag: ReviewAndBookRow.paymentAuthInfo.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath) else { return nil }

            let paragraphStyle = NSMutableParagraphStyle()
            paragraphStyle.lineSpacing = 4

            cell.bottomConstraint.constant = 10
            cell.topConstraint.constant = 0

            let message = accessLevel == .superUser ?
                PILocalizedString("paymentAuthRequiresPotentiallyUnknownCVVTravelManagerMessage") :
                PILocalizedString("paymentAuthRequiresPotentiallyUnknownCVVMessage")

            cell.content.attributedText = NSAttributedString(string: message, attributes: [.paragraphStyle: paragraphStyle])
            cell.content.textColor = .ColourDL1
            cell.content.font = .BodySmall()

            cell.containerView.backgroundColor = NotificationStyle.alert.background
            cell.containerView.layer.cornerRadius = 4
            cell.containerView.layer.borderColor = NotificationStyle.alert.tint.withAlphaComponent(0.4).cgColor
            cell.containerView.layer.borderWidth = 1

            cell.icon.image = NotificationStyle.alert.icon
            cell.icon.tintColor = NotificationStyle.alert.tint

            cell.backgroundColor = .white

            return cell
        })
    }
}

extension FormekaViewController: Trackable {
    @objc var screenName: String { "\(type(of: self))" }
    @objc var trackScreen: Bool { true }
    var environment: String { AnalyticsConstants.environment }
    var loggedIn: LoggedInAnalytic { UserSessionManager.sharedInstance.currentUser != nil ? .loggedIn : .notLoggedIn }
    var timeZone: String { TimeZone.current.description }
    var language: String { Locale.current.language.languageCode?.identifier ?? "n/a" }
    @objc var screenType: String { "PI_DEV" }
    var userID: String { UserSessionManager.sharedInstance.currentUser?.customerAccountId ?? "" }
    var time: String { Date().analyticsTimeFormat }

    @objc var customParameters: [String: Any]? { nil }

    override open func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        if trackScreen {
            trackState(withName: screenName, type: screenType, additionalData: customParameters)
        }

        NotificationCenter.default.addObserver(
            self,
            selector: #selector(applicationDidTakeScreenshot),
            name: UIApplication.userDidTakeScreenshotNotification,
            object: nil
        )
    }

    override open func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)

        NotificationCenter.default.removeObserver(self, name: UIApplication.userDidTakeScreenshotNotification, object: nil)
    }

    // MARK: -

    func trackState(withName name: String, type: String? = nil, additionalData: PIDictionary? = nil) {
        let data = analyticsDic(withAdditionalData: additionalData, type: type)

        AnalyticsManager.shared.trackState(name, data: data)
    }

    func track(action: String, additionalData: PIDictionary?) {
        let data = analyticsDic(withAdditionalData: additionalData)

        AnalyticsManager.shared.trackAction(action, userInfo: data)
    }

    private func analyticsDic(withAdditionalData additionalData: PIDictionary?, type: String? = nil) -> PIDictionary {
        var data: PIDictionary = [
            PIAnalytics.Keys.environment: environment,
            PIAnalytics.Keys.userLogin: loggedIn.rawValue,
            PIAnalytics.Keys.timeZone: timeZone,
            PIAnalytics.Keys.language: language,
            PIAnalytics.Keys.screenType: type ?? screenType,
            PIAnalytics.Keys.userID: userID,
            PIAnalytics.Keys.time: time
        ]

        if let user = UserSessionManager.sharedInstance.currentUser, let companyId = user.companyId,
           let accessLevel = user.accessLevel?.rawValue {
            data[PIAnalytics.Keys.companyID] = companyId
            data[PIAnalytics.Keys.businessUserLevel] = accessLevel
        }

        // Deep link tracking campaign properties
        if let campaign = AnalyticsManager.shared.campaignAttribution.remove() {
            data[PIAnalytics.Keys.sCampaign] = campaign.cid
            data[PIAnalytics.Keys.mckv] = campaign.mckv
            data[PIAnalytics.Keys.etRid] = campaign.etRid
            data[PIAnalytics.Keys.sFullURL] = campaign.fullURLString
            data[PIAnalytics.Keys.sReferrer] = campaign.referrerURLString
        }

        if let additionalData = additionalData {
            for key in additionalData.keys {
                data[key] = additionalData[key]
            }
        }

        return data
    }

    @objc func applicationDidTakeScreenshot() {
        AnalyticsManager.shared.trackAction(
            PIAnalytics.Action.screenshot + self.screenName,
            userInfo: [PIAnalytics.Keys.screenshots: true]
        )
    }
}

extension FormekaViewModel {
    func cell<T>(forRowNamed rowName: String, table: UITableView?) -> T? {
        guard let table = table else { return nil }
        guard let indexPath = indexPath(forRowNamed: rowName) else { return nil }
        guard let cell = table.cellForRow(at: indexPath) as? T else { return nil }

        return cell
    }
}

extension FormekaViewController {
    var textForTermsAndConditionsField: NSAttributedString {
        let attributedString = NSMutableAttributedString(
            string: PILocalizedString("reviewTermsAndConditionsAcceptedLabel"),
            attributes: [NSAttributedString.Key.font: UIFont.BodySmall()]
        )

        attributedString.style(
            text: PILocalizedString("reviewTermsAndConditionsTextToHighlight"),
            withAttributes: [NSAttributedString.Key.foregroundColor: UIColor.BasePurple]
        )

        attributedString.style(
            text: PILocalizedString("revivewScreenTermsAndConditionsNotAccepted"),
            withAttributes: [NSAttributedString.Key.foregroundColor: UIColor.paleRed]
        )

        return attributedString
    }

    // TODO: - Could it be worth creating a ToggleSwitchRow subclassing FormekaModelRow to encapsulate all this logic?

    func createToggleSwitchRow(
        tag: String,
        text: String,
        tint: UIColor,
        separatorLocations: [SimpleSeparatorsCell.SeparatorLocation],
        initialValue: Bool,
        onToggle: @escaping (Bool) -> Void
    ) -> FormekaModelRow {
        let row = FormekaModelRow(tag: tag, cellSetup: { indexPath, row, table in
            guard let cell: SwitchCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.message.text = PILocalizedString(text)
            cell.toggleSwitch.onTintColor = tint
            cell.hiddenSeparatorLocations = separatorLocations

            cell.toggleSwitch.setOn(row.value as? Bool ?? initialValue, animated: false)
            cell.toggled = { value in
                onToggle(value)
                row.value = value
            }
            return cell
        })

        row.value = initialValue
        return row
    }

    func termsAndConditionsRow(
        text: NSAttributedString? = nil,
        initialValue: Bool,
        onToggle: @escaping (Bool) -> Void
    ) -> FormekaModelRow {
        let row = FormekaModelRow(
            tag: RegisterRow.termsAndConditions.rawValue,
            onBlurValidators: [TermsAndConditionsValidator()],
            cellSetup: { [weak self] indexPath, row, table in
            guard let cell: TermsAndConditionsCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.toggled = { value in
                row.value = value
                try? row.validate()

                table.beginUpdates()
                cell.errorMessage = row.error?.localizedDescription
                table.endUpdates()

                onToggle(value)
            }

            cell.message.attributedText = text ?? self?.textForTermsAndConditionsField
            cell.message.isUserInteractionEnabled = true
            cell.message.addGestureRecognizer(UITapGestureRecognizer(
                target: self,
                action: #selector(self?.openTermsAndConditions)
            ))

            cell.toggleSwitch.onTintColor = .Tint1
            cell.toggleSwitch.isOn = row.value as? Bool ?? false
            cell.toggleSwitch.accessibilityIdentifier = "registerAcceptT&CsAcc"

            cell.errorLabel?.textColor = .Tint8
            cell.errorLabel?.font = UIFont.BodySmall()
            cell.errorMessage = row.error?.localizedDescription

            cell.contentView.backgroundColor = .ColourLD1

            return cell
        }
        )
        row.value = initialValue

        return row
    }

    func termsAndConditionsTextRow() -> FormekaModelRow {
        FormekaModelRow(tag: RegisterRow.termsAndConditions.rawValue, cellSetup: { [weak self] indexPath, _, table in
            guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.content.attributedText = self?.textForTermsAndConditionsField
            cell.content.isUserInteractionEnabled = true
            cell.content.addGestureRecognizer(UITapGestureRecognizer(
                target: self,
                action: #selector(self?.openTermsAndConditions)
            ))
            cell.content.backgroundColor = .clear
            cell.content.textAlignment = .left

            cell.hiddenSeparatorLocations = [.bottom, .top]
            cell.messageLeadingConstraint.constant = 16
            cell.messageTrailingConstraint.constant = 16
            cell.messageTopConstraint.constant = 4
            cell.messageBottomConstraint.constant = 4

            cell.contentView.backgroundColor = .BaseWhite

            return cell
        })
    }

    func spacerHeaderFooter(height: CGFloat, backgroundColor: UIColor = .clear) -> FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: height, viewSetup: { _, table in
            let view: SimpleFooter? = table.headerFooterView()
            view?.backgroundColor = backgroundColor
            view?.contentView.backgroundColor = backgroundColor
            view?.lineView.backgroundColor = backgroundColor

            return view
        })
    }

    @objc private func openTermsAndConditions() {
        openTermsAndConditionsExternalLink()
    }
}

extension FormekaViewController {
    static func inputToolbar(view: UIView) -> FormInputKeyboardToolbar? {
        let toolbar: FormInputKeyboardToolbar? = .fromNib()
        toolbar?.doneCompletion = {
            view.endEditing(true)
        }

        return toolbar
    }

    func memorableWordRow(tag: String, initialValue: String?) -> FormekaModelRow {
        let row = FormekaModelRow(
            tag: tag,
            title: PILocalizedString("memorable word"),
            inlineValidators: [.required],
            onBlurValidators: [.required],
            cellSetup: { [unowned self] indexPath, row, table in
                guard let cell: FullsizeTextFieldCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.fullLengthTextField.isSecureTextEntry = true
                cell.fullLengthTextField.adjustsFontSizeToFitWidth = false
                cell.fullLengthTextField.placeholder = PILocalizedString("memorableWordPlaceholder")
                cell.fullLengthTextField.accessibilityIdentifier = "memorableWordAcc"
                cell.fullLengthTextField.text = row.value as? String
                cell.fullLengthTextField.inputAccessoryView = FormekaViewController.inputToolbar(view: view)
                cell.errorMessage = row.error?.localizedDescription
                cell.fullLengthTextField.layer.borderColor = row.error == nil ? UIColor.TintD2.cgColor : UIColor.Tint8
                    .cgColor
                cell.delegate = self

                return cell
        }
        )

        row.value = initialValue

        return row
    }

    func memorableWordInfoRow(tag: String) -> FormekaModelRow {
        iconInfoRow(
            tag: tag,
            attributedString: NSAttributedString(string: PILocalizedString("memorableWordInfo")),
            topPadding: 0,
            style: .info
        )
    }

    func iconInfoRow(
        tag: String,
        text: String? = nil,
        attributedString: NSAttributedString? = nil,
        icon: String? = nil,
        backgroundColor: UIColor? = UIColor.BaseWhite,
        topPadding: CGFloat = 10,
        bottomPadding: CGFloat = 10,
        style: NotificationStyle = .info,
        url: URL? = nil,
        openLinkExternally: Bool? = false,
        isDismissible: Bool = false,
        isDismissed: (() -> Void)? = nil
    ) -> FormekaModelRow {
        // this row doesn't have a value so the notNil validator will be triggered
        let validators = style == .error ? [Validator.notNil] : []

        return FormekaModelRow(
            tag: tag,
            onBlurValidators: validators,
            cellSetup: { indexPath, _, table in
                guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.hiddenSeparatorLocations = [.top, .bottom]

                let mutableAttributedString: NSMutableAttributedString = {
                    if let attributedString = attributedString {
                        return NSMutableAttributedString(attributedString: attributedString)
                    } else {
                        return NSMutableAttributedString(string: text ?? "")
                    }
                }()

                let paragraphStyle = NSMutableParagraphStyle()
                paragraphStyle.lineSpacing = 4

                mutableAttributedString.addAttribute(
                    .paragraphStyle,
                    value: paragraphStyle,
                    range: NSRange(location: 0, length: mutableAttributedString.string.count)
                )

                cell.bottomConstraint.constant = bottomPadding
                cell.topConstraint.constant = topPadding

                cell.content.attributedText = NSAttributedString(attributedString: mutableAttributedString)
                cell.content.textColor = .ColourDL1
                cell.content.backgroundColor = .clear

                // to avoid overriding existing fonts
                if mutableAttributedString.attributes(at: 0, effectiveRange: nil)
                   .contains(where: { $0.key == .font }) == false {
                    cell.content.font = .BodySmall()
                }

                cell.containerView.backgroundColor = style.background
                cell.containerView.layer.borderColor = style.tint.withAlphaComponent(0.4).cgColor
                cell.containerView.layer.cornerRadius = 4
                cell.icon.image = {
                    if let icon = icon {
                        return UIImage(named: icon)
                    }
                    return style.icon
                }()
                cell.icon.tintColor = style.tint
                cell.crossIcon.isHidden = !isDismissible
                cell.isDismissed = isDismissed

                cell.backgroundColor = backgroundColor

                return cell
            },
            didSelect: { [unowned self] _, _ in
            guard let url = url else { return }

            // open in-app or externally
            if openLinkExternally == true {
                openURLInSafari(url: url)
            } else {
                openURL(url: url)
            }
        }
        )
    }

    func separatorLineRow(style: BottomBorderCell.PaddingStyle) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: BottomBorderCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.paddingStyle = style
            return cell
        })
    }
}

extension FormekaModelRow {
    static func iconInfoRow(
        tag: String,
        text: String? = nil,
        attributedString: NSAttributedString? = nil,
        icon: String? = nil,
        backgroundColor: UIColor? = UIColor.BaseWhite,
        topPadding: CGFloat = 10,
        bottomPadding: CGFloat = 10,
        style: NotificationStyle = .info,
        isDismissible: Bool = false,
        isDismissed: (() -> Void)? = nil
    ) -> FormekaModelRow {
        // this row doesn't have a value so the notNil validator will be triggered
        let validators = style == .error ? [Validator.notNil] : []

        return FormekaModelRow(tag: tag, onBlurValidators: validators, cellSetup: { indexPath, _, table in
            guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.hiddenSeparatorLocations = [.top, .bottom]

            let mutableAttributedString: NSMutableAttributedString = {
                if let attributedString = attributedString {
                    return NSMutableAttributedString(attributedString: attributedString)
                } else {
                    return NSMutableAttributedString(string: text ?? "")
                }
            }()

            let paragraphStyle = NSMutableParagraphStyle()
            paragraphStyle.lineSpacing = 4

            mutableAttributedString.addAttribute(
                .paragraphStyle,
                value: paragraphStyle,
                range: NSRange(location: 0, length: mutableAttributedString.string.count)
            )

            cell.bottomConstraint.constant = bottomPadding
            cell.topConstraint.constant = topPadding

            cell.content.attributedText = NSAttributedString(attributedString: mutableAttributedString)
            cell.content.textColor = .ColourDL1
            cell.content.backgroundColor = .clear

            // to avoid overriding existing fonts
            if mutableAttributedString.attributes(at: 0, effectiveRange: nil).contains(where: { $0.key == .font }) == false {
                cell.content.font = .BodySmall()
            }

            cell.containerView.backgroundColor = style.background
            cell.containerView.layer.borderColor = style.tint.withAlphaComponent(0.4).cgColor
            cell.containerView.layer.cornerRadius = 4
            cell.icon.image = {
                if let icon = icon {
                    return UIImage(named: icon)
                }
                return style.icon
            }()
            cell.icon.tintColor = style.tint
            cell.crossIcon.isHidden = !isDismissible
            cell.isDismissed = isDismissed

            cell.backgroundColor = backgroundColor

            return cell
        })
    }
}
