//
//  BiometricAuthentication.swift
//  PremierInnUITests
//
//  Created by Filippo Minelle on 10/08/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import UIKit
import Foundation

struct BiometricAuthentication {

    public enum BiometryTypeRetro {
        case touchId
        case faceId
        case none
    }

    public static var biometryTypeAvailable: BiometryTypeRetro {

        switch UIDevice.current.name {

        case "iPhone 5s", "iPhone SE (1st generation)", "iPhone SE (2nd generation)",
             "iPhone 6", "iPhone 6 Plus", "iPhone 6s", "iPhone 6s Plus",
             "iPhone 7", "iPhone 7 Plus", "iPhone 8", "iPhone 8 Plus",
             "iPad (5th generation)", "iPad (6th generation)", "iPad (7th generation)",
             "iPad Air", "iPad Air (3rd generation)", "iPad Air 2",
             "iPad Pro (9.7-inch)", "iPad Pro (10.5-inch)",
             "iPad Pro (12.9-inch)", "iPad Pro (12.9-inch) (2nd generation)":
            return .touchId

        case "iPhone 12", "iPhone 12 Pro", "iPhone 12 Pro Max",
             "iPhone 11", "iPhone 11 Pro", "iPhone 11 Pro Max",
             "iPhone X", "iPhone Xs", "iPhone Xs Max", "iPhone XR",
             "iPad Pro (11-inch) (1st generation)", "iPad Pro (11-inch) (2nd generation)",
             "iPad Pro (12.9-inch) (3rd generation)", "iPad Pro (12.9-inch) (4th generation)":
            return .faceId

        default:
            return .none
        }
    }
}
