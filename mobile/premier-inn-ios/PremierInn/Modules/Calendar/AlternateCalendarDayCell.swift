//
//  AlternateCalendarDayCell.swift
//  PremierInn
//
//  Created by Marcello Mascia on 24/09/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleCalendar

class AlternateCalendarDayCell: CalendarDayCell {
    var maskPosition: DayCellMaskPosition = .none

	override open func layoutSubviews() {
		super.layoutSubviews()

		contentView.layer.cornerRadius = 0
        contentView.layer.mask(corners: maskPosition, idealFrame: contentView.bounds)
	}
}
