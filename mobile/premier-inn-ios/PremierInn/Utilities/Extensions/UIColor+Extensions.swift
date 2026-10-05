//
//  UIColor+Extensions.swift
//  PremierInn
//
//  Created by Marcello Mascia on 17/06/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

public typealias RGBA = (red: CGFloat, green: CGFloat, blue: CGFloat, alpha: CGFloat)

extension UIColor {
    public convenience init(hex: String) {
        var hexString = hex

        if !hex.hasPrefix("#") {
            hexString = "#" + hex
        }

        let rgba = UIColor.RGBAFromHexString(hex: hexString)

        self.init(red: rgba.red, green: rgba.green, blue: rgba.blue, alpha: rgba.alpha)
    }

    public func rgba() -> RGBA {
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 1

        getRed(&red, green: &green, blue: &blue, alpha: &alpha)

        return (red: red, green: green, blue: blue, alpha: alpha)
    }

    private class func RGBAFromHexString(hex: String) -> RGBA {
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        let alpha: CGFloat = 1

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

	public class var premierInnPurple: UIColor {
        UIColor(red: 81.0 / 255.0, green: 30.0 / 255.0, blue: 98.0 / 255.0, alpha: 1.0)
    }

	public class var silver: UIColor {
        UIColor(red: 218.0 / 255.0, green: 225.0 / 255.0, blue: 224.0 / 255.0, alpha: 1.0)
    }

	public class var silverTwo: UIColor {
        UIColor(red: 182.0 / 255.0, green: 196.0 / 255.0, blue: 193.0 / 255.0, alpha: 1.0)
    }

	public class var butterscotch: UIColor {
        UIColor(red: 249.0 / 255.0, green: 201.0 / 255.0, blue: 64.0 / 255.0, alpha: 1.0)
    }

	public class var sea: UIColor {
        UIColor(red: 60.0 / 255.0, green: 134.0 / 255.0, blue: 139.0 / 255.0, alpha: 1.0)
    }

    public class var neoSea: UIColor {
        UIColor(red: 74.0 / 255.0, green: 151.0 / 255.0, blue: 157.0 / 255.0, alpha: 1.0)
    }

    public class var paleSea: UIColor {
        UIColor(red: 245.0 / 255.0, green: 249.0 / 255.0, blue: 249.0 / 255.0, alpha: 1.0)
    }

	public class var strongRed: UIColor {
        UIColor(red: 230.0 / 255.0, green: 92.0 / 255.0, blue: 68.0 / 255.0, alpha: 1.0)
    }

	public class var paleRed: UIColor {
        UIColor(red: 230 / 255, green: 92 / 255, blue: 68 / 255, alpha: 1)
    }

	public class var warmGrey: UIColor {
        UIColor(red: 151.0 / 255.0, green: 151.0 / 255.0, blue: 151.0 / 255.0, alpha: 1.0)
    }

	public class var slate: UIColor {
        UIColor(red: 51.0 / 255.0, green: 51.0 / 255.0, blue: 51.0 / 255.0, alpha: 1.0)
    }

    public class var slateGrey: UIColor {
        UIColor(red: 88.0 / 255.0, green: 89.0 / 255.0, blue: 91.0 / 255.0, alpha: 1.0)
    }

	public class var pinkishGrey: UIColor {
        UIColor(red: 204.0 / 255.0, green: 204.0 / 255.0, blue: 204.0 / 255.0, alpha: 1.0)
    }

	public class var greyishBrown: UIColor {
        UIColor(red: 74.0 / 255.0, green: 74.0 / 255.0, blue: 74.0 / 255.0, alpha: 1.0)
    }

    public class var greyishBrownTwo: UIColor {
        UIColor(red: 80.0 / 255.0, green: 80.0 / 255.0, blue: 80.0 / 255.0, alpha: 1.0)
    }

    public class var greyishBrownFour: UIColor {
        UIColor(red: 86.0 / 255.0, green: 86.0 / 255.0, blue: 86.0 / 255.0, alpha: 1.0)
    }

	public class var greyPurple: UIColor {
        UIColor(red: 142.0 / 255.0, green: 106.0 / 255.0, blue: 151.0 / 255.0, alpha: 1.0)
    }

	public class var deepOrange: UIColor {
        UIColor(red: 226.0 / 255.0, green: 63.0 / 255.0, blue: 0, alpha: 1.0)
    }

    public class var squash: UIColor {
        UIColor(red: 245.0 / 255.0, green: 159.0 / 255.0, blue: 17.0 / 255.0, alpha: 1.0)
    }

	public class var grape: UIColor {
        UIColor(red: 108.0 / 255.0, green: 48.0 / 255.0, blue: 114.0 / 255.0, alpha: 1.0)
    }

	public class var premierInnBlack: UIColor {
        UIColor(red: 51.0 / 255.0, green: 51.0 / 255.0, blue: 51.0 / 255.0, alpha: 1.0)
    }

    public class var coral: UIColor {
        UIColor(red: 254.0 / 255.0, green: 239.0 / 255.0, blue: 217.0 / 255.0, alpha: 1.0)
    }

    public class var whiteTwo: UIColor {
        UIColor(red: 243.0 / 255.0, green: 242.0 / 255.0, blue: 240.0 / 255.0, alpha: 1.0)
    }

    public class var whiteThree: UIColor {
        UIColor(red: 216.0 / 255.0, green: 216.0 / 255.0, blue: 216.0 / 255.0, alpha: 1.0)
    }

    public class var wheat: UIColor {
        UIColor(red: 251.0 / 255.0, green: 220.0 / 255.0, blue: 131.0 / 255.0, alpha: 1.0)
    }

    public class var greyish: UIColor {
        UIColor(red: 170 / 255, green: 170 / 255, blue: 170 / 255, alpha: 1.0)
    }

    public class var greyBorder: UIColor {
        UIColor(red: 221 / 255, green: 221 / 255, blue: 221 / 255, alpha: 1.0)
    }

    public class var paleTeal: UIColor {
        UIColor(red: 128 / 255, green: 191 / 255, blue: 189 / 255, alpha: 1.0)
    }

    public class var turquoise: UIColor {
        UIColor(red: 78 / 255, green: 158 / 255, blue: 186 / 255, alpha: 1.0)
    }

    public class var pinkishRed: UIColor {
        UIColor(red: 235 / 255, green: 25 / 255, blue: 60 / 255, alpha: 1.0)
    }

    public class var gunMetal: UIColor {
        UIColor(red: 97 / 255, green: 117 / 255, blue: 113 / 255, alpha: 1.0)
    }

    public class var lightestGray: UIColor {
        UIColor(red: 240 / 255, green: 242 / 255, blue: 242 / 255, alpha: 1.0)
    }

    public class var textFieldPlaceholder: UIColor {
        UIColor(hex: "58595B")
    }

    public class var formGray: UIColor {
        UIColor(red: 148 / 255, green: 148 / 255, blue: 148 / 255, alpha: 1.0)
    }

    public class var hubGreen: UIColor {
        UIColor(red: 190 / 255, green: 214 / 255, blue: 0, alpha: 1.0)
    }

    public class var hubGrey: UIColor {
        UIColor(red: 21 / 255, green: 21 / 255, blue: 21 / 255, alpha: 1.0)
    }

    public class var hubRoundelGrey: UIColor {
        UIColor(red: 56 / 255, green: 56 / 255, blue: 56 / 255, alpha: 1.0)
    }

    public class var zipRed: UIColor {
        UIColor(red: 252 / 255, green: 15 / 255, blue: 66 / 255, alpha: 1.0)
    }

    public class var segmentSelectorDarkAzure: UIColor {
        UIColor(red: 0 / 255, green: 121 / 255, blue: 142 / 255, alpha: 1.0)
    }

    public class var greyishWhite: UIColor {
        UIColor(red: 248 / 255, green: 248 / 255, blue: 248 / 255, alpha: 1.0)
    }

    public class var veryLightPink85: UIColor {
        UIColor(red: 230 / 255, green: 230 / 255, blue: 230 / 255, alpha: 0.85)
    }

    public class var darkerishGrey69: UIColor {
        UIColor(red: 58 / 255, green: 58 / 255, blue: 58 / 255, alpha: 0.85)
    }

    public class var darkishGrey69: UIColor {
        UIColor(red: 66 / 255, green: 66 / 255, blue: 66 / 255, alpha: 0.85)
    }

    public class var lightPeach50: UIColor {
        UIColor(red: 220 / 255, green: 216 / 255, blue: 216 / 255, alpha: 0.5)
    }

    public class var steelGrey: UIColor {
        UIColor(red: 123 / 255, green: 128 / 255, blue: 128 / 255, alpha: 1.0)
    }

    public class var cerise: UIColor {
        UIColor(red: 222 / 255, green: 8 / 255, blue: 74 / 255, alpha: 1.0)
    }

    public class var tripAdvisorShamrockGreen: UIColor {
        UIColor(red: 0 / 255, green: 176 / 255, blue: 135 / 255, alpha: 1.0)
    }
}
