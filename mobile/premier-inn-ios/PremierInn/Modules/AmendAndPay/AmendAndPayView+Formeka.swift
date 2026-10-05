//
//  AmendAndPayView+Formeka.swift
//  PremierInn
//
//  Created by Santa Gurung on 17/11/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import Formeka
import UIKit

extension AmendAndPayView {
    func viewModelSections(with amendAndPayViewModel: AmendAndPayViewModel) -> [FormekaModelSection] {
        var sections: [FormekaModelSection] = []
        sections.append(paymentMethodsSection(paymentMethods: amendAndPayViewModel.paymentMethodsViewModel))
        sections.append(billingAddressSection(paymentMethods: amendAndPayViewModel.paymentMethodsViewModel))
        sections.append(totalSection(amendAndPayViewModel: amendAndPayViewModel))
        return sections
    }

    private func paymentMethodsSection(paymentMethods: [AmendPaymentMethodViewModel]) -> FormekaModelSection {
        FormekaModelSection(
            header: sectionTitleRow(title: PILocalizedString("amendAndPayPayment3CPSectionTitle")),
            rows: [
                paymentMethodSubtitleRow(),
                paymentMethodsRow(paymentMethods: paymentMethods)
            ],
            footer: nil
        )
    }

    private func sectionTitleRow(title: String) -> FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: 55, viewSetup: { _, table in
            guard let header: ActionableHeader = table.headerFooterView() else {
                return nil
            }
            header.heading.text = title
            header.actionButton.isHidden = true
            return header
        })
    }

    private func paymentMethodSubtitleRow() -> FormekaModelRow {
        FormekaModelRow(tag: RegisterRow.termsAndConditions.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: FlexibleTextContentCell = table.dequeueCell(for: indexPath) else {
                return nil
            }
            cell.contentView.backgroundColor = .clear
            cell.content.text = PILocalizedString("amendPayNow3CMessage")
            cell.messageTopConstraint.constant = 20
            cell.messageBottomConstraint.constant = 20
            return cell
        })
    }

    private func paymentMethodsRow(paymentMethods: [AmendPaymentMethodViewModel]) -> FormekaModelRow {
        FormekaModelRow { [weak self] indexPath, _, table in
            guard let cell: DynamicListCell = table.dequeueCell(for: indexPath) else {
                return nil
            }
            cell.paddingLeft.constant = 20
            cell.paddingRight.constant = 20
            cell.paddingBottom.constant = 8
            cell.stackView.setBackgroundColor(.clear, cornerRadius: 3, borderWidth: 1, borderColor: .greyBorder)
            cell.stackView.arrangedSubviews.forEach { $0.removeFromSuperview() }

            for paymentMethod in paymentMethods {
                // Currently we only get back same card from API response. When we have new types, we will need to use another view and switch between them
                if let sameCardPaymentMethod = paymentMethod as? SameCardPaymentMethod,
                   let paymentMethodView: UIView = self?.sameCardPaymentMethodView(with: sameCardPaymentMethod) {
                    cell.stackView.addArrangedSubview(paymentMethodView)
                }
            }
            return cell
        }
    }

    private func sameCardPaymentMethodView(with sameCardPaymentMethod: SameCardPaymentMethod) -> UIView? {
        guard let paymentMethodView: StoredPaymentMethodView = UIView
              .fromNib(nibName: String(describing: StoredPaymentMethodView.self)) else {
            return nil
        }
        paymentMethodView.cardTypeAndNumberDescription.text = sameCardPaymentMethod.cardTypeName
        paymentMethodView.cardholder.text = sameCardPaymentMethod.cardHolderName
        paymentMethodView.expiry.text = sameCardPaymentMethod.expiryDate
        paymentMethodView.radioButton.isHidden = false
        paymentMethodView.radioButton.isSelected = sameCardPaymentMethod.isSelected
        paymentMethodView.paymentMethodType = nil
        paymentMethodView.action = { [weak self] in
            self?.presenter?.didSelectPaymentMethod(selectedPaymentMethodViewModel: sameCardPaymentMethod)
        }
        if let url = sameCardPaymentMethod.cardLogoURLs?.first {
            paymentMethodView.cardTypeIcon.setImage(with: url, transition: true)
        } else {
            paymentMethodView.cardTypeIcon.image = nil
        }
        return paymentMethodView
    }

    private func billingAddressSection(paymentMethods: [AmendPaymentMethodViewModel]) -> FormekaModelSection {
        let addressString = paymentMethods.first(where: { $0.isSelected })?.billingAddressText

        var rows = [FormekaModelRow]()

        rows.append(addressSwitchRow(addressString: addressString))

        return FormekaModelSection(
            header: sectionTitleRow(title: PILocalizedString("amendAndPayBillingAddressSectionTitle")),
            rows: rows,
            footer: footer(title: nil, height: 20)
        )
    }

    private func addressSwitchRow(addressString: String?) -> FormekaModelRow {
        let row = FormekaModelRow(tag: Step2Row.billingAddressSwitch.rawValue, cellSetup: { indexPath, _, table in
            guard let cell: SwitchCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.message.text = addressString
            cell.hiddenSeparatorLocations = [.top, .bottom]
            cell.toggleSwitch.isHidden = true // TODO: need to update when other payment methods are added
            return cell
        })

        return row
    }

    private func totalSection(amendAndPayViewModel: AmendAndPayViewModel) -> FormekaModelSection {
        var rows = [FormekaModelRow]()
        rows.append(termsAndConditionsTextRow())
        rows.append(totalPriceRow(amendAndPayViewModel: amendAndPayViewModel))
        rows.append(submitButtonRow(amendAndPayViewModel: amendAndPayViewModel))
        rows.append(gdprFooterRow())

        return FormekaModelSection(header: nil, rows: rows, footer: footer(title: nil, height: 40))
    }

    private func totalPriceRow(amendAndPayViewModel: AmendAndPayViewModel) -> FormekaModelRow {
        FormekaModelRow(tag: ReviewAndBookRow.totalCost.rawValue, cellSetup: { indexPath, _, table in
        guard let cell: BookingReviewTotalPriceCell = table.dequeueCell(for: indexPath) else { return nil }
            cell.contentView.backgroundColor = .whiteTwo
            cell.cityTaxLabel.text = PILocalizedString("amendAndPayCityTaxTitle")
            cell.totalPriceLabel.text = amendAndPayViewModel.totalCost

            return cell
        })
    }

    private func submitButtonRow(amendAndPayViewModel: AmendAndPayViewModel) -> FormekaModelRow {
        FormekaModelRow(tag: ReviewAndBookRow.confirm.rawValue, cellSetup: { [weak self] indexPath, _, table in
            guard let cell: BookingReviewSubmitCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.contentView.backgroundColor = .whiteTwo
            cell.continueButton.addTarget(self, action: #selector(self?.confirmButtonDidTap), for: .touchUpInside)
            cell.continueButton.setTitle(amendAndPayViewModel.ctaButtonTitle, for: .normal)
            cell.continueButton.setTitle("", for: .disabled)
            cell.continueButton.isEnabled = true
            cell.activityIndicator.stopAnimating()

            // Horrible but it works...
            if cell.continueButton.viewWithTag(999) == nil {
                let imageView = UIImageView(image: #imageLiteral(resourceName: "padlock"))
                imageView.frame = cell.continueButton.bounds.insetBy(dx: 20, dy: 0)
                imageView.contentMode = .right
                imageView.tintColor = .BaseWhite
                imageView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
                imageView.tag = 999
                cell.continueButton.addSubview(imageView)
            }

            return cell
        })
    }

    @objc func confirmButtonDidTap() {
        presenter?.ctaButtonDidTap()
    }
}
