//
//  CircledSquareLabel.swift
//  PremierInn
//
//  Created by Nick Jones on 21/11/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

class CircledSquareLabel: UIView {
    var innnerLabel: UILabel?

    init(
        lengthAndWidth: CGFloat,
        position: CGPoint,
        andText text: String,
        textColor: UIColor,
        backgroundColor: UIColor,
        andShouldEmboldenText shouldEmbolden: Bool
    ) {
        super.init(frame:
            CGRect(
                x: position.x,
                y: position.y,
                width: lengthAndWidth,
                height: lengthAndWidth
            )
        )

        let diameter = sqrt(
            (Double(lengthAndWidth) * Double(lengthAndWidth))
        )
        let side = CGFloat(diameter / sqrt(2.0))

        let innerSquare = UILabel(frame:
            CGRect(
                x: CGFloat(Double(lengthAndWidth / 2) - Double(side / 2)),
                y: CGFloat(Double(lengthAndWidth / 2) - Double(side / 2)),
                width: side,
                height: side
            )
        )
        innerSquare.text = text
        innerSquare.textAlignment = .center
        innerSquare.numberOfLines = 0
        innerSquare.lineBreakMode = .byWordWrapping
        innerSquare.textColor = textColor
        innerSquare.font = shouldEmbolden ? UIFont.SubtextStrong() : UIFont.Subtext()

        addSubview(innerSquare)

        layer.cornerRadius = lengthAndWidth / 2
        self.backgroundColor = backgroundColor
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
}
