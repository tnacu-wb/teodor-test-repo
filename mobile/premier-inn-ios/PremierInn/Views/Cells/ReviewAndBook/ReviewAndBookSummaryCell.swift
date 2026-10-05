//
//  ReviewAndBookSummaryCell.swift
//  PremierInn
//
//  Created by Santa Gurung on 09/01/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import UIKit

class ReviewAndBookSummaryCell: UITableViewCell {
    @IBOutlet weak var mainView: UIView! {
        didSet {
            mainView.layer.cornerRadius = 4.0
            mainView.layer.borderWidth = 1.0
            mainView.layer.borderColor = UIColor.TintL2.cgColor
        }
    }
    @IBOutlet weak var hotelImageView: UIImageView! {
        didSet {
            hotelImageView.layer.cornerRadius = 8.0
        }
    }
    @IBOutlet weak var hotelNameLabel: UILabel! {
        didSet {
            hotelNameLabel.font = UIFont.Heading2_Bold()
            hotelNameLabel.textColor = .BasePurple
        }
    }
    @IBOutlet weak var dateLabel: UILabel! {
        didSet {
            dateLabel.font = UIFont.Body_Medium()
            dateLabel.textColor = .TintD1
        }
    }
    @IBOutlet weak var extrasTitleLabel: UILabel! {
        didSet {
            extrasTitleLabel.text = PILocalizedString("bookingSummaryExtrasTitle", comment: "Booking summary extras title")
            extrasTitleLabel.font = .Heading3_Semibold()
            extrasTitleLabel.textColor = .TintD1
        }
    }
    @IBOutlet weak var editButton: UIButton! {
        didSet {
            editButton.configurationUpdateHandler = { button in
                var config = UIButton.Configuration.plain()
                config.title = PILocalizedString("reviewChangeButtonTitle", comment: "Edit button title")
                config.titleTextAttributesTransformer = UIConfigurationTextAttributesTransformer { attribute in
                    var newAttr = attribute
                    newAttr.font = UIFont.Body_Medium()
                    newAttr.foregroundColor = .BasePurple
                    return newAttr
                }
                let edgeInsets = NSDirectionalEdgeInsets(top: 0, leading: 0, bottom: 0, trailing: 0)
                config.contentInsets = edgeInsets
                button.configuration = config
            }
        }
    }
    @IBOutlet weak var breakdownSeparatorView: UIView! {
        didSet {
            breakdownSeparatorView.isHidden = true
        }
    }
    @IBOutlet weak var extrasHeaderView: UIStackView! {
        didSet {
            extrasHeaderView.isHidden = true
        }
    }
    @IBOutlet weak var promotionLabel: UIPaddingLabel! {
        didSet {
            promotionLabel.isHidden = true
            promotionLabel.font = .SubtextStrong()
            promotionLabel.textColor = .Tint1
            promotionLabel.backgroundColor = .Tint3
            promotionLabel.layer.cornerRadius = 4
            promotionLabel.layer.masksToBounds = true
        }
    }
    @IBOutlet weak var promotionLabelHeight: NSLayoutConstraint! {
        didSet {
            promotionLabelHeight.constant = 0
        }
    }
    @IBOutlet weak var breakdownStackView: UIStackView!
    @IBOutlet weak var roomLabel: UILabel! {
        didSet {
            roomLabel.font = UIFont.Body()
            roomLabel.textColor = .TintD1
        }
    }
    @IBOutlet weak var roomOriginalTotalLabel: UILabel! {
        didSet {
            roomOriginalTotalLabel.font = UIFont.Heading4_Bold()
            roomOriginalTotalLabel.textColor = .TintL1
        }
    }
    @IBOutlet weak var roomTotalLabel: UILabel! {
        didSet {
            roomTotalLabel.font = UIFont.Heading3_Semibold()
            roomTotalLabel.textColor = .TintD1
        }
    }
    @IBOutlet weak var totalCostLabel: UILabel! {
        didSet {
            totalCostLabel.font = UIFont.Heading2_Semibold()
            totalCostLabel.textColor = .TintD1
        }
    }
    @IBOutlet weak var totalAmount: UILabel! {
        didSet {
            totalAmount.font = UIFont.Heading2_Semibold()
            totalAmount.textColor = .TintD1
        }
    }
    @IBOutlet weak var taxesAndFeesLabel: UILabel! {
        didSet {
            taxesAndFeesLabel.text = PILocalizedString("PriceIncludesTaxesAndFees")
            taxesAndFeesLabel.textColor = .ColourDL1
            taxesAndFeesLabel.font = UIFont.BodySmall()
        }
    }
    @IBOutlet weak var fullBreakdownButton: UIButton! {
        didSet {
            fullBreakdownButton.configurationUpdateHandler = { button in
                var config = UIButton.Configuration.plain()
                config.title = PILocalizedString("reviewBreakdownLabel", comment: "Breakdown label title")
                config.titleTextAttributesTransformer = UIConfigurationTextAttributesTransformer { attribute in
                    var newAttr = attribute
                    newAttr.font = UIFont.BodySmall()
                    newAttr.foregroundColor = .BasePurple
                    return newAttr
                }
                let edgeInsets = NSDirectionalEdgeInsets(top: 0, leading: 0, bottom: 0, trailing: 0)
                config.contentInsets = edgeInsets
                button.configuration = config
            }
        }
    }

    func showOriginalRoomCost(originalCostString: NSAttributedString?) {
        // non-discounted
        let shouldHideOriginalCost = originalCostString == nil
        roomOriginalTotalLabel.isHidden = shouldHideOriginalCost
        roomTotalLabel.textColor = shouldHideOriginalCost ? UIColor.TintD1 : UIColor.Tint1
        roomTotalLabel.font = shouldHideOriginalCost ? .Heading3_Semibold() : .Heading3_Bold()

        if let originalCostString = originalCostString {
            roomOriginalTotalLabel.attributedText = originalCostString
        }
    }

    func hidePromotion() {
        promotionLabel.isHidden = true
        promotionLabelHeight.constant = 0
    }
}
