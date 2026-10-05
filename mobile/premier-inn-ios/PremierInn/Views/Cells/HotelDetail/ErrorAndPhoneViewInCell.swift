//
//  ErrorAndPhoneViewInCell.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 27/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

protocol ErrorAndPhoneViewProtocol: class {
    func callCustomerServices()
}

class ErrorAndPhoneViewInCell: ErrorViewInCell {
    weak var delegate: ErrorAndPhoneViewProtocol?

    var callUsView = UIView(frame: CGRect.zero)
    var callUsButton = UIButton(type: .custom)
    var callUsLabel = UILabel(frame: CGRect.zero)
    var chargeLabel = UILabel(frame: CGRect.zero)

    private let callUsCompactTopConstraint: CGFloat = 25
    private let callUsHeightCompactAdjustment: CGFloat = 65

    override func addAdditionalView() {
        if let cell = Bundle.main.loadNibNamed(String(describing: CallHotelCell.self), owner: nil, options: nil)!
           .first as? CallHotelCell {
            cell.callDescriptionTopConstraint.constant = callUsCompactTopConstraint

            callUsView = cell.contentView
            callUsView.backgroundColor = UIColor.clear
            cell.delegate = self
            additionalViewHeight = callUsView.frame.height - callUsHeightCompactAdjustment
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
}

extension ErrorAndPhoneViewInCell: CallHotelCellDelegate {
    func callHotelButtonDidTap(_ sender: CallHotelCell) {
        delegate?.callCustomerServices()
    }
}
