//
//  ParkingType+Extensions.swift
//  PremierInn
//
//  Created by Marcello Mascia on 26/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork

extension ParkingType {
	var title: String? { PILocalizedString(self.rawValue, comment: "") }
	var shortTitle: String? { PILocalizedString(self.rawValue + "_short", comment: "") }
	var summary: TextAndRanges {
		switch self {
		case .chargeableOnsite:
			let text = PILocalizedString(
				"hotelDetailsParkingChargeableOnSite",
				comment: "Hotel details: parking chargeable on site message"
			)
			return (text, text.ranges(of: PILocalizedString("hotelDetailsParkingChargeableOnSiteBold", comment: "")))
		case .free:
			let text = PILocalizedString("hotelDetailsFreeParking", comment: "Hotel details: free parking message")
			return (
				text,
				text.ranges(of: PILocalizedString("hotelDetailsFreeParkingBold", comment: "Hotel details: free parking bold text"))
			)
		case .chargeable:
			let text = PILocalizedString(
				"hotelDetailsParkingChargeableOffSite",
				comment: "Hotel details: parking chargeable off site message"
			)
			return (
				text,
				text.ranges(of: PILocalizedString(
					"hotelDetailsParkingChargeableOffSiteBold",
					comment: "Hotel details: parking chargeable off site bold text"
				))
			)
		}
	}

	func description(with optionalDescription: String?) -> TextAndRanges {
		var text = ""

		if let optionalDescription = optionalDescription {
			text = optionalDescription
		} else {
			switch self {
			case .chargeableOnsite:
				text = PILocalizedString(
					"hotelDetailsParkingChargeableOnSiteDescription",
					comment: "Hotel details: parking chargeable on site full description"
				)
			case .free:
				text = PILocalizedString("hotelDetailsFreeParkingDescription", comment: "Hotel details: free parking full description")
			case .chargeable:
				text = PILocalizedString(
					"hotelDetailsParkingChargeableOffSiteDescription",
					comment: "Hotel details: parking chargeable off site full description"
				)
			}
		}

		text = text.htmlStripped().trimmingCharacters(in: .whitespacesAndNewlines)

		return (text, text.ranges(ofRegex: Constants.Regex.simplePrice))
	}
}
