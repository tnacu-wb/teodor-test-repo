//
//  GoshCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 06/07/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

protocol GoshCellDelegate: AnyObject {
    func goshDonationButtonDidSelect(cell: GoshCell, selectedIndex: Int?)
}

class GoshCell: UITableViewCell {
    weak var delegate: GoshCellDelegate?

    private let smallPriceConfettiTag = 30
    private let largePriceConfettiTag = 300

    @IBOutlet weak var titleLabel: UILabel! {
        didSet {
            titleLabel.font = UIFont.Heading3_Semibold()
            titleLabel.textColor = UIColor.TintD1
            titleLabel.text = PILocalizedString("goshTitle", comment: "GOSH Box Title text")
        }
    }
    @IBOutlet weak var descriptionLabel: UILabel! {
        didSet {
            descriptionLabel.font = UIFont.BodySmall()
            descriptionLabel.textColor = UIColor.TintD1
            descriptionLabel.text = PILocalizedString("goshContentCopy", comment: "GOSH Box Content text")
        }
    }

    @IBOutlet weak var goshImage: UIImageView!

    @IBOutlet weak var firstButton: RoundedCornersButton! {
        didSet {
            firstButton.titleLabel?.font = UIFont.Body_Semibold()
            firstButton.setTitleColor(UIColor.TintD1, for: .normal)
            firstButton.setTitleColor(UIColor.white, for: .selected)
            firstButton.backgroundColor = UIColor.TintL3
            firstButton.cornerRadius = 3
            firstButton.prebuildConfettiContainer(withTag: largePriceConfettiTag)
        }
    }
    @IBOutlet weak var secondButton: RoundedCornersButton! {
        didSet {
            secondButton.titleLabel?.font = UIFont.Body_Semibold()
            secondButton.setTitleColor(UIColor.TintD1, for: .normal)
            secondButton.setTitleColor(UIColor.white, for: .selected)
            secondButton.backgroundColor = UIColor.TintL3
            secondButton.cornerRadius = 3
            secondButton.prebuildConfettiContainer(withTag: smallPriceConfettiTag)
        }
    }
    @IBOutlet weak var thirdButton: RoundedCornersButton! {
        didSet {
            thirdButton.titleLabel?.font = UIFont.Body_Semibold()
            thirdButton.setTitleColor(UIColor.TintD1, for: .normal)
            thirdButton.setTitleColor(UIColor.white, for: .selected)
            thirdButton.backgroundColor = UIColor.TintL3
            thirdButton.cornerRadius = 3
        }
    }

    @IBOutlet weak var noDonationsButton: RoundedCornersButton! {
        didSet {
            noDonationsButton.titleLabel?.font = UIFont.Body_Semibold()
            noDonationsButton.setTitleColor(UIColor.TintD1, for: .normal)
            noDonationsButton.setTitleColor(UIColor.white, for: .selected)
            noDonationsButton.backgroundColor = UIColor.TintL3
            noDonationsButton.setTitle(PILocalizedString("goshNoDonationTitle"), for: .normal)
            noDonationsButton.cornerRadius = 3
        }
    }

    @IBAction func priceButtonDidTap(_ sender: UIButton) {
        firstButton.isSelected = false
        secondButton.isSelected = false
        thirdButton.isSelected = false
        noDonationsButton.isSelected = false

        sender.isSelected = true

        var selectedOption: Int? {
            if sender == firstButton {
                NotificationFeedbackManager.shared.provideFeedback(for: .success)
                return 0
            } else if sender == secondButton {
                NotificationFeedbackManager.shared.provideFeedback(for: .success)
                return 1
            } else if sender == thirdButton {
                NotificationFeedbackManager.shared.provideFeedback(for: .error)
                return 2
            } else {
                return nil
            }
        }

        delegate?.goshDonationButtonDidSelect(cell: self, selectedIndex: selectedOption)
    }

    public func buttonFromIndex(index: Int?) -> RoundedCornersButton {
        switch index {
        case 0:
            return firstButton
        case 1:
            return secondButton
        case 2:
            return thirdButton
        default:
            return noDonationsButton
        }
    }
}

extension RoundedCornersButton {
    override var isSelected: Bool {
        didSet {
            backgroundColor = isSelected ? UIColor.Tint1 : UIColor.TintL3
        }
    }
}
