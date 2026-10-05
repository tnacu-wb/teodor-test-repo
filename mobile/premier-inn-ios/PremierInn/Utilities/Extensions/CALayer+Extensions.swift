//
//  CALayer+Extensions.swift
//  PremierInn
//
//  Created by Freddie Parks on 02/07/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

enum DayCellMaskPosition {
    case first
    case last
    case middle
    case today
    case none
}

extension CALayer {
    func mask(corners: DayCellMaskPosition, idealFrame: CGRect?) {
        cornerRadius = 0

        switch corners {
        case .first:
            mask = round(corners: [.topLeft, .bottomLeft], idealFrame: idealFrame ?? frame)
        case .last:
            mask = round(corners: [.topRight, .bottomRight], idealFrame: idealFrame ?? frame)
        case .middle:
            mask = nil
        case .today:
            mask = round(corners: [.topRight, .bottomRight, .bottomLeft, .topLeft], idealFrame: idealFrame ?? frame)
        case .none:
            mask = nil
        }
    }

    private func round(corners: UIRectCorner, idealFrame: CGRect) -> CALayer {
        let shapeLayer = CAShapeLayer()
        shapeLayer.frame = bounds

		let frame: CGRect = {
			var frame = idealFrame

            // Shave one pixel off the right side to improve corner smoothness
            if corners.contains(.bottomRight) || corners.contains(.topRight) {
                frame.size.width = frame.size.width.rounded(FloatingPointRoundingRule.up)
            }

			return frame
		}()

        let bezierPath = UIBezierPath(
            roundedRect: frame,
            byRoundingCorners: corners,
            cornerRadii: CGSize(width: frame.width / 2, height: frame.height / 2)
        )

        shapeLayer.path = bezierPath.cgPath

        return shapeLayer
    }
}
