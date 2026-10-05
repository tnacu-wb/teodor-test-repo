//
//  RegisterView+ViewModel.swift
//  PremierInn
//
//  Created by Freddie Parks on 02/01/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import UIKit

extension RegisterViewController {
    var viewModelSections: [FormekaModelSection] {
        var sections: [FormekaModelSection] = []

        // GDPR section
        sections.append(gdprInfoSection(
            text: PILocalizedString("createAccountDataUsageBox"),
            highlight: PILocalizedString("myDetailsUsageBoxHighlightedText"),
            link: PILocalizedString("myDetailsUsageBoxLink")
        ))

        sections.append(primaryDetailsSection())

        if let view = addressSectionView {
            sections.append(view.addressSection(for: self))
        }

        sections.append(termsAndConditionsSection())
        sections.append(marketingSection())
        sections.append(submitSection())

        return sections
    }

    // Sections

    private func primaryDetailsSection() -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(buttonRow(
            tag: RegisterRow.title.rawValue,
            title: PILocalizedString("userDetailsTitleLabel"),
            validators: [.required],
            value: nil
        ) { [unowned self] _, _ in
            presenter?.salutationRowDidTap()
        })

        var firstNameTraits = FormekaTextFieldTraits()
        firstNameTraits.autocapitalizationType = .words
        firstNameTraits.textContentType = .givenName

        rows.append(textFieldRow(
            name: RegisterRow.firstName.rawValue,
            title: PILocalizedString("userDetailsFirstNameLabel"),
            value: nil,
            traits: firstNameTraits,
            inlineValidators: [.name],
            onBlurValidators: [StringLengthValidator(range: 1...30)]
        ))

        var secondNameTraits = FormekaTextFieldTraits()
        secondNameTraits.autocapitalizationType = .words
        secondNameTraits.textContentType = .familyName

        rows.append(textFieldRow(
            name: RegisterRow.lastName.rawValue,
            title: PILocalizedString("userDetailsLastNameLabel"),
            value: nil,
            traits: secondNameTraits,
            inlineValidators: [.name],
            onBlurValidators: [StringLengthValidator(range: 1...30)]
        ))

        var numberTraits = FormekaTextFieldTraits()
        numberTraits.keyboardType = .phonePad
        numberTraits.textContentType = .telephoneNumber

        rows.append(textFieldRow(
            name: RegisterRow.mobileNumber.rawValue,
            title: PILocalizedString("userDetailsContactNumberLabel"),
            value: nil,
            traits: numberTraits,
            inlineValidators: [.phoneNumber],
            onBlurValidators: [.required, StringLengthValidator(range: 6...20)],
            onChange: nil
        ))

        rows.append(emailRow(isBusiness: false))

        rows.append(passwordRow(
            tag: RegisterRow.password.rawValue,
            regexsDict: SettingsManager.sharedInstance.passwordRegexs
        ))

        return FormekaModelSection(
            header: header(title: PILocalizedString("yourNameTitle"), height: 70),
            rows: rows,
            footer: spacerHeaderFooter(height: 13)
        )
    }

    private func passwordInfoRow() -> FormekaModelRow {
        FormekaModelRow(tag: ChangePasswordRow.info.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: ErrorCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.errorLabel.text = PILocalizedString("changePasswordInfoMessage", comment: "")
            cell.errorLabel.font = .BodySmall()
            cell.errorLabel.textColor = .TintD1

            cell.errorImageView.tintColor = .BasePurple

            cell.topConstraint.constant = 6
            cell.leftConstraint.constant = 10
            cell.bottomConstraint.constant = 18
            cell.errorView.layer.borderColor = UIColor.clear.cgColor

            return cell
        })
    }

    func show(error: Error) {
        table.beginUpdates()

        if let indexPath = viewModel?.indexPath(forRowNamed: RegisterRow.error.rawValue) {
            viewModel?.remove(sectionAtIndex: indexPath.section)
            table.deleteSections([indexPath.section], with: .automatic)
        }
        let sectionIndex = addErrorSection(with: error)
        table.insertSections([sectionIndex.section], with: .automatic)

        table.endUpdates()

        table.scrollToRow(at: sectionIndex, at: .top, animated: true)
    }

    private func addErrorSection(with error: Error) -> IndexPath {
        let row = FormekaModelRow(
            tag: RegisterRow.error.rawValue,
            cellSetup: { indexPath, _, table in
                guard let cell: ErrorCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.errorLabel.text = (error as? LocalizedError)?.errorDescription ?? error.localizedDescription
                cell.errorLabel.font = .Body()
                cell.errorLabel.textColor = .Tint8

                cell.errorView.layer.borderColor = UIColor.Tint8.cgColor

                cell.errorImageView.tintColor = .Tint8

                cell.topConstraint.constant = 15
                cell.leftConstraint.constant = 15
                cell.bottomConstraint.constant = 5
                cell.rightConstraint.constant = 15

                cell.hiddenSeparatorLocations = [.bottom]

                return cell
            },
            didSelect: nil
        )

        let section = FormekaModelSection(header: nil, rows: [row], footer: nil)
        let sectionIndex = 0

        viewModel?.add(section: section, index: sectionIndex)

        return IndexPath(row: 0, section: sectionIndex)
    }

    private func termsAndConditionsSection() -> FormekaModelSection {
        let textForTermsAndConditionsField: NSAttributedString = {
            let attributedString = NSMutableAttributedString(
                string: PILocalizedString("createAccountTermsAndConditionsLabel"),
                attributes: [.font: UIFont.BodySmall()]
            )

            attributedString.style(
                text: PILocalizedString("createAccountTermsAndConditionsTextToHighlight"),
                withAttributes: [.foregroundColor: UIColor.BasePurple]
            )

            attributedString.style(
                text: PILocalizedString("createAccountTermsAndConditionsNotAccepted"),
                withAttributes: [.foregroundColor: UIColor.paleRed]
            )

            return attributedString
        }()

        let row = termsAndConditionsRow(
            text: textForTermsAndConditionsField,
            initialValue: termsAndConditionEnabled
        ) { [weak self] enabled in
            self?.termsAndConditionEnabled = enabled
        }

        return FormekaModelSection(header: nil, rows: [row], footer: spacerHeaderFooter(height: 10))
    }

    private func marketingSection() -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil}
            cell.content.text = PILocalizedString("registerMarketingSignupIntro")
            cell.hiddenSeparatorLocations = [.bottom]

            return cell
        }))

        let isOptIn: Bool = isOptIn ?? false
        let row = createToggleSwitchRow(
            tag: RegisterRow.marketingOptIn.rawValue,
            text: "registerMarketingSignupMessage",
            tint: .Tint1,
            separatorLocations: [.top],
            initialValue: isOptIn
        ) { [weak self] isOptIn in
                self?.isOptIn = isOptIn
            }

        rows.append(row)

        return FormekaModelSection(
            header: header(title: PILocalizedString("emailMarketingHeading"), height: 70),
            rows: rows,
            footer: spacerHeaderFooter(height: 13)
        )
    }

    private func submitSection() -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(tag: RegisterRow.submit.rawValue, cellSetup: { [unowned self] indexPath, _, table in
            guard let cell: FormekaSubmitButtonCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.button.backgroundColor = .Tint1
            cell.button.setTitleColor(.BaseWhite, for: .normal)

            cell.button.setTitle(PILocalizedString("registerButtonTitle"), for: .normal)
            cell.button.titleLabel?.font = UIFont.Button1()

            cell.delegate = self
            cell.contentView.backgroundColor = .whiteTwo
            cell.hiddenSeparatorLocations = [.top, .bottom]

            return cell
        }))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    // Actions

    private func salutationAction(indexPath: IndexPath) {
        let viewModel = SalutationsListViewModel(salutations: Constants.CMS.salutations)

        let controller = ListViewController(viewModel: viewModel, invertedColours: true)
        controller.textFieldPlaceholder = PILocalizedString(
            "salutationListPlaceholder",
            comment: "Salutation list placeholder"
        )
        controller.textFieldValue = nil
        controller.shouldDelaySearchRequest = false
        controller.selectedObjectOutput = { [unowned self] sender, salutation in
            sender.dismiss(animated: true, completion: nil)

            guard let salutation = salutation as? String else { return }

            guard let row = self.viewModel?.row(at: indexPath) else { return }
            row.value = salutation

            try? row.validate()

            if var cell = table.cellForRow(at: indexPath) as? FormekaErrorCell {
                cell.errorMessage = row.error?.localizedDescription
            }

            table.reloadData()
        }
        controller.cancelButtonDidTap = { sender in
            sender.dismiss(animated: true, completion: nil)
        }

        present(controller, animated: true, completion: nil)
    }
}

extension RegisterViewController: FormekaSegmentedControlCellDelegate, CompanyNameDelegate {
    func formekaSegmentedControlValueDidChange(cell: FormekaSegmentedControlCell) {
        guard let indexPath = table.indexPath(for: cell) else { return }
        guard let row = viewModel?.row(at: indexPath) else { return }

        switch row.tag {
        case GuestDetailsRow.addressType.rawValue + Constants.bookerSuffix:
            let addressType: AddressType = (cell.segmentedControl.selectedSegmentIndex == 0) ? .home : .commercial

            row.value = addressType

            switch addressType {
            case .commercial:
                let newIndexPath = IndexPath(item: indexPath.row + 1, section: indexPath.section)
                let textFieldRow = textFieldRow(
                    name: RegisterRow.companyName.rawValue,
                    title: PILocalizedString("userDetailsCompanyName", comment: "User details form: company name label"),
                    value: persistedCompanyName,
                    inlineValidators: [.companyName],
                    onBlurValidators: [.required, StringLengthValidator(range: 1...40)]
                )
                viewModel?.add(row: textFieldRow, at: newIndexPath)
                table.insertRows(at: [newIndexPath], with: .automatic)

            default:
                persistedCompanyName = viewModel?.row(named: GuestDetailsRow.companyName.rawValue)?.value as? String
                if let removedIndexPath = viewModel?.remove(rowNamed: RegisterRow.companyName.rawValue) {
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

extension RegisterViewController: FormekaSubmitButtonCellDelegate {
    func submitButtonDidTap(cell: FormekaSubmitButtonCell) {
        presenter?.submitForm(viewModel: viewModel)
    }
}
