//
//  SimpleSeparatorsCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 28/06/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

open class SimpleSeparatorsCell: FormekaTableViewCell {

    public enum SeparatorLocation {
        case top
        case bottom
    }

    public var hiddenSeparatorLocations: [SeparatorLocation] = [] {
        didSet {
            setNeedsLayout()
        }
    }

    override open func layoutSubviews() {
        super.layoutSubviews()

        hideSeparators(atLocations: hiddenSeparatorLocations)
    }

    private func hideSeparators(atLocations locations: [SeparatorLocation]) {

        let separators = subviews.filter { type(of: $0).description() == "_UITableViewCellSeparatorView" }.sorted { $0.frame.minY < $1.frame.minY }

        for separator in separators {
            if separator.center.y < 10 && locations.contains(.top) {
                separator.backgroundColor = .clear
            } else if locations.contains(.bottom) {
                separator.backgroundColor = .clear
            }
        }
    }
}
