//
//  SimpleCalendar+Extensions.swift
//  PF
//
//  Created by Marcello Mascia on 26/08/2018.
//  Copyright © 2018 Whitbread PLC. All rights reserved.
//

import SimpleCalendar
import SimpleNetwork
import Foundation

extension SimpleCalendarSettings {
    static func settings(
    	arrivalDate: Date?,
    	overRideRules: Restrictions?,
    	closeButtonTitle: String? = nil
    ) -> SimpleCalendarSettings {
		SimpleCalendarSettings(
			colors: SimpleCalendarSettings.Colors(
				weekday: .clear,
				weekend: .clear,
				selectable: .TintD1,
				highlighted: .white,
				highlightedBackground: .clear,
				selected: .white,
				notSelectable: .TintL2,
				footer: .TintD1,
				month: .TintD1,
				today: .TintD1
			),
			fonts: SimpleCalendarSettings.Fonts(
				weekdays: .BodySmall_Semibold(),
				days: .BodySmall(),
				footer: .BodySmall(),
				month: .Heading2_Semibold()
			),
			metrics: SimpleCalendarSettings.Metrics(monthHeaderHeight: 75, weekdayHeaderMargin: 0),
			footerText: PILocalizedString("We only take bookings one year in advance"),
			selectedDate: arrivalDate ?? Date(),
			selectableDatesOffset: 0,
			andCustomTitle: "",
			monthAlignment: .left,
			closeButtonTitle: closeButtonTitle ?? PILocalizedString("Cancel"),
			showTodayButton: false,
			maxDepartureDateCount: overRideRules?.maxDepartureDateCount ?? SettingsManager.sharedInstance.activeRules
            .maxDepartureDateCount
		)
	}
}
