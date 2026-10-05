//
//  PricePin.swift
//  PremierInn
//
//  Created by Nick Jones on 23/03/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import UIKit
import MapKit
import SimpleNetwork

struct MapPinColourPalette {
    static let blackAndWhite = MapPinColourPalette(textColour: .black, backgroundColour: .white, borderColour: .black)

    let textColour: UIColor
    let backgroundColour: UIColor
    let borderColour: UIColor
}

/**
 - title: The text to be displayed within the banner; The view's width is based on both this property and the font you provide up to a maximum width of 95 📔
 - headerImage: An optional header 13 by 14 image which is placed in the center of the banner 🌄
 - palette: An object of colours to be used to style the banner made up of the textColour, backgroundColour and borderColour. Pass in the same colour to both this and the backgroundColour for a smooth single colour banner 🎨
 - font: The font to be used for the text within the banner; The view's width is based on both this property and the text you provide up to a maximum width of 95 🖋
*/
struct MapPinProperties {
    static let empty = MapPinProperties(title: "", headerImage: nil, palette: .blackAndWhite, font: .SubtextStrong())

    let title: String
    let headerImage: UIImage?
    let palette: MapPinColourPalette
    let font: UIFont
}

class SuggestionPin: MapPin {
    static let reuseIdentifier = "suggestionPinIdentifier"

    override var properties: MapPinProperties {
        let title: String = {
            guard let title = annotation?.title else { return "" }

            return title ?? ""
        }()
        let palette = MapPinColourPalette(textColour: .TintD1, backgroundColour: .white, borderColour: .white)

        return MapPinProperties(title: title, headerImage: nil, palette: palette, font: .SubtextStrong())
    }
}

class HotelPin: MapPin {
    static let reuseIdentifier = "hotelPinIdentifier"

    override var properties: MapPinProperties {
        guard let hotel = annotation as? Hotel else { return .empty }

        let title: String
        let headerImage: UIImage?
        let palette: MapPinColourPalette

        if hotel.available {
            title = hotel.getLowestCost()?.localizedValue ?? "-"

            switch hotel.brand {
            case .premierInn, .premierInnGermany:
                headerImage = nil
                palette = MapPinColourPalette(textColour: .white, backgroundColour: .BasePurple, borderColour: .white)

            case .hub:
                headerImage = nil
                palette = MapPinColourPalette(textColour: .TintD1, backgroundColour: .hubGreen, borderColour: .white)
            case .zip:
                headerImage = nil
                palette = MapPinColourPalette(textColour: .white, backgroundColour: .zipRed, borderColour: .white)
            }
        } else {
            title = PILocalizedString("mapScreenSoldOutTitle", comment: "Map Sold Out Title")
            headerImage = nil
            palette = MapPinColourPalette(textColour: .white, backgroundColour: .greyish, borderColour: .white)
        }

        return MapPinProperties(title: title, headerImage: headerImage, palette: palette, font: .SubtextStrong())
    }
}

class MapPin: MKAnnotationView {
    open var properties: MapPinProperties { .empty }

    private var bannerLabel: UILabel?
    private var headerImageView: UIImageView?
    private var shapeLayer: CAShapeLayer?

    // Trust us, these numbers are right, ask Henry Cull otherwise...
    private static let height: CGFloat = 21
    private static let sidePadding: CGFloat = 8
    private static let maximumWidth: CGFloat = 110
    private static let headerImageWidth: CGFloat = 14
    private static let headerImageHeight: CGFloat = 14
    private static let headerImageVerticalOffset: CGFloat = -9

    override var annotation: MKAnnotation? {
        get {
            super.annotation
        }
        set {
            super.annotation = newValue

            updateUI()
        }
    }

    override init(annotation: MKAnnotation?, reuseIdentifier: String?) {
        super.init(annotation: annotation, reuseIdentifier: reuseIdentifier)

        layer.shadowColor = UIColor.black.cgColor
        layer.shadowRadius = 2
        layer.shadowOffset = CGSize(width: 1, height: 1)
        layer.shadowOpacity = 0.5
        layer.shouldRasterize = true
        layer.rasterizationScale = UIScreen.main.scale

        shapeLayer = CAShapeLayer()
        if let shapeLayer = shapeLayer {
            layer.addSublayer(shapeLayer)
        }

        frame = CGRect(x: 0, y: 0, width: MapPin.sidePadding.doubled, height: MapPin.height)

        bannerLabel = UILabel(frame: bounds.insetBy(dx: MapPin.sidePadding, dy: 0))
        bannerLabel?.autoresizingMask = [.flexibleHeight, .flexibleWidth]
        bannerLabel?.textAlignment = .center
        if let bannerLabel = bannerLabel {
            addSubview(bannerLabel)
        }

        headerImageView = UIImageView(frame: CGRect(
            x: bounds.width.halved - MapPin.headerImageWidth.halved,
            y: MapPin.headerImageVerticalOffset,
            width: MapPin.headerImageWidth,
            height: MapPin.headerImageHeight
        ))
        headerImageView?.autoresizingMask = [.flexibleLeftMargin, .flexibleRightMargin, .flexibleBottomMargin]
        if let headerImageView = headerImageView {
            addSubview(headerImageView)
        }

        updateUI()
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }

    private func updateUI() {
        guard annotation != nil else { return }

        frame.size.width = min(
            properties.title.width(withConstrainedHeight: MapPin.height, font: properties.font) + MapPin.sidePadding.doubled,
            MapPin.maximumWidth
        )

        bannerLabel?.text = properties.title
        bannerLabel?.font = properties.font
        bannerLabel?.textColor = properties.palette.textColour

        headerImageView?.image = properties.headerImage

        shapeLayer?.path = roundedRectangleWithTrianglePointerPath()
        shapeLayer?.strokeColor = properties.palette.borderColour.cgColor
        shapeLayer?.fillColor = properties.palette.backgroundColour.cgColor
    }

    // MARK: - MKAnnotationView Delegate Functions

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        updateColours(forSelectedState: selected)
    }

    override func prepareForReuse() {
        super.prepareForReuse()

        gestureRecognizers = nil
    }

    // MARK: - Private Functions
    private func updateColours(forSelectedState selected: Bool) {
        guard let hotel = annotation as? Hotel else { return }

        var backgroundColour: UIColor?
        var borderColour: UIColor?

        if hotel.available {
            switch hotel.brand {
            case .hub:
                backgroundColour = selected ? .sea : .hubGreen
                bannerLabel?.textColor = selected ? .white : .TintD1
                borderColour = .white
            case .zip:
                backgroundColour = selected ? .sea : .zipRed
                borderColour = .white
            case .premierInn, .premierInnGermany:
                backgroundColour = selected ? .sea : .BasePurple
                borderColour = .white
            }
        } else {
            backgroundColour = selected ? .sea : .greyish
            borderColour = .white
        }

        shapeLayer?.strokeColor = borderColour?.cgColor
        shapeLayer?.fillColor = backgroundColour?.cgColor
    }

    // MARK: - View & Layer Configuration
    private func roundedRectangleWithTrianglePointerPath() -> CGPath {
        let r: CGFloat = 8          // Corner radius
        let tw: CGFloat = 10        // Triangle width
        let th: CGFloat = 7         // Triangle height
        let h = frame.size.height   // Frame height
        let w = frame.size.width    // Frame width

        let shape = UIBezierPath()

        shape.move(to: CGPoint(x: 0, y: r))

        // Top left curve
        shape.addCurve(to: CGPoint(x: r, y: 0), controlPoint1: .zero, controlPoint2: CGPoint(x: r, y: 0))

        // Top line
        shape.addLine(to: CGPoint(x: w - r, y: 0))

        // Top right curve
        shape.addCurve(to: CGPoint(x: w, y: r), controlPoint1: CGPoint(x: w, y: 0), controlPoint2: CGPoint(x: w, y: r))

        // Right line
        shape.addLine(to: CGPoint(x: w, y: h - r))

        // Bottom right curve
        shape.addCurve(
            to: CGPoint(x: w - r, y: h),
            controlPoint1: CGPoint(x: w, y: h),
            controlPoint2: CGPoint(x: w - r, y: h)
        )

        // Bottom line - right hand side
        shape.addLine(to: CGPoint(x: w.halved + tw.halved, y: h))

        // Triangle
        shape.addLine(to: CGPoint(x: w.halved, y: h + th))
        shape.addLine(to: CGPoint(x: w.halved - tw.halved, y: h))

        // Bottom line - Left Hand Side
        shape.addLine(to: CGPoint(x: r, y: h))

        // Bottom left curve
        shape.addCurve(
            to: CGPoint(x: 0, y: h - r),
            controlPoint1: CGPoint(x: 0, y: h),
            controlPoint2: CGPoint(x: 0, y: h - r)
        )

        // Left line
        shape.addLine(to: CGPoint(x: 0, y: r))

        // Close it off
        shape.close()

        return shape.cgPath
    }
}
