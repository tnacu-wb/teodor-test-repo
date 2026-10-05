//
//  Formeka+RateSections.swift
//  PremierInn
//
//  Created by Nick Jones on 18/07/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import Formeka
import UIKit

extension UIViewController {
    func titleHeader(
        title: String,
        textAlignment: NSTextAlignment = .center,
        height: CGFloat = 50,
        font: UIFont = UIFont.Heading3_Semibold(),
        backgroundColour: UIColor = .white,
        andColour colour: UIColor = .BasePurple,
        accessibilityIdentifier: String? = nil
    ) -> FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: height, viewSetup: { _, table in
            guard let header: SimpleHeaderWithActionLabel = table.headerFooterView() else { return nil }

            header.contentView.backgroundColor = backgroundColour

            header.accessibilityIdentifier = accessibilityIdentifier

            header.titleLabel.text = title
            header.titleLabel.font = font
            header.titleLabel.textAlignment = textAlignment
            header.titleLabel.textColor = colour
            header.titleLabel.accessibilityTraits.insert(.header)
            header.actionButton.isHidden = true

            return header
        })
    }
}
