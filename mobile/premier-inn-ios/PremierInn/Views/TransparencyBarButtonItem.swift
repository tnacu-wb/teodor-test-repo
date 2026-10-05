//
//  TransparencyBarButtonItem.swift
//  PremierInn
//
//  Created by Freddie Parks on 09/08/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class TransparencyBarButtonItem: UIBarButtonItem {
    var button: UIButton?

    override init() {
        super.init()
    }

    init(withButton button: UIButton) {
        self.init(customView: button)

        self.button = button
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
