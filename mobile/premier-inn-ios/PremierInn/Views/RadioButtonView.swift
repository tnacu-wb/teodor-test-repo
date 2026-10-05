//
//  RadioButtonView.swift
//  PremierInn
//
//  Created by Freddie Parks on 25/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

@IBDesignable class RadioButtonView: UIView {
    private var outerCircle: CAShapeLayer?
    private var innerCircle: CAShapeLayer?

    var isSelected: Bool = false {
        didSet {
            innerCircle?.isHidden = !isSelected
            outerCircle?.strokeColor = isSelected ? UIColor.Tint1.cgColor : UIColor.greyish.cgColor
        }
    }

    override init(frame: CGRect) {
        super.init(frame: frame)

        setup()
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)

        setup()
    }

    override func awakeFromNib() {
        super.awakeFromNib()

        setup()
    }

    override func prepareForInterfaceBuilder() {
        super.prepareForInterfaceBuilder()

        setup()
    }

    private func setup() {
        let sizeHalved = frame.size.width / 2
        let center = CGPoint(x: sizeHalved, y: sizeHalved)

        outerCircle?.removeFromSuperlayer()
        innerCircle?.removeFromSuperlayer()

        outerCircle = CAShapeLayer()

        let outerPath = UIBezierPath(
            arcCenter: center,
            radius: sizeHalved,
            startAngle: 0,
            endAngle: CGFloat.pi.doubled,
            clockwise: true
        )
        outerPath.lineWidth = 1

        outerCircle?.path = outerPath.cgPath
        outerCircle?.fillColor = UIColor.clear.cgColor
        outerCircle?.strokeColor = UIColor.greyish.cgColor

        innerCircle = CAShapeLayer()

        let innerPath = UIBezierPath(
            arcCenter: center,
            radius: sizeHalved - 5,
            startAngle: 0,
            endAngle: CGFloat.pi.doubled,
            clockwise: true
        )

        innerCircle?.path = innerPath.cgPath
        innerCircle?.fillColor = UIColor.Tint1.cgColor
        innerCircle?.strokeColor = UIColor.clear.cgColor
        innerCircle?.isHidden = true

        guard let outerCircle = outerCircle, let innerCircle = innerCircle else { return }
        layer.addSublayer(outerCircle)
        layer.addSublayer(innerCircle)

        backgroundColor = .clear
    }
}
