//
//  AlternateCalendarAmendDateConfirmView.swift
//  PremierInn
//
//  Created by Freddie Parks on 19/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

protocol AlternateCalendarConfirmViewModel {
    var localizedPriceDifference: String { get }
    var numberOfNights: Int { get }
}

protocol AlternateCalendarAmendDateConfirmViewDelegate: AnyObject {
    func continueButtonDidTap()
}

class AlternateCalendarAmendDateConfirmView: UIView {
    @IBOutlet weak var priceDifference: UILabel!
    @IBOutlet weak var continueButton: RoundedCornersButton!

    weak var delegate: AlternateCalendarAmendDateConfirmViewDelegate?

    @IBAction func continueButtonDidTap(_ sender: Any) {
        delegate?.continueButtonDidTap()
    }

    private func attributedButtonTitle(with prefix: String, and suffix: String) -> NSAttributedString? {
        let fullString = String(prefix + "\n" + suffix)

        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.alignment = .center

        let mutableString = NSMutableAttributedString(
            string: fullString,
            attributes: [.foregroundColor: UIColor.BaseWhite, .paragraphStyle: paragraphStyle]
        )

        let prefixRange = NSRange(location: 0, length: prefix.count)
        mutableString.addAttributes([.font: UIFont.Body_Semibold()], range: prefixRange)

        guard let suffixRange = fullString.ranges(of: suffix).first
            else { return NSAttributedString(attributedString: mutableString) }
        mutableString.addAttributes([.font: UIFont.BodySmall()], range: suffixRange)

        return NSAttributedString(attributedString: mutableString)
    }

    private func attributedPriceDifference(with prefix: String, and suffix: String) -> NSAttributedString? {
        let fullString = String(prefix + suffix)

        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.alignment = .center

        let mutableString = NSMutableAttributedString(
            string: fullString,
            attributes: [.foregroundColor: UIColor.TintD1, .paragraphStyle: paragraphStyle]
        )

        let prefixRange = NSRange(location: 0, length: prefix.count)
        mutableString.addAttributes([.font: UIFont.Heading3_Regular()], range: prefixRange)

        guard let suffixRange = fullString.ranges(of: suffix).first
            else { return NSAttributedString(attributedString: mutableString) }
        mutableString.addAttributes([.font: UIFont.Heading3_Semibold()], range: suffixRange)

        return NSAttributedString(attributedString: mutableString)
    }

    func update(with viewModel: AlternateCalendarConfirmViewModel) {
        let prefixString = PILocalizedString("bookingReviewContinueButtonTitle", comment: "")
        let suffixString = "(\(String.localizedStringWithFormat(PILocalizedString("%d night(s)", comment: "Message shown for number of nights"), viewModel.numberOfNights)))"

        continueButton.titleLabel?.numberOfLines = 0
        continueButton.setAttributedTitle(attributedButtonTitle(with: prefixString, and: suffixString), for: .normal)

        let pricePrefixString = "\(PILocalizedString("priceDifferenceLabel", comment: "")): "
        let priceSuffixString = viewModel.localizedPriceDifference

        priceDifference.attributedText = attributedPriceDifference(with: pricePrefixString, and: priceSuffixString)
    }
}
