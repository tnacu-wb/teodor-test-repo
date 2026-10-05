//
//  ErrorViewInCell.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 21/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class ErrorViewInCell: UIView {
    var label: UILabel?
    var additionalViewHeight: CGFloat = 0
    let marginHeight: CGFloat = 10

    private let marginWidth: CGFloat = 20
    private let padding: CGFloat = 12
    private let borderWidth: CGFloat = 1
    private let arrowWidth: CGFloat = 16
    private let arrowHeight: CGFloat = 8

    var shapeLayer: CAShapeLayer?

    override func awakeFromNib() {
        super.awakeFromNib()

        addShapeLayer()
        addLabel()
        addAdditionalView()
    }

    private func addShapeLayer() {
        shapeLayer = CAShapeLayer()
        shapeLayer?.lineWidth = CGFloat(borderWidth)
        shapeLayer?.strokeColor = UIColor.Tint8.cgColor
        shapeLayer?.fillColor = UIColor.white.cgColor
        if let aLayer = shapeLayer {
            layer.addSublayer(aLayer)
        }
    }

    private func addLabel() {
        label = UILabel(frame: CGRect.zero)
        label?.textAlignment = NSTextAlignment.left
        label?.textColor = .Tint8
        label?.numberOfLines = 0
        label?.accessibilityIdentifier = "errorAcc"
        label?.isHidden = false

        addSubview(label!)
    }

    func addAdditionalView() {
    }

    func updateWithErrorTextForButton(_ text: String, button: UIButton) -> CGFloat {
        let labelHeight = calculateLabelHeightWithText(text)
        errorBoxWithHeight(labelHeight, arrowAtPosition: button.center.x)
        let totalHeight = labelHeight + 2 * padding + borderWidth + marginHeight + arrowHeight + additionalViewHeight
        return totalHeight
    }

    private func calculateLabelHeightWithText(_ text: String) -> CGFloat {
        if let label = label {
            let fontAttr = [NSAttributedString.Key.font: UIFont.BodySmall()]
            label.attributedText = NSMutableAttributedString(string: text, attributes: fontAttr)
            let labelContainerWidth = frame.size.width - 2 * CGFloat(padding) - 2 * CGFloat(marginWidth)
            let labelContainer = CGSize(width: labelContainerWidth, height: CGFloat.greatestFiniteMagnitude)
            let neededSize = label.sizeThatFits(labelContainer)

            let labelHeight = neededSize.height

            return labelHeight
        }

        return 0
    }

    private func errorBoxWithHeight(_ height: CGFloat, arrowAtPosition: CGFloat) {
        let width = frame.size.width - marginWidth - marginWidth - padding - padding

        label?.frame = CGRect(
            x: padding + marginWidth,
            y: padding + marginHeight + arrowHeight,
            width: width,
            height: height
        )

        if let shapeLayer = shapeLayer {
            shapeLayer.path = errorBoxPath(arrowAtPosition, labelHeight: height)
        }
    }

    private func errorBoxPath(_ arrowPosition: CGFloat, labelHeight: CGFloat) -> CGPath {
        let path = UIBezierPath()

        let topline = marginHeight + arrowHeight
        let containerSize = labelHeight + padding + padding

        path.move(to: CGPoint(x: arrowPosition - arrowWidth.halved, y: topline))
        path.addLine(to: CGPoint(x: arrowPosition, y: topline - arrowHeight))
        path.addLine(to: CGPoint(x: arrowPosition + (arrowWidth.halved), y: topline))
        path.addLine(to: CGPoint(x: frame.size.width - marginWidth - borderWidth, y: topline))
        path.addLine(to: CGPoint(
            x: frame.size.width - marginWidth - borderWidth,
            y: marginHeight + arrowHeight + containerSize
        ))
        path.addLine(to: CGPoint(x: marginWidth + borderWidth, y: marginHeight + arrowHeight + containerSize))
        path.addLine(to: CGPoint(x: marginWidth + borderWidth, y: topline + borderWidth))

        path.close()

        return path.cgPath
    }
}
