//
//  Images.swift
//  PremierInn
//
//  Created by Filippo Minelle on 07/12/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import UIKit

extension UIImage {
    static var icon_search: UIImage {
        UIImage(named: "icon_search") ?? UIImage()
    }

    static var checkInImage = UIImage(named: "checkIn") ?? UIImage()
    static var checkOutImage = UIImage(named: "checkOut") ?? UIImage()
    static var accountHDP = (UIImage(named: "accountHDP") ?? UIImage()).withRenderingMode(.alwaysTemplate)
    static var calendarHDP = (UIImage(named: "calendarHDP") ?? UIImage()).withRenderingMode(.alwaysTemplate)

    static var ciolHubBG: UIImage {
        UIImage(named: "ciolHubBG") ?? UIImage()
    }

    static var ciolPiBG: UIImage {
        UIImage(named: "ciolPiBG") ?? UIImage()
    }

    static var ciolInfoIcon: UIImage {
        UIImage(named: "ciolInfoIcon") ?? UIImage()
    }

    static var arrowDown: UIImage { UIImage(named: "arrowDown") ?? UIImage() }

    static var alertIcon: UIImage { UIImage(named: "alert") ?? UIImage() }

    static var errorIcon: UIImage { UIImage(named: "errorInfoIcon") ?? UIImage() }
}
