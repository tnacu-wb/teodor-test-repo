//
//  FormPaymentStep2+Extensions.swift
//  PremierInn
//
//  Created by Freddie Parks on 14/03/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import Appsee
import Formeka

extension FormPaymentStep2 {
    func registerTableElements() {
        table.registerCellNib(with: FormekaSegmentedControlCell.self)
        table.registerCellNib(with: FormekaTextFieldCell.self)
        table.registerCellNib(with: FormekaCardDateCell.self)
        table.registerCellNib(with: CreditCardNumberCell.self)
        table.registerCellNib(with: FormekaSubmitButtonCell.self)
        table.registerCellNib(with: FormekaFreeTextCell.self)
        table.registerCellNib(with: FormekaPostCodeCell.self)
        table.registerCellNib(with: FormekaButtonCell.self)
        table.registerCellNib(with: SwitchCell.self)
        table.registerCellNib(with: ErrorCell.self)
		table.registerCellNib(with: FlexibleContentInformationCell.self)

        table.registerHeaderFooterNib(with: FormekaHeader.self)
        table.registerHeaderFooterNib(with: FormekaIconFooter.self)

        /* 🛡 GDPR Stuff 🛡 */
        table.registerCellClass(with: SimpleSeparatorsCell.self)
        /*=-=-=-=-=-=-=-=-=-=*/

        table.register(UITableViewCell.self, forCellReuseIdentifier: String(describing: UITableViewCell.self))
    }

    func loadViewModel() {
        viewModel = TableViewModel<FormekaModelRow>(sections: viewModelSections())
        viewModel?.delegate = self

        if bookingDetails.billingAddressIsBookerAddress == false,
           let indexPath = viewModel?.indexPath(forRowNamed: Step2Row.billingAddressSwitch.rawValue) {
            _ = addNewAddressRows(startingIndexPath: indexPath)
        }

        table.delegate = viewModel
        table.dataSource = viewModel
        table.backgroundColor = .whiteTwo
    }

    private func viewModelSections() -> [TableViewModelSection<FormekaModelRow>] {
        var sections: [TableViewModelSection<FormekaModelRow>] = [
            cardDetailsSection(),
            cardholderAddressSection(),
            submitSection()
        ]

        if bookingDetails.userCanChooseWhenToPay && isInEditMode == false {
            sections.insert(paymentIntervalSection(), at: 0)
        } else if bookingDetails.paymentOption == .later && isInEditMode == false {
            sections.insert(
                TableViewModelSection(
                    header: nil,
                    rows: [paymentTimeMessageRow(
                        message: PILocalizedString("cardDetailsDefaultExplanation"),
                        topConstraintConstant: 25
                    )],
                    footer: nil
                ),
                at: 0
            )
        }

        // Card expired error
        if let user = User.current, let card = user.paymentPreference?.card, card.expired(onDate: Date()) {
            sections.insert(
                errorSection(for: table, cardNumber: card.cardNumber.replacingOccurrences(of: "*", with: "")),
                at: 0
            )
        }

		sections.insert(gdprInfoSection(
		    text: PILocalizedString("paymentsDetailsUsageBox", comment: "Payment Details GDPR usage box"),
		    highlight: PILocalizedString("paymentsDetailsUsageBoxHighlightedText", comment: ""),
		    link: PILocalizedString("paymentsDetailsUsageBoxLink", comment: "")
		), at: 0)

        return sections
    }

    private func errorSection(for table: UITableView?, cardNumber: String) -> TableViewModelSection<FormekaModelRow> {
        var rows: [FormekaModelRow] = []

        rows.append(
            FormekaModelRow(
                tag: LoginRow.banner.rawValue,
                cellSetup: { indexPath in
                    guard let cell: ErrorCell = self.table?.dequeueCell(for: indexPath) else { return UITableViewCell() }
                    cell.errorLabel.text = String(
                        format: PILocalizedString("paymentScreenCardExpiredError"),
                        cardNumber
                    )
                    cell.topConstraint.constant = 15
                    cell.leftConstraint.constant = 15
                    cell.bottomConstraint.constant = 15
                    cell.rightConstraint.constant = 15

                    return cell
                },
                didSelect: nil)
        )

        return TableViewModelSection(header: nil, rows: rows, footer: footer(title: nil, height: 10))
    }

    private func paymentIntervalSection() -> TableViewModelSection<FormekaModelRow> {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(
            tag: Step2Row.paymentTime.rawValue,
            cellSetup: { [weak self] indexPath -> UITableViewCell in
            guard let cell: FormekaSegmentedControlCell = self?.table.dequeueCell(for: indexPath)
                else { return UITableViewCell() }

            cell.segmentedControl.tintColor = .gunMetal
            cell.segmentedControl.setTitleTextAttributes(
                [NSAttributedStringKey.font: UIFont.premierInnBold(ofSize: 16)],
                for: .normal
            )
            cell.segmentedControl.setTitle(PaymentIntervalOption.later.localizedString, forSegmentAt: 0)
            cell.segmentedControl.setTitle(PaymentIntervalOption.now.localizedString, forSegmentAt: 1)
            cell.segmentedControl.selectedSegmentIndex = {
                guard let option = self?.bookingDetails.selectedPaymentOption else { return 0 }

                return option == .later ? 0 : 1
            }()

            cell.errorLabel?.textColor = UIColor.strongRed
            cell.errorLabel?.font = UIFont.premierInn(ofSize: 12)

            cell.delegate = self

            cell.hiddenSeparatorLocations = [.top, .bottom]

            return cell
        }
        ))

        rows.append(paymentTimeMessageRow(message: PILocalizedString(
            "cardDetailsPayOnArrivalExplanation",
            comment: "Card details form: card requirements pay on arrival explanation"
        )))

        return TableViewModelSection(
            header: header(title: PILocalizedString(
                "cardDetailsTimingQuestion",
                comment: "Card details form: pay now/later question"
            ), height: 60),
            rows: rows,
            footer: footer(title: nil, height: 10)
        )
    }

    private func paymentTimeMessageRow(message: String, topConstraintConstant: CGFloat = 8) -> FormekaModelRow {
        FormekaModelRow(tag: Step2Row.paymentTimeMessage.rawValue, cellSetup: { [weak self] indexPath -> UITableViewCell in
            guard let cell: FormekaFreeTextCell = self?.table.dequeueCell(for: indexPath) else { return UITableViewCell() }
            cell.message.text = message
            cell.topConstraint.constant = topConstraintConstant
            return cell
        })
    }

    private func cardDetailsSection() -> TableViewModelSection<FormekaModelRow> {
        // Traits
        var dateTraits = FormekaTextFieldTraits()
        dateTraits.placeholder = PILocalizedString(
            "cardDetailsDateFormatPlaceholder",
            comment: "Card details form: date format placeholder"
        )

        var numberTraits = FormekaTextFieldTraits()
        numberTraits.keyboardType = .numberPad

        var nameTraits = FormekaTextFieldTraits()
        nameTraits.autocapitalizationType = .words


        // Rows
        var rows = [FormekaModelRow]()

        rows.append(cardNumberRow(withCard: paymentCardToPrePopulate))

        rows.append(cardDateFieldRow(
            name: Step2Row.cardEndDate.rawValue,
            title: PILocalizedString("cardDetailsEndDateLabel", comment: "Card details form: end date label"),
            value: paymentCardToPrePopulate?.expiryDate.creditCardDateFormat,
            traits: dateTraits,
            onBlurValidators: [RequiredValidator(), CardShortDateValidator(), CardExpiredDateValidator()]
        ))

        rows.append(textFieldRow(
            name: Step2Row.cardholderName.rawValue,
            title: PILocalizedString("cardDetailsName", comment: "Card details form: name label"),
            value: paymentCardToPrePopulate?.cardholderName ?? bookingDetails.booker?.informalFullName,
            traits: nameTraits,
            inlineValidators: [NameValidator()],
            onBlurValidators: [StringLengthValidator(range: 1...30)]
        ))

        return TableViewModelSection(
            header: header(
                title: PILocalizedString("cardDetailsHeaderTitle", comment: "Card details form: header title"),
                height: 70
            ),
            rows: rows,
            footer: footer(title: nil, height: 10)
        )
    }

    private func cardNumberRow(withCard card: PaymentCard?) -> FormekaModelRow {
        FormekaModelRow(
            tag: Step2Row.cardNumber.rawValue,
            value: card?.cardNumber,
            onBlurValidators: [RequiredValidator(), CreditCardNumberValidator()],
            cellSetup: { [weak self] indexPath -> UITableViewCell in
            guard let cell: CreditCardNumberCell = self?.table.dequeueCell(for: indexPath) else { return UITableViewCell() }

            let row = self?.viewModel?.row(at: indexPath)

            cell.delegate = self
            cell.titleLabel.text = PILocalizedString(
                "cardDetailsCardNumber",
                comment: "Card details form: card number label"
            )
            cell.titleLabel.textColor = .greyish
            cell.titleLabel.font = .premierInn(ofSize: 14)

            cell.textField.keyboardType = .numberPad
            cell.textField.text = row?.value?.displayName
            cell.textField.textColor = .premierInnBlack
            cell.textField.font = .premierInn(ofSize: 16)

            cell.errorLabel?.textColor = .strongRed
            cell.errorLabel?.font = .premierInn(ofSize: 12)

            cell.updateFees(fee: self?.bookingDetails.paymentCardFee(cardCode: card?.cardType.cardCode)?.localizedValue)
            cell.updateImage(type: card?.cardType.cardCode)
            cell.toggleError(message: row?.error?.errorDescription)

            Appsee.markView(asSensitive: cell.textField)
            Appsee.markView(asSensitive: cell.cardTypeImageView)

            return cell
            },
            didSelect: { [weak self] indexPath in
                self?.table.cellForRow(at: indexPath)?.becomeFirstResponder()
        }
        )
    }

    private func cardholderAddressSection() -> TableViewModelSection<FormekaModelRow> {
        var rows = [FormekaModelRow]()

        let row = FormekaModelRow(
            tag: Step2Row.billingAddressSwitch.rawValue,
            cellSetup: { [weak self] indexPath -> UITableViewCell in
            guard let cell: SwitchCell = self?.table.dequeueCell(for: indexPath) else { return UITableViewCell() }
            cell.message.text = PILocalizedString(
                "cardDetailsBillingAddressSwitchLabel",
                comment: "Card details form: billing address switch label"
            )
            cell.toggleSwitch.isOn = self?.bookingDetails.billingAddressIsBookerAddress ?? false
            cell.hiddenSeparatorLocations = [.top]
            cell.toggled = { value in
                self?.bookingDetails.billingAddressIsBookerAddress = value

                if value {
                    self?.bookingDetails.paymentCard?.address = self?.bookingDetails.booker?.address

                    if let removedIndexPaths = self?.removeNewAddressRows() {
                        self?.table.deleteRows(at: removedIndexPaths, with: .automatic)
                    }
                } else {
                    self?.bookingDetails.paymentCard?.address = nil

                    if let addressSwitchIndexPath = self?.viewModel?
                       .indexPath(forRowNamed: Step2Row.billingAddressSwitch.rawValue) {
                        if let addedIndexPaths = self?.addNewAddressRows(startingIndexPath: addressSwitchIndexPath) {
                            self?.table.insertRows(at: addedIndexPaths, with: .automatic)
                        }
                    }
                }
            }

            return cell
        }
        )

        rows.append(row)

        return TableViewModelSection(
            header: header(title: PILocalizedString(
                "cardDetailsAddressSectionTitle",
                comment: "Card details form: address section title"
            ), height: 70),
            rows: rows,
            footer: footer(title: nil, height: 10)
        )
    }

    private func submitSection() -> TableViewModelSection<FormekaModelRow> {
        var rows = [FormekaModelRow]()

        if User.current != nil {
            rows.append(FormekaModelRow(
                tag: Step2Row.storeCardSwitch.rawValue,
                cellSetup: { [weak self] indexPath -> UITableViewCell in
                guard let cell: SwitchCell = self?.table.dequeueCell(for: indexPath) else { return UITableViewCell() }
                cell.message.text = PILocalizedString(
                    "cardDetailsSaveCardLabel",
                    comment: "Card details form: save card for later switch label"
                )
                cell.toggleSwitch.isOn = self?.bookingDetails.shouldStorePaymentCard ?? false
                cell.hiddenSeparatorLocations = [.top]
                cell.toggled = { value in
                    self?.bookingDetails.shouldStorePaymentCard = value
                }

                return cell
            }
            ))
        }

        rows.append(FormekaModelRow(
            tag: Step2Row.submitButton.rawValue,
            cellSetup: { [weak self] indexPath -> UITableViewCell in
            guard let cell: FormekaSubmitButtonCell = self?.table.dequeueCell(for: indexPath)
                else { return UITableViewCell() }

            let inEditMode = self?.isInEditMode ?? false

            cell.button.setTitle(
                inEditMode ?
                    PILocalizedString(
                        "cardDetailsSubmitUpdate",
                        comment: "Card details form: submit button title - update"
                    ) :
                    PILocalizedString(
                        "cardDetailsSubmitContinue",
                        comment: "Card details form: submit button title - continue"
                    ),
                for: .normal
            )
            cell.button.backgroundColor = inEditMode ? .premierInnPurple : .butterscotch
            cell.button.setTitleColor(inEditMode ? .white : .premierInnPurple, for: .normal)
            cell.button.titleLabel?.font = UIFont.premierInnBold(ofSize: 18)

            cell.contentView.backgroundColor = .whiteTwo
            cell.hiddenSeparatorLocations = [.top, .bottom]
            cell.delegate = self

            return cell
        }
        ))

        /* 🛡 GDPR Stuff 🛡 */
        rows.append(gdprFooterRow())
        /*=-=-=-=-=-=-=-=-=-=*/

        return TableViewModelSection(
            header: rows.count > 1 ? header(title: nil, height: 20, backgroundColor: .white) : nil,
            rows: rows,
            footer: footer(title: nil, height: 52, backgroundColor: .clear)
        )
    }


    // MARK: Helpers


    func togglePaymentTimeSection(hide: Bool) {
        let tableModifier: () = hide ? {
            if let indexPath = viewModel?.indexPath(forRowNamed: Step2Row.paymentTime.rawValue) {
                viewModel?.remove(sectionAtIndex: indexPath.section)
                table.deleteSections([indexPath.section], with: .automatic)
            }
            }() : {
                if viewModel?.indexPath(forRowNamed: Step2Row.paymentTime.rawValue) == nil,
                   bookingDetails.userCanChooseWhenToPay {
                    viewModel?.add(section: paymentIntervalSection(), index: 0)
                    table.insertSections([0], with: .automatic)
                }
            }()

        table.beginUpdates()
        tableModifier
        table.endUpdates()
    }

    private func removeNewAddressRows() -> [IndexPath] {
        guard let viewModel = viewModel else { return [] }

        let tags: [CountryActionableRow] = [.country, .postCode, .addressLine1, .addressLine2, .addressLine3]

        // Reverse the array to removed rows from the last to the first
        // This way we don't lose reference to indexPaths
        return tags.reversed().compactMap { viewModel.remove(rowNamed: $0.rawValue) }
    }

    private func addNewAddressRows(startingIndexPath: IndexPath) -> [IndexPath] {
        guard let viewModel = viewModel else { return [] }

        let row = startingIndexPath.row
        let section = startingIndexPath.section
        var addedIndexPaths: [IndexPath] = []

        let countryIndexPath = IndexPath(item: row + 1, section: section)
        let selectedCountry = paymentCardToPrePopulate?.address?.country ?? Country.greatBritain

        var addressLineTraits = FormekaTextFieldTraits()
        addressLineTraits.autocapitalizationType = .words

        var optionalAddressLineTraits = FormekaTextFieldTraits()
        optionalAddressLineTraits.placeholder = PILocalizedString(
            "cardDetailsOptional",
            comment: "Card details form: optional placeholder"
        )

        // Country
        viewModel.add(row: countryRow(selectedCountry: selectedCountry), at: countryIndexPath)
        addedIndexPaths.append(countryIndexPath)

        // PostCode
        if let postCodeRow = postCodeRow(for: selectedCountry, postCode: paymentCardToPrePopulate?.address?.postcode) {
            let postcodeIndexPath = IndexPath(item: row + 2, section: section)
            viewModel.add(row: postCodeRow, at: postcodeIndexPath)
            addedIndexPaths.append(postcodeIndexPath)
        }

        // Address line 1
        let line1IndexPath = IndexPath(item: row + 3, section: section)
        viewModel.add(
            row: textFieldRow(
                name: CountryActionableRow.addressLine1.rawValue,
                title: PILocalizedString(
                    "cardDetailsAddressLine1",
                    comment: "Card details form: address line 1 label"
                ),
                value: paymentCardToPrePopulate?.address?.line1,
                traits: addressLineTraits,
                onBlurValidators: [RequiredValidator()]
            ),
            at: line1IndexPath
        )
        addedIndexPaths.append(line1IndexPath)

        // Address line 2
        let line2IndexPath = IndexPath(item: row + 4, section: section)
        viewModel.add(
            row: textFieldRow(
                name: CountryActionableRow.addressLine2.rawValue,
                title: PILocalizedString("cardDetailsAddressLine2"),
                value: paymentCardToPrePopulate?.address?.line2,
                traits: optionalAddressLineTraits
            ),
            at: line2IndexPath
        )
        addedIndexPaths.append(line2IndexPath)

        // Address line 3
        let line3IndexPath = IndexPath(item: row + 5, section: section)
        viewModel.add(
            row: textFieldRow(
                name: CountryActionableRow.addressLine3.rawValue,
                title: PILocalizedString("cardDetailsAddressLine3"),
                value: paymentCardToPrePopulate?.address?.line3,
                traits: addressLineTraits
            ),
            at: line3IndexPath
        )
        addedIndexPaths.append(line3IndexPath)

        return addedIndexPaths
    }
}

extension FormPaymentStep2: FormekaSegmentedControlCellDelegate {
    func formekaSegmentedControlValueDidChange(cell: FormekaSegmentedControlCell) {
        let paymentOption: PaymentIntervalOption = cell.segmentedControl.selectedSegmentIndex == 0 ? .later : .now

        bookingDetails.selectedPaymentOption = paymentOption
    }
}


// ANAL


extension FormPaymentStep2 {
    func logFirebase() {
        let criteria = bookingDetails.criteria
        let hotel = bookingDetails.hotel

        var parameters: [String: NSObject] = [:]
        parameters[FirebaseAnalytics.Parameter.currency] = (bookingDetails.roomAndMealCost?.currencyCode ?? "") as NSObject
        parameters[FirebaseAnalytics.Parameter.totalBookingPrice] = (bookingDetails.roomAndMealCost?
            .amount ?? 0.0) as NSObject
        parameters[FirebaseAnalytics.Parameter.rateName] = (bookingDetails.rate?.shortDescription ?? "") as NSObject
        parameters[FirebaseAnalytics.Parameter.startDate] = criteria.arrivalDate.parameterString as NSObject
        parameters[FirebaseAnalytics.Parameter.endDate] = (criteria.checkOutDate?.parameterString ?? "") as NSObject
        parameters[FirebaseAnalytics.Parameter.numberOfNights] = criteria.nights as NSObject
        parameters[FirebaseAnalytics.Parameter.numberOfRooms] = criteria.rooms.count as NSObject
        parameters[FirebaseAnalytics.Parameter.numberOfPeople] = criteria.guestsCount as NSObject
        parameters[FirebaseAnalytics.Parameter.hotelCode] = (hotel?.code ?? "") as NSObject

        AnalyticsManager.log(event: FirebaseAnalytics.Event.beginCheckout, parameters: parameters)
    }
}
