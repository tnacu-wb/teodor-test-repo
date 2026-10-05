//
//  RoomSubstitution.swift
//  SimpleNetwork
//
//  Created by Marcello Mascia on 23/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation

public struct RoomSubstitution {
	public var desired: RoomType?
	public var substituted: RoomType?
    public var substitutedRoomsConcatenated: String?
	public var roomName: String?

	public init(desired: RoomType?, substituted: RoomType?, substitutedRoomsConcatenated: String?, roomName: String?) {
		self.desired = desired
		self.substituted = substituted
        self.substitutedRoomsConcatenated = substitutedRoomsConcatenated
		self.roomName = roomName
	}
}
