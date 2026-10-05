//
//  PIAnnotationView.swift
//  PremierInn
//
//  Created by Marcello Mascia on 05/09/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import MapKit
import UIKit

class PIAnnotationView: MKAnnotationView {
    private static let top: CGFloat = 1
    private static let left: CGFloat = 10
    private static let height: CGFloat = 25
    private var labelRect: CGRect {
        let width: CGFloat = (image?.size.width ?? 0) - PIAnnotationView.left.doubled

        return CGRect(x: PIAnnotationView.left, y: PIAnnotationView.top, width: width, height: PIAnnotationView.height)
    }

    var text: String? {
        didSet {
            textLabel.text = text
        }
    }

    override var image: UIImage? {
        get { super.image }
        set {
            super.image = newValue

            textLabel.frame = labelRect
        }
    }

    private lazy var textLabel: UILabel = {
        let textLabel = UILabel(frame: labelRect)
        textLabel.font = UIFont.Body_Semibold()
        textLabel.minimumScaleFactor = 0.8
        textLabel.adjustsFontSizeToFitWidth = true
        textLabel.textColor = .TintD1
        textLabel.textAlignment = .center

        return textLabel
    }()

	override init(annotation: MKAnnotation?, reuseIdentifier: String?) {
		super.init(annotation: annotation, reuseIdentifier: reuseIdentifier)

        addSubview(textLabel)
	}

    init(annotation: MKAnnotation?, reuseIdentifier: String?, title: String?) {
        super.init(annotation: annotation, reuseIdentifier: reuseIdentifier)

        canShowCallout = false

        if let pinImage = UIImage(named: "mapPinBg") {
            image = pinImage
            centerOffset.y = -pinImage.size.height.halved
        }

        let shapeLayer = CAShapeLayer()
        shapeLayer.fillColor = UIColor.BasePurple.cgColor
        shapeLayer.strokeColor = UIColor.white.cgColor
        shapeLayer.lineWidth = 1
        shapeLayer.path = mapAnnotationPath(with: image?.size ?? CGSize.zero, and: 2)
        shapeLayer.shadowColor = UIColor.black.cgColor
        shapeLayer.shadowOpacity = 0.65
        shapeLayer.shadowOffset = CGSize(width: 0, height: 1)

        layer.addSublayer(shapeLayer)

        let label = UILabel(frame: CGRect(x: 3, y: 0, width: (image?.size.width ?? 0) - 6, height: 20))
        label.backgroundColor = .clear
        label.textAlignment = .center
        label.text = title
        label.textColor = .white
        label.font = .MapPin()

        addSubview(label)
    }

    private func mapAnnotationPath(with size: CGSize, and cornerRadius: CGFloat) -> CGPath {
        let pointyBitHeight: CGFloat = 8
        let pointyBitWidth: CGFloat = 8

        let bezierPath = UIBezierPath()
        bezierPath.move(to: CGPoint(x: 0, y: cornerRadius))
        bezierPath.addArc(
            withCenter: CGPoint(x: cornerRadius, y: cornerRadius),
            radius: cornerRadius,
            startAngle: CGFloat.pi,
            endAngle: CGFloat.pi * 1.5,
            clockwise: true
        )
        bezierPath.addLine(to: CGPoint(x: size.width - cornerRadius, y: bezierPath.currentPoint.y))
        bezierPath.addArc(
            withCenter: CGPoint(x: bezierPath.currentPoint.x, y: cornerRadius),
            radius: cornerRadius,
            startAngle: CGFloat.pi * 1.5,
            endAngle: 0,
            clockwise: true
        )
        bezierPath.addLine(to: CGPoint(x: bezierPath.currentPoint.x, y: size.height - pointyBitHeight - cornerRadius))
        bezierPath.addArc(
            withCenter: CGPoint(x: size.width - cornerRadius, y: size.height - pointyBitHeight - cornerRadius),
            radius: cornerRadius,
            startAngle: 0,
            endAngle: CGFloat.pi / 2,
            clockwise: true
        )

        // add pointyBit
        bezierPath.addLine(to: CGPoint(x: size.width.halved + pointyBitWidth.halved, y: bezierPath.currentPoint.y))
        bezierPath.addLine(to: CGPoint(x: size.width.halved, y: size.height))
        bezierPath.addLine(to: CGPoint(x: size.width.halved - pointyBitWidth.halved, y: size.height - pointyBitHeight))
        // finish pointyBit

        bezierPath.addLine(to: CGPoint(x: cornerRadius, y: size.height - pointyBitHeight))
        bezierPath.addArc(
            withCenter: CGPoint(x: cornerRadius, y: size.height - pointyBitHeight - cornerRadius),
            radius: cornerRadius,
            startAngle: CGFloat.pi / 2,
            endAngle: CGFloat.pi,
            clockwise: true
        )
        bezierPath.close()

        return bezierPath.cgPath
    }

    init(annotation: MKAnnotation?, reuseIdentifier: String?, imageName: String) {
        super.init(annotation: annotation, reuseIdentifier: reuseIdentifier)

        canShowCallout = false

        if let pinImage = UIImage(named: imageName) {
            image = pinImage
            centerOffset.y = -pinImage.size.height.halved
        }

        addSubview(textLabel)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
