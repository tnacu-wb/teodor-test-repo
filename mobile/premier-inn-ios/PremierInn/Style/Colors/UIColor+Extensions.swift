//
//  UIColor+Extensions.swift
//  PremierInn
//
//  Created by Marcello Mascia on 17/06/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

public typealias RGBA = (red: CGFloat, green: CGFloat, blue: CGFloat, alpha: CGFloat)

public extension UIColor {
    // MARK: - Properties

    /// RGB property with alpha channel.
    var rgba: RGBA {
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 1

        self.getRed(&red, green: &green, blue: &blue, alpha: &alpha)

        return (red: red, green: green, blue: blue, alpha: alpha)
    }

    // MARK: - Initialisers

    /// UIColor initialiser from hex property.
    /// - Parameter hexString: hex value of the color.
    /// - Parameter alpha: alpha channel (default 1.0).
    convenience init(hex: String, alpha: CGFloat = 1.0) {
        var hexString = hex

        if !hex.hasPrefix("#") {
            hexString = "#" + hex
        }

        let rgba = UIColor.RGBAFromHexString(hex: hexString, alpha: alpha)

        self.init(red: rgba.red, green: rgba.green, blue: rgba.blue, alpha: rgba.alpha)
    }

    private class func RGBAFromHexString(hex: String, alpha: CGFloat = 1.0) -> RGBA {
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0

        let index = hex.index(hex.startIndex, offsetBy: 1)
        let hex = String(hex[index...])
        let scanner = Scanner(string: hex)
        var hexValue: CUnsignedLongLong = 0

        if scanner.scanHexInt64(&hexValue) {
            switch hex.count {
            case 3:
                red   = CGFloat((hexValue & 0xF00) >> 8) / 15.0
                green = CGFloat((hexValue & 0x0F0) >> 4) / 15.0
                blue  = CGFloat(hexValue & 0x00F) / 15.0
            case 6:
                red   = CGFloat((hexValue & 0xFF0000) >> 16) / 255.0
                green = CGFloat((hexValue & 0x00FF00) >> 8) / 255.0
                blue  = CGFloat(hexValue & 0x0000FF) / 255.0
            default:
                debugPrint("Invalid RGB string, number of characters after '#' should be either 3, 4, 6 or 8")
            }
        }

        return (red: red, green: green, blue: blue, alpha: alpha)
    }
}
