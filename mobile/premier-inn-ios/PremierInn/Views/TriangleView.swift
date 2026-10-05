//
//  TriangleView.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 01/09/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//
import UIKit

class TriangleView: UIView {
    @IBInspectable var triangleColor: UIColor = .white
    private var shapeLayer: CAShapeLayer?

    override func awakeFromNib() {
        super.awakeFromNib()

        addShapeLayer()
    }

    func addShapeLayer() {
        shapeLayer = CAShapeLayer()
        shapeLayer?.strokeColor = triangleColor.cgColor
        shapeLayer?.fillColor = triangleColor.cgColor

        if let aLayer = shapeLayer {
            layer.addSublayer(aLayer)
        }

        shapeLayer?.path = viewPath()
    }

    private func viewPath() -> CGPath {
        let path = UIBezierPath()

        path.move(to: CGPoint(x: 0, y: frame.height))
        path.addLine(to: CGPoint(x: frame.width.halved, y: 0))
        path.addLine(to: CGPoint(x: frame.width, y: frame.height))

        path.close()

        return path.cgPath
    }
}
