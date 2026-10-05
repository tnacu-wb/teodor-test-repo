//
//  Style.swift
//  PremierInn
//
//  Created by Freddie Parks on 11/06/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

enum Style {
    enum HotelDetails {
        static let backButtonImage: UIImage = {
            switch UIDevice.current.userInterfaceIdiom {
            case .pad:
                return #imageLiteral(resourceName: "cross.pdf")
            default:
                return #imageLiteral(resourceName: "back.pdf")
            }
        }()
    }
}

typealias ButtonStyling = (font: UIFont, titleColour: UIColor, buttonColour: UIColor, icon: UIImage?)

enum CTAButtonStyle {
    case purple
    case butterscotch
    case teal(icon: UIImage?)
    case paypal

    var buttonStyling: ButtonStyling {
        switch self {
        case .purple:
            return (UIFont.Button1(), .BaseWhite, .BasePurple, nil)
        case .butterscotch:
            return (UIFont.Button1(), .BasePurple, .yellow, nil)
        case .teal(let icon):
            return (.Button1(), .ColourLD1, .Tint1, icon)
        case .paypal:
            return (.paypalButton(), .BaseBlack, .paypalGold, nil)
        }
    }
}

enum NotificationStyle: String {
    case alert
    case info
    case infoAlt
    case error
    case success

    var icon: UIImage {
        switch self {
        case .alert:
            return #imageLiteral(resourceName: "alert")
        case .info, .error:
            return #imageLiteral(resourceName: "UNKNOWN")
        case .infoAlt:
            return #imageLiteral(resourceName: "infoIconAlt")
        case .success:
            return #imageLiteral(resourceName: "notificationSuccess")
        }
    }

    var tint: UIColor {
        switch self {
        case .alert:
            return UIColor.Tint6
        case .info:
            return UIColor.Tint2
        case .infoAlt:
            return UIColor.TintL3
        case .error:
            return UIColor.Tint8
        case .success:
            return UIColor.Tint4
        }
    }

    var background: UIColor {
        switch self {
        case .alert:
            return UIColor.Tint7
        case .info:
            return UIColor.Tint3
        case .infoAlt:
            return UIColor.clear
        case .error:
            return UIColor.Tint9
        case .success:
            return UIColor.Tint5
        }
    }
}

enum AlertStyle {
    case info
    case success

    var icon: UIImage {
        switch self {
        case .success:
            return #imageLiteral(resourceName: "notificationSuccess")
        case .info:
            return #imageLiteral(resourceName: "UNKNOWN")
        }
    }

    var tint: UIColor {
        switch self {
        case .success:
            return UIColor.BaseWhite
        case .info:
            return UIColor.BaseWhite
        }
    }

    var background: UIColor {
        switch self {
        case .success:
            return UIColor.Tint4
        case .info:
            return UIColor.Tint2
        }
    }
}
