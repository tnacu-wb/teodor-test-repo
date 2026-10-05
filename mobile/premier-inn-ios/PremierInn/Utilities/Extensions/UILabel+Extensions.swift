//
//  UILabel+Extensions.swift
//  PremierInn
//
//  Created by Freddie Parks on 07/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import SimpleNetwork
import UIKit

extension UILabel {
    func boldenText(exceptTextInRange range: NSRange?, font: UIFont = .Body(), boldFont: UIFont = .Body_Semibold()) {
        boldenText(exceptTextInRange: range, color: nil, font: font, boldFont: boldFont)
    }

	func boldenText(
	    exceptTextInRange range: NSRange?,
	    color: UIColor?,
	    attributes: [NSAttributedString.Key: Any]? = nil,
	    font: UIFont = .Body(),
	    boldFont: UIFont = .Body_Semibold()
	) {
        if let text = self.text {
            if let range: NSRange = range {
				var attributesBold: [NSAttributedString.Key: Any] = [NSAttributedString.Key.font: boldFont]

                if let color = color {
                    attributesBold[NSAttributedString.Key.foregroundColor] = color
                }

                let attributedString = NSMutableAttributedString(string: text, attributes: attributesBold)

                let nsText = NSString(string: text)

                if range.length < nsText.length {
                    let boldString = NSAttributedString(
                        string: nsText.substring(with: range),
                        attributes: attributes ?? [NSAttributedString.Key.font: font]
                    )

                    attributedString.replaceCharacters(in: range, with: boldString)
                }

                self.attributedText = attributedString
            } else {
                self.attributedText = nil
                self.text = text
            }
        }
    }

	func fancyMantissaString(cost: Cost, mantissaFontSize: CGFloat) -> NSAttributedString {
		guard let mantissaFont = UIFont(name: font.fontName, size: mantissaFontSize)
		    else { return NSAttributedString(string: cost.localizedValue) }
        guard let font = font else { return NSAttributedString(string: cost.localizedValue) }
        guard let textColor = textColor else { return NSAttributedString(string: cost.localizedValue) }

        let attributes: [NSAttributedString.Key: Any] = [.font: font as Any, .foregroundColor: textColor as Any]
        let mantissaAttributes: [NSAttributedString.Key: Any] = [.font: mantissaFont, .foregroundColor: textColor as Any]

		return cost.fancyMantissaString(baseAttributes: attributes, mantissaAttributes: mantissaAttributes)
	}
}
