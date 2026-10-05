//
//  CalendarDayCell.swift
//  Calendar
//
//  Created by Freddie Parks on 29/06/2016.
//  Copyright © 2016 Freddie Parks. All rights reserved.
//

import UIKit

open class CalendarDayCell: UICollectionViewCell {

    private var selectableColor = UIColor.red
    private var notSelectableColor = UIColor.red
    private var selectedColor = UIColor.red
    private var highlightedColor = UIColor.white
    private var highlightedBackgroundColor = UIColor.red

    @IBOutlet public weak var dayLabel: UILabel! {
        didSet {
            dayLabel.backgroundColor = .clear
            dayLabel.highlightedTextColor = .red
            contentView.backgroundColor = .clear
        }
    }

    public var isSelectable: Bool = false {
        didSet {
            dayLabel.textColor = isSelectable ? selectableColor : notSelectableColor
            dayLabel.highlightedTextColor = highlightedColor
        }
    }

    private var currentSelectedColor: UIColor {
        return isSelectable && isSelected ? selectedColor : .clear
    }

	override open var isSelected: Bool {
        didSet {
            contentView.backgroundColor = currentSelectedColor
        }
    }

    public func setCustomColours(selectable: UIColor, notSelectable: UIColor, selected: UIColor, highlighted: UIColor, highlightedBackground: UIColor) {

        self.selectableColor = selectable
        self.notSelectableColor = notSelectable
        self.selectedColor = selected
        self.highlightedColor = highlighted
        self.highlightedBackgroundColor = highlightedBackground
    }

    public func setCustomFont(_ font: UIFont) {

        dayLabel.font = font
    }

	override open func layoutSubviews() {
		super.layoutSubviews()

		contentView.layer.cornerRadius = contentView.frame.width * 0.5
	}
}
