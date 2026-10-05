//
//  RoomType+Extensions.swift
//  PremierInn
//
//  Created by Marcello Mascia on 26/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork

extension RoomType {
	var name: String {
		PILocalizedString(self.rawValue, comment: "Room type name")
	}

	var localizedName: String {
		switch self {
		case .single:
			return PILocalizedString("single", comment: "")
		case .double:
			return PILocalizedString("double", comment: "")
		case .twin:
			return PILocalizedString("twin", comment: "")
		case .family:
			return PILocalizedString("family", comment: "")
		case .accessible:
			return PILocalizedString("accessible", comment: "")
		}
	}


	/**
	Builds a fully formed room description for a given array of rooms:
	- *2 single rooms*
	- *1 double room*

	- returns: A fully formed room description for a given array of rooms
	*/
	func localizedDescription(for rooms: [Room]) -> String? {
		let numberOfRoomsFound = rooms.filter {$0.type == self}.count

		guard numberOfRoomsFound > 0 else { return nil }

        if self != .accessible {
            return String.localizedStringWithFormat(
            	PILocalizedString("%d %@room(s)", comment: "Message shown for number of rooms"),
            	numberOfRoomsFound,
            	localizedName
            )
        } else {
            return String.localizedStringWithFormat(
            	PILocalizedString("%d %@ room(s)", comment: "Message shown for number of rooms"),
            	numberOfRoomsFound,
            	localizedName
            )
        }
	}

	var description: String {
		PILocalizedString(self.rawValue + "_description", comment: "Room type name")
	}
}
