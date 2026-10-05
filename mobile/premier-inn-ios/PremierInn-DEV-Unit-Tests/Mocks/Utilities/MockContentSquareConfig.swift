//
//  MockContentSquareConfig.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit
@testable import PremierInn

enum MockContentsquareConfig: ContentsquareConfigurable {

    static var callCount = 0

    static func setUp() {
        callCount += 1
    }

    static func defaultMask(isMasked: Bool) { }

    static func mask(view: UIView) { }

    static func reset() {
        callCount = 0
    }
}
