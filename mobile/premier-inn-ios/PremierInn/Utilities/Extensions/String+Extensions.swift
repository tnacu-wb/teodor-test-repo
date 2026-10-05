//
//  String+Extensions.swift
//  PremierInn
//
//  Created by Freddie Parks on 19/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

extension String {
    var isNotEmpty: Bool {
        isEmpty == false
    }

    var dateValue: Date? {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy/MM/dd"

        return dateFormatter.date(from: self)
    }

    var shouldBePrecededByAn: Bool {
        guard let firstLetter = self.first else { return false }
        let firstLetterAsString = String(firstLetter).uppercased()

        let vowels = ["A", "E", "I", "O", "U"]
        return vowels.contains(firstLetterAsString)
    }

    // Extension of Swift's baked in contains operator where the lhs of the operator is the string you want to check and rhs is a String of the regex pattern you want to check against
    static func ~= (lhs: String, rhs: String) -> Bool {
        guard let regex = try? NSRegularExpression(pattern: rhs) else { return false }

        let range = NSRange(location: 0, length: lhs.utf16.count)

        return regex.firstMatch(in: lhs, options: [], range: range) != nil
    }

    func timeToRead() -> Double {
        max(Double(components(separatedBy: .whitespacesAndNewlines).count) / 2, 4)
    }

    func width(withConstrainedHeight height: CGFloat, font: UIFont) -> CGFloat {
        let constraintRect = CGSize(width: .greatestFiniteMagnitude, height: height)
        let boundingBox = self.boundingRect(
            with: constraintRect,
            options: .usesLineFragmentOrigin,
            attributes: [.font: font],
            context: nil
        )

        return ceil(boundingBox.width)
    }

    func height(withConstrainedWidth width: CGFloat, font: UIFont) -> CGFloat {
        let constraintRect = CGSize(width: width, height: .greatestFiniteMagnitude)
        let boundingBox = self.boundingRect(
            with: constraintRect,
            options: .usesLineFragmentOrigin,
            attributes: [.font: font],
            context: nil
        )

        return ceil(boundingBox.height)
    }

    func substringToIndex(_ index: Int) -> String {
        let index = clamp(value: index, lower: 0, upper: self.count)
        let idx = self.index(startIndex, offsetBy: index)

        return String(self[..<idx])
    }

    func substringFromIndex(_ index: Int) -> String {
        let index = clamp(value: index, lower: 0, upper: self.count)
        let idx = self.index(startIndex, offsetBy: index)

        return String(self[idx...])
    }

    func substring(with nsrange: NSRange) -> Substring? {
        guard let range = Range(nsrange, in: self) else { return nil }
        return self[range]
    }

    func truncated(withLength length: Int) -> String {
        guard length < self.count else { return self }
        guard length > 0 else { return "" }

        return substringToIndex(length) + PILocalizedString("...", comment: "")
    }

    func withNumbersRemoved() -> String {
        var newCleanedString = self

        let isNumber: (Character) -> Bool = {
            "0"..."9" ~= $0
        }

        newCleanedString.removeAll(where: isNumber)

        return newCleanedString
    }

	func ranges(of searchString: String) -> [NSRange] {
		let inputLength = self.count
		var range = NSRange(location: 0, length: inputLength)
		var results = [NSRange]()

		while range.location != NSNotFound {
			range = (self as NSString).range(of: searchString, options: [], range: range)

			if range.location != NSNotFound {
				let foundRange = NSRange(location: range.location, length: searchString.count)
				results.append(foundRange)

				let nextLocation = range.location + range.length
				range = NSRange(location: nextLocation, length: inputLength - nextLocation)
			}
		}

		return results
	}

    func ranges(ofRegex pattern: String, caseSensitive: Bool = false) -> [NSRange] {
        do {
            let regex = try NSRegularExpression(pattern: pattern, options: caseSensitive ? [] : .caseInsensitive)

            return regex.matches(in: self, range: NSRange(location: 0, length: self.count)).compactMap { $0.range }
        } catch {
            return []
        }
    }

	func attributedString(with ranges: [NSRange], attributes: [NSAttributedString.Key: Any]) -> NSAttributedString? {
        let text = NSMutableAttributedString(string: self)

        for range in ranges {
            text.addAttributes(attributes, range: range)
        }

        return text
    }

    func creditCardDisplayValue() -> String {
        self.enumerated()
            .reduce(into: "") {
                $0 += $1.offset.isMultiple(of: 4) && $1.offset != 0 ?
                " \($1.element)" :
                String($1.element)
        }
    }

    func toAttributedString(kern: CGFloat) {
        let attributedString = NSMutableAttributedString(string: self)
        attributedString.addAttribute(
            NSAttributedString.Key.kern,
            value: CGFloat(-1.0),
            range: NSRange(location: 0, length: attributedString.length)
        )
    }

    func toAttributedString(kern: CGFloat) -> NSAttributedString {
        let attributes = [NSAttributedString.Key.kern: kern] as [NSAttributedString.Key: Any]

        return NSAttributedString(string: self, attributes: attributes)
    }

    func toASCIIImageName() -> String {
        self.lowercased()
            .replacingOccurrences(of: "ü", with: "u")
            .replacingOccurrences(of: "ö", with: "o")
            .replacingOccurrences(of: "ä", with: "a")
            .replacingOccurrences(of: "ß", with: "ss")
    }

    func matches(_ regex: some RegexComponent) -> Bool {
        wholeMatch(of: regex) != nil
    }
}

extension String {
    static var analyticsUserJourney: String {
        let journey = UserSessionManager.sharedInstance.currentUser?.isBusiness == true ? "PB" : "PI"
        return journey
    }

    var analyticsComponentsPrefixed: String {
        let language = LanguageManager.supportedLanguage.analyticsCode
        return "iOS:\(String.analyticsUserJourney):\(language): \(self)"
    }

    var ciolPriceRangeSubfixed: String {
        self
        + " "
        + PILocalizedString("upsellPer")
        + " "
        + PILocalizedString("upsellAdultDay", comment: "per adult/day")
    }
}

extension Optional where Wrapped == String {
    var hasValue: Bool {
        guard let string = self else { return false }
        return !string.isEmpty
    }
}

extension NSAttributedString {
    class func attributedStringWith(
        text: String?,
        lineSpacing: CGFloat? = nil,
        maxLines: Int? = nil,
        font: UIFont? = UIFont(name: "Arial", size: 14.0),
        textColor: UIColor = .black,
        textAlignment: NSTextAlignment = .left
    ) -> NSAttributedString? {
        mutableAttributedStringWith(
            text: text,
            lineSpacing: lineSpacing,
            maxLines: maxLines,
            font: font,
            textColor: textColor,
            textAlignment: textAlignment
        )
    }

    class func mutableAttributedStringWith(
        text: String?,
        lineSpacing: CGFloat? = nil,
        maxLines: Int? = nil,
        font: UIFont? = UIFont(name: "Arial", size: 14.0),
        textColor: UIColor = .black,
        textAlignment: NSTextAlignment = .left
    ) -> NSMutableAttributedString? {
        guard let text = text else { return nil }

        let attributedString = NSMutableAttributedString(string: text)

        if let lineSpacing = lineSpacing {
            let paragraphStyle = NSMutableParagraphStyle()
            paragraphStyle.lineSpacing = lineSpacing
            paragraphStyle.alignment = textAlignment
            paragraphStyle.lineBreakMode = NSLineBreakMode.byTruncatingTail

            attributedString.addAttribute(
                NSAttributedString.Key.paragraphStyle,
                value: paragraphStyle,
                range: NSRange(location: 0, length: text.count)
            )
            attributedString.addAttributes(
                [
                    NSAttributedString.Key.paragraphStyle: paragraphStyle,
                    NSAttributedString.Key.font: font!,
                    NSAttributedString.Key.foregroundColor: textColor
                ],
                range: NSRange(location: 0, length: text.count)
            )
        }

        return attributedString
    }

    func attributedStringByColoringBullets(with color: UIColor) -> NSAttributedString {
        let mutableString = NSMutableAttributedString(attributedString: self)
        let ranges = string.ranges(of: "•")

        for range in ranges {
            mutableString.addAttribute(NSAttributedString.Key.foregroundColor, value: color, range: range)
        }

        return mutableString
    }

    func attributedStringByHighlightingCharacters(
        highlights: [String],
        font: UIFont = UIFont.Heading3_Semibold()
    ) -> NSAttributedString {
        let mutableString = NSMutableAttributedString(attributedString: self)

		let paragraphStyle = NSMutableParagraphStyle()
		paragraphStyle.lineSpacing = 11
		paragraphStyle.alignment = .left
		paragraphStyle.lineBreakMode = NSLineBreakMode.byTruncatingTail

        for highlight in highlights {
			let ranges = string.ranges(of: highlight)

            for range in ranges {
                mutableString.addAttributes([
                    NSAttributedString.Key.paragraphStyle: paragraphStyle,
                    NSAttributedString.Key.foregroundColor: UIColor.TintD1,
                    NSAttributedString.Key.font: font
                ], range: range)
            }
        }

        return mutableString
    }

    static func attributedStringAndImageForDiscount(label: String) -> NSAttributedString {
        let promotionImage = NSTextAttachment()
        promotionImage.image = UIImage(named: "promotion")

        promotionImage.bounds = CGRect(x: 0, y: (UIFont.SubtextStrong().capHeight - 16) / 2, width: 16, height: 16)

        let mutableAttributedString = NSMutableAttributedString(attachment: promotionImage)
        mutableAttributedString.append(NSAttributedString(string: " "))
        mutableAttributedString.append(NSAttributedString(
            string: label,
            attributes:
                                                            [
                                                                .font: UIFont.SubtextStrong(),
                                                                .foregroundColor: UIColor.Tint1
                                                            ]
        ))
        return mutableAttributedString
    }
}

extension NSMutableAttributedString {
	func style(text: String, withAttributes attributes: [NSAttributedString.Key: Any]) {
        let ranges = self.string.ranges(of: text)
        style(withParamaters: attributes, atRanges: ranges)
    }

    private func style(withParamaters attributes: [NSAttributedString.Key: Any], atRanges ranges: [NSRange]) {
        for range in ranges {
            self.addAttributes(attributes, range: range)
        }
    }
}

extension NSMutableAttributedString {
    /// Exists purely as interfaces such as `InstructionsViewModel`'s `topDescription` returns `NSAttributedString`.
    /// Due to time constraints, `topDescription` cannot be refactored at this time.
    /// Time constraints win, so this extension handles icon + text layout.
    static func iconText(
        icon: UIImage,
        text: String,
        boldText: String? = nil,
        font: UIFont = UIFont.Body(),
        boldFont: UIFont = UIFont.Heading4_Bold(),
        iconSize: CGFloat = 28,
        spacing: CGFloat = 8,
        xPosition: CGFloat = 0,
        yPosition: CGFloat = -10
    ) -> NSAttributedString {
        let attachment = NSTextAttachment()
        attachment.image = icon

        attachment.bounds = CGRect(
            x: xPosition,
            y: yPosition,
            width: iconSize,
            height: iconSize
        )

        let textStart = iconSize + spacing

        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.firstLineHeadIndent = 0
        paragraphStyle.headIndent = textStart
        paragraphStyle.tabStops = [
            NSTextTab(textAlignment: .left, location: textStart)
        ]
        paragraphStyle.defaultTabInterval = textStart

        let result = NSMutableAttributedString()
        result.append(NSAttributedString(attachment: attachment))
        result.append(NSAttributedString(string: "\t\(text)"))

        result.addAttributes(
            [
                .font: font,
                .paragraphStyle: paragraphStyle
            ],
            range: NSRange(location: 0, length: result.length)
        )

        if let boldText = boldText {
            result.style(
                text: boldText,
                withAttributes: [
                    .font: boldFont,
                    .paragraphStyle: paragraphStyle
                ]
            )
        }

        return result
    }
}
