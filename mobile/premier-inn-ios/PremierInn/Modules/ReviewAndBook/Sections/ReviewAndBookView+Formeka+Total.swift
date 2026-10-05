//
//  ReviewAndBookView+Formeka+Total.swift
//  PremierInn
//
//  Created by Marcello Mascia on 08/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import Formeka
import SimpleNetwork

extension ReviewAndBookViewController {
    func totalSection(bookingDetails: BookingDetails) -> FormekaModelSection {
        var rows = [FormekaModelRow]()
        rows.append(termsAndConditionsTextRow())
        rows.append(costWithPayRow(with: bookingDetails))

        return FormekaModelSection(header: paymentMethodsSectionFooter, rows: rows, footer: footer(title: nil, height: 40))
    }

    @objc func confirmButtonDidTap() {
        presenter?.confirmButtonDidTap()
    }
}

extension ReviewAndBookViewController {
    func costWithPayRow(with bookingDetails: BookingDetails) -> FormekaModelRow {
        FormekaModelRow(tag: ReviewAndBookRow.totalCost.rawValue) { indexPath, _, table in
            guard let cell: CostWithPayButtonCell = table.dequeueCell(for: indexPath) else { return nil }

            cell.contentView.backgroundColor = .BaseWhite
            cell.totalCostLabel
                .text = (bookingDetails.totalCostOperaWithDonations ?? bookingDetails.totalCostAfterTaxCalculation)?
                .localizedValue
            cell.payLabel.text = bookingDetails.paymentOption.textForReviewAndBookTotal

            let ctaConfig = self.ctaSetup(for: bookingDetails)

            cell.continueButton.addTarget(self, action: #selector(self.confirmButtonDidTap), for: .touchUpInside)
            cell.continueButton.backgroundColor = ctaConfig.style.buttonStyling.buttonColour
            cell.continueButton.setTitleColor(ctaConfig.style.buttonStyling.titleColour, for: .normal)
            cell.continueButton.setTitle(ctaConfig.title, for: .normal)
            cell.continueButton.setTitle("", for: .disabled)
            cell.continueButton.titleLabel?.font = ctaConfig.style.buttonStyling.font
            cell.continueButton.setAttributedTitle(nil, for: .normal)

            cell.continueButton.isEnabled = true
            cell.activityIndicator.stopAnimating()

            if bookingDetails.primaryPaymentMethod?.paymentMethodType == .paypal {
                cell.continueButton.setAttributedStringForPayPal(style: ctaConfig.style.buttonStyling)
                return cell
            }

            return cell
        }
    }

    private struct CTASetup {
        let title: String
        let style: CTAButtonStyle
    }

    private func ctaSetup(for bookingDetails: BookingDetails) -> CTASetup {
        let primaryPaymentMethod = bookingDetails.primaryPaymentMethod

        if primaryPaymentMethod?.paymentMethodType == .reserveWithoutCard {
            return CTASetup(title: PILocalizedString("reviewSubmitButtonTitle"), style: .teal(icon: nil))
        } else if primaryPaymentMethod?.paymentMethodType == .paypal {
            return CTASetup(title: "", style: .paypal)
        } else if primaryPaymentMethod?.paymentMethodType?.isNewCard == true {
            return CTASetup(title: PILocalizedString("reviewSubmitButton3CNewCardTitle"), style: .teal(icon: #imageLiteral(resourceName: "accessChevron")))
        } else {
            return CTASetup(title: PILocalizedString("reviewSubmitButton3CStoredCardTitle"), style: .teal(icon: nil))
        }
    }
}

extension BookingDetails {
    var totalCostAfterTaxCalculation: Cost? {
        cityTaxRequired ? totalCostWithCityTaxAndExtras : totalCostWithoutCityTax
    }
}
