//
//  ErrorAndPhoneViewInCell.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 27/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

protocol ErrorAndPhoneViewProtocol: AnyObject {
    func callCustomerService()
}

class ErrorAndPhoneViewInCell: ErrorViewInCell {
    private static let callUsCompactTopConstraint: CGFloat = 25
    private static let callUsHeightCompactAdjustment: CGFloat = 65

    weak var delegate: ErrorAndPhoneViewProtocol?

    private var callUsView = UIView(frame: .zero)

    override func addAdditionalView() {
        if let cell: CallHotelCell = CallHotelCell.fromNib() {
            cell.callDescriptionTopConstraint.constant = ErrorAndPhoneViewInCell.callUsCompactTopConstraint
            cell.callChargeInformationLabel.text = PILocalizedString("telephoneNoCost", comment: "Call cost message")
            cell.callHotelButton.addTarget(self, action: #selector(callHotelButtonDidTap), for: .touchUpInside)

            callUsView = cell.contentView
            callUsView.backgroundColor = UIColor.clear
            additionalViewHeight = callUsView.frame.height - ErrorAndPhoneViewInCell.callUsHeightCompactAdjustment
        }

        callUsView.translatesAutoresizingMaskIntoConstraints = false
        addSubview(callUsView)

        addingConstraintsForCallUsView()
    }

    private func addingConstraintsForCallUsView() {
        let callUsViewTopConstraint = NSLayoutConstraint(
            item: callUsView,
            attribute: .top,
            relatedBy: .equal,
            toItem: label!,
            attribute: .bottom,
            multiplier: 1.0,
            constant: marginHeight
        )
        let callUsViewLeadingConstraint = NSLayoutConstraint(
            item: callUsView,
            attribute: .leading,
            relatedBy: .equal,
            toItem: self,
            attribute: .leading,
            multiplier: 1.0,
            constant: 0
        )
        let callUsViewTrailingConstraint = NSLayoutConstraint(
            item: callUsView,
            attribute: .trailing,
            relatedBy: .equal,
            toItem: self,
            attribute: .trailing,
            multiplier: 1.0,
            constant: 0
        )
        let callUsViewHeightConstraint = NSLayoutConstraint(
            item: callUsView,
            attribute: .height,
            relatedBy: .equal,
            toItem: nil,
            attribute: .notAnAttribute,
            multiplier: 1.0,
            constant: additionalViewHeight
        )
        addConstraints([
            callUsViewTopConstraint,
            callUsViewLeadingConstraint,
            callUsViewTrailingConstraint,
            callUsViewHeightConstraint
        ])
    }

    @objc private func callHotelButtonDidTap() {
        delegate?.callCustomerService()
    }
}
