//
//  Reservation+Extensions.swift
//  PremierInn
//
//  Created by Marcello Mascia on 26/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

extension Reservation {
	var adultsCountDescription: String {
		String.localizedStringWithFormat(
		    PILocalizedString("%d adult(s)", comment: "Message shown for number of adults"),
		    adultsCount
		)
	}

	var childrenCountDescription: String {
		String.localizedStringWithFormat(
		    PILocalizedString("%d child(children)", comment: "Message shown for number of children"),
		    childrenCount
		)
	}

	var guestsCountDescription: String {
		String.localizedStringWithFormat(
		    PILocalizedString("%d guest(s)", comment: "Message shown for number of guests"),
		    guestsCount
		)
	}

	var nightsCountDescription: String {
		String.localizedStringWithFormat(
		    PILocalizedString("%d night(s)", comment: "Message shown for number of nights"),
		    nights
		)
	}

    var roomsCountDescription: String {
        let allRoomsAreAccessible = rooms.first(where: { $0.type != .accessible }) == nil

        if allRoomsAreAccessible {
            return String.localizedStringWithFormat(
                PILocalizedString("%d accessible room(s)", comment: "Message shown for number of rooms"),
                rooms.count
            )
        } else {
            return String.localizedStringWithFormat(
                PILocalizedString("%d room(s)", comment: "Message shown for number of rooms"),
                rooms.count
            )
        }
    }

	var guestsAndNightsSummary: String {
		let nightsString = String.localizedStringWithFormat(
		    PILocalizedString("%d night(s)", comment: "Message shown for number of nights"),
		    nights
		)

		return guestsCountDescription + PILocalizedString(" for ", comment: "") + nightsString
	}

    var staySummary: String {
        var string = ""

        guard let arrivalDate = arrivalDate, let checkOutDate = checkOutDate else { return string }

        string.append(DateFormatter.veryShortStringFormatter.string(from: arrivalDate))
        string.append(" - ")
        string.append(DateFormatter.veryShortStringFormatter.string(from: checkOutDate))
        string.append(", \(guestsCountDescription), \(roomsCountDescription)")

        return string
    }

    var datesSummary: String? {
        guard let arrivalDate = arrivalDate else { return nil }
        guard let checkOutDate = checkOutDate else { return nil }

        var string = arrivalDate.localizedVeryShortStringFormat
        string += " - " + checkOutDate.localizedVeryShortStringFormat
        string += " (\(nightsCountDescription))"

        return string
    }

    // Not sure this ever even got used despite there being a load of tests for it 😅
    mutating func correctBreakfasts() {
        for (index, breakfast) in breakfasts.enumerated() {
            for room in rooms where breakfast.roomId == room.roomId {
                guard let adultsWithBreakfast = breakfast.adults,
                      let childrenWithBreakfast = breakfast.children else { continue }

                // "When the number of adults goes up don't add breakfasts"
                if room.adults > adultsWithBreakfast {
                    continue
                }

                // "When the nuber of adults goes down remove the same amount of breakfasts"
                if adultsWithBreakfast > room.adults {
                    breakfasts[index].adults = room.adults
                }

                // "When the nuber of children goes down remove the same amount of breakfasts"
                if childrenWithBreakfast > room.children {
                    breakfasts[index].children = room.children
                }
            }
        }
    }

    var outstandingAmount: Cost? {
        guard let totalCost = self.totalCost else { return nil }

        let prepaidAmount = self.prepaidAmount?.amount.doubleValue ?? 0.0
        let currencyCode = totalCost.currencyCode
        let outstandingAmount = totalCost.amount.doubleValue - prepaidAmount

        return Cost(amount: outstandingAmount, currencyCode: currencyCode)
    }

    var bookingPreferences: [HotelPreferenceViewModel]? {
        preferences?.map { HotelPreferenceViewModel(code: $0.code ?? "", type: $0.preferenceType) }
    }
}

extension Reservation: GermanCityTaxViewModelValues {
    var cityTaxRequired: Bool {
        guard let cityTaxAmount = cityTax else { return false }

        return cityTaxAmount.amount.doubleValue > 0
    }
}
