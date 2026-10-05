//
//  RoundedCornersLabel.swift
//  PremierInn
//
//  Created by Freddie Parks on 29/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

@IBDesignable class RoundedCornersLabel: UILabel {
    @IBInspectable var cornerRadius: CGFloat = 0.0

    override init(frame: CGRect) {
        super.init(frame: frame)

        setup()
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    override func awakeFromNib() {
        super.awakeFromNib()

        setup()
    }

    override func prepareForInterfaceBuilder() {
        super.prepareForInterfaceBuilder()

        setup()
    }

    func setup() {
        layer.cornerRadius = cornerRadius
    }
}
