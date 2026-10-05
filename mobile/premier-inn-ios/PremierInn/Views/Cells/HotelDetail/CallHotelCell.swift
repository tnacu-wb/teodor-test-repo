//
//  CallHotelCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 09/09/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class CallHotelCell: UITableViewCell {
    @IBOutlet weak var callChargeInformationLabel: UILabel! {
        didSet {
            configureCallChargeInformationLabel()
        }
    }
    @IBOutlet weak var needHelpLabel: UILabel! {
        didSet {
            configureNeedHelpLabel()
            callChargeInformationLabel.text = PILocalizedString("telephoneUK", comment: "Call cost message")
            callChargeInformationLabel.font = UIFont.SubtextSmall()
            callChargeInformationLabel.textColor = .TintD1
        }
    }
    @IBOutlet weak var callDescriptionTopConstraint: NSLayoutConstraint!
    @IBOutlet weak var callHotelButton: RoundedCornersButton! {
        didSet {
            configureCallHotelButton()
        }
    }
    @IBOutlet weak var containerView: UIView! {
        didSet {
            configureContainerView()
        }
    }

    override func awakeFromNib() {
        super.awakeFromNib()
        configureContentView()
    }


    private func configureCallChargeInformationLabel() {
        callChargeInformationLabel.text = PILocalizedString("telephoneCost", comment: "Call cost message")
        callChargeInformationLabel.font = UIFont.Subtext()
        callChargeInformationLabel.textColor = .ColourDL1
    }

    private func configureNeedHelpLabel() {
        needHelpLabel.text = PILocalizedString("needHelp", comment: "Label title to call section")
        needHelpLabel.textColor = UIColor.ColourDL1
        needHelpLabel.font = UIFont.Body_Semibold()
    }

    private func configureCallHotelButton() {
        callHotelButton.titleLabel?.text = PILocalizedString("callHotelButtonTitle", comment: "Title of the call button")
        callHotelButton.titleLabel?.font = UIFont.Action2()
        callHotelButton.setTitleColor(UIColor.ColourDL1, for: .normal)
        callHotelButton.layer.borderColor = UIColor.TintL3.cgColor
        callHotelButton.layer.borderWidth = 1
        var config = UIButton.Configuration.plain()
        config.image = UIImage(systemName: "callIcon")
        config.imagePlacement = .top
        config.imagePadding = 4.0
        callHotelButton.configuration = config
    }

    private func configureContainerView() {
        containerView.layer.cornerRadius = 8
        containerView.backgroundColor = UIColor.TintL5
    }

    private func configureContentView() {
        self.contentView.backgroundColor = .BaseWhite
		textLabel?.text = nil
        callChargeInformationLabel.text = PILocalizedString("telephoneUK", comment: "Call cost message")
    }
}
