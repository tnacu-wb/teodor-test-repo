//
//  UserDetailsModelManager.swift
//  PremierInn
//
//  Created by Clint Mengolli on 23/12/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import SimpleNetwork
import UIKit

protocol BookingDetailsMessageContextType {
    var cityTaxForLeisure: Bool? { get }
    var cityTaxForBusiness: Bool? { get }
    var totalCostWithoutCityTax: Cost? { get }
}

struct BookingDetailsMessageContext: BookingDetailsMessageContextType {
    let cityTaxForLeisure: Bool?
    let cityTaxForBusiness: Bool?
    let totalCostWithoutCityTax: Cost?

    init(bookingDetails: BookingDetails = .sharedInstance) {
        self.cityTaxForLeisure = bookingDetails.hotel?.cityTaxForLeisure
        self.cityTaxForBusiness = bookingDetails.hotel?.cityTaxForBusiness
        self.totalCostWithoutCityTax = bookingDetails.totalCostWithoutCityTax
    }
}

final class UserDetailsMessageConfigurations {
    private let suppressEmailSection: Bool
    private let isMarketingSwitchEnabled: Bool
    private let bookingContext: BookingDetailsMessageContextType
    private let scope: UserDetailScope

    init(
        suppressEmailSection: Bool,
        isMarketingSwitchEnabled: Bool,
        bookingContext: BookingDetailsMessageContextType = BookingDetailsMessageContext(),
        scope: UserDetailScope
    ) {
        self.suppressEmailSection = suppressEmailSection
        self.isMarketingSwitchEnabled = isMarketingSwitchEnabled
        self.bookingContext = bookingContext
        self.scope = scope
    }

    var marketingModel: UserMarketingModel? {
        guard scope == .userPreferences || !suppressEmailSection else { return nil }

        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineSpacing = 4
        paragraphStyle.paragraphSpacing = 5

        let emailMarketingHeading = PILocalizedString("emailMarketingHeading")
        let emailMarketingDescription = PILocalizedString("emailMarketingDescription")
        let emailMarketingFooter = PILocalizedString("emailMarketingFooter")

        let marketingString = NSMutableAttributedString(
            string: "\(emailMarketingHeading)\n\(emailMarketingDescription)\n\(emailMarketingFooter)",
            attributes: [.font: UIFont.BodySmall(), .foregroundColor: UIColor.ColourDL1, .paragraphStyle: paragraphStyle]
        )

        let headingRange = marketingString.string.ranges(of: emailMarketingHeading)

        marketingString.addAttribute(
            .font,
            value: UIFont.Body_Semibold(),
            range: headingRange.first ?? NSRange(location: 0, length: 15)
        )

        return UserMarketingModel(
            description: NSAttributedString(attributedString: marketingString),
            isActive: isMarketingSwitchEnabled
        )
    }

    var tripPurposeModel: TripPurposeMessagesModel? {
        guard let cityTaxForLeisure = bookingContext.cityTaxForLeisure,
              let cityTaxForBusiness = bookingContext.cityTaxForBusiness else { return nil }

        switch (cityTaxForLeisure, cityTaxForBusiness) {
        case (false, false):
            return nil
        case (true, true):
            return TripPurposeMessagesModel(
                leisureMessages: [cityTaxAppliesToAllMessage, totalPriceWithTaxMessageWillBeCalculated],
                businessMessages: [cityTaxAppliesToAllMessage, totalPriceWithTaxMessageWillBeCalculated]
            )
        case (false, true):
            return TripPurposeMessagesModel(
                leisureMessages: [leisureExemptFromCityTaxMessage],
                businessMessages: [leisureExemptFromCityTaxMessage, totalPriceWithTaxMessageWillBeCalculated]
            )
        case (true, false):
            return TripPurposeMessagesModel(
                leisureMessages: [businessExemptFromCityTaxMessage, totalPriceWithTaxMessageWillBeCalculated],
                businessMessages: [businessExemptFromCityTaxMessage]
            )
        }
    }

    private var totalPriceWithTaxMessageWillBeCalculated: TripPurposeMessageModel {
        TripPurposeMessageModel(
            message: NSAttributedString(
                string: PILocalizedString("userDetailsTotalCostWithCityTaxToBeCalculatedMessage"),
                attributes: [
                    .font: UIFont.BodySmall(),
                    .foregroundColor: UIColor.ColourDL1
                ]
            ),
            style: .info
        )
    }

    private var leisureExemptFromCityTaxMessage: TripPurposeMessageModel {
        TripPurposeMessageModel(
            message: NSAttributedString(
                string: PILocalizedString("userDetailsLeisureExemptMessage"),
                attributes: [.font: UIFont.BodySmall(), .foregroundColor: UIColor.ColourDL1]
            ),
            style: .info
        )
    }

    private var businessExemptFromCityTaxMessage: TripPurposeMessageModel {
        TripPurposeMessageModel(
            message: NSAttributedString(
                string: PILocalizedString("userDetailsBusinessExemptMessage"),
                attributes: [.font: UIFont.BodySmall(), .foregroundColor: UIColor.ColourDL1]
            ),
            style: .alert
        )
    }

    private var cityTaxAppliesToAllMessage: TripPurposeMessageModel {
        TripPurposeMessageModel(
            message: NSAttributedString(
                string: PILocalizedString("userDetailsCityTaxAllStays"),
                attributes: [.font: UIFont.BodySmall(), .foregroundColor: UIColor.ColourDL1]
            ),
            style: .info
        )
    }

    private var totalPriceWithoutTaxMessage: TripPurposeMessageModel? {
        guard let cost = bookingContext.totalCostWithoutCityTax else { return nil }

        let attributedString = NSMutableAttributedString(
            string: PILocalizedString("userDetailsCityTaxNewPrice"),
            attributes: [
                .font: UIFont.BodySmall(),
                .foregroundColor: UIColor.ColourDL1
            ]
        )

        let priceString = NSAttributedString(
            string: "\(cost.localizedValue)",
            attributes: [
                .font: UIFont.BodySmall_Bold(),
                .foregroundColor: UIColor.ColourDL1
            ]
        )

        attributedString.append(priceString)

        return TripPurposeMessageModel(
            message: attributedString,
            style: .info
        )
    }
}
