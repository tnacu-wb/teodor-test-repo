//
//  CheckInOnlineGuestDetailsView+Formeka.swift
//  PremierInn
//
//  Created by Freddie Parks on 18/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import UIKit

enum CheckInOnlineGuestRows: String {
    case title
    case firstName
    case lastName
    case email
    case contactNumber
    case nationality
    case passportNumber
    case nextDestination
    case additionalGuestTitle
    case additionalGuestFirstName
    case additionalGuestLastName
    case carRegistration
    case imigrationInfo
    case useBookerAddress
    case addressSummary
    case submitButton
}

extension CheckInOnlineGuestDetailsView {
    func viewModelSection(with model: CheckInOnlineGuestDetailsViewModel) -> [FormekaModelSection] {
        var sections = [FormekaModelSection]()

        sections.append(primaryDataSection(with: model))
        sections.append(addressSummarySection(with: model))

        if model.useBookerAddress == false, let addressDetailSection = addressDetailSection(with: model) {
            sections.append(addressDetailSection)
        }

        if model.shouldShowAdditionalGuestRows {
            sections.append(additionalGuestDataSection(with: model))
        }

        sections.append(carRegistrationSection(with: model))
        sections.append(submitSection)

        return sections
    }

    private func primaryDataSection(with formViewModel: CheckInOnlineGuestDetailsViewModel) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(guestPermissionInfoRow)
        rows.append(sectionHeadingRow(with: PILocalizedString("userDetailsOneAdultLeadGuestTitle", comment: "")))

        let localisedTitle = try? Title(title: formViewModel.titleRowModel.value as? String ?? "")
        rows.append(buttonRow(
            tag: CheckInOnlineGuestRows.title.rawValue,
            title: formViewModel.titleRowModel.title,
            validators: nil,
            value: localisedTitle?.localised ?? formViewModel.titleRowModel.value as? String,
            action: { [weak self] _, _ in
                                self?.eventHandler?.titleRowDidTap()
                            }
        ))
        rows.append(textFieldRow(
            name: CheckInOnlineGuestRows.firstName.rawValue,
            title: formViewModel.firstNameRowModel.title,
            value: formViewModel.firstNameRowModel.value as? String,
            inlineValidators: [RequiredValidator()]
        ))
        rows.append(textFieldRow(
            name: CheckInOnlineGuestRows.lastName.rawValue,
            title: formViewModel.lastNameRowModel.title,
            value: formViewModel.lastNameRowModel.value as? String,
            inlineValidators: [RequiredValidator()]
        ))
        rows.append(emailRow(isBusiness: false, value: formViewModel.emailRowModel.value as? String))

        var numberTraits = FormekaTextFieldTraits()
        numberTraits.keyboardType = .phonePad

        rows.append(textFieldRow(
            name: CheckInOnlineGuestRows.contactNumber.rawValue,
            title: formViewModel.contactNumberRowModel.title,
            value: formViewModel.contactNumberRowModel.value as? String,
            traits: numberTraits,
            inlineValidators: [.phoneNumber],
            onBlurValidators: [.required, StringLengthValidator(range: 6...20)]
        ))

        rows.append(buttonRow(
            tag: CheckInOnlineGuestRows.nationality.rawValue,
            title: formViewModel.nationalityRowModel.title,
            validators: nil,
            value: formViewModel.nationalityRowModel.value as? Country ?? Country.greatBritain,
            action: { [weak self] _, _ in
                                self?.eventHandler?.nationalityRowDidTap()
                            }
        )
        )

        if formViewModel.shouldShowPassportAndNextDestinationRows, let passportModel = formViewModel.passportNumberRowModel,
           let nextDestinationModel = formViewModel.nextDestinationRowModel {
            rows.append(textFieldRow(
                name: CheckInOnlineGuestRows.passportNumber.rawValue,
                title: passportModel.title,
                value: passportModel.value as? String,
                inlineValidators: [RequiredValidator()]
            ))
            rows.append(textFieldRow(
                name: CheckInOnlineGuestRows.nextDestination.rawValue,
                title: nextDestinationModel.title,
                value: nextDestinationModel.value as? String,
                inlineValidators: [RequiredValidator()]
            ))
            rows.append(imigrationInfoRow)
        }

        return FormekaModelSection(
            header: nil,
            rows: rows,
            footer: simpleFooterView(backgroundColor: .whiteTwo, showLine: true)
        )
    }

    private func addressSummarySection(with formViewModel: CheckInOnlineGuestDetailsViewModel) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(sectionHeadingRow(
            with: PILocalizedString("userDetailsAddressSectionTitle", comment: ""),
            isAddressSection: true
        ))

        let useBookerAddressSwitchRow = FormekaModelRow(
            tag: CheckInOnlineGuestRows.useBookerAddress.rawValue,
            cellSetup: { indexPath, _, table in
            guard let cell: SwitchCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.message.text = PILocalizedString("userDetailsSameAsBookersAddress", comment: "")
            cell.toggleSwitch.isOn = self.viewModel?.row(named: CheckInOnlineGuestRows.useBookerAddress.rawValue)?
                .value as? Bool ?? formViewModel.useBookerAddress
            cell.toggleSwitch.onTintColor = .Tint1
            cell.toggled = { on in
                self.viewModel?.row(named: CheckInOnlineGuestRows.useBookerAddress.rawValue)?.value = on
                if on {
                    self.useBookerAddress(with: formViewModel)
                } else {
                    self.showAddressInputs()
                }
            }

            cell.hiddenSeparatorLocations = [.bottom]
            ContentsquareConfig.mask(view: cell)
            return cell
        }
        )
        useBookerAddressSwitchRow.value = formViewModel.useBookerAddress

        rows.append(useBookerAddressSwitchRow)

        if formViewModel.useBookerAddress {
            rows.append(bookerAddressSummaryRow(with: formViewModel.bookerAddressSummary))
        }

        return FormekaModelSection(
            header: nil,
            rows: rows,
            footer: nil
        )
    }

    private func addressDetailSection(with formViewModel: CheckInOnlineGuestDetailsViewModel) -> FormekaModelSection? {
        addressSectionView = {
            let addressRequirements = AddressSectionRequirements(
                address: formViewModel.address,
                storedAddress: nil,
                shouldShowAddressSwitch: false,
                useStoredAddressSwitchDescription: nil,
                addressSwitchInitialState: false,
                shouldShowAddressForm: true,
                shouldShowHeader: false,
                shouldShowFooter: false,
                shouldShowAddressSummary: true
            )
            let view = AddressSectionRouter.buildSection(with: addressRequirements)
            view.parentFormekaViewController = self
            view.addressSectionSwitchDelegate = nil

            return view
        }()

        guard let addressSectionView = addressSectionView else { return nil }

        return addressSectionView.addressSection(for: self)
    }

    private func additionalGuestDataSection(with formViewModel: CheckInOnlineGuestDetailsViewModel) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(sectionHeadingRow(with: PILocalizedString("userDetailsSecondAdultTitle", comment: "")))

        rows.append(buttonRow(
            tag: CheckInOnlineGuestRows.additionalGuestTitle.rawValue,
            title: formViewModel.additionalGuestTitleRowModel.title,
            validators: nil,
            value: formViewModel.additionalGuestTitleRowModel.value as? String,
            action: { [weak self] _, _ in
                                let viewModel = SalutationsListViewModel(salutations: Constants.CMS.salutations)

                                let controller = ListViewController(viewModel: viewModel, invertedColours: true)
                                controller.textFieldPlaceholder = PILocalizedString(
                                    "salutationListPlaceholder",
                                    comment: "Salutation list placeholder"
                                )
                                controller.textFieldValue = nil
                                controller.shouldDelaySearchRequest = false
                                controller.selectedObjectOutput = { [weak self] sender, salutation in
                                    sender.dismiss(animated: true)

                                    guard let salutation = salutation as? String else { return }
                                    let localisedTitle = try? Title(title: salutation)
                                    self?.viewModel?.row(named: CheckInOnlineGuestRows.additionalGuestTitle.rawValue)?
                                        .value = localisedTitle?.localised ?? salutation

                                    self?.table.reloadData()
                                }
                                controller.cancelButtonDidTap = { sender in
                                    sender.dismiss(animated: true)
                                }

                                self?.present(controller, animated: true)
        }
        ))
        rows.append(textFieldRow(
            name: CheckInOnlineGuestRows.additionalGuestFirstName.rawValue,
            title: formViewModel.additionalGuestFirstNameRowModel.title,
            value: formViewModel.additionalGuestFirstNameRowModel.value as? String,
            inlineValidators: [RequiredValidator()]
        ))
        rows.append(textFieldRow(
            name: CheckInOnlineGuestRows.additionalGuestLastName.rawValue,
            title: formViewModel.additionalGuestLastNameRowModel.title,
            value: formViewModel.additionalGuestLastNameRowModel.value as? String,
            inlineValidators: [RequiredValidator()]
        ))

        return FormekaModelSection(
            header: simpleFooterView(backgroundColor: .whiteTwo, showLine: true),
            rows: rows,
            footer: nil
        )
    }

    private func carRegistrationSection(with formViewModel: CheckInOnlineGuestDetailsViewModel) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(sectionHeadingRow(with: formViewModel.carRegistrationSectionTitle))

        var traits = FormekaTextFieldTraits()
        traits.keyboardType = .default
        traits.autocapitalizationType = .allCharacters
        traits.placeholder = formViewModel.carRegistrationRowModel.placeholder

        rows.append(textFieldRow(
            name: CheckInOnlineGuestRows.carRegistration.rawValue,
            title: formViewModel.carRegistrationRowModel.title,
            value: formViewModel.carRegistrationRowModel.value as? String,
            traits: traits
        ))

        rows.append(infoRow(with: "", message: formViewModel.carRegistrationSectionDescription))

        return FormekaModelSection(
            header: simpleFooterView(backgroundColor: .whiteTwo, showLine: true),
            rows: rows,
            footer: simpleFooterView(backgroundColor: .whiteTwo, showLine: true)
        )
    }

    private var submitSection: FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(
            tag: CheckInOnlineGuestRows.submitButton.rawValue,
            cellSetup: { [unowned self] indexPath, _, table in
            guard let cell: FormekaSubmitButtonCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self
            cell.backgroundColor = .whiteTwo
            cell.contentView.backgroundColor = .whiteTwo

            cell.button.backgroundColor = .BasePurple
            cell.button.setTitleColor(.white, for: .normal)
            cell.button.setTitle(PILocalizedString("Done", comment: ""), for: .normal)

            cell.button.titleLabel?.font = UIFont.Heading3_Semibold()

            cell.hiddenSeparatorLocations = [.top, .bottom]

            return cell
        }
        ))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func simpleFooterView(
        backgroundColor: UIColor,
        showLine: Bool = true,
        withCustomHeight height: CGFloat = 10
    ) -> FormekaModelHeaderFooter? {
        FormekaModelHeaderFooter(height: height, viewSetup: { _, table in
            let view: SimpleFooter? = table.headerFooterView()
            view?.contentView.backgroundColor = backgroundColor
            view?.lineView.backgroundColor = showLine ? UIColor.TintL2 : nil

            return view
        })
    }

    private func bookerAddressSummaryRow(with summary: String) -> FormekaModelRow {
        FormekaModelRow(tag: CheckInOnlineGuestRows.addressSummary.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return UITableViewCell() }

            cell.content.text = summary
            cell.content.font = .Body()
            cell.messageTopConstraint.constant = 5

            cell.hiddenSeparatorLocations = [.top]

            return cell
        })
    }

    private var guestPermissionInfoRow: FormekaModelRow {
        infoRow(with: "", message: PILocalizedString("sharingGuestDetailsPrivacyMessage", comment: ""))
    }

    private var imigrationInfoRow: FormekaModelRow {
        infoRow(
            with: CheckInOnlineGuestRows.imigrationInfo.rawValue,
            message: PILocalizedString("checkInOnlineImigrationInfo", comment: "")
        )
    }

    private func infoRow(
        with tag: String,
        message: String,
        topPadding: CGFloat = 12,
        bottomPadding: CGFloat = 14
    ) -> FormekaModelRow {
        FormekaModelRow(
            tag: tag,
            cellSetup: { indexPath, _, table in
                                guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath)
                                    else { return UITableViewCell() }

                                cell.backgroundColor = .clear
                                cell.content.text = message
                                cell.topConstraint.constant = topPadding
                                cell.bottomConstraint.constant = bottomPadding

                                cell.hiddenSeparatorLocations = [.top, .bottom]

                                return cell
        }
        )
    }

    private func sectionHeadingRow(with heading: String, isAddressSection: Bool = false) -> FormekaModelRow {
        FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return UITableViewCell() }

            cell.content.text = heading
            cell.content.textColor = .TintD1
            cell.content.font = UIFont.Heading3_Semibold()
            cell.contentView.backgroundColor = .white

            cell.messageTopConstraint.constant = 20

            cell.hiddenSeparatorLocations = isAddressSection ? [.top, .bottom] : [.top]

            return cell
        })
    }

    private var formOutput: CheckInOnlineGuestDetailsViewOutput? {
        do {
            try viewModel?.validate()
            guard let values = viewModel?.values else { return nil }
            guard let title = values[CheckInOnlineGuestRows.title.rawValue] as? String,
                  let firstName = values[CheckInOnlineGuestRows.firstName.rawValue] as? String,
                  let lastName = values[CheckInOnlineGuestRows.lastName.rawValue] as? String,
                  let email = values[CheckInOnlineGuestRows.email.rawValue] as? String,
                  let contactNumber = values[CheckInOnlineGuestRows.contactNumber.rawValue] as? String,
                  let country = values[CheckInOnlineGuestRows.nationality.rawValue] as? Country else { return nil }

            let useBookerAddress: Bool = {
                viewModel?.row(named: CheckInOnlineGuestRows.useBookerAddress.rawValue)?.value as? Bool ?? true
            }()

            if !useBookerAddress, viewModel?.row(named: CountryActionableRow.addressLine1.rawValue) == nil {
                // user must enter an address!!!
                showAlertWith(
                    title: PILocalizedString("Address required", comment: ""),
                    message: PILocalizedString(
                        "Please complete the address section using the postcode lookup or manually entering an address",
                        comment: ""
                    )
                )
                return nil
            }

            return CheckInOnlineGuestDetailsViewOutput(
                title: title,
                firstName: firstName,
                lastName: lastName,
                email: email,
                contactNumber: contactNumber,
                nationality: country,
                passportNumber: values[CheckInOnlineGuestRows.passportNumber.rawValue] as? String,
                nextDestination: values[CheckInOnlineGuestRows.nextDestination.rawValue] as? String,
                carRegistration: values[CheckInOnlineGuestRows.carRegistration.rawValue] as? String,
                useBookerAddress: useBookerAddress,
                addressLine1: values[CountryActionableRow.addressLine1.rawValue] as? String,
                addressLine2: values[CountryActionableRow.addressLine2.rawValue] as? String,
                addressLine3: values[CountryActionableRow.addressLine3.rawValue] as? String,
                country: values[CountryActionableRow.country.rawValue] as? Country,
                postcode: values[CountryActionableRow.postCode.rawValue] as? String,
                additionalGuestTitle: values[CheckInOnlineGuestRows.additionalGuestTitle.rawValue] as? String,
                additionalGuestFirstName: values[CheckInOnlineGuestRows.additionalGuestFirstName.rawValue] as? String,
                additionalGuestLastName: values[CheckInOnlineGuestRows.additionalGuestLastName.rawValue] as? String
            )
        } catch let error as RowValidatorError {
            scrollAndFocus(at: error.row)
            return nil
        } catch {
            return nil
        }
    }

    func addAdditionalImmigrationRowsIfAbsent(with formViewModel: CheckInOnlineGuestDetailsViewModel) {
        guard viewModel?.row(named: CheckInOnlineGuestRows.passportNumber.rawValue) == nil else { return }
        guard let indexPath = viewModel?.indexPath(forRowNamed: CheckInOnlineGuestRows.nationality.rawValue) else { return }
        guard let passportModel = formViewModel.passportNumberRowModel,
              let nextDestinationModel = formViewModel.nextDestinationRowModel else { return }

        viewModel?.add(
            row: textFieldRow(
                name: CheckInOnlineGuestRows.passportNumber.rawValue,
                title: passportModel.title,
                value: passportModel.value as? String,
                inlineValidators: [RequiredValidator()]
            ),
            at: IndexPath(row: indexPath.row + 1, section: indexPath.section)
        )
        viewModel?.add(
            row: textFieldRow(
                name: CheckInOnlineGuestRows.nextDestination.rawValue,
                title: nextDestinationModel.title,
                value: nextDestinationModel.value as? String,
                inlineValidators: [RequiredValidator()]
            ),
            at: IndexPath(row: indexPath.row + 2, section: indexPath.section)
        )

        guard viewModel?.row(named: CheckInOnlineGuestRows.imigrationInfo.rawValue) == nil else { return }

        viewModel?.add(row: imigrationInfoRow, at: IndexPath(row: indexPath.row + 3, section: indexPath.section))
    }

    func removeAdditionalImmigrationRowsIfPresent() {
        _ = viewModel?.remove(rowNamed: CheckInOnlineGuestRows.nextDestination.rawValue)
        _ = viewModel?.remove(rowNamed: CheckInOnlineGuestRows.passportNumber.rawValue)
        _ = viewModel?.remove(rowNamed: CheckInOnlineGuestRows.imigrationInfo.rawValue)
    }

    private func showAddressInputs() {
        guard let addressSummaryIndexPath = viewModel?.remove(rowNamed: CheckInOnlineGuestRows.addressSummary.rawValue)
            else { return }

        addressSectionView = {
            let addressRequirements = AddressSectionRequirements(
                address: nil,
                storedAddress: nil,
                shouldShowAddressSwitch: false,
                useStoredAddressSwitchDescription: nil,
                addressSwitchInitialState: false,
                shouldShowAddressForm: true,
                shouldShowHeader: false,
                shouldShowFooter: true,
                shouldShowAddressSummary: true
            )
            let view = AddressSectionRouter.buildSection(with: addressRequirements)
            view.parentFormekaViewController = self
            view.addressSectionSwitchDelegate = nil

            return view
        }()

        guard let addressSectionView = addressSectionView else { return }
        viewModel?.add(section: addressSectionView.addressSection(for: self), index: addressSummaryIndexPath.section + 1)

        table.reloadData()
    }

    private func useBookerAddress(with formViewModel: CheckInOnlineGuestDetailsViewModel) {
        guard let postcodeIndexPath = viewModel?.indexPath(forRowNamed: CountryActionableRow.postCode.rawValue)
            else { return }
        guard let addressSwitchIndexPath = viewModel?
              .indexPath(forRowNamed: CheckInOnlineGuestRows.useBookerAddress.rawValue) else { return }

        viewModel?.remove(sectionAtIndex: postcodeIndexPath.section)
        viewModel?.add(
            row: bookerAddressSummaryRow(with: formViewModel.bookerAddressSummary),
            at: IndexPath(row: addressSwitchIndexPath.row + 1, section: addressSwitchIndexPath.section)
        )

        table.reloadData()
    }
}

extension CheckInOnlineGuestDetailsView: FormekaSubmitButtonCellDelegate {
    func submitButtonDidTap(cell: FormekaSubmitButtonCell) {
        guard let formOutput = formOutput else { return }

        eventHandler?.guestDetailsDidComplete(with: formOutput)
    }
}
