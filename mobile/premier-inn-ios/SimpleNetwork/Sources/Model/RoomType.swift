//
//  RoomType.swift
//  SimpleNetwork
//
//  Created by Marcello Mascia on 23/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation

public enum RoomType: String, Codable {
	case single = "SB"
	case double = "DB"
	case twin = "TWIN"
	case family = "FAM"
	case accessible = "DIS"

	public var code: String { self.rawValue }
	public var iconName: String { self.rawValue + "Room" }
}

extension RoomType {
	public func availableTypes(adults: Int, children: Int, cot: Bool) -> [RoomType] {
		if cot {
			return availableTypesWithCot(adults: adults, children: children)
		}

		switch (adults, children) {
		case (1, 0):
			return availableTypesOneAdultNoChildren

		case (2...Int.max, 0):
			return availableTypesManyAdultsNoChildren

		case (1...Int.max, 1...Int.max):
			return [.family]

		default:
			return []
		}
	}

	private var availableTypesOneAdultNoChildren: [RoomType] {
		switch self {
		case .single:
			return [.single, .double, .accessible]

		case .double, .twin, .family:
			return [.double, .single, .accessible]

		case .accessible:
			return [.accessible, .double, .single]
		}
	}

	private var availableTypesManyAdultsNoChildren: [RoomType] {
		switch self {
		case .single, .double, .family:
			return [.double, .twin, .accessible]

		case .twin:
			return [.twin, .double, .accessible]

		case .accessible:
			return [.accessible, .double, .twin]
		}
	}

    private func availableTypesWithCot(adults: Int, children: Int) -> [RoomType] {
        switch (adults, children) {
        case (1, 0):
            return [.single, .double, .accessible]
        case (2...Int.max, 0):
            return [.double, .twin, .accessible]
        case (1...Int.max, 1...Int.max):
            return [.family]

        default:
            return []
        }
    }
}
