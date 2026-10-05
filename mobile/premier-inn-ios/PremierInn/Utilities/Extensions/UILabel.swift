//
//  UILabel.swift
//  PremierInn
//
//  Created by andrei.cojocaru on 7/26/24.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import UIKit

extension UILabel {
    /// Creates a customised UILabel
    /// - Parameters:
    ///     - text: text to be displayed
    ///     - font : font to be used, default is body2
    ///     - lineHeightMultiple: line height, default value is 1.17
    ///     - kern: kern, default value is 0.5
    ///     - textAlignment: alignment of the text, default value is left
    ///     - textColor: color of the text
    ///     - isUserInteractionEnabled: true or false, default value is false
    ///     - accessibilityIdentifier: identifier to be used by automation
    /// - Returns: a customised UILabel
    static func label(
        text: String,
        font: UIFont,
        lineHeightMultiple: CGFloat = 1.17,
        kern: NSNumber = 0.5,
        textAlignment: NSTextAlignment = .left,
        textColor: UIColor,
        numberOfLines: Int = 0,
        lineBreak: NSLineBreakMode = .byWordWrapping,
        isUserInteractionEnabled: Bool = false,
        accessibilityIdentifier: String
    ) -> Self {
        let label = Self()
        label.numberOfLines = numberOfLines

        let style = NSMutableParagraphStyle()
        style.lineHeightMultiple = lineHeightMultiple
        style.alignment = textAlignment
        style.lineBreakMode = lineBreak

        let attributedString = NSAttributedString(
            string: text,
            attributes: [
                .kern: kern,
                .paragraphStyle: style,
                .font: font,
                .foregroundColor: textColor
            ]
        )
        label.attributedText = attributedString
        label.translatesAutoresizingMaskIntoConstraints = false
        label.isUserInteractionEnabled = isUserInteractionEnabled
        label.accessibilityIdentifier = accessibilityIdentifier

        return label
    }

    func setupLabel(
        text: String = "",
        font: UIFont,
        lineHeightMultiple: CGFloat = 1.17,
        kern: NSNumber = 0.5,
        textAlignment: NSTextAlignment = .left,
        textColor: UIColor,
        numberOfLines: Int = 0,
        lineBreak: NSLineBreakMode = .byWordWrapping,
        isUserInteractionEnabled: Bool = false,
        accessibilityIdentifier: String,
        accessibilityTraits: UIAccessibilityTraits? = nil
    ) {
        self.numberOfLines = numberOfLines

        let style = NSMutableParagraphStyle()
        style.lineHeightMultiple = lineHeightMultiple
        style.alignment = textAlignment
        style.lineBreakMode = lineBreak

        let attributedString = NSAttributedString(
            string: text.isEmpty ? self.text ?? "" : text,
            attributes: [
                .kern: kern,
                .paragraphStyle: style,
                .font: font,
                .foregroundColor: textColor
            ]
        )
        self.attributedText = attributedString

        self.isUserInteractionEnabled = isUserInteractionEnabled
        self.translatesAutoresizingMaskIntoConstraints = false
        self.accessibilityIdentifier = accessibilityIdentifier

        if let accessibilityTraits {
            self.accessibilityTraits.insert(accessibilityTraits)
        }
    }
}
