//
//  MealPreferenceCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 06/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

class MealPreferenceCell: UIView {
    var tapAction: (() -> Void)?

    @IBOutlet weak var separatorView: UIView! {
        didSet {
            separatorView.backgroundColor = .greyBorder
        }
    }
    @IBOutlet weak var mealTitle: UILabel! {
        didSet {
            mealTitle.font = .Body_Semibold()
            mealTitle.textColor = .TintD1
        }
    }
    @IBOutlet weak var radioButton: RadioButtonView! {
        didSet {
            radioButton.backgroundColor = .clear
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
