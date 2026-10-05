//
//  CustomDrawBuilder.swift
//  PremierInn
//
//  Created by Clint Mengolli on 13/01/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit

struct CustomDrawBuilder {
    let rect: CGRect
    let cornerRadius: CGFloat
    let tailHost: TailHost
    let tailDimensions: CGSize
    let tailAlignment: TailAlignment

    func build() -> UIBezierPath {
        let path = UIBezierPath()
        path.lineJoinStyle = .round

        let tailWidth = tailDimensions.width
        let tailHeight = tailDimensions.height

        let bodyTop = tailHost.isTop ? tailHeight : 0
        let bodyBottom = rect.height - (tailHost.isBottom ? tailHeight : 0)

        let tailX = determineTailXPosition()

        path.move(to: CGPoint(x: cornerRadius, y: bodyTop))

        if tailHost.isTop {
            path.addLine(to: CGPoint(x: tailX, y: bodyTop))
            path.addLine(to: CGPoint(x: tailX + tailWidth / 2, y: 0))
            path.addLine(to: CGPoint(x: tailX + tailWidth, y: bodyTop))
        }

        path.addLine(to: CGPoint(x: rect.width - cornerRadius, y: bodyTop))

        path.addQuadCurve(
            to: CGPoint(x: rect.width, y: bodyTop + cornerRadius),
            controlPoint: CGPoint(x: rect.width, y: bodyTop)
        )

        path.addLine(to: CGPoint(x: rect.width, y: bodyBottom - cornerRadius))

        path.addQuadCurve(
            to: CGPoint(x: rect.width - cornerRadius, y: bodyBottom),
            controlPoint: CGPoint(x: rect.width, y: bodyBottom)
        )

        if tailHost.isBottom {
            path.addLine(to: CGPoint(x: tailX + tailWidth, y: bodyBottom))
            path.addLine(to: CGPoint(x: tailX + tailWidth / 2, y: rect.height))
            path.addLine(to: CGPoint(x: tailX, y: bodyBottom))
        }

        path.addLine(to: CGPoint(x: cornerRadius, y: bodyBottom))

        path.addQuadCurve(
            to: CGPoint(x: 0, y: bodyBottom - cornerRadius),
            controlPoint: CGPoint(x: 0, y: bodyBottom)
        )

        path.addLine(to: CGPoint(x: 0, y: bodyTop + cornerRadius))

        path.addQuadCurve(
            to: CGPoint(x: cornerRadius, y: bodyTop),
            controlPoint: CGPoint(x: 0, y: bodyTop)
        )

        return path
    }

    private func determineTailXPosition() -> CGFloat {
        switch tailAlignment {
        case .centered:
            return (rect.width - tailDimensions.width) / 2
        case .leading(let offset):
            return offset
        case .trailing(let offset):
            return rect.width - offset - tailDimensions.width
        case .custom(let offset):
            return offset
        case .none:
            return (rect.width - tailDimensions.width) / 2
        }
    }
}
