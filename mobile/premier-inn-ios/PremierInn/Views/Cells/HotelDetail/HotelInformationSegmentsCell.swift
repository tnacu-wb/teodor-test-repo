//
//  HotelInformationSegmentsCell.swift
//  PremierInn
//
//  Created by Freddie Parks on 31/08/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

protocol HotelInformationSegmentsCellDelegate: AnyObject {
    func hotelInformationSegmentsCellSegmentDidChange(cell: HotelInformationSegmentsCell)
}

class HotelInformationSegmentsCell: UITableViewCell {
    @IBOutlet weak var segmentControl: SimpleSegmentedControl!
    @IBOutlet weak var selectedViewWidthConstraint: NSLayoutConstraint!
    @IBOutlet weak var selectedViewLeadingEdgeConstraint: NSLayoutConstraint!
    @IBOutlet weak var containerView: UIView! {
        didSet {
            containerView.backgroundColor = .red
        }
    }
    @IBOutlet weak var selectorView: UIView! {
        didSet {
            selectorView.backgroundColor = .green
        }
    }

    var selectorData: SegmentSelectorData? {
        didSet {
            segmentControl.segmentData = selectorData
            segmentControl.selectedSegmentIndex = selectorData?.selectedIndex ?? 0

            updateSelectorPosition()
        }
    }

    weak var delegate: HotelInformationSegmentsCellDelegate?

    func updateUI(
        backgroundColor: UIColor,
        selectorColor: UIColor,
        normalTextAttributes: [NSAttributedString.Key: Any],
        selectedTextAttributes: [NSAttributedString.Key: Any]
    ) {
        containerView.backgroundColor = backgroundColor
        selectorView.backgroundColor = selectorColor
        SimpleSegmentedControl.appearance().setTitleTextAttributes(normalTextAttributes, for: .normal)
        SimpleSegmentedControl.appearance().setTitleTextAttributes(selectedTextAttributes, for: .selected)
    }

    private func updateSelectorPosition() {
        guard segmentControl.numberOfSegments > 0 else { return }

        selectedViewWidthConstraint.constant = frame.size.width / CGFloat(segmentControl.numberOfSegments)
        selectedViewLeadingEdgeConstraint.constant = (0...frame.size.width - selectedViewWidthConstraint.constant)
            .clamp(selectedViewWidthConstraint.constant * CGFloat(segmentControl.selectedSegmentIndex) - 32)

        setNeedsLayout()
        layoutIfNeeded()
        contentView.setNeedsLayout()
        contentView.layoutIfNeeded()
    }

    @IBAction func segmentControlValueDidChange(_ sender: AnyObject) {
        delegate?.hotelInformationSegmentsCellSegmentDidChange(cell: self)

        UIView.animate(withDuration: .ocd) {
            self.updateSelectorPosition()
        }
    }
}
