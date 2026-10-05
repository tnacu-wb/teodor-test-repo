//
//  ContentsquareConfig.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 15/05/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import UIKit
import ContentsquareModule

protocol ContentsquareConfigurable {
    static func setUp()
    static func defaultMask(isMasked: Bool)
    static func mask(view: UIView)
}

struct ContentsquareConfig: ContentsquareConfigurable {
    static func setUp() {
        Contentsquare.start()
        Contentsquare.optIn()
    }

    static func defaultMask(isMasked: Bool) {
        Contentsquare.setDefaultMasking(isMasked)
    }

    static func mask(view: UIView) {
        Contentsquare.mask(view: view)
    }
}
