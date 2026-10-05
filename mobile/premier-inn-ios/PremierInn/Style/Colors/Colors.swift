//
//  Colors.swift
//  PremierInn
//
//  Created by Filippo Minelle on 27/07/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import UIKit

// TODO: To be removed and replaced with Themes extension
public extension UIColor {
    // MARK: - Colors

    // Old Palette

    /// Color: textFieldPlaceholder [#58595B]
    static var textFieldPlaceholder: UIColor { UIColor(named: "textFieldPlaceholder") ?? UIColor(hex: "#58595B") }

    static var sea: UIColor {
        UIColor(red: 60.0 / 255.0, green: 134.0 / 255.0, blue: 139.0 / 255.0, alpha: 1.0)
    }

    static var neoSea: UIColor {
        UIColor(red: 74.0 / 255.0, green: 151.0 / 255.0, blue: 157.0 / 255.0, alpha: 1.0)
    }

    static var paleSea: UIColor {
        UIColor(red: 245.0 / 255.0, green: 249.0 / 255.0, blue: 249.0 / 255.0, alpha: 1.0)
    }

    static var strongRed: UIColor {
        UIColor(red: 230.0 / 255.0, green: 92.0 / 255.0, blue: 68.0 / 255.0, alpha: 1.0)
    }

    static var paleRed: UIColor {
        UIColor(red: 230 / 255, green: 92 / 255, blue: 68 / 255, alpha: 1)
    }

    static var warmGrey: UIColor {
        UIColor(red: 151.0 / 255.0, green: 151.0 / 255.0, blue: 151.0 / 255.0, alpha: 1.0)
    }

    static var slateGrey: UIColor {
        UIColor(red: 88.0 / 255.0, green: 89.0 / 255.0, blue: 91.0 / 255.0, alpha: 1.0)
    }

    static var pinkishGrey: UIColor {
        UIColor(red: 204.0 / 255.0, green: 204.0 / 255.0, blue: 204.0 / 255.0, alpha: 1.0)
    }

    static var greyishBrown: UIColor {
        UIColor(red: 74.0 / 255.0, green: 74.0 / 255.0, blue: 74.0 / 255.0, alpha: 1.0)
    }

    static var greyishBrownTwo: UIColor {
        UIColor(red: 80.0 / 255.0, green: 80.0 / 255.0, blue: 80.0 / 255.0, alpha: 1.0)
    }

    static var greyPurple: UIColor {
        UIColor(red: 142.0 / 255.0, green: 106.0 / 255.0, blue: 151.0 / 255.0, alpha: 1.0)
    }

    static var deepOrange: UIColor {
        UIColor(red: 226.0 / 255.0, green: 63.0 / 255.0, blue: 0, alpha: 1.0)
    }

    static var squash: UIColor {
        UIColor(red: 245.0 / 255.0, green: 159.0 / 255.0, blue: 17.0 / 255.0, alpha: 1.0)
    }

    static var grape: UIColor {
        UIColor(red: 108.0 / 255.0, green: 48.0 / 255.0, blue: 114.0 / 255.0, alpha: 1.0)
    }

    static var coral: UIColor {
        UIColor(red: 254.0 / 255.0, green: 239.0 / 255.0, blue: 217.0 / 255.0, alpha: 1.0)
    }

    static var whiteTwo: UIColor {
        UIColor(red: 243.0 / 255.0, green: 242.0 / 255.0, blue: 240.0 / 255.0, alpha: 1.0)
    }

    static var whiteThree: UIColor {
        UIColor(red: 216.0 / 255.0, green: 216.0 / 255.0, blue: 216.0 / 255.0, alpha: 1.0)
    }

    static var wheat: UIColor {
        UIColor(red: 251.0 / 255.0, green: 220.0 / 255.0, blue: 131.0 / 255.0, alpha: 1.0)
    }

    static var greyish: UIColor {
        UIColor(red: 170 / 255, green: 170 / 255, blue: 170 / 255, alpha: 1.0)
    }

    static var greyBorder: UIColor {
        UIColor(red: 221 / 255, green: 221 / 255, blue: 221 / 255, alpha: 1.0)
    }

    static var paleTeal: UIColor {
        UIColor(red: 128 / 255, green: 191 / 255, blue: 189 / 255, alpha: 1.0)
    }

    static var turquoise: UIColor {
        UIColor(red: 78 / 255, green: 158 / 255, blue: 186 / 255, alpha: 1.0)
    }

    static var lightestGray: UIColor {
        UIColor(red: 240 / 255, green: 242 / 255, blue: 242 / 255, alpha: 1.0)
    }

    static var hubGreen: UIColor {
        UIColor(red: 190 / 255, green: 214 / 255, blue: 0, alpha: 1.0)
    }

    static var hubGrey: UIColor {
        UIColor(red: 21 / 255, green: 21 / 255, blue: 21 / 255, alpha: 1.0)
    }

    static var hubRoundelGrey: UIColor {
        UIColor(red: 56 / 255, green: 56 / 255, blue: 56 / 255, alpha: 1.0)
    }

    static var zipRed: UIColor {
        UIColor(red: 252 / 255, green: 15 / 255, blue: 66 / 255, alpha: 1.0)
    }

    static var tripAdvisorShamrockGreen: UIColor {
        UIColor(red: 0 / 255, green: 176 / 255, blue: 135 / 255, alpha: 1.0)
    }

    static var greyishWhite: UIColor {
        UIColor(red: 248 / 255, green: 248 / 255, blue: 248 / 255, alpha: 1.0)
    }

    static var veryLightPink85: UIColor {
        UIColor(red: 230 / 255, green: 230 / 255, blue: 230 / 255, alpha: 0.85)
    }

    static var darkerishGrey69: UIColor {
        UIColor(red: 58 / 255, green: 58 / 255, blue: 58 / 255, alpha: 0.85)
    }

    static var darkishGrey69: UIColor {
        UIColor(red: 66 / 255, green: 66 / 255, blue: 66 / 255, alpha: 0.85)
    }

    static var lightPeach50: UIColor {
        UIColor(red: 220 / 255, green: 216 / 255, blue: 216 / 255, alpha: 0.5)
    }

    static var steelGrey: UIColor {
        UIColor(red: 123 / 255, green: 128 / 255, blue: 128 / 255, alpha: 1.0)
    }

    static var cerise: UIColor {
        UIColor(red: 222 / 255, green: 8 / 255, blue: 74 / 255, alpha: 1.0)
    }

    static var alertOrangeTint: UIColor {
        UIColor(red: 254 / 255, green: 239 / 255, blue: 217 / 255, alpha: 1.0)
    }

    static var alertOrange: UIColor {
        UIColor(red: 215 / 255, green: 61 / 255, blue: 0 / 255, alpha: 0.4)
    }

    static var marketingMaroon: UIColor {
        UIColor(red: 125 / 255, green: 43 / 255, blue: 94 / 255, alpha: 1.0)
    }

    static var redBanner: UIColor {
        UIColor(red: 217 / 255, green: 9 / 255, blue: 65 / 255, alpha: 1.0)
    }

    static var greenBanner: UIColor {
        UIColor(red: 28 / 255, green: 135 / 255, blue: 84 / 255, alpha: 1.0)
    }

    static var blueBanner: UIColor {
        UIColor(red: 0 / 255, green: 127 / 255, blue: 171 / 255, alpha: 1.0)
    }

    static var pinkishRed: UIColor {
        UIColor(red: 235 / 255, green: 25 / 255, blue: 60 / 255, alpha: 1.0)
    }

    static var gunMetal: UIColor {
        UIColor(red: 97 / 255, green: 117 / 255, blue: 113 / 255, alpha: 1.0)
    }

    static var deepTeal: UIColor {
        UIColor(hex: "#00798E")
    }
}

/// PI Static Colours
///  Only used for error handling, so they are not present in the `Colors.xcassets` asset.
///
/// Zeplin: https://zpl.io/aMePJvN
private extension UIColor {
    // MARK: - Brand Marketing Colours

    /// Brand Colour 1 Yellow [#F0D065]
    static var brandColour1Yellow: UIColor { UIColor(hex: "#F0D065") }

    /// Brand Colour 2 Blue [#0178A5]
    static var brandColour2Blue: UIColor { UIColor(hex: "#0178A5") }

    /// Brand Colour 3 Dusty Pink [#A9758F]
    static var brandColour3DustyPink: UIColor { UIColor(hex: "#A9758F") }

    /// Brand Colour 4 Beige [#D4CAC6]
    static var brandColour4Beige: UIColor { UIColor(hex: "#D4CAC6") }

    /// Brand Colour 5 Dark Pink [#AC467A]
    static var brandColour5DarkPink: UIColor { UIColor(hex: "#AC467A") }

    /// Brand Colour 6 Maroon [#73325B]
    static var brandColour6Maroon: UIColor { UIColor(hex: "#73325B") }

    /// Brand Colour 7 Dark Maroon [#4D2841]
    static var brandColour7DarkMaroon: UIColor { UIColor(hex: "#4D2841") }

    /// Brand Colour 8 Mint [#BAD6DA]
    static var brandColour8Mint: UIColor { UIColor(hex: "#BAD6DA") }

    /// Brand Colour 9 Pink [#C17B9E]
    static var brandColour9Pink: UIColor { UIColor(hex: "#C17B9E") }

    /// Brand Colour 10 Aqua [#89BED1]
    static var brandColour10Aqua: UIColor { UIColor(hex: "#89BED1") }

    /// Brand Colour 11 Grey [#D7D8D6]
    static var brandColour11Grey: UIColor { UIColor(hex: "#D7D8D6") }

    /// Brand Colour 12 Dark Purple [#351142]
    static var brandColour12DarkPurple: UIColor { UIColor(hex: "#351142") }

    /// Brand Colour 13 Dark Mint [#B0CACE]
    static var brandColour13DarkMint: UIColor { UIColor(hex: "#B0CACE") }

    /// Brand Colour 14 Light Purple [#6C3072]
    static var brandColour14LightPurple: UIColor { UIColor(hex: "#6C3072") }

    // MARK: - Primary Colours

    /// Teal [#205D70]
    static var teal: UIColor { UIColor(hex: "#205D70") }

    /// Purple [#511E62]
    static var purple: UIColor { UIColor(hex: "#511E62") }

    /// DarkPurple [#471A56]
    static var darkPurple: UIColor { UIColor(hex: "#471A56") }

    // MARK: - Greys Colours

    /// Base Black [#000000]
    static var baseBlack: UIColor { UIColor(hex: "#000000") }

    /// Base Grey [#F2F2F2]
    static var baseGrey: UIColor { UIColor(hex: "#f2f2f2") }

    /// Dark Grey 1 [#333333]
    static var darkGrey1: UIColor { UIColor(hex: "#333333") }

    /// Dark Grey 2 [#58595B]
    static var darkGrey2: UIColor { UIColor(hex: "#58595B") }

    /// Light Grey 1 [#979797]
    static var lightGrey1: UIColor { UIColor(hex: "#979797") }

    /// Light Grey 2 [#CCCCCC]
    static var lightGrey2: UIColor { UIColor(hex: "#CCCCCC") }

    /// Light Grey 3 [#DDDDDD]
    static var lightGrey3: UIColor { UIColor(hex: "#DDDDDD") }

    /// Light Grey 4 [#E0E0E0]
    static var lightGrey4: UIColor { UIColor(hex: "#E0E0E0") }

    /// Light Grey 5 [#F8F8F8]
    static var lightGrey5: UIColor { UIColor(hex: "#F8F8F8") }

    /// Base White [#FFFFFF]
    static var baseWhite: UIColor { UIColor(hex: "#FFFFFF") }

    // MARK: - Notifications Colours

    /// Alert [#D73D00]
    static var alert: UIColor { UIColor(hex: "#D73D00") }

    /// Error [#D90941]
    static var error: UIColor { UIColor(hex: "#D90941") }

    /// Info [#007FAB]
    static var info: UIColor { UIColor(hex: "#007FAB") }

    /// Success [#1C8754]
    static var success: UIColor { UIColor(hex: "#1C8754") }

    /// Accessible [#007FAB]
    static var accessible: UIColor { UIColor(hex: "#007FAB") }

    /// Tooltip Alert [#FEEFD9]
    static var tooltipAlert: UIColor { UIColor(hex: "#FEEFD9") }

    /// Tooltip Error [#FBE6EC]
    static var tooltipError: UIColor { UIColor(hex: "#FBE6EC") }

    /// Light Blue Bathroom [#E5F1F5]
    static var lightBlue: UIColor { UIColor(hex: "#E5F1F5") }

    /// Tooltip Info [#E5F2F6]
    static var tooltipInfo: UIColor { UIColor(hex: "#E5F2F6") }

    /// Tooltip Success [#E8F3ED]
    static var tooltipSuccess: UIColor { UIColor(hex: "#E8F3ED") }

    /// Promotion Yellow [#FDB913]
    static var yellowPromotion: UIColor { UIColor(hex: "#FDB913") }

    // MARK: - Sub Brands Colours

    /// Hub Primary [#BDD500]
    static var hubPrimary: UIColor { UIColor(hex: "#BDD500") }

    /// Hub Secondary [#7A3E98]
    static var hubSecondary: UIColor { UIColor(hex: "#7A3E98") }

    /// Zip Primary [#FC0F42]
    static var zipPrimary: UIColor { UIColor(hex: "#FC0F42") }

    /// Zip Secondary [#1900F1]
    static var zipSecondary: UIColor { UIColor(hex: "#1900F1") }
}

/// PI App Colour Theme
///
/// Zeplin: https://zpl.io/bPey6jL
extension UIColor {
    // MARK: - Bases

    /// BasePurple
    static var BasePurple: UIColor { UIColor(named: "BasePurple") ?? UIColor.purple }

    /// BaseWhite
    static var BaseWhite: UIColor { UIColor(named: "BaseWhite") ?? UIColor.baseWhite }

    /// BaseBlack
    static var BaseBlack: UIColor { UIColor(named: "BaseBlack") ?? UIColor.baseBlack }

    /// BaseGrey
    static var BaseGrey: UIColor { UIColor(named: "BaseGrey") ?? UIColor.baseGrey }

    /// DarkPurple
    static var DarkPurple: UIColor { UIColor(named: "DarkPurple") ?? UIColor.darkPurple }

    // MARK: - Tints

    /// Tint1
    static var Tint1: UIColor { UIColor(named: "Tint1") ?? UIColor.teal }

    /// Tint2
    static var Tint2: UIColor { UIColor(named: "Tint2") ?? UIColor.info }

    /// Tint3
    static var Tint3: UIColor { UIColor(named: "Tint3") ?? UIColor.tooltipInfo }

    /// Tint4
    static var Tint4: UIColor { UIColor(named: "Tint4") ?? UIColor.success }

    /// Tint5
    static var Tint5: UIColor { UIColor(named: "Tint5") ?? UIColor.tooltipSuccess }

    /// Tint6
    static var Tint6: UIColor { UIColor(named: "Tint6") ?? UIColor.alert }

    /// Tint7
    static var Tint7: UIColor { UIColor(named: "Tint7") ?? UIColor.tooltipAlert }

    /// Tint8
    static var Tint8: UIColor { UIColor(named: "Tint8") ?? UIColor.error }

    /// Tint9
    static var Tint9: UIColor { UIColor(named: "Tint9") ?? UIColor.tooltipError }

    /// Tint10
    static var Tint10: UIColor { UIColor(named: "Tint10") ?? UIColor.lightBlue }

    /// Tint11
    static var Tint11: UIColor { UIColor(named: "Tint11") ?? UIColor.yellowPromotion }

    /// TintD1
    static var TintD1: UIColor { UIColor(named: "TintD1") ?? UIColor.darkGrey1 }

    /// TintD2
    static var TintD2: UIColor { UIColor(named: "TintD2") ?? UIColor.darkGrey2 }

    /// TintL1
    static var TintL1: UIColor { UIColor(named: "TintL1") ?? UIColor.lightGrey1 }

    /// TintL2
    static var TintL2: UIColor { UIColor(named: "TintL2") ?? UIColor.lightGrey2 }

    /// TintL3
    static var TintL3: UIColor { UIColor(named: "TintL3") ?? UIColor.lightGrey3 }

    /// TintL4
    static var TintL4: UIColor { UIColor(named: "TintL4") ?? UIColor.lightGrey4 }

    /// TintL5
    static var TintL5: UIColor { UIColor(named: "TintL5") ?? UIColor.lightGrey5 }

    // MARK: - Colours

    // DL

    /// ColourDL1
    static var ColourDL1: UIColor { UIColor(named: "ColourDL1") ?? UIColor.darkGrey1 }

    /// ColourDL2
    static var ColourDL2: UIColor { UIColor(named: "ColourDL2") ?? UIColor.darkGrey2 }

    /// ColourDL3
    static var ColourDL3: UIColor { UIColor(named: "ColourDL3") ?? UIColor.darkGrey2 }

    /// ColourDL4
    static var ColourDL4: UIColor { UIColor(named: "ColourDL4") ?? UIColor.purple }

    /// ColourDL5
    static var ColourDL5: UIColor { UIColor(named: "ColourDL5") ?? UIColor.purple }

    /// ColourDL6
    static var ColourDL6: UIColor { UIColor(named: "ColourDL6") ?? UIColor.purple }

    /// ColourDL7
    static var ColourDL7: UIColor { UIColor(named: "ColourDL7") ?? UIColor.purple }

    /// ColourDL8
    static var ColourDL8: UIColor { UIColor(named: "ColourDL8") ?? UIColor.darkGrey1 }

    /// ColourDL9
    static var ColourDL9: UIColor { UIColor(named: "ColourDL9") ?? UIColor.darkGrey1 }

    // LD

    /// ColourLD1
    static var ColourLD1: UIColor { UIColor(named: "ColourLD1") ?? UIColor.BaseWhite }

    /// ColourLD2
    static var ColourLD2: UIColor { UIColor(named: "ColourLD2") ?? UIColor.lightGrey5 }

    /// ColourLD3
    static var ColourLD3: UIColor { UIColor(named: "ColourLD3") ?? UIColor.lightGrey2 }

    /// ColourLD4
    static var ColourLD4: UIColor { UIColor(named: "ColourLD4") ?? UIColor.purple }

    /// ColourLD5
    static var ColourLD5: UIColor { UIColor(named: "ColourLD5") ?? UIColor.baseWhite }

    /// ColourLD6
    static var ColourLD6: UIColor { UIColor(named: "ColourLD6") ?? UIColor.lightGrey5 }

    /// ColourLD7
    static var ColourLD7: UIColor { UIColor(named: "ColourLD7") ?? UIColor.lightGrey1 }

    /// ColourLD8
    static var ColourLD8: UIColor { UIColor(named: "ColourLD8") ?? UIColor.lightGrey5 }

    // MARK: - Brands

    /// HubPrimary
    static var HubPrimary: UIColor { UIColor(named: "HubPrimary") ?? UIColor.hubPrimary }

    /// HubSecondary
    static var HubSecondary: UIColor { UIColor(named: "HubSecondary") ?? UIColor.hubSecondary }

    /// ZipPrimary
    static var ZipPrimary: UIColor { UIColor(named: "ZipPrimary") ?? UIColor.zipPrimary }

    /// ZipSecondary
    static var ZipSecondary: UIColor { UIColor(named: "ZipSecondary") ?? UIColor.zipSecondary }

    // MARK: - PayPal

    /// PayPal
    static var paypalGold: UIColor { UIColor(hex: "FFC439") }

    /// Errors
    static var errorBackground: UIColor { UIColor(named: "ErrorBackground") ?? UIColor.error }
}
