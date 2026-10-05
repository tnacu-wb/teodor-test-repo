//
//  PromotionPopoverViewModel.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 01/09/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import UIKit

struct PromotionPopoverViewModel {
    let dismissButtonTitle = PILocalizedString("Close")
    let imageView = "promotionPILogo"
    let title = PILocalizedString("promotionPopoverTitle")
    let offerNumber = PILocalizedString("promotionPopoverOfferAmount")
    let offerPercentage = PILocalizedString("promotionPopoverOfferPercentage")
    let offerOff = PILocalizedString("promotionPopoverOfferOff")
    let offerDescription = PILocalizedString("promotionPopoverYourStay")
    let bookByDate = {
        var attributedString = AttributedString(
            PILocalizedString("promotionPopoverBookBy"),
            attributes: AttributeContainer([.font: UIFont.Heading3_Regular()])
        )

        attributedString.append(AttributedString(" "))
        attributedString.append(AttributedString(
            PILocalizedString("promotionPopoverEndDate"),
            attributes: AttributeContainer([.font: UIFont.Heading3_Semibold()])
        ))

        return attributedString
    }()
    let actionButtonTitle = PILocalizedString("promotionPopoverContinueButton")
    let disclaimer = PILocalizedString("promotionPopoverDisclaimer")
    let termsAndConditions = PILocalizedString("promotionPopoverTermsAndConditions")

    let urlString = PILocalizedString("promotionStickyBannerURL")

    func termsAndConditionsDidTap(viewController: UIViewController) {
        if let url = URL(string: urlString) {
            viewController.openURLInSafari(url: url)
        }
    }
}
