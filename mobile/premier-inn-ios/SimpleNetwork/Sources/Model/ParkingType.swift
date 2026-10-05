//
//  ParkingType.swift
//  PremierInn
//
//  Created by Marcello Mascia on 03/11/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

public enum ParkingType: String {
    case free = "CPF"
    case chargeable = "COP"
    case chargeableOnsite = "CPP"

    public var image: UIImage? {
        UIImage(named: self.rawValue)?.withRenderingMode(.alwaysTemplate)
    }

    public var squareIconName: String {
        switch self {
        case .free: "CPF_square"
        case .chargeable, .chargeableOnsite: "COP_CPP_square"
        }
    }
}
