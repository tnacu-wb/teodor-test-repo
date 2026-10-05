//
//  PIFormSegmentedControl.swift
//  PremierInn
//
//  Created by Freddie Parks on 07/10/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit

class PIFormSegmentedControl: UISegmentedControl {
    private var setupComplete: Bool = false

    override func layoutSubviews() {
        super.layoutSubviews()

        if !setupComplete {
            setup()
        }
    }

    private func setup() {
        setupComplete = true

        setTitleTextAttributes([.font: UIFont.Body_Semibold(), .foregroundColor: UIColor.BasePurple], for: .normal)
        setTitleTextAttributes([.font: UIFont.Body_Semibold(), .foregroundColor: UIColor.white], for: .selected)


        selectedSegmentTintColor = .BasePurple
        layer.borderColor = UIColor.warmGrey.cgColor
        backgroundColor = .white
    }
}
