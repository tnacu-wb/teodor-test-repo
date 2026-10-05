//
//  LeadGuestDataSource.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 31.03.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Formeka
import UIKit

protocol LeadGuestDataSource: AnyObject {
    var eventHandler: EditDetailsEventHandler? { get set }

    func adress1Row(with editDetailsModel: EditDetailsModel) -> FormekaModelRow
}

extension LeadGuestDataSource where Self: FormekaTextFieldCellDelegate {
    func adress1Row(with editDetailsModel: EditDetailsModel) -> FormekaModelRow {
        // DE VALIDATOR
        let emptyValidator = NotEmptyValidator(customErrorValue: PILocalizedString("guestAddress1MissingError"))
        let guestAddressValidator = GuestAddressValidator(customErrorValue: PILocalizedString("guestAddress1InvalidError"))
        let lengthValidator = LengthValidator(range: 1...35, customErrorValue: PILocalizedString("guestAddress1LengthError"))


        let address1Row = FormekaModelRow(
            tag: Step1Row.address1.rawValue,
            inlineValidators: [emptyValidator],
            onBlurValidators: [lengthValidator, guestAddressValidator],
            cellSetup: { [weak self] indexPath, row, table in
            let editDetailsCellModel = EditDetailsCellModel(
                title: PILocalizedString("guestAddress1"),
                textFieldText: row.value?.displayName ?? "",
                errorMessage: row.error?.localizedDescription
            )
            guard let cell: EditDetailsCell = table.dequeueReusableCell(
                withIdentifier: EditDetailsCell.reuseIdentifier,
                for: indexPath
            ) as? EditDetailsCell
            else { return nil }


            cell.configureCell(editDetailsCellModel: editDetailsCellModel)
            cell.delegate = self
            cell.valueChanged = {
                var address = self?.eventHandler?.address
                address?.line1 = row.value as? String
                self?.eventHandler?.updateEditDetailsModel(with: EditDetailsModel(
                    flow: editDetailsModel.flow,
                    address: address
                ))
            }
            return cell
        }
        )
        address1Row.value = editDetailsModel.address?.line1
        return address1Row
    }

    func adress2Row(with editDetailsModel: EditDetailsModel) -> FormekaModelRow {
        let address2Row = FormekaModelRow(
            tag: Step1Row.address2.rawValue,
            cellSetup: { [weak self] indexPath, row, table in
            let editDetailsCellModel = EditDetailsCellModel(
                title: PILocalizedString("guestAddress2"),
                textFieldText: row.value?.displayName ?? "",
                errorMessage: row.error?.localizedDescription
            )
            guard let cell: EditDetailsCell = table.dequeueReusableCell(
                withIdentifier: EditDetailsCell.reuseIdentifier,
                for: indexPath
            ) as? EditDetailsCell
            else { return nil }


            cell.configureCell(editDetailsCellModel: editDetailsCellModel)
            cell.delegate = self
            cell.valueChanged = {
                var address = self?.eventHandler?.address
                address?.line2 = row.value as? String
                self?.eventHandler?.updateEditDetailsModel(with: EditDetailsModel(
                    flow: editDetailsModel.flow,
                    address: address
                ))
            }
            return cell
        }
        )
        address2Row.value = editDetailsModel.address?.line2
        return address2Row
    }

    func adress3Row(with editDetailsModel: EditDetailsModel) -> FormekaModelRow {
        let address3Row = FormekaModelRow(
            tag: Step1Row.address3.rawValue,
            cellSetup: { [weak self] indexPath, row, table in
            let editDetailsCellModel = EditDetailsCellModel(
                title: PILocalizedString("guestAddress3"),
                textFieldText: row.value?.displayName ?? "",
                errorMessage: row.error?.localizedDescription
            )
            guard let cell: EditDetailsCell = table.dequeueReusableCell(
                withIdentifier: EditDetailsCell.reuseIdentifier,
                for: indexPath
            ) as? EditDetailsCell
            else { return nil }


            cell.configureCell(editDetailsCellModel: editDetailsCellModel)
            cell.delegate = self
            cell.valueChanged = {
                var address = self?.eventHandler?.address
                address?.line3 = row.value as? String
                self?.eventHandler?.updateEditDetailsModel(with: EditDetailsModel(
                    flow: editDetailsModel.flow,
                    address: address
                ))
            }
            return cell
        }
        )
        address3Row.value = editDetailsModel.address?.line3
        return address3Row
    }

    func postcodeRow(with editDetailsModel: EditDetailsModel) -> FormekaModelRow {
        let isGreatBritain = editDetailsModel.address?.country == .greatBritain
        let isGerman = editDetailsModel.address?.country == .germany

        let postcodeRow = FormekaModelRow(
            tag: CountryActionableRow.postCode.rawValue,
            onBlurValidators: [isGreatBritain ? .ukPostCode : isGerman ? .dePostCode :
                                          .required],
            cellSetup: { [weak self] indexPath, row, table in
                let editDetailsCellModel = EditDetailsCellModel(
                    title: PILocalizedString("guestAddressPostcode") + "*",
                    textFieldText: row.value?.displayName ?? "",
                    errorMessage: row.error?.localizedDescription,
                    showActionButton: editDetailsModel.showFindAddressButton
                )

                guard let cell: EditDetailsCell = table.dequeueReusableCell(
                    withIdentifier: EditDetailsCell.reuseIdentifier,
                    for: indexPath
                ) as? EditDetailsCell else {
                    return nil
                }

                cell.configureCell(editDetailsCellModel: editDetailsCellModel)
                cell.delegate = self

                cell.valueChanged = {
                    var address = self?.eventHandler?.address
                    address?.postcode = row.value as? String
                    self?.eventHandler?.updateEditDetailsModel(with: EditDetailsModel(
                        flow: editDetailsModel.flow,
                        address: address
                    )
                    )
                }

                cell.didTapActionButton = { [weak self] in
                    let currentPostcode = row.value as? String
                    self?.eventHandler?.didTapPostcodeSearch(with: currentPostcode)
                }

                return cell
            }
        )
        postcodeRow.value = editDetailsModel.address?.postcode
        return postcodeRow
    }

    func cityRow(with editDetailsModel: EditDetailsModel) -> FormekaModelRow {
        let emptyValidator = NotEmptyValidator(customErrorValue: PILocalizedString("guestAddressCityMissingError"))
        let lengthValidator = LengthValidator(
            range: 1...35,
            customErrorValue: PILocalizedString("guestAddressCityLengthError")
        )
        let cityValidator = GuestCityValidator(customErrorValue: PILocalizedString("guestAddressCityValidError"))
        let cityRow = FormekaModelRow(
            tag: Step1Row.city.rawValue,
            inlineValidators: [emptyValidator],
            onBlurValidators: [lengthValidator, cityValidator],
            cellSetup: { [weak self] indexPath, row, table in
            let editDetailsCellModel = EditDetailsCellModel(
                title: PILocalizedString("guestAddressCity") + "*",
                textFieldText: row.value?.displayName ?? "",
                errorMessage: row.error?.localizedDescription
            )
            guard let cell: EditDetailsCell = table.dequeueReusableCell(
                withIdentifier: EditDetailsCell.reuseIdentifier,
                for: indexPath
            ) as? EditDetailsCell
            else { return nil }


            cell.configureCell(editDetailsCellModel: editDetailsCellModel)
            cell.delegate = self
            cell.valueChanged = {
                var address = self?.eventHandler?.address
                address?.city = row.value as? String
                self?.eventHandler?.updateEditDetailsModel(with: EditDetailsModel(
                    flow: editDetailsModel.flow,
                    address: address
                ))
            }
            return cell
        }
        )
        cityRow.value = editDetailsModel.address?.city
        return cityRow
    }

    func countryRow(with editDetailsModel: EditDetailsModel) -> FormekaModelRow {
        let emptyValidator = NotEmptyValidator(customErrorValue: PILocalizedString("guestAddressCountryMissingError"))

        let row = FormekaModelRow(
            tag: Step1Row.country.rawValue,
            inlineValidators: [emptyValidator],
            cellSetup: { indexPath, row, table in
            let editDetailsCellModel = EditDetailsCellModel(
                title: PILocalizedString("guestAddressCountry") + "*",
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
            self?.eventHandler?.showCountriesView(indexPath: index, source: .countries)
        }
        )
        row.value = editDetailsModel.address?.country?.displayName
        return row
    }
}

extension EditDetailsViewController: LeadGuestDataSource {}
