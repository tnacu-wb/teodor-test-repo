//
//  ReviewAndBookView+Formeka+PaymentCard.swift
//  PremierInn
//
//  Created by Marcello Mascia on 08/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import UIKit

// CNP Rows
extension ReviewAndBookViewController {
    func cardNotPresentRow(bookingDetails: BookingDetails) -> FormekaModelRow {
        let rowTag = ReviewAndBookRow.cnpToggle.rawValue

        return FormekaModelRow(tag: rowTag, cellSetup: { [weak self] indexPath, _, table in
            guard let self,
                  let cell: SwitchCell = table.dequeueCell(for: indexPath) else {
                return nil
            }

            let cnpText = PILocalizedString("reviewCardNotPresentLabel")
            let cnpTextBoldTarget = PILocalizedString("reviewCardNotPresentLabelBold")
            let textAttributes = [NSAttributedString.Key.font: UIFont.Heading4_Semibold()]

            cell.message.attributedText = cnpText.attributedString(
                with: cnpText.ranges(of: cnpTextBoldTarget),
                attributes: textAttributes
            )

            cell.toggleSwitch.onTintColor = .Tint1
            let isToggleOn = viewModel?.row(named: ReviewAndBookRow.memorableWord.rawValue) != nil
            cell.toggleSwitch.isOn = isToggleOn
            cell.toggled = { toggled in
                let prevOffset = table.contentOffset

                if toggled {
                    if let cnpToggleIndexPath = self.viewModel?
                       .indexPath(forRowNamed: ReviewAndBookRow.cnpToggle.rawValue) {
                        self.addCNPRows(startingIndexPath: cnpToggleIndexPath, bookingDetails: bookingDetails)

                        table.reloadData()
                        table.contentOffset = prevOffset

                        if let cell: FullsizeTextFieldCell = self.viewModel?.cell(
                            forRowNamed: ReviewAndBookRow.memorableWord.rawValue,
                            table: table
                        ) {
                            _ = cell.becomeFirstResponder()
                        }

                        self.refreshCNPRow(cardNotPresentOn: toggled)
                        table.reloadData()
                    }
                } else {
                    _ = self.removeDinnerAllowanceRows()
                    _ = self.removeCNPRows()

                    self.refreshCNPRow(cardNotPresentOn: toggled)
                    table.reloadData()
                    table.contentOffset = prevOffset
                }
            }

            return cell
        })
    }

    @discardableResult
    private func addCNPRows(startingIndexPath: IndexPath, bookingDetails: BookingDetails) -> [IndexPath] {
        guard let viewModel = viewModel else { return [] }

        let section = startingIndexPath.section
        var row = startingIndexPath.row
        var addedIndexPaths: [IndexPath] = []

        func nextRowIndex() -> Int {
            row += 1
            return row
        }

        let memorableWordIndexPath = IndexPath(item: nextRowIndex(), section: section)
        viewModel.add(
            row: memorableWordRow(
                tag: ReviewAndBookRow.memorableWord.rawValue,
                initialValue: bookingDetails.businessAccount?.atosPassword
            ),
            at: memorableWordIndexPath
        )
        addedIndexPaths.append(memorableWordIndexPath)

        let memorableWordInfoIndexPath = IndexPath(item: nextRowIndex(), section: section)
        viewModel.add(
            row: memorableWordInfoRow(tag: ReviewAndBookRow.memorableWordInfo.rawValue),
            at: memorableWordInfoIndexPath
        )
        addedIndexPaths.append(memorableWordInfoIndexPath)

        let dinnerAllowanceDottedSeparatorIndexPath = IndexPath(item: nextRowIndex(), section: section)
        viewModel.add(
            row: dottedSeparatorRow(tag: ReviewAndBookRow.dinnerAllowanceDottedSeparator.rawValue),
            at: dinnerAllowanceDottedSeparatorIndexPath
        )
        addedIndexPaths.append(dinnerAllowanceDottedSeparatorIndexPath)

        if bookingDetails.shouldShowRestaurantAllowancesForCNP {
            let dinnerAllowanceToggleIndexPath = IndexPath(item: nextRowIndex(), section: section)
            viewModel.add(row: dinnerAllowanceRow(bookingDetails: bookingDetails), at: dinnerAllowanceToggleIndexPath)
            addedIndexPaths.append(dinnerAllowanceToggleIndexPath)
        } else {
            // show info message telling user dinner/drink allowances not available at this hotel
        }

        let parkingToggleIndexPath = IndexPath(item: nextRowIndex(), section: section)
        viewModel.add(row: parkingToggleRow(), at: parkingToggleIndexPath)
        addedIndexPaths.append(parkingToggleIndexPath)

        let wifiToggleIndexPath = IndexPath(item: nextRowIndex(), section: section)
        viewModel.add(row: wifiToggleRow(), at: wifiToggleIndexPath)
        addedIndexPaths.append(wifiToggleIndexPath)

        return addedIndexPaths
    }

    private func refreshCNPRow(cardNotPresentOn: Bool) {
        guard let existingCnpRow = viewModel?.indexPath(forRowNamed: ReviewAndBookRow.paymentTimeMessage.rawValue)
            else { return }

        let informativeText: String = {
            if (UserSessionManager.sharedInstance.currentUser?.isBusiness ?? false) == false { return PILocalizedString(
                "reviewPayOnArrivalMessage",
                comment: "Review and Book: pay on arrival message"
            ) }

            guard let centrallyStoredCard = UserSessionManager.sharedInstance.currentUser?.centrallyStoredBusinessCard
                else { return PILocalizedString(
                    "reviewPayOnArrivalMessage",
                    comment: "Review and Book: pay on arrival message"
                ) }

            if centrallyStoredCard.cardType.cardCode == "AT" {
                return cardNotPresentOn ? PILocalizedString("reviewPayOnArrivalBACCnPOn") :
                    PILocalizedString("reviewPayOnArrivalBACCnPOff")
            } else {
                return cardNotPresentOn ? PILocalizedString("reviewPayOnArrivalCreditOrDebitCnPOn") :
                    PILocalizedString("reviewPayOnArrivalCreditOrDebitCnPOff")
            }
        }()

        let newCNPRow = infoTextRow(
            withName: ReviewAndBookRow.paymentTimeMessage.rawValue,
            andText: informativeText,
            imageOverride: UIImage(named: "importantInfo"),
            colourOverride: .BasePurple
        )

        _ = viewModel?.remove(rowNamed: ReviewAndBookRow.paymentTimeMessage.rawValue)
        viewModel?.add(row: newCNPRow, at: existingCnpRow)
    }

    private func dinnerAllowanceRow(bookingDetails: BookingDetails) -> FormekaModelRow {
        FormekaModelRow(
            tag: ReviewAndBookRow.dinnerAllowanceToggle.rawValue,
            cellSetup: { [weak self] indexPath, _, table in
            guard let cell = self?.simpleToggleCell(
                withTitle: PILocalizedString("reviewDinnerAllowanceTitleLabel"),
                subTitle: PILocalizedString("reviewDinnerAllowanceSubtitleLabel"),
                isOn: self?.viewModel?.row(named: ReviewAndBookRow.dinnerAllowanceBudget.rawValue) != nil,
                toggle: { toggled in
                    if toggled {
                        if let dinnerAllowanceToggleStartingIndexPath = self?.viewModel?
                           .indexPath(forRowNamed: ReviewAndBookRow.dinnerAllowanceToggle.rawValue) {
                            if self?.addDinnerAllowanceRows(
                                startingIndexPath: dinnerAllowanceToggleStartingIndexPath,
                                bookingDetails: bookingDetails
                            ) != nil {
                                if let indexPath = self?.viewModel?
                                   .indexPath(forRowNamed: ReviewAndBookRow.dinnerAllowanceBudget.rawValue),
                                   let cell = self?.table.cellForRow(at: indexPath) as? FullsizeTextFieldCell {
                                    cell.fullLengthTextField.becomeFirstResponder()
                                }
                                table.reloadData()
                            }
                        }
                    } else {
                        if self?.removeDinnerAllowanceRows() != nil {
                            table.reloadData()
                        }
                    }
                },
                indexPath: indexPath
            ) else { return nil }

            return cell
        }
        )
    }

    private func addDinnerAllowanceRows(startingIndexPath: IndexPath, bookingDetails: BookingDetails) -> [IndexPath] {
        guard let viewModel = viewModel else { return [] }

        let row = startingIndexPath.row
        let section = startingIndexPath.section
        var addedIndexPaths: [IndexPath] = []

        let dinnerAllowanceDescriptionIndexPath = IndexPath(item: row + 1, section: section)
        viewModel.add(row: dinnerAllowanceDescriptionRow(), at: dinnerAllowanceDescriptionIndexPath)
        addedIndexPaths.append(dinnerAllowanceDescriptionIndexPath)

        let dinnerAllowanceBudgetIndexPath = IndexPath(item: row + 2, section: section)
        viewModel.add(
            row: dinnerAllowanceBudgetRow(initialValue: bookingDetails.businessAccount?.dinnerAllowance),
            at: dinnerAllowanceBudgetIndexPath
        )
        addedIndexPaths.append(dinnerAllowanceBudgetIndexPath)

        let dinnerAllowanceIncludeAlcoholIndexPath = IndexPath(item: row + 3, section: section)
        viewModel.add(row: dinnerAllowanceAlcoholRow(), at: dinnerAllowanceIncludeAlcoholIndexPath)
        addedIndexPaths.append(dinnerAllowanceIncludeAlcoholIndexPath)

        return addedIndexPaths
    }

    private func dinnerAllowanceDescriptionRow() -> FormekaModelRow {
        FormekaModelRow(tag: ReviewAndBookRow.dinnerAllowanceDescription.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: FormekaFreeTextCell = table.dequeueCell(for: indexPath) else { return nil }

            let dinnerAllowanceDescriptionAttributedString = NSMutableAttributedString(
                string: PILocalizedString("dinnerAllowanceDescription"),
                attributes: [NSAttributedString.Key.font: UIFont.Heading4_Semibold()]
            )
            cell.message.attributedText = dinnerAllowanceDescriptionAttributedString
            cell.hiddenSeparatorLocations = [.bottom, .top]
            cell.topConstraint.constant = 0
            cell.bottomConstraint.constant = 10

            return cell
        })
    }

    private func dinnerAllowanceBudgetRow(initialValue: String?) -> FormekaModelRow {
        let row = FormekaModelRow(
            tag: ReviewAndBookRow.dinnerAllowanceBudget.rawValue,
            title: PILocalizedString("dinner allowance", comment: ""),
            inlineValidators: [CostValidator(min: Constants.MealAllowance.min, max: Constants.MealAllowance.max)],
            onBlurValidators: [.required],
            cellSetup: { [unowned self] indexPath, row, table in
                guard let cell: CostInputCell = table.dequeueCell(for: indexPath) else { return nil }

                cell.accessibilityIdentifier = "dinnerAllowanceAcc"
                cell.fullLengthTextField.keyboardType = .decimalPad
                cell.fullLenghtTextFieldWidthConstrant.constant = 100
                // supports only £
                cell.shouldTrimCurrencyPrefix = true
                cell.fullLengthTextField.text = "£\((row.value as? String ?? "").replacingOccurrences(of: "£", with: ""))"
                cell.fullLengthTextField.inputAccessoryView = FormekaViewController.inputToolbar(view: view)
                cell.errorMessage = row.error?.localizedDescription
                cell.delegate = self

                return cell
        }
        )
        row.value = initialValue

        return row
    }

    private func dinnerAllowanceAlcoholRow() -> FormekaModelRow {
        FormekaModelRow(
            tag: ReviewAndBookRow.dinnerAllowanceIncludeAlcohol.rawValue,
            cellSetup: { [unowned self] indexPath, row, _ in
            guard let cell = simpleToggleCell(
                withTitle: PILocalizedString(
                    "dinnerAllowanceIncludeAlcoholTitleLabel",
                    comment: "Review and Book: Dinner allowance Include Alcohol title switch label bold text"
                ),
                subTitle: PILocalizedString(
                    "dinnerAllowanceIncludeAlcoholSubtitleLabel",
                    comment: "Review and Book: Dinner allowance Include Alcohol subtitle switch label text"
                ),
                isOn: row.value as? Bool == true,
                toggle: { row.value = $0 },
                indexPath: indexPath
            ) else { return nil }

            return cell
        }
        )
    }

    private func removeDinnerAllowanceRows() -> [IndexPath] {
        guard let viewModel = viewModel else { return [] }

        let tags: [ReviewAndBookRow] = [
            .dinnerAllowanceDescription,
            .dinnerAllowanceBudget,
            .dinnerAllowanceIncludeAlcohol
        ]

        return tags.reversed().compactMap { viewModel.remove(rowNamed: $0.rawValue) }
    }

    private func parkingToggleRow() -> FormekaModelRow {
        FormekaModelRow(tag: ReviewAndBookRow.parkingIncluded.rawValue, cellSetup: { [unowned self] indexPath, row, _ in
            guard let cell = simpleToggleCell(
                withTitle: PILocalizedString(
                    "reviewParkingAllowedTitleLabel",
                    comment: "Review and Book: CNP parking allowed title"
                ),
                subTitle: PILocalizedString(
                    "reviewParkingAllowedSubtitleLabel",
                    comment: "Review and Book: CNP parking allowed subtitle"
                ),
                isOn: row.value as? Bool == true,
                toggle: { row.value = $0 },
                indexPath: indexPath
            ) else { return nil }

            return cell
        })
    }

    private func wifiToggleRow() -> FormekaModelRow {
        FormekaModelRow(tag: ReviewAndBookRow.wifiIncluded.rawValue, cellSetup: { [unowned self] indexPath, row, _ in
            guard let cell = simpleToggleCell(
                withTitle: PILocalizedString(
                    "reviewWifiAllowedTitleLabel",
                    comment: "Review and Book: CNP wifi allowed title"
                ),
                subTitle: PILocalizedString(
                    "reviewWifiAllowedSubtitleLabel",
                    comment: "Review and Book: CNP wifi allowed subtitle"
                ),
                isOn: row.value as? Bool == true,
                toggle: { row.value = $0 },
                indexPath: indexPath
            ) else { return nil }

            return cell
        })
    }

    private func removeCNPRows() -> [IndexPath] {
        guard let viewModel = viewModel else { return [] }

        let tags: [ReviewAndBookRow] = [
            .memorableWord,
            .memorableWordInfo,
            .dinnerAllowanceDottedSeparator,
            .dinnerAllowanceToggle,
            .parkingIncluded,
            .wifiIncluded
        ]

        return tags.reversed().compactMap { viewModel.remove(rowNamed: $0.rawValue) }
    }
}
