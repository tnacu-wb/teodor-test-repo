//
//  ExpandTextFooter.swift
//  PremierInn
//
//  Created by Georgios Aikaterinakis on 14/04/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Formeka
import UIKit

public protocol ExpandTextFooterDelegate: AnyObject {
    func didTapAction(view: ExpandTextFooter)
}

public class ExpandTextFooter: UITableViewHeaderFooterView {
    public weak var delegate: ExpandTextFooterDelegate?

    @IBOutlet weak var buttonTitleLabel: UILabel! {
        didSet {
            buttonTitleLabel.font = .SubtextSmall()
            buttonTitleLabel.textColor = .BasePurple
            buttonTitleLabel.text = PILocalizedString("otpCantFindMyPasscode")
            buttonTitleLabel.isUserInteractionEnabled = true

            let tap = UITapGestureRecognizer(target: self, action: #selector(expandButtonDidTap))
            buttonTitleLabel.addGestureRecognizer(tap)
        }
    }
    @IBOutlet weak var expandButton: UIButton! {
        didSet {
            expandButton.tintColor = .TintD2
        }
    }
    @IBOutlet weak var infoLabel: UILabel! {
        didSet {
            infoLabel.font = .Body()
            infoLabel.textColor = .TintD1
            infoLabel.attributedText = expandedText
        }
    }
    @IBOutlet weak var faqLabel: UILabel! {
        didSet {
            faqLabel.font = .BodySmall()
            faqLabel.textColor = .BasePurple
            faqLabel.text = PILocalizedString("otpFAQButton")
            faqLabel.isUserInteractionEnabled = true

            let tap = UITapGestureRecognizer(target: self, action: #selector(faqButtonDidTap))
            faqLabel.addGestureRecognizer(tap)
        }
    }
    @IBOutlet weak var faqButton: UIButton! {
        didSet {
            faqButton.tintColor = .BasePurple
        }
    }
    @IBOutlet weak var infoLabelHeightConstraint: NSLayoutConstraint!

    private var isExpanded: Bool = false

    @IBAction func expandButtonDidTap(_ sender: Any) {
        isExpanded.toggle()

        expandAnimation()
    }

    @IBAction func faqButtonDidTap(_ sender: Any) {
        delegate?.didTapAction(view: self)
    }

    private func expandAnimation() {
        UIView.animate(
            withDuration: .ocd,
            delay: 0,
            usingSpringWithDamping: 0.7,
            initialSpringVelocity: 0.9,
            options: [.curveEaseOut],
            animations: {
            let imageView = UIImage.init(imageLiteralResourceName: self.isExpanded == true ? "arrowUp" : "arrowDown")
            self.expandButton.setImage(imageView, for: .normal)
            // self.infoLabelHeightConstraint.constant = self.isExpanded == true ? 280 : 0
            self.infoLabel.isHidden = !self.isExpanded
            self.faqLabel.isHidden = !self.isExpanded
            self.faqButton.isHidden = !self.isExpanded

//            self.layoutIfNeeded()
        },
            completion: nil
        )
    }

    private var expandedText: NSAttributedString {
        let cantFindPasswordText = NSMutableAttributedString()

        let haventReceivedEmailTitle = NSMutableAttributedString(
            attributedString: NSAttributedString(
                string: PILocalizedString("otpHaventReceivedEmailTitle"),
                attributes: [
                    NSAttributedString.Key.foregroundColor: UIColor.TintD1,
                    NSAttributedString.Key.font: UIFont.Body_Bold()
                ]
            )
        )

        let haventReceivedEmailText = NSAttributedString(
            string: "\n\(PILocalizedString("otpHaventReceivedEmailText"))",
            attributes: [
                NSAttributedString.Key.foregroundColor: UIColor.TintD1,
                NSAttributedString.Key.font: UIFont.Body()
            ]
        )

        let passcodeExpiredTitle = NSAttributedString(
            attributedString: NSAttributedString(
                string: "\n\n\(PILocalizedString("otpPasscodeExpiredTitle"))",
                attributes: [
                    NSAttributedString.Key.foregroundColor: UIColor.TintD1,
                    NSAttributedString.Key.font: UIFont.Body_Bold()
                ]
            )
        )

        let passcodeExpiredText = NSAttributedString(
            string: "\n\(PILocalizedString("otpPasscodeExpiredText"))",
            attributes: [
                NSAttributedString.Key.foregroundColor: UIColor.TintD1,
                NSAttributedString.Key.font: UIFont.Body()
            ]
        )

        cantFindPasswordText.append(haventReceivedEmailTitle)
        cantFindPasswordText.append(haventReceivedEmailText)
        cantFindPasswordText.append(passcodeExpiredTitle)
        cantFindPasswordText.append(passcodeExpiredText)

        return cantFindPasswordText
    }
}
