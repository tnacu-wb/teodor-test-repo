//
//  AccountView+Formeka.swift
//  PremierInn
//
//  Created by Marcello Mascia on 19/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import UIKit

enum AccountsRowType {
    case native
    case weblink

    var iconImage: UIImage? {
        switch self {
        case .native:
            return UIImage(named: "discloseIndicator")
        case .weblink:
            return UIImage(named: "link")
        }
    }
}

extension AccountViewController {
    func tableViewModel(with accountViewModel: AccountViewModel) -> FormekaViewModel {
        var sections = [FormekaModelSection]()
        sections.append(userDetailSection(with: accountViewModel))

        if accountViewModel.userLoggedIn {
            sections.append(personalDetailsSection(with: accountViewModel))
            sections.append(bookingPreferencesSection())
        }

        sections.append(actionsSection(with: accountViewModel.customLinks))

        if accountViewModel.userLoggedIn {
            sections.append(logoutSection())
        }

        return FormekaViewModel(sections: sections)
    }

    // MARK: - Actions

    @objc func loginButtonDidTap() {
        eventHandler?.loginButtonDidTap()
    }

    @objc func registerButtonDidTap() {
        eventHandler?.registerButtonDidTap()
    }

    @objc func logoutButtonDidTap() {
        eventHandler?.logoutButtonDidTap()
    }
}

extension AccountViewController: GDPRInterstitialEventHandler {
    func userDidAccept(sender: UIViewController?) {
        UserDefaults.standard.set(true, forKey: Constants.hasAcceptedGDPRChanges)
        UserDefaults.standard.synchronize()

        sender?.dismiss(animated: true, completion: { [weak self] in
            self?.setNeedsStatusBarAppearanceUpdate()
        })
    }
}

private extension AccountViewController {
    private func separatorLineRow() -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: BottomBorderCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.topGap = 0
            cell.paddingStyle = .paddedGap(left: 16, right: -16)
            return cell
        })
    }

    func userDetailSection(with accountViewModel: AccountViewModel) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        // Logged In user
        if let username = accountViewModel.username, let email = accountViewModel.email {
            // Company info for Business user
            if accountViewModel.business.isBusiness {
                rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
                    guard let cell: ImageWithTextCell = table.dequeueCell(for: indexPath) else { return nil }

                    cell.topLabel.text = PILocalizedString("companyAccount")
                    cell.companyNameLabel.text = accountViewModel.business.companyName

                    return cell
                }))
            }

            rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
                guard let cell: AccountLogoutCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.username.text = username
                cell.username.accessibilityIdentifier = "myAccountName"
                ContentsquareConfig.mask(view: cell.username)
                cell.email.text = email
                cell.email.accessibilityIdentifier = "myAccountEmailId"
                ContentsquareConfig.mask(view: cell.email)

                cell.backgroundColor = .ColourLD1

                return cell
            }))
        } else {
            // Logged Out user
            let contentRow = FormekaModelRow { indexPath, _, table in
                guard let cell: ContentCell = table.dequeueCell(for: indexPath) else { return nil }
                cell.contentLabel.text = PILocalizedString("accountLoginCellTitle")
                cell.contentLabel.font = UIFont.Body()
                cell.contentLabel.textColor = .ColourDL1
                cell.contentLabel.isAccessibilityElement = true
                cell.contentLabelTopConstraint.constant = 16
                cell.contentLabelBottomConstraint.constant = 8

                return cell
            }

            let loginRow = buttonRow(
                title: PILocalizedString("loginButtonTitle"),
                backgroundColor: .Tint1,
                borderColor: .Tint1,
                textColor: .BaseWhite,
                selector: #selector(self.loginButtonDidTap),
                accessibilityId: AccessibilityIdentifiers.Account.loginButton,
                topConstraint: 8,
                bottomConstraint: 8
            )

            let createAccountRow = buttonRow(
                title: PILocalizedString("myAccountRegisterButtonTitle"),
                backgroundColor: .BaseWhite,
                borderColor: .BasePurple,
                textColor: .BasePurple,
                selector: #selector(self.registerButtonDidTap),
                accessibilityId: nil,
                topConstraint: 8,
                bottomConstraint: 16
            )

            rows = [contentRow, loginRow, createAccountRow]
        }

        rows.append(separatorLineRow())

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func buttonRow(
        title: String,
        backgroundColor: UIColor,
        borderColor: UIColor,
        textColor: UIColor,
        selector: Selector,
        accessibilityId: String?,
        topConstraint: CGFloat,
        bottomConstraint: CGFloat
    ) -> FormekaModelRow {
        FormekaModelRow { indexPath, _, table in
            guard let cell: SimpleButtonCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.button.layer.borderColor = borderColor.resolvedColor(with: cell.traitCollection).cgColor
            cell.button.layer.borderWidth = 1
            cell.button.backgroundColor = backgroundColor
            cell.button.layer.cornerRadius = 5
            cell.button.titleLabel?.font = .Button1()
            cell.button.setTitleColor(textColor, for: .normal)
            cell.button.setTitle(title, for: .normal)
            cell.button.removeTarget(nil, action: nil, for: .touchUpInside)
            cell.button.addTarget(self, action: selector, for: .touchUpInside)
            cell.button.isAccessibilityElement = true
            cell.button.accessibilityIdentifier = accessibilityId
            cell.hiddenSeparatorLocations = [.bottom]
            cell.topConstraint.constant = topConstraint
            cell.bottomConstraint.constant = bottomConstraint

            return cell
        }
    }

    func personalDetailsSection(with accountViewModel: AccountViewModel) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        if accountViewModel.business.isBusiness == false {
            rows.append(simpleRow(title: PILocalizedString(
                "userDetailsScreenTitle",
                comment: "User details form: screen title"
            )) { [unowned self] _, _ in
                eventHandler?.myDetailsButtonDidTap()
            })
        }

        if accountViewModel.shouldShowPaymentMethods {
            rows.append(simpleRow(title: PILocalizedString(
                "userDetailsPaymentMethodsTitle",
                comment: "Payment Methods Row Title"
            )) { [unowned self] _, _ in
                eventHandler?.paymentMethodsButtonDidTap()
            })
        }

        rows.append(simpleRow(title: PILocalizedString("changePasswordCellTitle", comment: "")) { [unowned self] _, _ in
            eventHandler?.changePasswordButtonDidTap()
        })

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    func bookingPreferencesSection() -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        if UserSessionManager.sharedInstance.currentUser?.isBusiness == false {
            rows.append(simpleRow(title: PILocalizedString(
                "bookingPreferencesCellTitle",
                comment: ""
            )) { [unowned self] _, _ in
                eventHandler?.bookingPreferencesButtonDidTap()
            })

            rows.append(simpleRow(title: PILocalizedString(
                "newsletterUpdatesCellTitle",
                comment: ""
            )) { [unowned self] _, _ in
                eventHandler?.newsletterUpdatesButtonDidTap()
            })
        }

        rows.append(separatorLineRow())

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    func actionsSection(with customLinks: [CustomAccountLinkViewModel]) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        for customLink in customLinks {
            rows.append(simpleRow(title: customLink.title) { [weak self] _, _ in
                self?.openURLInSafari(url: customLink.url)
            })
        }

        rows.append(simpleRow(
            title: PILocalizedString("faqCellTitle", comment: "FAQ title"),
            rowType: .weblink
        ) { [unowned self] _, _ in
            openFAQExternalLink()
        })
        rows.append(simpleRow(
            title: PILocalizedString("contactUsCellTitle", comment: "Contact us title"),
            rowType: .weblink
        ) { [unowned self] _, _ in
            openContactUsExternalLink()
        })
        rows.append(simpleRow(
            title: PILocalizedString("termsAndConditionCellTitle", comment: "Terms & Conditions title"),
            rowType: .weblink
        ) { [unowned self] _, _ in
            openTermsAndConditionsExternalLink()
        })
        rows.append(simpleRow(
            title: PILocalizedString("privacyPolicyCellTitle", comment: "Privacy policy title"),
            rowType: .weblink
        ) { [unowned self] _, _ in
            openPrivacyPolicyExternalLink()
        })
        rows.append(simpleRow(title: PILocalizedString(
            "gdprDataPolicyRowTitle",
            comment: "GDPR data policy row title"
        )) { [weak self] _, _ in
            guard let controller = self?.technologiesWeUseViewController(withEventHandler: self) else { return }

            self?.navigationController?.pushViewController(controller, animated: true)
        })
        rows.append(simpleRow(title: PILocalizedString("aboutCellTitle", comment: "About title")) { [unowned self] _, _ in
            openAboutPage()
        })

        if BiometricAuthenticationManager.status != .unknown && UserSessionManager.sharedInstance.currentUser != nil {
            let row = FormekaModelRow(cellSetup: { indexPath, _, table in
                guard let cell: SwitchCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.message.textColor = .BasePurple
                cell.message.font = UIFont.Body_Semibold()
                cell.message.text = BiometricAuthenticationManager.biometryTypeAvailable.title
                cell.toggleSwitch.isOn = BiometricAuthenticationManager.status == .enabled ? true : false
                cell.hiddenSeparatorLocations = [.top, .bottom]
                cell.toggled = { value in
                    BiometricAuthenticationManager.status = value ? .enabled : .disabled
                }

                return cell
            })
            rows.append(row)
        }

        if SettingsManager.sharedInstance.allowEmployeeOfferFeature == true && UserDefaults.standard
           .value(forKey: Constants.employeeRatesKey) != nil {
            let row = FormekaModelRow(cellSetup: { indexPath, _, table in
                guard let cell: SwitchCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.message.textColor = .BasePurple
                cell.message.font = UIFont.Body_Semibold()
                cell.message.text = PILocalizedString("employeeRatesTogggleTitle", comment: "Employee Rates toggle title")

                cell.toggleSwitch.isOn = UserDefaults.standard.bool(forKey: Constants.employeeRatesKey)
                cell.hiddenSeparatorLocations = [.top, .bottom]
                cell.toggled = { value in
                    SettingsManager.sharedInstance.enableEmployeeRates = value
                }

                return cell
            })
            rows.append(row)
        }

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    func logoutSection() -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(cellSetup: { [unowned self] indexPath, _, table in
            guard let cell: SimpleButtonCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.backgroundColor = .BaseWhite
            cell.contentView.backgroundColor = .BaseWhite

            cell.button.layer.borderColor = UIColor.Tint1.resolvedColor(with: cell.traitCollection).cgColor
            cell.button.layer.borderWidth = 1
            cell.button.backgroundColor = .Tint1
            cell.button.titleLabel?.font = .Button1()
            cell.button.setTitleColor(.BaseWhite, for: .normal)
            cell.button.layer.cornerRadius = 5
            cell.button.setTitle(PILocalizedString("logoutButtonTitle", comment: ""), for: .normal)
            cell.button.removeTarget(nil, action: nil, for: .touchUpInside)
            cell.button.addTarget(self, action: #selector(logoutButtonDidTap), for: .touchUpInside)
            cell.button.accessibilityIdentifier = AccessibilityIdentifiers.Account.logoutButton
            cell.hiddenSeparatorLocations = [.bottom]

            cell.topConstraint.constant = 30
            cell.bottomConstraint.constant = 50

            return cell
        }))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    func simpleRow(title: String, rowType: AccountsRowType = .native, onSelect: TableRowCellSelection?) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: SimpleActionCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.textLabel?.text = title
            cell.textLabel?.textColor = .BasePurple
            cell.action.font = UIFont.Body_Semibold()
            cell.setAccessoryIcon(icon: rowType.iconImage)
            cell.backgroundColor = .ColourLD1

            return cell
        }, didSelect: onSelect)
    }

    var simpleFooter: FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: 10, viewSetup: { _, table in
            let view: SimpleFooter? = table.headerFooterView()
            view?.contentView.backgroundColor = .ColourLD1
            view?.lineView.backgroundColor = .ColourLD3

            return view
        })
    }
}
