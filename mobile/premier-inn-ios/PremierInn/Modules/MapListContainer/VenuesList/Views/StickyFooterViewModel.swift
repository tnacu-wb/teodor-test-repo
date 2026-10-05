//
//  AppIncentiveStickyFooterViewModel.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 09/09/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import UIKit

protocol StickyFooterViewModel {
    var image: String { get }
    var title: String { get }
    var description: AttributedString { get }
    func termsAndConditionsDidTap()
}

struct SiteWidePromotionViewModel: StickyFooterViewModel {
	var delegate: UIViewController?
	let image = "promotionPILogo"
	let title: String
	let description: AttributedString
	let urlString: String?

	func termsAndConditionsDidTap() {
		if let urlString,
		   let url = URL(string: urlString) {
			delegate?.openURLInSafari(url: url)
		}
	}
}

struct FreeBreakfastViewModel: StickyFooterViewModel {
    var delegate: UIViewController?

    let image = "promotionPILogo"

    let title = PILocalizedString("freeBreakfastPromotionStickyBannerTitle")

    let description = {
        let stringTokens = [
            PILocalizedString("freeBreakfastPromotionPopoverBookBy"),
            PILocalizedString("freeBreakfastPromotionPopoverEndDate"),
            PILocalizedString("freeBreakfastPromotionStickyBannerDisclaimer"),
            PILocalizedString("freeBreakfastPromotionStickyBannerDescription"),
            PILocalizedString("freeBreakfastPromotionStickyBannerTaCApply")
        ]

        var attributedString = AttributedString(stringTokens.joined(separator: " "))
        attributedString.font = UIFont.BodySmall()

        if let range = attributedString.range(of: PILocalizedString("freeBreakfastPromotionPopoverEndDate")) {
            attributedString[range].font = UIFont.BodySmall_Bold()
        }

        if let range = attributedString
           .range(of: PILocalizedString("freeBreakfastPromotionStickyBannerTermsAndConditions")) {
            attributedString[range].underlineStyle = .single
        }

        return attributedString
    }()

    let urlString = PILocalizedString("freeBreakfastPromotionStickyBannerURL")

    func termsAndConditionsDidTap() {
        if let url = URL(string: urlString) {
            delegate?.openURLInSafari(url: url)
        }
    }
}

struct AppIncentiveStickyFooterViewModel: StickyFooterViewModel {
    var delegate: UIViewController?

    let image = "promotionPILogo"
    let title = PILocalizedString("promotionStickyBannerTitle")
    let description = {
        let stringTokens = [
            PILocalizedString("promotionPopoverBookBy"),
            PILocalizedString("promotionPopoverEndDate"),
            PILocalizedString("promotionStickyBannerDisclaimer"),
            PILocalizedString("promotionStickyBannerDescription"),
            PILocalizedString("promotionStickyBannerTaCApply")
        ]

        var attributedString = AttributedString(stringTokens.joined(separator: " "))
        attributedString.font = UIFont.BodySmall()

        if let range = attributedString.range(of: PILocalizedString("promotionPopoverEndDate")) {
            attributedString[range].font = UIFont.BodySmall_Bold()
        }

        if let range = attributedString.range(of: PILocalizedString("promotionStickyBannerTermsAndConditions")) {
            attributedString[range].underlineStyle = .single
        }

        return attributedString
    }()

    let urlString = PILocalizedString("promotionStickyBannerURL")

    func termsAndConditionsDidTap() {
        if let url = URL(string: urlString) {
            delegate?.openURLInSafari(url: url)
        }
    }
}
