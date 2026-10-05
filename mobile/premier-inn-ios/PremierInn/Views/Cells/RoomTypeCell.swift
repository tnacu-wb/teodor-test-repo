//
//  RoomTypeCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 19/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class RoomTypeCell: UIView {
    var tapAction: (() -> Void)?

    @IBOutlet weak var typeImage: UIImageView!
    @IBOutlet weak var typeTitle: UILabel! {
        didSet {
            typeTitle.font = UIFont.Heading3_Semibold()
        }
    }
    @IBOutlet weak var typeDescription: UILabel! {
        didSet {
            typeDescription.font = UIFont.BodySmall()
            typeDescription.accessibilityIdentifier = "roomDescriptionAcc"
        }
    }
    @IBOutlet weak var radioButton: RadioButtonView! {
        didSet {
            radioButton.backgroundColor = .clear
        }
    }
    @IBOutlet weak var separatorView: UIView! {
        didSet {
            separatorView.backgroundColor = .greyBorder
        }
    }

    override func awakeFromNib() {
        super.awakeFromNib()
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(handleTap))
        addGestureRecognizer(tapGesture)
    }

    @objc private func handleTap() {
        tapAction?()
    }
}
