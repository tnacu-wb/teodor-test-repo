//
//  DottedLine.swift
//  PremierInn
//
//  Created by Freddie Parks on 24/03/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

class DottedLine: UIView {
    private var dottedLineLayer: CAShapeLayer?

    override func layoutSubviews() {
        super.layoutSubviews()

        let dotSize = max(Int(frame.size.height), 1)
        let excess: Int = (Int(frame.size.width) % dotSize)

        backgroundColor = .clear

        dottedLineLayer?.removeFromSuperlayer()

        dottedLineLayer = CAShapeLayer()
        dottedLineLayer?.strokeColor = UIColor.greyBorder.cgColor
        dottedLineLayer?.lineWidth = frame.size.height

        let dotSizeNumber = NSNumber(value: dotSize)
        dottedLineLayer?.lineDashPattern = [dotSizeNumber, dotSizeNumber]

        let offsetX = (excess > 0) ? (excess / 2) : 0

        let path = UIBezierPath()
        path.move(to: CGPoint(x: CGFloat(offsetX), y: frame.size.height.halved))
        path.addLine(to: CGPoint(x: frame.size.width - CGFloat(excess).doubled, y: path.currentPoint.y))

        dottedLineLayer?.path = path.cgPath

        guard let dottedLineLayer = dottedLineLayer else { return }
        layer.addSublayer(dottedLineLayer)
    }
}
