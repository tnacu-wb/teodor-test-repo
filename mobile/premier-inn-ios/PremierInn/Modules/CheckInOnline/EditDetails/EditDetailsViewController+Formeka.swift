//
//  EditDetailsViewController+Formeka.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 17.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import UIKit

extension EditDetailsViewController {
    func tableViewModel(with editDetailsModel: EditDetailsModel) -> FormekaViewModel {
        var rows = [FormekaModelRow]()

        switch editDetailsModel.flow {
        case .regCard(let index):

            rows.append(firstNameRow(with: editDetailsModel))
            rows.append(lastNameRow(with: editDetailsModel))
            if index == 0 {
                rows.append(adress1Row(with: editDetailsModel))
                rows.append(adress2Row(with: editDetailsModel))
                rows.append(adress3Row(with: editDetailsModel))
                rows.append(postcodeRow(with: editDetailsModel))
                rows.append(cityRow(with: editDetailsModel))
                rows.append(countryRow(with: editDetailsModel))
            }
            rows.append(dateOfBirthRow(with: editDetailsModel))
            rows.append(nationalityRow(with: editDetailsModel))

            if editDetailsModel.shouldShowIdentificationDocumentsSection {
                rows.append(identificationNumberRow(with: editDetailsModel))
            }
        case .leadGuestTitleNameInfo, .secondGuestTitleNameInfo:
            rows.append(titleRow(with: editDetailsModel))
            rows.append(firstNameRow(with: editDetailsModel))
            rows.append(lastNameRow(with: editDetailsModel))
            rows.append(nationalityRow(with: editDetailsModel))

            if editDetailsModel.shouldShowIdentificationDocumentsSection {
                rows.append(identificationTypeRow(with: editDetailsModel))
                rows.append(identificationNumberRow(with: editDetailsModel))
            }

        case .emailAddress:
            rows.append(emailAddressRow(with: editDetailsModel))

        case .phoneNumber:
            rows.append(phoneNumberRow(with: editDetailsModel))

        case .address:
            rows.append(countryRow(with: editDetailsModel))
            rows.append(postcodeRow(with: editDetailsModel))
            rows.append(adress1Row(with: editDetailsModel))
            rows.append(adress2Row(with: editDetailsModel))
            rows.append(adress3Row(with: editDetailsModel))

        default:
            break
        }
        return FormekaViewModel(sections: [FormekaModelSection(
            header: nil,
            rows: rows,
            footer: nil
        )])
    }
}

private extension EditDetailsViewController {
    func titleRow(with editDetailsModel: EditDetailsModel) -> FormekaModelRow {
        let titleRow = FormekaModelRow(
            tag: Step1Row.salutation.rawValue,
            inlineValidators: [RequiredValidator(customErrorValue: PILocalizedString("userDetailsFormTitleErrorValue"))],
            cellSetup: { [weak self] indexPath, row, table in
            let editDetailsCellModel = EditDetailsCellModel(
                title: PILocalizedString("userDetailsTitleLabel") + "*",
                textFieldText: row.value?.displayName ?? "",
                errorMessage: row.error?.localizedDescription,
                rightTextFieldImage: .arrowDown,
                textFieldIsInteractionEnabled: false
            )
                guard let cell: EditDetailsCell = table.dequeueReusableCell(
                    withIdentifier: EditDetailsCell.reuseIdentifier,
                    for: indexPath
                ) as? EditDetailsCell
                else { return nil }
                cell.configureCell(editDetailsCellModel: editDetailsCellModel)
                cell.delegate = self
                return cell
        },
            didSelect: { [weak self] indexPath, _ in
            self?.eventHandler?.showSalutationView(indexPath: indexPath)
        }
        )
        titleRow.value = editDetailsModel.title
        return titleRow
    }

    private var nameTextFieldTraits: FormekaTextFieldTraits {
        var textFieldTraits = FormekaTextFieldTraits()
        textFieldTraits.autocapitalizationType = .words
        return textFieldTraits
    }

    func firstNameRow(with editDetailsModel: EditDetailsModel) -> FormekaModelRow {
        // DE VALIDATOR
        let emptyValidator = NotEmptyValidator(customErrorValue: PILocalizedString("ciolRegCardFirstNameEmptyError"))
        let minimumLengthValidator = MinimumLengthValidator(
            minimumLength: 1,
            customErrorValue: PILocalizedString("ciolRegCardFirstNameShortError")
        )
        let lengthValidator = LengthValidator(
            range: 2...20,
            customErrorValue: PILocalizedString("ciolRegCardFirstNameLongError")
        )
        let onlyLettersValidator = OnlyLettersValidator(customErrorValue: PILocalizedString("ciolRegCardOnlyLettersError"))
        // UK VALIDATOR
        let nameValidator = NameValidator(customErrorValue: PILocalizedString("userDetailsFormFirstNameErrorValue"))
        let stringLengthValidator = StringLengthValidator(range: 1...30)

        let firstNameRow = FormekaModelRow(
            tag: Step1Row.firstName.rawValue,
            inlineValidators: editDetailsModel.isDERegCard ? [
                emptyValidator,
                onlyLettersValidator
            ] : [nameValidator],
            onBlurValidators: editDetailsModel.isDERegCard ? [
                minimumLengthValidator,
                lengthValidator
            ] : [stringLengthValidator],
            cellSetup: { [weak self] indexPath, row, table in
            let editDetailsCellModel = EditDetailsCellModel(
                title: PILocalizedString("userDetailsFirstNameLabel") + "*",
                textFieldText: row.value?.displayName ?? "",
                errorMessage: row.error?.localizedDescription,
                traits: self?.nameTextFieldTraits
            )
                guard let cell: EditDetailsCell = table.dequeueReusableCell(
                    withIdentifier: EditDetailsCell.reuseIdentifier,
                    for: indexPath
                ) as? EditDetailsCell
                else { return nil }
                cell.configureCell(editDetailsCellModel: editDetailsCellModel)
                cell.delegate = self
                cell.valueChanged = {
                    self?.eventHandler?.updateEditDetailsModel(with: EditDetailsModel(
                        flow: editDetailsModel.flow,
                        firstName: row.value as? String
                    ))
                }
                ContentsquareConfig.mask(view: cell)
                return cell
        }
        )
        firstNameRow.value = editDetailsModel.firstName
        return firstNameRow
    }

    func lastNameRow(with editDetailsModel: EditDetailsModel) -> FormekaModelRow {
        // DE VALIDATOR
        let emptyValidator = NotEmptyValidator(customErrorValue: PILocalizedString("ciolRegCardLastNameEmptyError"))
        let minimumLengthValidator = MinimumLengthValidator(
            minimumLength: 1,
            customErrorValue: PILocalizedString("ciolRegCardLastNameShortError")
        )
        let lengthValidator = LengthValidator(
            range: 2...20,
            customErrorValue: PILocalizedString("ciolRegCardLastNameLongError")
        )
        let onlyLettersValidator = OnlyLettersValidator(customErrorValue: PILocalizedString("ciolRegCardOnlyLettersError"))
        // UK VALIDATOR
        let nameValidator = NameValidator(customErrorValue: PILocalizedString("userDetailsFormFirstNameErrorValue"))
        let stringLengthValidator = StringLengthValidator(range: 1...30)

        let lastNameRow = FormekaModelRow(
            tag: Step1Row.lastName.rawValue,
            inlineValidators: editDetailsModel.isDERegCard ? [
                emptyValidator,
                onlyLettersValidator
            ] : [nameValidator],
            onBlurValidators: editDetailsModel.isDERegCard ? [
                minimumLengthValidator,
                lengthValidator
            ] : [stringLengthValidator],
            cellSetup: { [weak self] indexPath, row, table in
            guard let cell: EditDetailsCell = table.dequeueReusableCell(
                withIdentifier: EditDetailsCell.reuseIdentifier,
                for: indexPath
            ) as? EditDetailsCell
            else { return nil }
            let editDetailsCellModel = EditDetailsCellModel(
                title: PILocalizedString("userDetailsLastNameLabel") + "*",
                textFieldText: row.value?.displayName ?? "",
                errorMessage: row.error?.localizedDescription ?? "",
                traits: self?.nameTextFieldTraits,
                textFieldIsInteractionEnabled: !editDetailsModel
                                                            .isLastNameDisabled,
                isDisabled: editDetailsModel.isLastNameDisabled
            )
            cell.configureCell(editDetailsCellModel: editDetailsCellModel)
            cell.delegate = self
            cell.valueChanged = {
                self?.eventHandler?.updateEditDetailsModel(with: EditDetailsModel(
                    flow: editDetailsModel.flow,
                    lastName: row.value as? String
                ))
            }
            ContentsquareConfig.mask(view: cell)
            return cell
        }
        )
        lastNameRow.value = editDetailsModel.lastName
        return lastNameRow
    }

    func nationalityRow(with editDetailsModel: EditDetailsModel) -> FormekaModelRow {
        let emptyValidator = NotEmptyValidator(customErrorValue: PILocalizedString("ciolRegCardNationalityError"))

        let row = FormekaModelRow(
            tag: Step1Row.nationality.rawValue,
            inlineValidators: [emptyValidator],
            cellSetup: { indexPath, row, table in
            let editDetailsCellModel = EditDetailsCellModel(
                title: PILocalizedString("ciolNationalityLabel") + "*",
                textFieldText: row.value?.displayName ?? "",
                errorMessage: row.error?.localizedDescription,
                rightTextFieldImage: .arrowDown,
                textFieldIsInteractionEnabled: false
            )
                guard let cell: EditDetailsCell = table.dequeueReusableCell(
                    withIdentifier: EditDetailsCell.reuseIdentifier,
                    for: indexPath
                ) as? EditDetailsCell
                else { return nil }

                cell.configureCell(editDetailsCellModel: editDetailsCellModel)
                cell.delegate = self
                return cell
        },
            didSelect: { [weak self] index, _ in
            self?.eventHandler?.showCountriesView(indexPath: index, source: .nationalities)
        }
        )
        row.value = editDetailsModel.country?.nationality
        return row
    }

    func identificationTypeRow(with editDetailsModel: EditDetailsModel) -> FormekaModelRow {
        let row = FormekaModelRow(
            tag: Step1Row.salutation.rawValue,
            cellSetup: { indexPath, row, table in
            let editDetailsCellModel = EditDetailsCellModel(
                title: PILocalizedString("ciolIdentificationType") + "*",
                textFieldText: row.value?.displayName ?? "",
                errorMessage: row.error?.localizedDescription,
                rightTextFieldImage: .arrowDown,
                textFieldIsInteractionEnabled: false
            )
                guard let cell: EditDetailsCell = table.dequeueReusableCell(
                    withIdentifier: EditDetailsCell.reuseIdentifier,
                    for: indexPath
                ) as? EditDetailsCell
                else { return nil }
                cell.configureCell(editDetailsCellModel: editDetailsCellModel)
                cell.delegate = self
                return cell
        }
        )
        row.value = PILocalizedString("ciolPassportLabel")
        return row
    }

    func identificationNumberRow(with editDetailsModel: EditDetailsModel) -> FormekaModelRow {
        let emptyValidator = NotEmptyValidator(customErrorValue: PILocalizedString("ciolRegCardPassportError"))
        let minimumLengthValidator = MinimumLengthValidator(
            minimumLength: 1,
            customErrorValue: PILocalizedString("ciolRegCardPassportShortError")
        )
        let lengthValidator = LengthValidator(
            range: 2...20,
            customErrorValue: PILocalizedString("ciolRegCardPassportLongError")
        )
        let passportValidator =
            GuestPassportValidator(customErrorValue: PILocalizedString("ciolRegCardPassportInvalidError"))

        let title = PILocalizedString(editDetailsModel.isDERegCard ? "ciolPassportNumber" : "ciolIdentificationNumber") + "*"

        let row = FormekaModelRow(
            tag: Step1Row.passport.rawValue,
            inlineValidators: [emptyValidator, passportValidator],
            onBlurValidators: [minimumLengthValidator, lengthValidator],
            cellSetup: { [weak self] indexPath, row, table in
            let editDetailsCellModel = EditDetailsCellModel(
                title: title,
                textFieldText: row.value?.displayName ?? "",
                errorMessage: row.error?.localizedDescription,
                traits: FormekaTextFieldTraits()
            )
                guard let cell: EditDetailsCell = table.dequeueReusableCell(
                    withIdentifier: EditDetailsCell.reuseIdentifier,
                    for: indexPath
                ) as? EditDetailsCell
                else { return nil }
                cell.configureCell(editDetailsCellModel: editDetailsCellModel)
                cell.delegate = self
                cell.valueChanged = {
                    let passportNumber = (row.value as? String)?.replacingOccurrences(of: " ", with: "")
                    self?.eventHandler?.updateEditDetailsModel(with: EditDetailsModel(
                        flow: editDetailsModel.flow,
                        passportNumber: passportNumber
                    ))
                }
                ContentsquareConfig.mask(view: cell)
                return cell
        }
        )
        row.value = editDetailsModel.passportNumber
        return row
    }

    func dateOfBirthRow(with editDetailsModel: EditDetailsModel) -> FormekaModelRow {
        let emptyValidator = NotEmptyDateValidator()
        let leadGuestAgeValidator = GuestAgeValidator(isLeadGuest: editDetailsModel.isLeadGuest)

        let row = FormekaModelRow(
            tag: Step1Row.dateOfBirth.rawValue,
            inlineValidators: [emptyValidator, leadGuestAgeValidator],
            cellSetup: { indexPath, row, table in
            guard let cell: GuestEditDateCell = table.dequeueReusableCell(
                withIdentifier: GuestEditDateCell.reuseIdentifier,
                for: indexPath
            ) as? GuestEditDateCell
            else { return UITableViewCell() }
            let dobCellModel = DateOfBirthCellModel(date: row.value as? Date, error: row.error?.localizedDescription)
            cell.configure(model: dobCellModel)
            cell.delegate = self
            cell.valueChanged = { [weak self] _ in
                self?.eventHandler?.updateEditDetailsModel(with: EditDetailsModel(
                    flow: editDetailsModel.flow,
                    dateOfBirth: row.value as? Date
                ))
            }
            ContentsquareConfig.mask(view: cell)
            return cell
        }
        )
        row.value = editDetailsModel.dateOfBirth
        return row
    }
}

private extension EditDetailsViewController {
    func emailAddressRow(with editDetailsModel: EditDetailsModel) -> FormekaModelRow {
        var emailTraits = FormekaTextFieldTraits()
        emailTraits.keyboardType = .default
        emailTraits.textContentType = .none

        let row = FormekaModelRow(
            tag: Step1Row.emailAddress.rawValue,
            onBlurValidators: [.email],
            cellSetup: { [weak self] indexPath, row, table in
                let editDetailsCellModel = EditDetailsCellModel(
                    title: PILocalizedString("userDetailsEmailLabel"),
                    textFieldText: row.value?.displayName ?? "",
                    errorMessage: row.error?.localizedDescription,
                    traits: emailTraits
                )

                guard let cell: EditDetailsCell = table.dequeueReusableCell(
                    withIdentifier: EditDetailsCell.reuseIdentifier,
                    for: indexPath
                ) as? EditDetailsCell else {
                    return nil
                }

                cell.configureCell(editDetailsCellModel: editDetailsCellModel)
                cell.textField.text = editDetailsModel.emailAddress

                cell.valueChanged = { [weak self] in
                    let model = EditDetailsModel(flow: editDetailsModel.flow, emailAddress: row.value as? String)
                    self?.eventHandler?.updateEditDetailsModel(with: model)
                }

                cell.delegate = self
                return cell
            },
        )

        row.value = editDetailsModel.emailAddress
        return row
    }

    func phoneNumberRow(with editDetailsModel: EditDetailsModel) -> FormekaModelRow {
        let lengthValidator = StringLengthValidator(range: 6...20)
        var numberTraits = FormekaTextFieldTraits()
        numberTraits.keyboardType = .phonePad
        numberTraits.textContentType = .telephoneNumber

        let row = FormekaModelRow(
            tag: Step1Row.contactNumber.rawValue,
            inlineValidators: [.phoneNumber],
            onBlurValidators: [.required, lengthValidator],
            cellSetup: { [weak self] indexPath, row, table in
                let editDetailsCellModel = EditDetailsCellModel(
                    title: PILocalizedString("editDetailsPhoneNumberLabel"),
                    textFieldText: row.value?.displayName ?? "",
                    errorMessage: row.error?.localizedDescription,
                    traits: numberTraits
                )

                guard let cell: EditDetailsCell = table.dequeueReusableCell(
                    withIdentifier: EditDetailsCell.reuseIdentifier,
                    for: indexPath
                ) as? EditDetailsCell else {
                    return nil
                }

                cell.configureCell(editDetailsCellModel: editDetailsCellModel)
                cell.textField.text = editDetailsModel.phoneNumber

                cell.valueChanged = { [weak self] in
                    let model = EditDetailsModel(flow: editDetailsModel.flow, phoneNumber: row.value as? String)
                    self?.eventHandler?.updateEditDetailsModel(with: model)
                }

                cell.delegate = self
                return cell
            },
        )

        row.value = editDetailsModel.phoneNumber
        return row
    }
}
