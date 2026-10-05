//
//  DebugButton.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 11/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit

final class DebugButtonView: UIButton {
    static let identifier = 666

    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setup()
    }

    private func setup() {
        layer.cornerRadius = 22
        layer.shadowOpacity = 0.5
        layer.shouldRasterize = true
        layer.rasterizationScale = UIScreen.main.scale
        layer.shadowOffset = .zero
        layer.shadowColor = UIColor.black.cgColor
        layer.shadowRadius = 2

        titleLabel?.font = UIFont.systemFont(ofSize: 13)
        titleLabel?.adjustsFontSizeToFitWidth = true

        setTitleColor(.black, for: .normal)
        backgroundColor = .strongRed

        tag = Self.identifier
        accessibilityIdentifier = AccessibilityIdentifiers.Home.redCircleDebugButton
    }

    func update(title: String) {
        setTitle(title, for: .normal)
    }
}
