//
//  SimpleSegmentedControl.swift
//  PremierInn
//
//  Created by Freddie Parks on 01/09/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class SimpleSegmentedControl: UISegmentedControl {
    var segmentData: SegmentSelectorData? {
        didSet {
            guard let segmentData = segmentData else { return }

            removeAllSegments()

            for (index, element) in segmentData.segments.enumerated() {
                insertSegment(withTitle: element.title, at: index, animated: false)
            }
        }
    }

    override init(items: [Any]?) {
        super.init(items: items)

        setupControl()
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)

        setupControl()
    }

    func setupControl() {
        tintColor = .clear
        accessibilityIdentifier = "simpleSegmentedControlAcc"

        let image = #imageLiteral(resourceName: "trans")

        setBackgroundImage(image, for: .normal, barMetrics: .default)
        setBackgroundImage(image, for: .selected, barMetrics: .default)
        setDividerImage(image, forLeftSegmentState: .normal, rightSegmentState: .normal, barMetrics: .default)
    }
}
