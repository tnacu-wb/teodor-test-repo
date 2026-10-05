//
//  GdprShieldFooterView.swift
//  PremierInn
//
//  Created by Marcello Mascia on 10/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

protocol GdprShieldFooterViewDelegate: AnyObject {
    func gdprButtonDidTap()
}

class GdprShieldFooterView: UICollectionReusableView {
    // MARK: - Views

    @IBOutlet weak var button: UIButton! {
        didSet {
            let dataPolicyString = PILocalizedString(
                "youCanReadOurDataPolicyBannerTitle",
                comment: "Data policy banner title"
            )

            let formattedAttributedDataPolicyString = premierInnPurplizeLastWord(in: dataPolicyString)

            button.tintColor = .ColourDL5
            button.titleLabel?.numberOfLines = 0
            button.setAttributedTitle(formattedAttributedDataPolicyString, for: .normal)
        }
    }

    // MARK: - Actions

    @IBAction func buttonDidTap(_ sender: UIButton) {
        delegate?.gdprButtonDidTap()
    }

    // MARK: - Properties

    weak var delegate: GdprShieldFooterViewDelegate?

    // MARK: - Lifecycle

    private func premierInnPurplizeLastWord(in sentence: String) -> NSAttributedString {
        var dataPolicyStringsSeperatedBySpaces = sentence.components(separatedBy: .whitespaces)

        let dataPolicyLastWord = dataPolicyStringsSeperatedBySpaces.removeLast()

        let dataPolicyStringsBarLastWord = dataPolicyStringsSeperatedBySpaces.joined(separator: " ")

        let mutableDataPolicyString = NSMutableAttributedString(
            attributedString: NSAttributedString(
                string: dataPolicyStringsBarLastWord,
                attributes: [
                    NSAttributedString.Key.foregroundColor: UIColor.ColourDL1,
                    NSAttributedString.Key.font: UIFont.Body()
                ]
            )
        )

        let mutableLastDataPolicyWord = NSAttributedString(
            string: " \(dataPolicyLastWord)",
            attributes: [
                NSAttributedString.Key.foregroundColor: UIColor.ColourDL5,
                NSAttributedString.Key.font: UIFont.Heading4_Semibold()
            ]
        )

        mutableDataPolicyString.append(mutableLastDataPolicyWord)

        return mutableDataPolicyString
    }
}
