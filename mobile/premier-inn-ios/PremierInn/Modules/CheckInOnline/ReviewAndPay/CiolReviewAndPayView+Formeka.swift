//
//  CiolReviewAndPayView+Formeka.swift
//  PremierInn
//
//  Created by Muresan, Andreea (Cognizant) on 30.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Formeka
import UIKit

extension CiolReviewAndPayViewController {
    func tableViewModel(with ciolReviewAndPayViewModel: CiolReviewAndPayViewModel) -> FormekaViewModel {
        var sections = [FormekaModelSection]()

        if ciolReviewAndPayViewModel.isGermanHotel {
            sections.append(paymentDisclaimer(with: ciolReviewAndPayViewModel))
        }
        sections.append(summarySection(with: ciolReviewAndPayViewModel))
        sections.append(cccpPaymentMethodsSections(with: ciolReviewAndPayViewModel))
        sections.append(billingAddressSection(with: ciolReviewAndPayViewModel))

        if ciolReviewAndPayViewModel.isBillingFieldOn {
            var billingAddressRows = [FormekaModelRow]()
            billingAddressRows.append(nationalityRow(with: EditDetailsModel(
                flow: .paymentMethod,
                address: ciolReviewAndPayViewModel.billingAddress
            )))
            billingAddressRows.append(postCodeRow(with: EditDetailsModel(
                flow: .paymentMethod,
                address: ciolReviewAndPayViewModel.billingAddress
            )))
            billingAddressRows.append(addressLine1Row(with: EditDetailsModel(
                flow: .paymentMethod,
                address: ciolReviewAndPayViewModel.billingAddress
            )))
            billingAddressRows.append(addressLine2Row(with: EditDetailsModel(
                flow: .paymentMethod,
                address: ciolReviewAndPayViewModel.billingAddress
            )))
            billingAddressRows.append(addressLine3Row(with: EditDetailsModel(
                flow: .paymentMethod,
                address: ciolReviewAndPayViewModel.billingAddress
            )))
            let billingAddressSection = FormekaModelSection(header: nil, rows: billingAddressRows, footer: nil)

            sections.append(billingAddressSection)
        }
        return FormekaViewModel(sections: sections)
    }
}

private extension CiolReviewAndPayViewController {
    func paymentDisclaimer(with viewModel: CiolReviewAndPayViewModel) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.hiddenSeparatorLocations = [.top, .bottom]

            let mutableAttributedString = NSMutableAttributedString(attributedString: viewModel
                .deRegCardPaymentInformationMessage)
            let paragraphStyle = NSMutableParagraphStyle()
            paragraphStyle.lineSpacing = 4

            mutableAttributedString.addAttribute(
                .paragraphStyle,
                value: paragraphStyle,
                range: NSRange(location: 0, length: mutableAttributedString.string.count)
            )

            cell.content.attributedText = NSAttributedString(attributedString: mutableAttributedString)
            cell.content.textColor = .ColourDL1
            cell.content.backgroundColor = .clear

            cell.containerView.backgroundColor = NotificationStyle.info.background
            cell.containerView.layer.borderColor = NotificationStyle.info.tint.withAlphaComponent(0.4).cgColor
            cell.containerView.layer.cornerRadius = 4

            cell.icon.image = NotificationStyle.info.icon
            cell.icon.tintColor = NotificationStyle.info.tint

            cell.backgroundColor = .white

            return cell
        }))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    func summarySection(with viewModel: CiolReviewAndPayViewModel) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: BookingSummaryCIOLCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.configure(with: viewModel.bookingSummaryViewModel)
            cell.isUserInteractionEnabled = false

            return cell
        }))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    func cccpPaymentMethodsSections(with viewModel: CiolReviewAndPayViewModel) -> FormekaModelSection {
        var paymentSectionRows: [FormekaModelRow] = []

        if viewModel.paymentMethodPIBAUnavailable.shouldShow {
            paymentSectionRows.append(
                inlineMessageInfoRow(message: viewModel.paymentMethodPIBAUnavailable.message)
            )
        }

        paymentSectionRows.append(paymentMethodsRow(with: viewModel.paymentMethods))

        let selectedPaymentMethod = viewModel.selectedPaymentMethod
        if selectedPaymentMethod?.paymentMethodType == .storedCompanyBB && selectedPaymentMethod?.card?.type
           .isBusiness == false {
            // ADD CVV REQUIRED FROM TRAVEL MANAGER INFO MESSAGE
            paymentSectionRows.append(cvvRequiredForBusinessCardRow(viewModel: viewModel))
        }

        let header = FormekaModelHeaderFooter(height: 40, viewSetup: { _, _ in
            let headerView = UITableViewHeaderFooterView()
            let label = UILabel()
            label.text = PILocalizedString("ciolPaymentType")
            label.font = UIFont.Heading2_ExtraBold()
            label.textColor = .BaseBlack
            label.translatesAutoresizingMaskIntoConstraints = false
            label.accessibilityTraits.insert(.header)

            headerView.contentView.addSubview(label)
            NSLayoutConstraint.activate([
                label.leadingAnchor.constraint(equalTo: headerView.contentView.leadingAnchor, constant: 16),
                label.trailingAnchor.constraint(equalTo: headerView.contentView.trailingAnchor, constant: -16),
                label.centerYAnchor.constraint(equalTo: headerView.contentView.centerYAnchor)
            ])

            return headerView
        })

        return FormekaModelSection(header: header, rows: paymentSectionRows, footer: nil)
    }

    private func paymentMethodsRow(with paymentMethodModels: [PaymentMethodViewModelType]) -> FormekaModelRow {
        FormekaModelRow(tag: "", cellSetup: { [weak self] indexPath, _, table in
            guard let cell: DynamicListCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.paddingLeft.constant = 16
            cell.paddingRight.constant = 16
            cell.paddingBottom.constant = 8

            cell.stackView.arrangedSubviews.forEach { $0.removeFromSuperview() }

            if cell.stackView.arrangedSubviews.isEmpty {
                for (index, paymentMethod) in paymentMethodModels.enumerated() {
                    guard let paymentMethodView: UIView = {
                        switch paymentMethod.type {
                        case .newCreditDebitCard, .applePay, .paypal:
                            return self?.newPaymentMethodView(with: paymentMethod, isFirst: index == 0)
                        case .stored, .storedCompanyBB, .storedPersonalBB:
                            return self?.storedPaymentMethodView(with: paymentMethod, isFirst: index == 0)
                        case .newBAC, .newBACEuro:
                            return self?.newPaymentMethodView(with: paymentMethod, isFirst: index == 0)
                        case .reserveWithoutCard:
                            return nil
                        }
                    }() else { continue }

                    cell.stackView.addArrangedSubview(paymentMethodView)
                }
            }

            return cell
        })
    }

    private func storedPaymentMethodView(with paymentViewModel: PaymentMethodViewModelType, isFirst: Bool) -> UIView? {
        guard let paymentMethodView: CiolStoredPaymentMethodView = UIView
              .fromNib(nibName: String(describing: CiolStoredPaymentMethodView.self)) else { return nil }

        paymentMethodView.customise(with: paymentViewModel, isFirst: isFirst)
        paymentMethodView.action = { [weak self] in
            self?.eventHandler?.updateSelectedPaymentMethod(paymentViewModel: paymentViewModel)
        }

        return paymentMethodView
    }

    private func newPaymentMethodView(with paymentViewModel: PaymentMethodViewModelType, isFirst: Bool) -> UIView? {
        guard let paymentMethodView: CiolPayWithNewCardView = UIView
              .fromNib(nibName: String(describing: CiolPayWithNewCardView.self)) else { return nil }

        paymentMethodView.customise(with: paymentViewModel, isFirst: isFirst)
        paymentMethodView.action = { [weak self] in
            self?.eventHandler?.updateSelectedPaymentMethod(paymentViewModel: paymentViewModel)
        }

        return paymentMethodView
    }

    func cvvRequiredForBusinessCardRow(viewModel: CiolReviewAndPayViewModel) -> FormekaModelRow {
        FormekaModelRow(tag: ReviewAndBookRow.paymentAuthInfo.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath) else { return nil }

            let paragraphStyle = NSMutableParagraphStyle()
            paragraphStyle.lineSpacing = 4

            cell.bottomConstraint.constant = 10
            cell.topConstraint.constant = 0

            let message = viewModel
                .currentUserAccessLevel ?? .stayer == .superUser ?
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

    func inlineMessageInfoRow(message: String) -> FormekaModelRow {
        FormekaModelRow(tag: ReviewAndBookRow.paymentAuthInfo.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: FlexibleContentInformationCell = table.dequeueCell(for: indexPath) else { return nil }

            let paragraphStyle = NSMutableParagraphStyle()
            paragraphStyle.lineSpacing = 4

            cell.bottomConstraint.constant = 10
            cell.topConstraint.constant = 0

            let message = message

            cell.content.attributedText = NSAttributedString(string: message, attributes: [.paragraphStyle: paragraphStyle])
            cell.content.textColor = .ColourDL1
            cell.content.font = .BodySmall()

            cell.containerView.backgroundColor = NotificationStyle.info.background
            cell.containerView.layer.cornerRadius = 4
            cell.containerView.layer.borderColor = NotificationStyle.info.tint.withAlphaComponent(0.4).cgColor
            cell.containerView.layer.borderWidth = 1

            cell.icon.image = NotificationStyle.info.icon
            cell.icon.tintColor = NotificationStyle.info.tint

            cell.backgroundColor = .white

            return cell
        })
    }

    func billingAddressSection(with viewModel: CiolReviewAndPayViewModel) -> FormekaModelSection {
        let rows = billingAddressRows(with: viewModel)

        let header = FormekaModelHeaderFooter(height: 40, viewSetup: { _, _ in
            let headerView = UITableViewHeaderFooterView()
            let label = UILabel()
            label.text = PILocalizedString("ciolBillingAddress")
            label.font = UIFont.Heading2_ExtraBold()
            label.textColor = .BaseBlack
            label.translatesAutoresizingMaskIntoConstraints = false
            label.accessibilityTraits.insert(.header)

            headerView.contentView.addSubview(label)
            NSLayoutConstraint.activate([
                label.leadingAnchor.constraint(equalTo: headerView.contentView.leadingAnchor, constant: 16),
                label.trailingAnchor.constraint(equalTo: headerView.contentView.trailingAnchor, constant: -16),
                label.centerYAnchor.constraint(equalTo: headerView.contentView.centerYAnchor)
            ])
            ContentsquareConfig.mask(view: headerView)
            return headerView
        })

        return FormekaModelSection(header: header, rows: rows, footer: nil)
    }

    private func billingAddressRows(with viewModel: CiolReviewAndPayViewModel) -> [FormekaModelRow] {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(cellSetup: { indexPath, _, table in
            guard let cell: CiolReviewAndPayBillingAddressCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.customise(with: viewModel.formattedBillingAddress)
            cell.didToggleSwitch = { [weak self] isOn in
                self?.eventHandler?.showBillingAddressField(!isOn)
            }
            return cell
        }))
        return rows
    }

    func nationalityRow(with editDetailsModel: EditDetailsModel) -> FormekaModelRow {
        let row = FormekaModelRow(
            tag: CountryActionableRow.country.rawValue,
            cellSetup: { indexPath, row, table in
            guard let cell: EditDetailsCell = table.dequeueReusableCell(
                withIdentifier: EditDetailsCell.reuseIdentifier,
                for: indexPath
            ) as? EditDetailsCell
            else { return nil }
            let editDetailsCellModel = EditDetailsCellModel(
                title: PILocalizedString("countryRowTitle") + "*",
                textFieldText: row.value?.displayName ?? "",
                errorMessage: row.error?.localizedDescription ?? "",
                rightTextFieldImage: .arrowDown,
                textFieldIsInteractionEnabled: false
            )
            cell.configureCell(editDetailsCellModel: editDetailsCellModel)
            cell.delegate = self
            return cell
        },
            didSelect: { [weak self] index, _ in
            self?.eventHandler?.showCountriesView(indexPath: index)
        }
        )
        row.value = editDetailsModel.address?.country?.displayName
        return row
    }

    func postCodeRow(with editDetailsModel: EditDetailsModel) -> FormekaModelRow {
        let isGreatBritain = editDetailsModel.address?.country == .greatBritain
        let isGerman = editDetailsModel.address?.country == .germany

        let row = FormekaModelRow(
            tag: CountryActionableRow.postCode.rawValue,
            onBlurValidators: [isGreatBritain ? .ukPostCode : isGerman ? .dePostCode : .required],
            cellSetup: { indexPath, row, table in
            guard let cell: EditDetailsCell = table.dequeueReusableCell(
                withIdentifier: EditDetailsCell.reuseIdentifier,
                for: indexPath
            ) as? EditDetailsCell
            else { return nil }
            let editDetailsCellModel = EditDetailsCellModel(
                title: PILocalizedString("postCodeSearchInputPlaceholder").capitalized + "*",
                textFieldText: row.value?.displayName ?? "",
                errorMessage: row.error?.localizedDescription ?? "",
                showActionButton: editDetailsModel.showFindAddressButton
            )
            cell.didTapActionButton = { [weak self] in
                self?.eventHandler?.showPostcodePicker(with: cell.textField.text)
            }
            cell.configureCell(editDetailsCellModel: editDetailsCellModel)
            cell.delegate = self
            cell.valueChanged = { [weak self] in
                self?.eventHandler?.updateAddress(with: .postCode(row.value as? String ?? ""))
            }
            ContentsquareConfig.mask(view: cell)
            return cell
        }
        )
        row.value = editDetailsModel.address?.postcode
        return row
    }

    func addressLine1Row(with editDetailsModel: EditDetailsModel) -> FormekaModelRow {
        let row = FormekaModelRow(
            tag: CountryActionableRow.addressLine1.rawValue,
            onBlurValidators: [.required],
            cellSetup: { indexPath, row, table in
            guard let cell: EditDetailsCell = table.dequeueReusableCell(
                withIdentifier: EditDetailsCell.reuseIdentifier,
                for: indexPath
            ) as? EditDetailsCell
            else { return nil }
            let editDetailsCellModel = EditDetailsCellModel(
                title: PILocalizedString("userDetailsAddressLine1"),
                textFieldText: row.value?.displayName ?? "",
                errorMessage: row.error?.localizedDescription ?? ""
            )
            cell.configureCell(editDetailsCellModel: editDetailsCellModel)
            cell.delegate = self
            cell.valueChanged = { [weak self] in
                self?.eventHandler?.updateAddress(with: .addressLine1(row.value as? String ?? ""))
            }
            ContentsquareConfig.mask(view: cell)
            return cell
        }
        )
        row.value = editDetailsModel.address?.line1
        return row
    }

    func addressLine2Row(with editDetailsModel: EditDetailsModel) -> FormekaModelRow {
        let row = FormekaModelRow(
            tag: CountryActionableRow.addressLine2.rawValue,
            cellSetup: { indexPath, row, table in
            guard let cell: EditDetailsCell = table.dequeueReusableCell(
                withIdentifier: EditDetailsCell.reuseIdentifier,
                for: indexPath
            ) as? EditDetailsCell
            else { return nil }
            let editDetailsCellModel = EditDetailsCellModel(
                title: PILocalizedString("userDetailsAddressLine2"),
                textFieldText: row.value?.displayName ?? "",
                errorMessage: row.error?.localizedDescription ?? ""
            )
            cell.configureCell(editDetailsCellModel: editDetailsCellModel)
            cell.delegate = self
            cell.valueChanged = { [weak self] in
                self?.eventHandler?.updateAddress(with: .addressLine2(row.value as? String ?? ""))
            }
            ContentsquareConfig.mask(view: cell)
            return cell
        }
        )
        row.value = editDetailsModel.address?.line2
        return row
    }

    func addressLine3Row(with editDetailsModel: EditDetailsModel) -> FormekaModelRow {
        let row = FormekaModelRow(
            tag: CountryActionableRow.addressLine3.rawValue,
            cellSetup: { indexPath, row, table in
            guard let cell: EditDetailsCell = table.dequeueReusableCell(
                withIdentifier: EditDetailsCell.reuseIdentifier,
                for: indexPath
            ) as? EditDetailsCell
            else { return nil }
            let editDetailsCellModel = EditDetailsCellModel(
                title: PILocalizedString("userDetailsAddressLine3"),
                textFieldText: row.value?.displayName ?? "",
                errorMessage: row.error?.localizedDescription ?? ""
            )
            cell.configureCell(editDetailsCellModel: editDetailsCellModel)
            cell.delegate = self
            cell.valueChanged = { [weak self] in
                self?.eventHandler?.updateAddress(with: .addressLine3(row.value as? String ?? ""))
            }
            ContentsquareConfig.mask(view: cell)
            return cell
        }
        )
        row.value = editDetailsModel.address?.line3
        return row
    }
}
