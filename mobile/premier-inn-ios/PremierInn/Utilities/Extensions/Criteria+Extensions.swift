//
//  Criteria+Extensions.swift
//  PremierInn
//
//  Created by Marcello Mascia on 26/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import UIKit

extension Criteria {
	var summary: String {
		var summaryItems: [String] = []

		summaryItems.append(arrivalDate.localizedShortStringFormat)
		summaryItems.append(nightsCountDescription)

		if adultsCount > 0 {
			let string = String.localizedStringWithFormat(
				PILocalizedString("%d adult(s)", comment: "Message shown for number of adults"),
				adultsCount
			)
			summaryItems.append(string)
		}

		if childrenCount > 0 {
			let string = String.localizedStringWithFormat(
				PILocalizedString("%d child(children)", comment: "Message shown for number of children"),
				childrenCount
			)
			summaryItems.append(string)
		}

		return summaryItems.joined(separator: " - ")
	}

	var guestsAndNightsSummary: String {
		let nightsString = String.localizedStringWithFormat(
			PILocalizedString("%d night(s)", comment: "Message shown for number of nights"),
			nights
		)

		return guestsCountDescription + ", " + nightsString
	}

	var guestsCountDescription: String {
		Criteria.guestsCountDescription(for: guestsCount)
	}

    static func guestsCountDescription(for guests: Int) -> String {
        String.localizedStringWithFormat(
        	PILocalizedString("%d guest(s)", comment: "Message shown for number of guests"),
        	guests
        )
    }

    var guestsCountDescriptionBreakdown: String {
        var entities: [String] = [adultsCountDescription]
        if childrenCount > 0 { entities.append(childrenCountDescription) }
        return entities.joined(separator: ", ")
    }

	var adultsCountDescription: String {
		Criteria.adultsCountDescription(for: adultsCount)
	}

    static func adultsCountDescription(for adults: Int) -> String {
        String.localizedStringWithFormat(
        	PILocalizedString("%d adult(s)", comment: "Message shown for number of adults"),
        	adults
        )
    }

	var childrenCountDescription: String {
		Criteria.childrenCountDescription(for: childrenCount)
	}

    static func childrenCountDescription(for children: Int) -> String {
        String.localizedStringWithFormat(
        	PILocalizedString("%d child(children)", comment: "Message shown for number of children"),
        	children
        )
    }

	var nightsCountDescription: String {
        Criteria.nightCountDescription(for: nights)
	}

    static func nightCountDescription(for nights: Int) -> String {
        String.localizedStringWithFormat(
        	PILocalizedString("%d night(s)", comment: "Message shown for number of nights"),
        	nights
        )
    }

    var shortRoomsCountDescription: String {
        String.localizedStringWithFormat(
        	PILocalizedString("%d room(s)", comment: "Message shown for number of rooms"),
        	rooms.count
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

	var rateDatesSummary: String {
		guard let checkOutDate = checkOutDate else { return "Arriving \(arrivalDate.localizedShortStringFormat)" }

		return "\(arrivalDate.localizedVeryShortStringFormat) - \(checkOutDate.localizedVeryShortStringFormat)"
	}

	var rateGuestsSummary: String {
		"\(nightsCountDescription), \(guestsCountDescription)"
	}

    var rateCriteriaSummary: String {
        "\(shortRoomsCountDescription), \(nightsCountDescription)"
    }

	var reviewDatesSummary: String {
		guard let checkOutDate = checkOutDate else { return "Arriving \(arrivalDate.localizedShortStringFormat)" }

        return "\(arrivalDate.localizedShortDayMonthStringFormat) \("-") \(checkOutDate.localizedShortDayMonthStringFormat)"
	}

	var reviewDatesSummaryShort: String {
		guard let checkOutDate = checkOutDate else { return "Arriving \(arrivalDate.localizedShortStringFormat)" }

		return "\(arrivalDate.localizedShortDayMonthStringFormat) - \(checkOutDate.localizedShortDayMonthStringFormat)"
	}

    var criteriaRoomsGuestSummary: String {
        let roomsSwatch = rooms.count == 1 ? "1 \(rooms[0].typeSummary)" : roomsCountDescription

        return "\(guestsCountDescription), \(roomsSwatch)"
    }

	var reviewRoomsSummary: String {
		"\(guestsCountDescription) - \(roomsCountDescription)"
	}

    var amendReviewTripSummary: String {
        "\(roomsCountDescription), \(nightsCountDescription)"
    }

    var amendReviewArriving: String {
        arrivalDate.localizedVeryShortStringFormat
    }

    var amendReviewDeparting: String? {
        checkOutDate?.localizedVeryShortStringFormat
    }

	var titleSummary: String {
		var string = arrivalDate.localizedVeryShortStringFormat

		if let checkOutDate = checkOutDate {
			string += " - " + checkOutDate.localizedVeryShortStringFormat
		}

		string += " • " + guestsCountDescription + ", " + roomsCountDescription

		return string
	}

	var leadDays: Int {
		Date().daysToCheckInDate(arrivalDate)
	}

	func addGuests(_ guests: [User]) {
		for (index, room) in rooms.enumerated() {
			guard index < guests.count else { break }

			room.leadGuest = guests[index]
		}
	}

	/**
	Builds a fully formed string based on the current room requirements such as:
	- *(2 single rooms, 1 double room)*
	- *(1 single room, 1 family room, 1 accessible room)*

	- returns: A fully formed string based on the current room requirements
	*/
	func roomBreakdown() -> String {
		[RoomType]([.single, .double, .twin, .family, .accessible]).compactMap { $0.localizedDescription(for: rooms) }
			.joined(separator: ", ")
	}

	var analyticsDictionary: PIDictionary {
		let checkoutDate = checkOutDate ?? Date()

		var data: PIDictionary = [:]
		data[PIAnalytics.Keys.nights] = "\(nights)"
		data[PIAnalytics.Keys.rooms] = "\(rooms.count)"
		data[PIAnalytics.Keys.checkIn] = arrivalDate.analyticsDateFormat
		data[PIAnalytics.Keys.checkOut] = checkoutDate.analyticsDateFormat
		data[PIAnalytics.Keys.adults] = "\(adultsCount)"
		data[PIAnalytics.Keys.children] = "\(childrenCount)"
		data[PIAnalytics.Keys.guests] = "\(guestsCount)"
		data[PIAnalytics.Keys.leadDays] = "\(leadDays)"
		data[PIAnalytics.Keys.roomType] = rooms.compactMap { $0.type.code }.joined(separator: ":")
		data[PIAnalytics.Keys.roomTypeNames] = rooms.compactMap { $0.type.localizedName.capitalized }.joined(separator: ":")

		let startDay = arrivalDate.analyticsDayFormat
		let endDay = checkoutDate.analyticsDayFormat

		data[PIAnalytics.Keys.startEndDay] = [startDay, endDay].joined(separator: "-")
		data[PIAnalytics.Keys.startDay] = startDay
		data[PIAnalytics.Keys.endDay] = endDay

		return data
	}

    var attributedSummaryString: NSAttributedString {
        let string = NSMutableAttributedString()

        let attributedArrivalDate = NSAttributedString(
        	string: DateFormatter.veryShortStringFormatter.string(from: arrivalDate) + " ",
        	attributes: nil
        )
        string.append(attributedArrivalDate)

        let attachment = NSTextAttachment()
        attachment.image = UIImage(named: "dateRangeArrow")!
        attachment.bounds = CGRect(x: 0, y: -4, width: 18, height: 18)

        string.append(NSAttributedString(attachment: attachment))

        if let checkOutDate = checkOutDate {
            let attributedCheckOutDate = NSAttributedString(
            	string: " " + DateFormatter.veryShortStringFormatter.string(from: checkOutDate),
            	attributes: nil
            )
            string.append(attributedCheckOutDate)
        }

        string.append(NSAttributedString(string: "  •  \(guestsCountDescription), \(roomsCountDescription)"))
        string.addAttributes(
        	[.foregroundColor: UIColor.ColourDL2, .font: UIFont.Subtext()],
        	range: NSRange(location: 0, length: string.length)
        )

        return NSAttributedString(attributedString: string)
    }
}

extension Criteria: @retroactive Equatable {
    public static func == (lhs: Criteria, rhs: Criteria) -> Bool {
        (lhs.arrivalDate.ignoringTime == rhs.arrivalDate.ignoringTime && lhs.nights == rhs.nights && lhs.rooms == rhs.rooms)
    }
}
