//
//  AdditionalGuestFormView+Formeka.swift
//  PremierInn
//
//  Created by Freddie Parks on 19/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import UIKit

enum AdditionalGuestFormRow: String {
    case title
    case firstName
    case lastName
    case email
    case nationality
}

extension AdditionalGuestFormView {
    func viewModelSections(guest: AdditionalGuest?, content: AdditionalGuestFormContent) -> [FormekaModelSection] {
        var sections = [FormekaModelSection]()

        sections.append(guestDetailsSection(guest: guest))
        sections.append(saveGuestSection(title: content.submitTitle))

        return sections
    }

    private func guestDetailsSection(guest: AdditionalGuest?) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(buttonRow(
            tag: AdditionalGuestFormRow.title.rawValue,
            title: PILocalizedString("userDetailsTitleLabel", comment: "User details form: user's title label"),
            validators: [.required],
            value: guest?.title
        ) { [unowned self] _, _ in
            eventHandler?.salutationRowDidTap()
        })

        var nameTraits = FormekaTextFieldTraits()
        nameTraits.autocapitalizationType = .words

        rows.append(textFieldRow(
            name: AdditionalGuestFormRow.firstName.rawValue,
            title: PILocalizedString("userDetailsFirstNameLabel", comment: "User details form: user's first name label"),
            value: guest?.firstName,
            traits: nameTraits,
            inlineValidators: [.name],
            onBlurValidators: [StringLengthValidator(range: 1...30)],
            onChange: nil
        ))

        rows.append(textFieldRow(
            name: AdditionalGuestFormRow.lastName.rawValue,
            title: PILocalizedString("userDetailsLastNameLabel", comment: "User details form: user's last name label"),
            value: guest?.lastName,
            traits: nameTraits,
            inlineValidators: [.name],
            onBlurValidators: [StringLengthValidator(range: 1...30)],
            onChange: nil
        ))

        var emailTraits = FormekaTextFieldTraits()
        emailTraits.autocapitalizationType = .none
        emailTraits.keyboardType = .emailAddress

        rows.append(textFieldRow(
            name: AdditionalGuestFormRow.email.rawValue,
            title: PILocalizedString("userDetailsEmailLabel", comment: "User details form: user's last name label"),
            value: guest?.email,
            traits: emailTraits,
            onBlurValidators: [.email, .required],
            onChange: nil
        ))

        let country = Country.countriesList.first { $0.code == guest?.nationality }

        rows.append(buttonRow(
            tag: AdditionalGuestFormRow.nationality.rawValue,
            title: PILocalizedString("countryRowTitle", comment: "Country row title"),
            validators: [CountryValidator()],
            value: country
        ) { [unowned self] _, _ in
            eventHandler?.countryRowDidTap()
        })

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func saveGuestSection(title: String) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(tag: "", cellSetup: { [unowned self] indexPath, _, table in
            guard let cell: FormekaSubmitButtonCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self
            cell.backgroundColor = .clear
            cell.contentView.backgroundColor = .clear

            cell.button.backgroundColor = .BasePurple
            cell.button.setTitleColor(.white, for: .normal)
            cell.button.setTitle(title, for: .normal)

            cell.button.titleLabel?.font = .Button1()

            cell.button.layer.cornerRadius = 5
            cell.button.layer.borderColor = UIColor.BasePurple.cgColor
            cell.button.layer.borderWidth = 1

            cell.hiddenSeparatorLocations = [.top, .bottom]

            return cell
        }))

        return FormekaModelSection(header: spacerHeaderFooter(height: 10), rows: rows, footer: nil)
    }
}
