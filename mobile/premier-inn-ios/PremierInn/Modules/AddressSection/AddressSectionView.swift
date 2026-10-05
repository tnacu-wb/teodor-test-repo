//
//  AddressSectionView.swift
//  PremierInn
//
//  Created by Nick Jones on 20/03/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import Formeka
import SimpleNetwork

protocol AddressSectionSwitchDelegate: AnyObject {
    func useBookerAddressSwitchValueDidChange(to value: Bool)
}

protocol AddressSectionViewInput: AnyObject {
    var shouldShowAddressSummary: Bool { get }

    func removeAddressSection()
    func showCountryPicker()
    func dismissCountryPicker()
    func showAddressSection()
    func showAllAddressRows(with address: Address?, completion: (() -> Void)?)
    func updateCountryRow(with country: Country)
    func updatePostcodeRow(with postcode: String?)
    func configurePostcodeRow(for country: Country)
    func addAddressButton()
    func addAddressManually()
    func removeAddressButton()
    func showPostcodePicker(with postCode: String?)
    func dismissPostcodePicker()
    func updateCompanyRow(with address: Address?)
    func toggleCompanyRow(with address: Address?)
    func addAddressSummaryRow()
    func removeAddressSummaryRow()
}

private struct AddressLineRequirements {
    let tag: String
    let title: String
    let validators: [Validator]
    let indexPath: IndexPath
    let traits: FormekaTextFieldTraits
}

private enum AddressSectionRow: String {
    case summary
}

class AddressSectionView {
    var presenter: AddressSectionPresenterInput?

    private let requirements: AddressSectionRequirements
    private var postCodeRowIndexPath: IndexPath? {
        parentFormekaViewController?.viewModel?.indexPath(forRowNamed: CountryActionableRow.postCode.rawValue)
    }
    private var footerSpacerHeight: CGFloat = 10

	weak var parentFormekaViewController: FormekaViewController?
	weak var segmentedControlDelegate: FormekaSegmentedControlCellDelegate?
    weak var companyNameDelegate: CompanyNameDelegate?

    weak var addressSectionSwitchDelegate: AddressSectionSwitchDelegate?

    public var countryChanged: ((String) -> Void)?

	deinit {
		print("DEINIT: \(self)")
	}

	init(with requirements: AddressSectionRequirements) {
		self.requirements = requirements
	}

    func addAddressManually() {
        presenter?.addAddressManuallyButtonDidTap()
    }

    func addressSection(
        for parentController: FormekaViewController,
        optionalAddressHeader: String? = nil,
        footerHeight: CGFloat = 10
    ) -> FormekaModelSection {
        footerSpacerHeight = footerHeight

		var rows = [FormekaModelRow]()

        if requirements.shouldShowAddressSwitch {
            rows.append(addressSwitchRow(initialState: requirements.addressSwitchInitialState))
            if !requirements.shouldShowAddressForm && requirements.shouldShowAddressSummary {
                rows.append(addressSummaryRow)
            }
        }

        if requirements.shouldShowAddressForm {
            rows.append(addressTypeRow(with: requirements.address?.companyName))

            if let companyName = requirements.address?.companyName, companyName.isNotEmpty == true {
                if let row = addCompanyNameRow(companyName: companyName) {
                    rows.append(row)
                }
            }

            let defaultCountry = LanguageManager.supportedLanguage == .german ? Country.germany : Country.greatBritain
            let selectedCountry = requirements.address?.country ?? defaultCountry

			if let countryRow = countryRow(selectedCountry: selectedCountry) {
				rows.append(countryRow)
                countryChanged?(selectedCountry?.isoCode ?? "")
			}

            if let row = postCodeRow(for: selectedCountry, postCode: requirements.address?.postcode) {
                rows.append(row)
            }

            if let address = requirements.address {
                rows += addressLineRows(with: address)
            }
        }

        let footer: FormekaModelHeaderFooter? = {
            if requirements.shouldShowFooter == true { return addAddressFooter() }
            return footerHeight > 0 ? parentController.footer(title: nil, height: footerHeight) : nil
        }()

        return FormekaModelSection(
            header: requirements.shouldShowHeader ? parentController.header(
                title: optionalAddressHeader ??
                    PILocalizedString(
                        "userDetailsAddressSectionTitle",
                        comment: "User details form: user's address section title"
                    ),
                height: 70,
                accessibilityIdentifier: AccessibilityIdentifiers.UserDetails.bookerAddressHeader
            ) : nil,
            rows: rows,
            footer: footer
        )
    }
}

extension AddressSectionView: AddressSectionViewInput {
    var shouldShowAddressSummary: Bool {
        requirements.shouldShowAddressSummary
    }

    func showPostcodePicker(with postCode: String?) {
        let addressType: AddressType = {
            guard let addressType = parentFormekaViewController?.viewModel?
                  .row(named: GuestDetailsRow.addressType.rawValue + Constants.bookerSuffix)?.value as? AddressType
            else { return .home }
            return addressType
        }()

        let postCodeViewModel = PostCodeLookupViewModel()
        postCodeViewModel.addressType = addressType

        let controller = ListViewController(viewModel: postCodeViewModel, invertedColours: true)
        controller.textFieldPlaceholder = PILocalizedString(
            "postCodeSearchInputPlaceholder",
            comment: "Post code search input placeholder"
        )
        controller.textFieldValue = postCode
        controller.minimumSearchRequestCharacters = Constants.minimumCharactersForPostCodeLookup
        controller.shouldUppercaseTextFieldInput = true
        controller.shouldShowKeyboardOnLoad = true
        controller.cancelButtonDidTap = { [weak self] _ in
            self?.presenter?.userCancelledPostcodePicker()
        }
        controller.selectedObjectOutput = { [weak self] _, object in
            self?.companyNameDelegate?.removePersistedCompanyName()
            self?.presenter?.userPicked(object as? Address)
        }
        ContentsquareConfig.mask(view: controller.view)
        parentFormekaViewController?.present(controller, animated: true)
    }

    func dismissPostcodePicker() {
        guard let picker = parentFormekaViewController?.presentedViewController as? ListViewController else { return }

        picker.dismiss(animated: true)
    }

    func showCountryPicker() {
        let controller = ListViewController(
            viewModel: CountriesListViewModel(countries: Country.countriesList),
            invertedColours: true
        )
        controller.textFieldPlaceholder = PILocalizedString(
            "searchCountryPlaceholder",
            comment: "Search country input placeholder"
        )
        controller.shouldDelaySearchRequest = false
        controller.cancelButtonDidTap = { [weak self] _ in
            self?.presenter?.userCancelledCountryPicker()
        }
        controller.selectedObjectOutput = { [weak self] _, object in
            self?.presenter?.userPicked(object as? Country)
        }

        parentFormekaViewController?.present(controller, animated: true)
    }

    func dismissCountryPicker() {
        guard let picker = parentFormekaViewController?.presentedViewController as? ListViewController else { return }

        picker.dismiss(animated: true)
    }

    func toggleCompanyRow(with address: Address?) {
        guard let row = parentFormekaViewController?.viewModel?
              .row(named: GuestDetailsRow.addressType.rawValue + Constants.bookerSuffix) else { return }

        guard let indexPath = parentFormekaViewController?.viewModel?.indexPath(for: row) else { return }
        guard let cell = parentFormekaViewController?.table.cellForRow(at: indexPath) as? FormekaSegmentedControlCell
            else { return }

        let selectedAddressType = address?.companyName?.isNotEmpty == true ? AddressType.commercial : AddressType.home
        // Don't add/remove an extra row if they are already on the work switch
        if selectedAddressType != row.value as? AddressType {
            row.value = selectedAddressType
            cell.segmentedControl.selectedSegmentIndex = address?.companyName?.isNotEmpty == true ? 1 : 0
            segmentedControlDelegate?.formekaSegmentedControlValueDidChange(cell: cell)

            parentFormekaViewController?.table.reloadRows(at: [indexPath], with: .none)
        }
    }

    func updateCompanyRow(with address: Address?) {
        if let companyName = address?.companyName {
            parentFormekaViewController?.viewModel?.row(named: GuestDetailsRow.companyName.rawValue)?.value = companyName
        }
    }

    func updateCountryRow(with country: Country) {
        guard let row = parentFormekaViewController?.viewModel?.row(named: CountryActionableRow.country.rawValue)
            else { return }
        guard let indexPath = parentFormekaViewController?.viewModel?.indexPath(for: row) else { return }

        row.value = country
        countryChanged?(country.isoCode)

        parentFormekaViewController?.table.reloadRows(at: [indexPath], with: .none)
    }

    func configurePostcodeRow(for country: Country) {
        let existingPostcode = parentFormekaViewController?.viewModel?.row(named: CountryActionableRow.postCode.rawValue)?
            .value

        guard let indexPath = parentFormekaViewController?.viewModel?
              .remove(rowNamed: CountryActionableRow.postCode.rawValue) else { return }
        guard let postCodeRow = postCodeRow(for: country, postCode: existingPostcode?.displayName) else { return }

        parentFormekaViewController?.viewModel?.add(row: postCodeRow, at: indexPath)
        parentFormekaViewController?.table.reloadRows(at: [indexPath], with: .none)
    }

    func updatePostcodeRow(with postcode: String?) {
        guard let indexPath = postCodeRowIndexPath else { return }
        guard let postCodeRow = parentFormekaViewController?.viewModel?.row(at: indexPath) else { return }

        if let postcode = postcode {
            postCodeRow.value = postcode
            try? postCodeRow.validate()
        }

        parentFormekaViewController?.table.reloadData()
    }

    func showAllAddressRows(with address: Address?, completion: (() -> Void)? = nil) {
		let newIndexPaths = createOrUpdateAddressLineRows(with: address)

		parentFormekaViewController?.table.performAnimation({
			parentFormekaViewController?.table.insertRows(at: newIndexPaths, with: .automatic)
		}, completion: completion)
    }

    func addAddressButton() {
        guard let indexPath = postCodeRowIndexPath else { return }
        guard let sections = parentFormekaViewController?.viewModel?.sections else { return }
        guard indexPath.section < sections.count else { return }

        sections[indexPath.section].footer = addAddressFooter()

        UIView.performWithoutAnimation {
            parentFormekaViewController?.table.reloadSections([indexPath.section], with: .none)
        }
    }

    func removeAddressButton() {
        guard let indexPath = postCodeRowIndexPath else { return }
        guard let sections = parentFormekaViewController?.viewModel?.sections else { return }
        guard indexPath.section < sections.count else { return }

        sections[indexPath.section].footer = footerSpacerHeight > 0 ? parentFormekaViewController?.footer(
            title: nil,
            height: footerSpacerHeight
        ) : nil

		parentFormekaViewController?.table.reloadSections([indexPath.section], with: .none)
    }

    func removeAddressSection() {
        let removedIndexPaths: [IndexPath] = {
            guard let viewModel = parentFormekaViewController?.viewModel else { return [] }

            let tags: [CountryActionableRow] = [.country, .postCode, .addressLine1, .addressLine2, .addressLine3]

            // Reverse the array to removed rows from the last to the first
            // This way we don't lose reference to indexPaths
            var indexPaths = tags.reversed().compactMap { viewModel.remove(rowNamed: $0.rawValue) }
            if let indexPath = viewModel.remove(rowNamed: GuestDetailsRow.companyName.rawValue) {
                indexPaths.append(indexPath)
            }
            if let indexPath = viewModel.remove(rowNamed: "addressTypeBooker") {
                indexPaths.append(indexPath)
            }
            return indexPaths
        }()

        if removedIndexPaths.isNotEmpty {
            parentFormekaViewController?.table.deleteRows(at: removedIndexPaths, with: .none)
        }
    }

    private var addressSummaryRow: FormekaModelRow {
        FormekaModelRow(
            tag: AddressSectionRow.summary.rawValue,
            cellSetup: { [weak self] indexPath, _, table in
                guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.content.text = self?.requirements.storedAddress?.description.replacingOccurrences(of: ",", with: ",\n")

                cell.contentContainer.backgroundColor = .greyishWhite
                cell.contentContainer.layer.cornerRadius = 4
                cell.contentContainer.layer.borderColor = UIColor.veryLightPink85.cgColor

                cell.contentContainerTopConstraint.constant = 12
                cell.contentContainerBottomConstraint.constant = 24
                cell.contentContainerLeadingConstraint.constant = 24
                cell.contentContainerTrailingConstraint.constant = 24

                cell.hiddenSeparatorLocations = [.top]

                return cell
        }
        )
    }

    func addAddressSummaryRow() {
        guard requirements.storedAddress != nil else { return }
        guard let indexPath = parentFormekaViewController?.viewModel?
              .indexPath(forRowNamed: Step2Row.billingAddressSwitch.rawValue) else { return }

        let newIndexPath = IndexPath(row: indexPath.row + 1, section: indexPath.section)

        parentFormekaViewController?.viewModel?.add(row: addressSummaryRow, at: newIndexPath)
        parentFormekaViewController?.table.insertRows(at: [newIndexPath], with: .none)
    }

    func removeAddressSummaryRow() {
        guard let row = parentFormekaViewController?.viewModel?.remove(rowNamed: AddressSectionRow.summary.rawValue)
            else { return }
        parentFormekaViewController?.table.deleteRows(at: [row], with: .none)
    }

    func showAddressSection() {
        guard let indexPath = parentFormekaViewController?.viewModel?
              .indexPath(forRowNamed: Step2Row.billingAddressSwitch.rawValue) else { return }

        let addedIndexPaths = addAddressRowsToViewModel(startingIndexPath: indexPath, address: nil)

        parentFormekaViewController?.table.insertRows(at: addedIndexPaths, with: .none)
    }
}

extension AddressSectionView: FormekaPostCodeCellDelegate {
    func textFieldDidUpdateContent(value: String, cell: FormekaTextFieldCell) {
        parentFormekaViewController?.textFieldDidUpdateContent(value: value, cell: cell)
    }

    func textFieldDidBeginEditing(cell: FormekaTextFieldCell) {
        parentFormekaViewController?.textFieldDidBeginEditing(cell: cell)
    }

    func textFieldDidEndEditing(value: String, cell: FormekaTextFieldCell) {
        parentFormekaViewController?.textFieldDidEndEditing(value: value, cell: cell)
    }

    func searchButtonDidTap(cell: FormekaPostCodeCell) {
        guard let indexPath = parentFormekaViewController?.table.indexPath(for: cell) else { return }
        guard let row = parentFormekaViewController?.viewModel?.row(at: indexPath) else { return }

        presenter?.postcodeSearchButtonDidTap(with: row.value?.displayName)
    }
}

// MARK: - Rows

extension AddressSectionView {
    private func addressSwitchRow(initialState: Bool) -> FormekaModelRow {
		let row = FormekaModelRow(tag: Step2Row.billingAddressSwitch.rawValue, cellSetup: { indexPath, row, table in
			guard let cell: SwitchCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.message.text = self.requirements.useStoredAddressSwitchDescription ?? PILocalizedString(
                "cardDetailsBillingAddressSwitchLabel",
                comment: "Card details form: billing address switch label"
            )
            cell.hiddenSeparatorLocations = [.top, .bottom]
			cell.toggleSwitch.isOn = row.value as? Bool == true
            cell.toggleSwitch.onTintColor = .Tint1
			cell.toggled = { [weak self] value in
                row.value = value
                self?.presenter?.billingAddressSwitchDidChange(isSameAsYourAddress: value)
                self?.addressSectionSwitchDelegate?.useBookerAddressSwitchValueDidChange(to: value)
            }
            ContentsquareConfig.mask(view: cell)
			return cell
		})
        row.value = initialState

        return row
	}

    private func addCompanyNameRow(companyName: String) -> FormekaModelRow? {
        let textFieldRow = parentFormekaViewController?.textFieldRow(
            name: GuestDetailsRow.companyName.rawValue,
            title: PILocalizedString("userDetailsCompanyName", comment: "User details form: company name label"),
            value: companyName,
            inlineValidators: [.companyName],
            onBlurValidators: [.required, StringLengthValidator(range: 1...40)]
        )
        guard let row = textFieldRow else { return nil }

        return row
    }

	private func addressTypeRow(with companyName: String?) -> FormekaModelRow {
		let row = FormekaModelRow(
		    tag: GuestDetailsRow.addressType.rawValue + Constants.bookerSuffix,
		    cellSetup: { [unowned self] indexPath, row, table in
				guard let cell: FormekaSegmentedControlCell = table.dequeueCell(for: indexPath) else { return nil }

				cell.hiddenSeparatorLocations = [.top]

                cell.segmentedControl.backgroundColor = .ColourLD8
                cell.segmentedControl.tintColor = .ColourLD8
                cell.segmentedControl.setTitleTextAttributes(
                    [NSAttributedString.Key.font: UIFont.Heading4_Semibold(), .foregroundColor: UIColor.BasePurple],
                    for: .normal
                )
                cell.segmentedControl.setTitleTextAttributes([.foregroundColor: UIColor.BaseWhite], for: .selected)
				cell.segmentedControl.setTitle(
				    PILocalizedString("userDetailsAddressTypeHome", comment: "User details form: address type home"),
				    forSegmentAt: 0
				)
				cell.segmentedControl.setTitle(
				    PILocalizedString("userDetailsAddressTypeWork", comment: "User details form: address type work"),
				    forSegmentAt: 1
				)
                cell.segmentedControl.imageForSegment(at: 0)?.accessibilityLabel = AccessibilityIdentifiers.UserDetails
                    .bookerAddressTypeHome
                cell.segmentedControl.imageForSegment(at: 1)?.accessibilityLabel = AccessibilityIdentifiers.UserDetails
                    .bookerAddressTypeWork

				cell.errorLabel?.textColor = .Tint8
				cell.errorLabel?.font = UIFont.BodySmall()

				cell.delegate = segmentedControlDelegate

				if let value = row.value as? AddressType {
					cell.segmentedControl.selectedSegmentIndex = (value == AddressType.home) ? 0 : 1
				}
				cell.errorMessage = row.error?.localizedDescription

                cell.contentView.backgroundColor = .ColourLD1

                return cell
            }
		)

        row.value = companyName?.isNotEmpty == true ? AddressType.commercial : AddressType.home

        return row
	}

    private func countryRow(selectedCountry: Country?) -> FormekaModelRow? {
        parentFormekaViewController?.buttonRow(
            tag: CountryActionableRow.country.rawValue,
            title: PILocalizedString("countryRowTitle", comment: "Country row title"),
            validators: [CountryValidator()],
            value: selectedCountry,
            titleLabelAccessibilityIdentifier: AccessibilityIdentifiers.UserDetails.bookerAddressCountryLabel,
            action: { [unowned self] _, _ in
                presenter?.userDidTapCountryRow()
        }
        )
    }

    private func postCodeRow(for country: Country?, postCode: String?) -> FormekaModelRow? {
        if country == .greatBritain {
            return postCodeRow(validator: .ukPostCode, postCode: postCode, hideFindButton: false)
        }
        if country == .germany {
            return postCodeRow(validator: .dePostCode, postCode: postCode, hideFindButton: true)
        }

        return overseasPostCodeRow(postCode: postCode)
    }

    private func postCodeRow(validator: Validator, postCode: String?, hideFindButton: Bool) -> FormekaModelRow {
        let row = FormekaModelRow(
            tag: CountryActionableRow.postCode.rawValue,
            title: PILocalizedString("postcode", comment: ""),
            onBlurValidators: [validator],
            cellSetup: { [unowned self] indexPath, row, table in
            guard let cell: FormekaPostCodeCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self
            cell.postCodeDelegate = self

            let title = PILocalizedString("userDetailsFindAddressButtonTitle", comment: "Find address button title")
            cell.searchButton.setTitle(title, for: .normal)
            cell.searchButton.setTitleColor(.BaseWhite, for: .normal)
            cell.searchButton.backgroundColor = .BasePurple
            cell.searchButton.titleLabel?.font = UIFont.Body()
            cell.searchButton.titleLabel?.adjustsFontSizeToFitWidth = true
            cell.searchButton.layer.cornerRadius = 3
            cell.searchButton.isHidden = hideFindButton

            cell.titleLabel.text = PILocalizedString("postCodeRowTitle", comment: "Post code row title")
            cell.titleLabel.textColor = .ColourDL3
            cell.titleLabel.font = UIFont.BodySmall()
            cell.titleLabel.accessibilityIdentifier = AccessibilityIdentifiers.UserDetails.bookerAddressPostcodeLabel

            cell.textField.text = row.value?.displayName
            cell.textField.textColor = .ColourDL1
            cell.textField.font = UIFont.Body()
            cell.textField.autocapitalizationType = .allCharacters
            cell.textField.autocorrectionType = .no
            cell.textField.spellCheckingType = .no
            cell.textField.keyboardType = .default
            cell.textField.keyboardAppearance = .default
            cell.textField.returnKeyType = .default
            cell.textField.textContentType = .postalCode
            cell.textField.accessibilityIdentifier = "\(CountryActionableRow.postCode.rawValue)Acc"

            cell.errorLabel?.textColor = .Tint8
            cell.errorLabel?.font = UIFont.BodySmall()

            cell.errorMessage = row.error?.localizedDescription

            cell.contentView.backgroundColor = .ColourLD1

            return cell
            }, didSelect: { [unowned self] indexPath, _ in
                parentFormekaViewController?.table.cellForRow(at: indexPath)?.becomeFirstResponder()
        }
        )
		row.value = postCode

		return row
    }

    private func overseasPostCodeRow(postCode: String?) -> FormekaModelRow {
        let row = FormekaModelRow(
            tag: CountryActionableRow.postCode.rawValue,
            cellSetup: { [unowned self] indexPath, row, table in
            guard let cell: FormekaTextFieldCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self
            cell.titleLabel.text = PILocalizedString("zipCodeRowTitle", comment: "Zip code row title")
            cell.titleLabel.textColor = .ColourDL3
            cell.titleLabel.font = UIFont.BodySmall()
            cell.titleLabel.accessibilityIdentifier = AccessibilityIdentifiers.UserDetails.bookerAddressPostcodeLabel

            cell.textField.text = row.value?.displayName
            cell.textField.autocorrectionType = .no
            cell.textField.spellCheckingType = .no
            cell.textField.keyboardType = .default
            cell.textField.keyboardAppearance = .default
            cell.textField.returnKeyType = .default
            cell.textField.textColor = .ColourDL1
            cell.textField.font = UIFont.Body()
            cell.textField.accessibilityIdentifier = "\(CountryActionableRow.postCode.rawValue)Acc"

            cell.errorLabel?.textColor = .Tint8
            cell.errorLabel?.font = UIFont.BodySmall()

            cell.contentView.backgroundColor = .ColourLD1

            return cell
            }, didSelect: { [unowned self] indexPath, _ in
                parentFormekaViewController?.table.cellForRow(at: indexPath)?.becomeFirstResponder()
        }
        )
		row.value = postCode

		return row
    }

    private func addAddressFooter() -> FormekaModelHeaderFooter {
        let height: CGFloat = 70

        return FormekaModelHeaderFooter(height: height, viewSetup: { [unowned self] _, table in
            let view = UITableViewHeaderFooterView(frame: CGRect(x: 0, y: 0, width: table.frame.size.width, height: height))

            let addressButton = UIButton(frame: CGRect(x: 20, y: 10, width: view.frame.size.width - 20, height: 30))
            addressButton.contentHorizontalAlignment = .left
            addressButton.setTitleColor(.ColourDL5, for: .normal)
            addressButton.titleLabel?.font = UIFont.BodySmall()
            addressButton.setTitle(
                PILocalizedString("userDetailsAddAddressManuallyButtonTitle", comment: "Add address manually button title"),
                for: .normal
            )
            addressButton.addTarget(self, action: #selector(addAddressManuallyButtonDidTap), for: .touchUpInside)
            addressButton.accessibilityIdentifier = "addAddressManually"

            view.addSubview(addressButton)

            return view
        })
    }
}

// MARK: - Helpers

extension AddressSectionView {
    @objc private func addAddressManuallyButtonDidTap() {
        presenter?.addAddressManuallyButtonDidTap()
    }

    private func addAddressRowsToViewModel(startingIndexPath: IndexPath, address: Address?) -> [IndexPath] {
        guard let viewModel = parentFormekaViewController?.viewModel else { return [] }

        let country = address?.country ?? .greatBritain

        var row = startingIndexPath.row
        let section = startingIndexPath.section
        var addedIndexPaths: [IndexPath] = []

        let addressType = addressTypeRow(with: nil)
        row += 1
        let addressTypeIndexPath = IndexPath(item: row, section: section)
        viewModel.add(row: addressType, at: addressTypeIndexPath)
        addedIndexPaths.append(addressTypeIndexPath)


        let countryIndexPath = IndexPath(item: row + 1, section: section)

        var addressLineTraits = FormekaTextFieldTraits()
        addressLineTraits.autocapitalizationType = .words

        var optionalAddressLineTraits = FormekaTextFieldTraits()
        optionalAddressLineTraits.placeholder = PILocalizedString(
            "cardDetailsOptional",
            comment: "Card details form: optional placeholder"
        )

        // Country
		if let countryRow = countryRow(selectedCountry: country) {
			viewModel.add(row: countryRow, at: countryIndexPath)
			addedIndexPaths.append(countryIndexPath)
		}

        // PostCode
        if let postCodeRow = postCodeRow(for: country, postCode: address?.postcode) {
            let postcodeIndexPath = IndexPath(item: row + 2, section: section)
            viewModel.add(row: postCodeRow, at: postcodeIndexPath)
            addedIndexPaths.append(postcodeIndexPath)
        }

        return addedIndexPaths
    }

    private func addressLineRows(with address: Address?) -> [FormekaModelRow] {
        guard let formekaController = parentFormekaViewController else { return [] }
        var line1Address = FormekaTextFieldTraits()
        line1Address.textContentType = .streetAddressLine1

        var line2Address = FormekaTextFieldTraits()
        line2Address.textContentType = .addressCity

        var line3Address = FormekaTextFieldTraits()
        line3Address.textContentType = .addressState
        return [
            formekaController.textFieldRow(
                name: CountryActionableRow.addressLine1.rawValue,
                title: PILocalizedString("userDetailsAddressLine1"),
                value: address?.line1?.capitalized,
                traits: line1Address,
                onBlurValidators: [.required],
                titleLabelAccessibilityIdentifier: AccessibilityIdentifiers.UserDetails.bookerAddressLine1Label,
                textFieldAccessibilityIdentifier: AccessibilityIdentifiers.UserDetails.bookerAddressLine1Input,
                maskField: true
            ),
            formekaController.textFieldRow(
                name: CountryActionableRow.addressLine2.rawValue,
                title: PILocalizedString("userDetailsAddressLine2"),
                value: address?.line2?.capitalized,
                traits: line2Address,
                titleLabelAccessibilityIdentifier: AccessibilityIdentifiers.UserDetails.bookerAddressLine2Label,
                textFieldAccessibilityIdentifier: AccessibilityIdentifiers.UserDetails.bookerAddressLine2Input,
                maskField: true
            ),
            formekaController.textFieldRow(
                name: CountryActionableRow.addressLine3.rawValue,
                title: PILocalizedString("userDetailsAddressLine3"),
                value: address?.line3?.capitalized,
                traits: line3Address,
                titleLabelAccessibilityIdentifier: AccessibilityIdentifiers.UserDetails.bookerAddressLine3Label,
                textFieldAccessibilityIdentifier: AccessibilityIdentifiers.UserDetails.bookerAddressLine3Input,
                maskField: true
            )
        ]
    }

    private func createOrUpdateAddressLineRows(with address: Address?) -> [IndexPath] {
        guard let startingIndexPath = postCodeRowIndexPath else { return [] }

		var newIndexPaths: [IndexPath] = []
        var line1Address = FormekaTextFieldTraits()
        line1Address.textContentType = .streetAddressLine1

        var line2Address = FormekaTextFieldTraits()
        line2Address.textContentType = .addressCity

        var line3Address = FormekaTextFieldTraits()
        line3Address.textContentType = .addressState

        let row1 = getRow(
            with: AddressLineRequirements(
                tag: CountryActionableRow.addressLine1.rawValue,
                title: PILocalizedString("userDetailsAddressLine1"),
                validators: [.required],
                indexPath: IndexPath(
                    row: startingIndexPath.row + 1,
                    section: startingIndexPath.section
                ),
                traits: line1Address
            )
        )
        if let lineValue = address?.line1 {
            row1.row?.value = lineValue
        }
		if let indexPath = row1.newIndexPath {
			newIndexPaths.append(indexPath)
		}

        let row2 = getRow(
            with: AddressLineRequirements(
                tag: CountryActionableRow.addressLine2.rawValue,
                title: PILocalizedString("userDetailsAddressLine2"),
                validators: [],
                indexPath: IndexPath(
                    row: startingIndexPath.row + 2,
                    section: startingIndexPath.section
                ),
                traits: line2Address
            )
        )
        if let lineValue = address?.line2 {
            row2.row?.value = lineValue
        }
		if let indexPath = row2.newIndexPath {
			newIndexPaths.append(indexPath)
		}

        let row3 = getRow(
            with: AddressLineRequirements(
                tag: CountryActionableRow.addressLine3.rawValue,
                title: PILocalizedString("userDetailsAddressLine3"),
                validators: [],
                indexPath: IndexPath(
                    row: startingIndexPath.row + 3,
                    section: startingIndexPath.section
                ),
                traits: line3Address
            )
        )
        if let lineValue = address?.line3 {
            row3.row?.value = lineValue
        }
		if let indexPath = row3.newIndexPath {
			newIndexPaths.append(indexPath)
		}

		return newIndexPaths
	}

	private func getRow(with requirements: AddressLineRequirements) -> (row: FormekaModelRow?, newIndexPath: IndexPath?) {
        if let row = parentFormekaViewController?.viewModel?.row(named: requirements.tag) {
            return (row, nil)
        }

        if let row = parentFormekaViewController?.textFieldRow(
            name: requirements.tag,
            title: requirements.title,
            value: nil,
            traits: requirements.traits,
            onBlurValidators: requirements.validators
        ) {
            parentFormekaViewController?.viewModel?.add(row: row, at: requirements.indexPath)

            return (row, requirements.indexPath)
        }

        return (nil, nil)
    }
}
