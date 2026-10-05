//
//  BookingStatus.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 07.04.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//
import UIKit

enum BookingStatus {
    case isUpcoming
    case isPreCheckIn
    case isPast
    case isBusiness
    case cancelled
    case unowned

    var statusText: NSMutableAttributedString {
        switch self {
        case .isUpcoming:
            return NSMutableAttributedString(string: PILocalizedString("reservationsUpcomingReservation"))
        case .isPreCheckIn:
            return NSMutableAttributedString(string: PILocalizedString("ciolCheckedInStatus"))
        case .isPast:
            return NSMutableAttributedString(string: PILocalizedString("reservationsPastReservation"))
        case .isBusiness:
            let imageAttachment = NSTextAttachment()
            imageAttachment.image = UIImage(named: "briefcase")
            imageAttachment.bounds = CGRect(x: 0, y: -3, width: 15, height: 14)

            let imageString = NSAttributedString(attachment: imageAttachment)
            let textString = NSAttributedString(string: "  \(PILocalizedString("reservationBusinessTripLabel"))")
            let finalString = NSMutableAttributedString()
            finalString.append(imageString)
            finalString.append(textString)

            return finalString
        case .cancelled:
            return NSMutableAttributedString(string: PILocalizedString("reservationsCancelledReservation"))
        case .unowned:
            return NSMutableAttributedString(string: "")
        }
    }

    var statusTextColor: UIColor {
        switch self {
        case .isUpcoming, .isPreCheckIn:
            return .Tint4
        case .isPast:
            return .ColourDL2
        case .isBusiness:
            return .TintD2
        case .cancelled:
            return .Tint8
        case .unowned:
            return .clear
        }
    }

    var statusBackgroundColor: UIColor {
        switch self {
        case .isUpcoming, .isPreCheckIn:
            return .Tint5
        case .isPast:
            return .TintL3
        case .isBusiness:
            return .BaseWhite
        case .cancelled:
            return .Tint9
        case .unowned:
            return .clear
        }
    }

    var borderColor: UIColor {
        switch self {
        case .isBusiness:
            return .ColourLD3
        default:
            return .clear
        }
    }
}
