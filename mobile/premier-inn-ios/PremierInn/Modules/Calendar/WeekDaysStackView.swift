//
//  WeekDaysStackView.swift
//  PF
//
//  Created by Marcello Mascia on 26/08/2018.
//  Copyright © 2018 Whitbread PLC. All rights reserved.
//

import UIKit
import SimpleCalendar

class WeekDaysStackView: UIStackView {
	required init(coder: NSCoder) {
		super.init(coder: coder)

		addWeekDays()
	}

	private func addWeekDays() {
		CalendarViewModel.weekdays.forEach { weekday in
			let label = UILabel()
			label.text = weekday.text
            label.isAccessibilityElement = false
			label.textAlignment = .center
			label.backgroundColor = .Tint1
			label.font = UIFont.BodySmall_Semibold()
			label.textColor = .white

			addArrangedSubview(label)
		}
	}
}
