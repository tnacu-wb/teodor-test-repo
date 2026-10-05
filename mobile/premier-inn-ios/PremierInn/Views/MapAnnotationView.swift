//
//  MapAnnotationView.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 30/08/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

public enum ArrowDirection: Int {
    case up
    case down
    case left
    case right
}

protocol MapAnnotationViewDelegate: AnyObject {
	func mapAnnotationViewDidChangeSize(_ view: MapAnnotationView, size: CGSize)
}

class MapAnnotationView: UIView {
    var label: UILabel?
	weak var delegate: MapAnnotationViewDelegate?
	var arrowDirection: ArrowDirection = .down {
		didSet {
			if arrowDirection != oldValue {
				shapeLayer?.path = viewPath(arrowDirection)
			}
		}
	}

    private let marginWidth: CGFloat = 15
    private let marginHeight: CGFloat = 6
    private let arrowWidth: CGFloat = 9
    private let arrowHeight: CGFloat = 6
    private let labelHeight: CGFloat = 20
    private let cornerRadius: CGFloat = 3
    private var shapeLayer: CAShapeLayer?

    func updateWithTextForView(_ text: String, arrowDirection: ArrowDirection) {
        addShapeLayer()
        addLabel()
        addShadow()

        let labelWidth = calculateLabelWidthWithText(text)

        let totalWidth = labelWidth + 2 * marginWidth + 2 * arrowHeight
        let totalHeight = labelHeight + 2 * marginHeight + 2 * arrowHeight
        let frame = CGRect(x: 0, y: 0, width: totalWidth, height: totalHeight)
        label?.frame = CGRect(
            x: marginWidth + arrowHeight,
            y: marginHeight + arrowHeight,
            width: labelWidth,
            height: labelHeight
        )
        self.frame = frame

		shapeLayer?.path = viewPath(arrowDirection)

		delegate?.mapAnnotationViewDidChangeSize(self, size: frame.size)
    }

    private func addShadow() {
        layer.shadowOffset = CGSize(width: 0, height: 2)
        layer.shadowColor = UIColor.black.cgColor
        layer.shadowOpacity = 0.2
        layer.shadowRadius = 2
    }

    private func calculateLabelWidthWithText(_ text: String) -> CGFloat {
        if let label = label {
            label.text = text
            label.font = UIFont.Body_Semibold()
            label.textColor = .TintD1
            let labelContainer = CGSize(width: CGFloat.greatestFiniteMagnitude, height: labelHeight)
            let neededSize = label.sizeThatFits(labelContainer)

            return neededSize.width
        }

        return 0
    }

    private func addLabel() {
        label = UILabel(frame: CGRect.zero)
        label?.textAlignment = .center
        label?.textColor = .black
        label?.numberOfLines = 1
        addSubview(label!)
    }

    private func addShapeLayer() {
        shapeLayer = CAShapeLayer()
        shapeLayer?.strokeColor = UIColor.white.cgColor
        shapeLayer?.fillColor = UIColor.white.cgColor

        if let aLayer = shapeLayer {
            layer.addSublayer(aLayer)
        }
    }

    private func viewPath(_ arrowDirection: ArrowDirection) -> CGPath {
        let path = UIBezierPath(
            roundedRect: CGRect(
                x: arrowHeight,
                y: arrowHeight,
                width: frame.width - arrowHeight.doubled,
                height: frame.height - arrowHeight.doubled
            ),
            cornerRadius: cornerRadius
        )

        switch arrowDirection {
        case .up:
            path.move(to: CGPoint(x: frame.width.halved - arrowWidth.halved, y: arrowHeight))
            path.addLine(to: CGPoint(x: frame.width.halved, y: 0))
            path.addLine(to: CGPoint(x: frame.width.halved + arrowWidth.halved, y: arrowHeight))
        case .down:
            path.move(to: CGPoint(x: frame.width.halved - arrowWidth.halved, y: frame.height - arrowHeight))
            path.addLine(to: CGPoint(x: frame.width.halved, y: frame.height))
            path.addLine(to: CGPoint(x: frame.width.halved + arrowWidth.halved, y: frame.height - arrowHeight))
        case .left:
            path.move(to: CGPoint(x: arrowHeight, y: frame.height.halved - arrowWidth.halved))
            path.addLine(to: CGPoint(x: 0, y: frame.height.halved))
            path.addLine(to: CGPoint(x: arrowHeight, y: frame.height.halved + arrowWidth.halved))
        case .right:
            path.move(to: CGPoint(x: frame.width - arrowHeight, y: frame.height.halved - arrowWidth.halved))
            path.addLine(to: CGPoint(x: frame.width, y: frame.height.halved))
            path.addLine(to: CGPoint(x: frame.width - arrowHeight, y: frame.height.halved + arrowWidth.halved))
        }
        path.close()

        return path.cgPath
    }
}
