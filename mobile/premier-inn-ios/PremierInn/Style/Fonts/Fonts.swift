//
//  Fonts.swift
//  PremierInn
//
//  Created by Filippo Minelle on 27/07/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import UIKit

// MARK: - File Names

public extension UIFont {
    // Proxima Nova Sans

    static var name_ProximaNovaSans_Bold = "ProximaNova-Bold"
    static var name_ProximaNovaSans_Bold_Italic = "ProximaNova-BoldIt"
    static var name_ProximaNovaSans_ExtraBold = "ProximaNova-Extrabld"
    static var name_ProximaNovaSans_ExtraBold_Italic = "ProximaNova-ExtrabldIt"
    static var name_ProximaNovaSans_Light = "ProximaNova-Light"
    static var name_ProximaNovaSans_LightItalic = "ProximaNova-LightIt"
    static var name_ProximaNovaSans_Medium = "ProximaNova-Medium"
    static var name_ProximaNovaSans_Regular = "ProximaNova-Regular"
    static var name_ProximaNovaSans_Italic = "ProximaNova-RegularIt"
    static var name_ProximaNovaSans_SemiBold = "ProximaNova-Semibold"
    static var name_ProximaNovaSans_SemiBold_Italic = "ProximaNova-SemiboldIt"
    static var name_ProximaNovaSans_Thin = "ProximaNova-Thin"
    static var name_ProximaNovaSans_Thin_Italic = "ProximaNova-ThinIt"
}

// MARK: - Family

private extension UIFont {
    // Proxima Nova Sans

    class func proximaNovaSans_Bold(_ size: CGFloat = 12.0) -> UIFont {
        UIFont(name: UIFont.name_ProximaNovaSans_Bold, size: size) ?? UIFont.systemFont(ofSize: size)
    }

    class func proximaNovaSans_Bold_Italic(_ size: CGFloat = 12.0) -> UIFont {
        UIFont(name: UIFont.name_ProximaNovaSans_Bold_Italic, size: size) ?? UIFont.systemFont(ofSize: size)
    }

    class func proximaNovaSans_ExtraBold(_ size: CGFloat = 12.0) -> UIFont {
        UIFont(name: UIFont.name_ProximaNovaSans_ExtraBold, size: size) ?? UIFont.systemFont(ofSize: size)
    }

    class func proximaNovaSans_ExtraBold_Italic(_ size: CGFloat = 12.0) -> UIFont {
        UIFont(name: UIFont.name_ProximaNovaSans_ExtraBold_Italic, size: size) ?? UIFont.systemFont(ofSize: size)
    }

    class func proximaNovaSans_Light(_ size: CGFloat = 12.0) -> UIFont {
        UIFont(name: UIFont.name_ProximaNovaSans_Light, size: size) ?? UIFont.systemFont(ofSize: size)
    }

    class func proximaNovaSans_LightItalic(_ size: CGFloat = 12.0) -> UIFont {
        UIFont(name: UIFont.name_ProximaNovaSans_LightItalic, size: size) ?? UIFont.systemFont(ofSize: size)
    }

    class func proximaNovaSans_Medium(_ size: CGFloat = 12.0) -> UIFont {
        UIFont(name: UIFont.name_ProximaNovaSans_Medium, size: size) ?? UIFont.systemFont(ofSize: size)
    }

    class func proximaNovaSans_Regular(_ size: CGFloat = 12.0) -> UIFont {
        UIFont(name: UIFont.name_ProximaNovaSans_Regular, size: size) ?? UIFont.systemFont(ofSize: size)
    }

    class func proximaNovaSans_Italic(_ size: CGFloat = 12.0) -> UIFont {
        UIFont(name: UIFont.name_ProximaNovaSans_Italic, size: size) ?? UIFont.systemFont(ofSize: size)
    }

    class func proximaNovaSans_SemiBold(_ size: CGFloat = 12.0) -> UIFont {
        UIFont(name: UIFont.name_ProximaNovaSans_SemiBold, size: size) ?? UIFont.systemFont(ofSize: size)
    }

    class func proximaNovaSans_SemiBold_Italic(_ size: CGFloat = 12.0) -> UIFont {
        UIFont(name: UIFont.name_ProximaNovaSans_SemiBold_Italic, size: size) ?? UIFont.systemFont(ofSize: size)
    }

    class func proximaNovaSans_Thin(_ size: CGFloat = 12.0) -> UIFont {
        UIFont(name: UIFont.name_ProximaNovaSans_Thin, size: size) ?? UIFont.systemFont(ofSize: size)
    }

    class func proximaNovaSans_Thin_Italic(_ size: CGFloat = 12.0) -> UIFont {
        UIFont(name: UIFont.name_ProximaNovaSans_Thin_Italic, size: size) ?? UIFont.systemFont(ofSize: size)
    }
}

// MARK: - Style: Headings

public extension UIFont {
    var isBold: Bool { fontDescriptor.symbolicTraits.contains(.traitBold) }

    var isItalic: Bool { fontDescriptor.symbolicTraits.contains(.traitItalic) }

    /// Title1 ExtraBold
    /// - Returns: Proxima Nova Sans ExtraBold 35.0
    class func Title1_ExtraBold() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_ExtraBold(35)
        case .phone:
            return UIFont.proximaNovaSans_ExtraBold(35)
        default:
            return UIFont.proximaNovaSans_ExtraBold(35)
        }
    }

    // H1
    /// Heading1 ExtraBold
    /// - Returns: Proxima Nova Sans ExtraBold, Default: 26.0
    class func Heading1_ExtraBold(_ size: CGFloat = 26.0) -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_ExtraBold(size)
        case .phone:
            return UIFont.proximaNovaSans_ExtraBold(size)
        default:
            return UIFont.proximaNovaSans_ExtraBold(size)
        }
    }

    /// Heading1 ExtraBold
    /// - Returns: Proxima Nova Sans ExtraBold 26.0
    class func Heading1_ExtraBold() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_ExtraBold(26.0)
        case .phone:
            return UIFont.proximaNovaSans_ExtraBold(26.0)
        default:
            return UIFont.proximaNovaSans_ExtraBold(26.0)
        }
    }

    /// Heading1 Regular
    /// - Returns: Proxima Nova Sans Regular 23.0
    class func Heading1_Regular() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Regular(23.0)
        case .phone:
            return UIFont.proximaNovaSans_Regular(23.0)
        default:
            return UIFont.proximaNovaSans_Regular(23.0)
        }
    }

    /// Heading1 Medium
    /// - Returns: Proxima Nova Sans Medium 23.0
    class func Heading1_Medium() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Medium(23.0)
        case .phone:
            return UIFont.proximaNovaSans_Medium(23.0)
        default:
            return UIFont.proximaNovaSans_Medium(23.0)
        }
    }

    /// Heading1 Semibold
    /// - Returns: Proxima Nova Sans Semibold 23.0
    class func Heading1_Semibold() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_SemiBold(23.0)
        case .phone:
            return UIFont.proximaNovaSans_SemiBold(23.0)
        default:
            return UIFont.proximaNovaSans_SemiBold(23.0)
        }
    }

    /// Heading1 Bold
    /// - Returns: Proxima Nova Sans Bold 23.0
    class func Heading1_Bold() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Bold(23.0)
        case .phone:
            return UIFont.proximaNovaSans_Bold(23.0)
        default:
            return UIFont.proximaNovaSans_Bold(23.0)
        }
    }

    /// Heading1 Bold Custom
    /// - Returns: Proxima Nova Sans Bold
    class func Heading1_Bold_custom(_ size: CGFloat) -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_ExtraBold(size)
        case .phone:
            return UIFont.proximaNovaSans_ExtraBold(size)
        default:
            return UIFont.proximaNovaSans_ExtraBold(size)
        }
    }

    // H2

    /// Heading2 Regular
    /// - Returns: Proxima Nova Sans Regular 20.0
    class func Heading2_Regular() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Regular(20.0)
        case .phone:
            return UIFont.proximaNovaSans_Regular(20.0)
        default:
            return UIFont.proximaNovaSans_Regular(20.0)
        }
    }

    /// Heading2 Medium
    /// - Returns: Proxima Nova Sans Medium 20.0
    class func Heading2_Medium() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Medium(20.0)
        case .phone:
            return UIFont.proximaNovaSans_Medium(20.0)
        default:
            return UIFont.proximaNovaSans_Medium(20.0)
        }
    }

    /// Heading2 Semibold
    /// - Returns: Proxima Nova Sans Semibold 20.0
    class func Heading2_Semibold() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_SemiBold(20.0)
        case .phone:
            return UIFont.proximaNovaSans_SemiBold(20.0)
        default:
            return UIFont.proximaNovaSans_SemiBold(20.0)
        }
    }

    /// Heading2 Bold
    /// - Returns: Proxima Nova Sans Bold 20.0
    class func Heading2_Bold() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Bold(20.0)
        case .phone:
            return UIFont.proximaNovaSans_Bold(20.0)
        default:
            return UIFont.proximaNovaSans_Bold(20.0)
        }
    }

    /// Heading2 ExtraBold
    /// - Returns: Proxima Nova Sans ExtraBold 20.0
    class func Heading2_ExtraBold() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_ExtraBold(20.0)
        case .phone:
            return UIFont.proximaNovaSans_ExtraBold(20.0)
        default:
            return UIFont.proximaNovaSans_ExtraBold(20.0)
        }
    }

    // H3

    /// Heading3 Regular
    /// - Returns: Proxima Nova Sans Regular 18.0
    class func Heading3_Regular() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Regular(18.0)
        case .phone:
            return UIFont.proximaNovaSans_Regular(18.0)
        default:
            return UIFont.proximaNovaSans_Regular(18.0)
        }
    }

    /// Heading3 Medium
    /// - Returns: Proxima Nova Sans Medium 18.0
    class func Heading3_Medium() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Medium(18.0)
        case .phone:
            return UIFont.proximaNovaSans_Medium(18.0)
        default:
            return UIFont.proximaNovaSans_Medium(18.0)
        }
    }

    /// Heading3 Semibold
    /// - Returns: Proxima Nova Sans Semibold 18.0
    class func Heading3_Semibold() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_SemiBold(18.0)
        case .phone:
            return UIFont.proximaNovaSans_SemiBold(18.0)
        default:
            return UIFont.proximaNovaSans_SemiBold(18.0)
        }
    }

    /// Heading3 Bold
    /// - Returns: Proxima Nova Sans Bold 18.0
    class func Heading3_Bold() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Bold(18.0)
        case .phone:
            return UIFont.proximaNovaSans_Bold(18.0)
        default:
            return UIFont.proximaNovaSans_Bold(18.0)
        }
    }

    // H4

    /// Heading4 Regular
    /// - Returns: Proxima Nova Sans Regular 16.0
    class func Heading4_Regular() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Regular(16.0)
        case .phone:
            return UIFont.proximaNovaSans_Regular(16.0)
        default:
            return UIFont.proximaNovaSans_Regular(16.0)
        }
    }

    /// Heading4 Medium
    /// - Returns: Proxima Nova Sans Medium 16.0
    class func Heading4_Medium() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Medium(16.0)
        case .phone:
            return UIFont.proximaNovaSans_Medium(16.0)
        default:
            return UIFont.proximaNovaSans_Medium(16.0)
        }
    }

    /// Heading4 Semibold
    /// - Returns: Proxima Nova Sans Semibold 16.0
    class func Heading4_Semibold() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_SemiBold(16.0)
        case .phone:
            return UIFont.proximaNovaSans_SemiBold(16.0)
        default:
            return UIFont.proximaNovaSans_SemiBold(16.0)
        }
    }

    /// Heading4 Bold
    /// - Returns: Proxima Nova Sans Bold 16.0
    class func Heading4_Bold() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Bold(16.0)
        case .phone:
            return UIFont.proximaNovaSans_Bold(16.0)
        default:
            return UIFont.proximaNovaSans_Bold(16.0)
        }
    }
}

// MARK: - Style: Body

public extension UIFont {
    // Body

    /// Body Regular
    /// - Returns: Proxima Nova Sans Regular 16.0
    class func Body() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Regular(16.0)
        case .phone:
            return UIFont.proximaNovaSans_Regular(16.0)
        default:
            return UIFont.proximaNovaSans_Regular(16.0)
        }
    }

    /// Body Medium
    /// - Returns: Proxima Nova Sans Medium 16.0
    class func Body_Medium() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Medium(16.0)
        case .phone:
            return UIFont.proximaNovaSans_Medium(16.0)
        default:
            return UIFont.proximaNovaSans_Medium(16.0)
        }
    }

    /// Body Semibold
    /// - Returns: Proxima Nova Sans Semibold 16.0
    class func Body_Semibold() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_SemiBold(16.0)
        case .phone:
            return UIFont.proximaNovaSans_SemiBold(16.0)
        default:
            return UIFont.proximaNovaSans_SemiBold(16.0)
        }
    }

    /// Body Bold
    /// - Returns: Proxima Nova Sans Bold 16.0
    class func Body_Bold() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Bold(16.0)
        case .phone:
            return UIFont.proximaNovaSans_Bold(16.0)
        default:
            return UIFont.proximaNovaSans_Bold(16.0)
        }
    }

    // Body Small

    /// BodySmall Regular
    /// - Returns: Proxima Nova Sans Regular 14.0
    class func BodySmall() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Regular(14.0)
        case .phone:
            return UIFont.proximaNovaSans_Regular(14.0)
        default:
            return UIFont.proximaNovaSans_Regular(14.0)
        }
    }

    /// BodySmall Medium
    /// - Returns: Proxima Nova Sans Medium 14.0
    class func BodySmall_Medium() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Medium(14.0)
        case .phone:
            return UIFont.proximaNovaSans_Medium(14.0)
        default:
            return UIFont.proximaNovaSans_Medium(14.0)
        }
    }

    /// BodySmall Semibold
    /// - Returns: Proxima Nova Sans Semibold 14.0
    class func BodySmall_Semibold() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_SemiBold(14.0)
        case .phone:
            return UIFont.proximaNovaSans_SemiBold(14.0)
        default:
            return UIFont.proximaNovaSans_SemiBold(14.0)
        }
    }

    /// BodySmall Bold
    /// - Returns: Proxima Nova Sans Bold 14.0
    class func BodySmall_Bold() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Bold(14.0)
        case .phone:
            return UIFont.proximaNovaSans_Bold(14.0)
        default:
            return UIFont.proximaNovaSans_Bold(14.0)
        }
    }

    // Subtext

    /// Subtext Regular
    /// - Returns: Proxima Nova Sans Regular 13.0
    class func Subtext() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Regular(13.0)
        case .phone:
            return UIFont.proximaNovaSans_Regular(13.0)
        default:
            return UIFont.proximaNovaSans_Regular(13.0)
        }
    }

    /// Subtext Medium
    /// - Returns: Proxima Nova Sans Medium 13.0
    class func Subtext_Medium() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Medium(13.0)
        case .phone:
            return UIFont.proximaNovaSans_Medium(13.0)
        default:
            return UIFont.proximaNovaSans_Medium(13.0)
        }
    }

    /// Subtext Semibold
    /// - Returns: Proxima Nova Sans Semibold 13.0
    class func SubtextStrong() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_SemiBold(13.0)
        case .phone:
            return UIFont.proximaNovaSans_SemiBold(13.0)
        default:
            return UIFont.proximaNovaSans_SemiBold(13.0)
        }
    }

    /// Subtext Bold
    /// - Returns: Proxima Nova Sans Bold 13.0
    class func Subtext_Bold() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Bold(13.0)
        case .phone:
            return UIFont.proximaNovaSans_Bold(13.0)
        default:
            return UIFont.proximaNovaSans_Bold(13.0)
        }
    }

    /// Subtext Italic
    /// - Returns: Proxima Nova Sans Italic 13.0
    class func Subtext_Italic() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Italic(13.0)
        case .phone:
            return UIFont.proximaNovaSans_Italic(13.0)
        default:
            return UIFont.proximaNovaSans_Italic(13.0)
        }
    }

    // Subtext Small

    /// SubtextSmall Regular
    /// - Returns: Proxima Nova Sans Regular 12.0
    class func SubtextSmall() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Regular(12.0)
        case .phone:
            return UIFont.proximaNovaSans_Regular(12.0)
        default:
            return UIFont.proximaNovaSans_Regular(12.0)
        }
    }

    /// SubtextSmall Medium
    /// - Returns: Proxima Nova Sans Medium 12.0
    class func SubtextSmall_Medium() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Medium(12.0)
        case .phone:
            return UIFont.proximaNovaSans_Medium(12.0)
        default:
            return UIFont.proximaNovaSans_Medium(12.0)
        }
    }

    /// SubtextSmall Semibold
    /// - Returns: Proxima Nova Sans Semibold 12.0
    class func SubtextSmallStrong() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_SemiBold(12.0)
        case .phone:
            return UIFont.proximaNovaSans_SemiBold(12.0)
        default:
            return UIFont.proximaNovaSans_SemiBold(12.0)
        }
    }

    /// SubtextSmall BoldBold
    /// - Returns: Proxima Nova Sans Bold 12.0
    class func SubtextSmall_Bold() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Bold(12.0)
        case .phone:
            return UIFont.proximaNovaSans_Bold(12.0)
        default:
            return UIFont.proximaNovaSans_Bold(12.0)
        }
    }
}

// MARK: - Style: Marketing

public extension UIFont {
    // Link

    /// Link
    /// - Returns: Proxima Nova Sans Regular 16.0
    class func Link() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Regular(14.0)
        case .phone:
            return UIFont.proximaNovaSans_Regular(14.0)
        default:
            return UIFont.proximaNovaSans_Regular(14.0)
        }
    }

    // Title

    /// XXLargeTitle
    /// - Returns: Proxima Nova Sans Semibold 50.0
    class func XXLargeTitle() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_SemiBold(50.0)
        case .phone:
            return UIFont.proximaNovaSans_SemiBold(50.0)
        default:
            return UIFont.proximaNovaSans_SemiBold(50.0)
        }
    }

    /// LargeTitle
    /// - Returns: Proxima Nova Sans Semibold 26.0
    class func LargeTitle() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_SemiBold(26.0)
        case .phone:
            return UIFont.proximaNovaSans_SemiBold(26.0)
        default:
            return UIFont.proximaNovaSans_SemiBold(26.0)
        }
    }
}

// MARK: - Style: App Native

public extension UIFont {
    // Buttons

    /// Button1
    /// - Returns: Proxima Nova Sans Semibold 18.0
    class func Button1() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_SemiBold(18.0)
        case .phone:
            return UIFont.proximaNovaSans_SemiBold(18.0)
        default:
            return UIFont.proximaNovaSans_SemiBold(18.0)
        }
    }

    /// Button2
    /// - Returns: Proxima Nova Sans Semibold 17.0
    class func Button2() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_SemiBold(17.0)
        case .phone:
            return UIFont.proximaNovaSans_SemiBold(17.0)
        default:
            return UIFont.proximaNovaSans_SemiBold(17.0)
        }
    }

    // Actions

    /// Action1
    /// - Returns: Proxima Nova Sans 16.0
    class func Action1() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Regular(16.0)
        case .phone:
            return UIFont.proximaNovaSans_Regular(16.0)
        default:
            return UIFont.proximaNovaSans_Regular(16.0)
        }
    }

    /// Action2
    /// - Returns: Proxima Nova Sans 14.0
    class func Action2() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Regular(14.0)
        case .phone:
            return UIFont.proximaNovaSans_Regular(14.0)
        default:
            return UIFont.proximaNovaSans_Regular(14.0)
        }
    }

    /// PayPal Button
    /// - Returns: Proxima Nova Sans Bold 13.0
    class func paypalButton() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_Bold(13.0)
        case .phone:
            return UIFont.proximaNovaSans_Bold(13.0)
        default:
            return UIFont.proximaNovaSans_Bold(13.0)
        }
    }

    /// Action2
    /// - Returns: Proxima Nova Sans 14.0 SemiBold
    class func Action3() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_SemiBold(14.0)
        case .phone:
            return UIFont.proximaNovaSans_SemiBold(14.0)
        default:
            return UIFont.proximaNovaSans_SemiBold(14.0)
        }
    }
}

// MARK: - Style: iOS Specific

public extension UIFont {
    // Nav Bar

    /// NavTitle1
    /// - Returns: Proxima Nova Sans Semibold 16.0
    class func NavTitle1() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_SemiBold(16.0)
        case .phone:
            return UIFont.proximaNovaSans_SemiBold(16.0)
        default:
            return UIFont.proximaNovaSans_SemiBold(16.0)
        }
    }

    /// NavActive
    /// - Returns: Proxima Nova Sans Semibold 12.0
    class func NavActive() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_SemiBold(12.0)
        case .phone:
            return UIFont.proximaNovaSans_SemiBold(12.0)
        default:
            return UIFont.proximaNovaSans_SemiBold(12.0)
        }
    }

    /// NavInactive
    /// - Returns: Proxima Nova Sans Semibold 12.0
    class func NavInactive() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_SemiBold(12.0)
        case .phone:
            return UIFont.proximaNovaSans_SemiBold(12.0)
        default:
            return UIFont.proximaNovaSans_SemiBold(12.0)
        }
    }

    // Custom

    /// MapPin
    /// - Returns: Proxima Nova Sans Semibold 10.0
    class func MapPin() -> UIFont {
        switch UIScreen.main.traitCollection.userInterfaceIdiom {
        case .pad:
            return UIFont.proximaNovaSans_SemiBold(10.0)
        case .phone:
            return UIFont.proximaNovaSans_SemiBold(10.0)
        default:
            return UIFont.proximaNovaSans_SemiBold(10.0)
        }
    }
}
