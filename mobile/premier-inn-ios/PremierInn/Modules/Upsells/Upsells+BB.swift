//
//  Upsells+BB.swift
//  PremierInn
//
//  Created by Freddie Parks on 17/10/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import SimpleNetwork
import UIKit

extension UpsellsInteractor {
    enum BBUpsells {
        static let sharedParagraphStyling: NSParagraphStyle = {
            let paragraphStyle = NSMutableParagraphStyle()
            paragraphStyle.paragraphSpacing = 3
            paragraphStyle.lineSpacing = 3

            return paragraphStyle
        }()

        static func mealAllowanceDescription(
            for upsellName: String?,
            and amount: Cost?,
            encouraging alcoholism: Bool,
            mealDealActivated: Bool
        ) -> NSAttributedString {
            guard let localizedPrice = amount?.localizedValue else { return NSAttributedString() }

            let dinnerAllowanceString = String.localizedStringWithFormat(PILocalizedString("perDay"), localizedPrice)

            var string = "\(PILocalizedString("Dinner allowance"))\n\(dinnerAllowanceString)"

            if let upsellName = upsellName {
                string.append(" (\(PILocalizedString("or")) \(upsellName))")
            }

            string.append("\n\(PILocalizedString("This can be spent at our restaurants on-site or linked to your hotel."))")

            if !alcoholism {
                string.append(" \(PILocalizedString("Alcohol not included")).")
            }

            if mealDealActivated {
                string.append("\n\(PILocalizedString("Meal Deal selected"))")
            }

            let mutableString = NSMutableAttributedString(
                string: string,
                attributes: [
                    .paragraphStyle: BBUpsells.sharedParagraphStyling,
                    .foregroundColor: mealDealActivated ? UIColor.TintL1 : UIColor.TintD1,
                    .font: UIFont.BodySmall()
                ]
            )

            if let amountRange = string.ranges(of: dinnerAllowanceString).first {
                mutableString.addAttributes([.font: UIFont.BodySmall()], range: amountRange)
            }
            if let titleRange = string.ranges(of: PILocalizedString("Dinner allowance")).first {
                mutableString.addAttributes([.font: UIFont.Heading4_Semibold()], range: titleRange)
            }
            if let mealDealRange = string.ranges(of: PILocalizedString("Meal Deal selected")).first {
                mutableString.addAttribute(.foregroundColor, value: UIColor.Tint8, range: mealDealRange)
            }

            return NSAttributedString(attributedString: mutableString)
        }

        static let parkingAllowanceDescription: NSAttributedString = {
            let string = "\(PILocalizedString("Hotel parking allowance"))\n\(PILocalizedString("This can be spent on parking at the hotel or car parks off-site. Parking space is subject to availability and can't be guaranteed."))"

            let mutableString = NSMutableAttributedString(
                string: string,
                attributes: [
                    .paragraphStyle: BBUpsells.sharedParagraphStyling,
                    .foregroundColor: UIColor.TintD1,
                    .font: UIFont.BodySmall()
                ]
            )

            if let titleRange = string.ranges(of: PILocalizedString("Hotel parking allowance")).first {
                mutableString.addAttributes(
                    [.font: UIFont.Heading4_Semibold(), .foregroundColor: UIColor.TintD1],
                    range: titleRange
                )
            }

            return NSAttributedString(attributedString: mutableString)
        }()

        static let wifiAllowanceDescription: NSAttributedString = {
            let string = "\(PILocalizedString("WiFi is cool"))\n\(PILocalizedString("This can be spent on Ultimate-WiFi at the hotel. Bandwidth is subject to availability and can't be guaranteed."))"

            let mutableString = NSMutableAttributedString(
                string: string,
                attributes: [
                    .paragraphStyle: BBUpsells.sharedParagraphStyling,
                    .foregroundColor: UIColor.TintD1,
                    .font: UIFont.BodySmall()
                ]
            )

            if let titleRange = string.ranges(of: PILocalizedString("WiFi is cool")).first {
                mutableString.addAttributes(
                    [.font: UIFont.Heading4_Semibold(), .foregroundColor: UIColor.TintD1],
                    range: titleRange
                )
            }

            return NSAttributedString(attributedString: mutableString)
        }()
    }
}
