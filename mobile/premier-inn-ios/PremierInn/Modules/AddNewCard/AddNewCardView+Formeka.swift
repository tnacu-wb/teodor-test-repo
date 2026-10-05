//
//  AddNewCardView+Formeka.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 04/10/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import Foundation
import Formeka
import UIKit

extension AddNewCardView {
    func viewModelSections(addNewCardViewModel: AddNewCardViewModel) -> [FormekaModelSection] {
        var sections: [FormekaModelSection] = []

        sections.append(FormekaModelSection(header: titleHeader, rows: [savedCardSyncInfoRow], footer: nil))

        if let section = addressSectionView?.addressSection(
            for: self,
            optionalAddressHeader: PILocalizedString("addNewCardBillingAddressSectionTitle"),
            footerHeight: 10
        ) {
            sections.append(section)
        }

        if let paymentMethodsViewModel = addNewCardViewModel.paymentMethodsViewModel {
            sections.append(paymentMethodsSection(with: paymentMethodsViewModel))
        }

        sections.append(submitButtonSection())

        return sections
    }

    private var titleHeader: FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: 50, viewSetup: { _, table in
            guard let header: TitleAndOptionalImageHeader = table.headerFooterView() else { return nil }

            header.contentView.backgroundColor = .BaseWhite
            header.backgroundColor = .BaseWhite

            header.titleLabel.text = PILocalizedString("addNewCardTitle")
            header.titleLabel.accessibilityTraits.insert(.header)

            header.imageView.image = nil

            return header
        })
    }

    private var savedCardSyncInfoRow: FormekaModelRow {
        iconInfoRow(
            tag: AddNewCardViewRow.savedCardSyncInfo.rawValue,
            text: PILocalizedString("myAccountSavedCardSyncInfo"),
            topPadding: 8,
            style: .info
        )
    }

    func paymentMethodsSection(with viewModel: AddNewCardPaymentMethodsViewModel) -> FormekaModelSection {
        var paymentSectionRows: [FormekaModelRow] = []

        paymentSectionRows.append(paymentMethodsRow(with: viewModel.paymentMethods))

        if viewModel.shouldShowCNP {
            let cnpEnabled = viewModel.cnpEnabled

            paymentSectionRows.append(newCardCNPView(with: cnpEnabled))

            if viewModel.cnpEnabled {
                paymentSectionRows.append(memorableWordRow(tag: AddNewCardViewRow.memorableWord.rawValue, initialValue: nil))
                paymentSectionRows.append(memorableWordInfoRow(tag: AddNewCardViewRow.memorableWordInfo.rawValue))
            }
        }

        return FormekaModelSection(
            header: header(title: viewModel.title, height: 70),
            rows: paymentSectionRows,
            footer: nil
        )
    }

    private func paymentMethodsRow(with paymentMethodModels: [AddNewCardPaymentMethodViewModel]) -> FormekaModelRow {
        FormekaModelRow(tag: AddNewCardViewRow.paymentMethod.rawValue, cellSetup: { [weak self] indexPath, _, table in
            guard let cell: DynamicListCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.paddingTop.constant = 0
            cell.paddingLeft.constant = 20
            cell.paddingRight.constant = 20
            cell.paddingBottom.constant = 8

            cell.stackView.setBackgroundColor(.clear, cornerRadius: 3, borderWidth: 1, borderColor: .greyBorder)

            _ = cell.stackView.arrangedSubviews.map { $0.removeFromSuperview() }

            if cell.stackView.arrangedSubviews.isEmpty {
                for (index, paymentMethod) in paymentMethodModels.enumerated() {
                    guard let paymentMethodView: UIView = {
                        self?.newPaymentMethodView(
                            with: paymentMethod,
                            withRadio: true,
                            isLast: index == paymentMethodModels.indices.last
                        )
                    }() else {
                        continue
                    }

                    cell.stackView.addArrangedSubview(paymentMethodView)
                }
            }

            return cell
        })
    }

    private func newPaymentMethodView(
        with viewModel: AddNewCardPaymentMethodViewModel,
        withRadio: Bool,
        isLast: Bool
    ) -> UIView? {
        guard let paymentMethodView: PayWithNewCardView = UIView
              .fromNib(nibName: String(describing: PayWithNewCardView.self)) else { return nil }

        paymentMethodView.methodType.text = viewModel.name
        paymentMethodView.cardUrls = viewModel.imageUrls
        paymentMethodView.radioButton.isHidden = !withRadio
        paymentMethodView.radioButton.isSelected = viewModel.selected
//        paymentMethodView.accessibilityIdentifier = viewModel.type.getAccessbilityIdentifier
        paymentMethodView.action = { [weak self] in
            self?.eventHandler?.selectedPaymentMethod(type: viewModel.type.rawValue)
        }

        if isLast {
            paymentMethodView.separatorView.isHidden = true
        }

        return paymentMethodView
    }

    private func newCardCNPView(with value: Bool) -> FormekaModelRow {
        FormekaModelRow(tag: AddNewCardViewRow.cnpRow.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: SwitchCell = table.dequeueCell(for: indexPath) else { return nil }

            let cnpText = PILocalizedString("reviewCardNotPresentLabel")
            cell.message.attributedText = cnpText.attributedString(
                with: cnpText.ranges(of: PILocalizedString("reviewCardNotPresentLabelBold")),
                attributes: [NSAttributedString.Key.font: UIFont.Heading4_Semibold()]
            )

            cell.toggleSwitch.isOn = value
            cell.hiddenSeparatorLocations = [.top, .bottom]

            cell.toggled = { value in
                self.eventHandler?.toggledCNP(toggle: value)
            }

            return cell
        })
    }

    private func submitButtonSection() -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(
            tag: AddNewCardViewRow.submitButton.rawValue,
            cellSetup: { [weak self] indexPath, _, table in
            guard let cell: FormekaSubmitButtonCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.delegate = self
            cell.backgroundColor = .clear
            cell.contentView.backgroundColor = .clear

            cell.button.backgroundColor = .Tint1
            cell.button.setTitleColor(.white, for: .normal)
            cell.button.setTitle(PILocalizedString("addNewCardButtonTitle"), for: .normal)
            cell.button.titleLabel?.font = .Body_Semibold()

            cell.button.layer.cornerRadius = 5
            cell.button.layer.borderColor = UIColor.Tint1.cgColor
            cell.button.layer.borderWidth = 1

            cell.hiddenSeparatorLocations = [.top, .bottom]

            return cell
        }
        ))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }
}
