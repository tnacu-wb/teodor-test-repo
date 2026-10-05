//
//  GDPRBannerHeaderRow.swift
//  PremierInn
//
//  Created by Nick Jones on 23/04/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Formeka
import UIKit

class GDPRBannerHeaderRow: SimpleSeparatorsCell {
    // MARK: - Views

    @IBOutlet weak var bannerHeaderIcon: UIImageView! {
        didSet {
            bannerHeaderIcon.image = #imageLiteral(resourceName: "shield")
            bannerHeaderIcon.image = bannerHeaderIcon.image?.withRenderingMode(.alwaysTemplate)
            bannerHeaderIcon.tintColor = .ColourDL1
        }
    }
    @IBOutlet weak var informationLabel: UILabel! {
        didSet {
            let dataPolicyString = PILocalizedString(
                "youCanReadOurDataPolicyBannerTitle",
                comment: "Data policy banner title"
            )

            let formattedAttributedDataPolicyString = premierInnPurplizeLastWord(in: dataPolicyString)

            informationLabel.attributedText = formattedAttributedDataPolicyString
        }
    }

    // MARK: - Lifecycle

    /** - Takes a string, applies a Premier Inn font size of 15 to it and then changes the colour of the last word to purple via
     1. Store every word in an array - **[Some, text, about, data, policy]**
     2. Extract the last word in the array - **"policy"**
     3. Flatten the array of strings with a whitespace seperator - **"Some text about data"**
     4. Format the first part of our attributed string
     5. Format the second part of our attributed string and prepend a whitespace - **" policy"**
     6. Combine our two attributed strings - **"Some text about data policy"**
     */
    private func premierInnPurplizeLastWord(in sentence: String) -> NSAttributedString {
        /* 1 */
        var dataPolicyStringsSeperatedBySpaces = sentence.components(separatedBy: .whitespaces)

        /* 2 */
        let dataPolicyLastWord = dataPolicyStringsSeperatedBySpaces.removeLast()

        /* 3 */
        let dataPolicyStringsBarLastWord = dataPolicyStringsSeperatedBySpaces.joined(separator: " ")

        /* 4 */
        let mutableDataPolicyString = NSMutableAttributedString(
            attributedString: NSAttributedString(
                string: dataPolicyStringsBarLastWord,
                attributes: [
                    NSAttributedString.Key.foregroundColor: UIColor.ColourDL1,
                    NSAttributedString.Key.font: UIFont.Body()
                ]
            )
        )

        /* 5 */
        let mutableLastDataPolicyWord = NSAttributedString(
            string: " \(dataPolicyLastWord)",
            attributes: [
                NSAttributedString.Key.foregroundColor: UIColor.ColourDL5,
                NSAttributedString.Key.font: UIFont.Heading4_Semibold()
            ]
        )

        /* 6 */
        mutableDataPolicyString.append(mutableLastDataPolicyWord)

        return mutableDataPolicyString
    }
}
