//
//  UITableViewCell+Extensions.swift
//  PremierInn
//
//  Created by Freddie Parks on 07/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

extension UITableViewCell {
    func boldenTextLabel(text: String?, unboldRange: NSRange?) {
        if let text = text {
            let attributedString = NSMutableAttributedString(
                string: text,
                attributes: [NSFontAttributeName: UIFont.premierInnBold(ofSize: self.textLabel?.font.pointSize ?? 10.0)]
            )

            if let range: NSRange = unboldRange {
                let nsText = NSString(string: text)
                let boldString = NSAttributedString(
                    string: nsText.substringWithRange(range),
                    attributes: [NSFontAttributeName: UIFont.premierInn(ofSize: self.textLabel?.font.pointSize ?? 10.0)]
                )

                attributedString.replaceCharactersInRange(range, withAttributedString: boldString)
            }

            self.textLabel?.attributedText = attributedString
        }
    }
}
