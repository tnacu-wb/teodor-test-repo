//
//  BookingPreference.swift
//  SimpleNetwork
//
//  Created by Marcello Mascia on 24/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation

public struct BookingPreference: Codable {
    static let empty = BookingPreference(
    	foodPreference: MealOption.none,
    	wantSmsConfirmations: false,
    	preselectWifi: false,
    	roomRequirements: RoomRequirements.standard
    )

	public var foodPreference: MealOption?
	let wantSmsConfirmations: Bool?
	public let preselectWifi: Bool?
	public var roomRequirements: RoomRequirements? = RoomRequirements.standard
}
