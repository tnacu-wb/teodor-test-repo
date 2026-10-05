//
//  UIImage+Extensions.swift
//  PremierInn
//
//  Created by Freddie Parks on 26/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

extension UIImage {
    enum OverlayTextPosition {
        case topLeft
        case topRight
        case bottomLeft
        case bottomRight
    }

    struct OverlayText {
        let text: String
        let position: UIImage.OverlayTextPosition
        let attributes: [NSAttributedString.Key: Any]
        let padding: CGFloat
    }

    func centerCropped(withWidth width: CGFloat, andHeight height: CGFloat) -> UIImage {
        let imageSize = CGSize(width: self.size.width * self.scale, height: self.size.height * self.scale)
        let desiredRatio = (width / height)
        if self.size.width / self.size.height == desiredRatio {
            return self
        }

        let cropSize = CGSize(width: (width * (imageSize.height / height)), height: imageSize.height)
        let xOffset = (imageSize.width - cropSize.width).halved
        let cropRect = CGRect(origin: CGPoint(x: xOffset, y: 0), size: cropSize)

        if let image = self.cgImage?.cropping(to: cropRect) {
            let croppedImage = UIImage(cgImage: image)
            return croppedImage
        }

        return self
    }

    func imageWithColor(_ color: UIColor) -> UIImage {
        self.withTintColor(color)
    }

    func alpha(_ value: CGFloat) -> UIImage? {
        UIGraphicsBeginImageContextWithOptions(size, false, scale)
        draw(at: CGPoint.zero, blendMode: .normal, alpha: value)

        let newImageWithAlpha = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()

        return newImageWithAlpha
    }

    func overlayWithText(overlays: [OverlayText]) -> UIImage {
        let renderer = UIGraphicsImageRenderer(size: size)

        return renderer.image { _ in
            self.draw(at: .zero)

            for overlay in overlays {
                let textSize = overlay.text.size(withAttributes: overlay.attributes)

                let point: CGPoint
                switch overlay.position {
                case .topLeft:
                    point = CGPoint(
                        x: overlay.padding,
                        y: overlay.padding
                    )

                case .topRight:
                    point = CGPoint(
                        x: size.width - textSize.width - overlay.padding,
                        y: overlay.padding
                    )

                case .bottomLeft:
                    point = CGPoint(
                        x: overlay.padding,
                        y: size.height - textSize.height - overlay.padding
                    )

                case .bottomRight:
                    point = CGPoint(
                        x: size.width - textSize.width - overlay.padding,
                        y: size.height - textSize.height - overlay.padding
                    )
                }

                overlay.text.draw(at: point, withAttributes: overlay.attributes)
            }
        }
    }
}
