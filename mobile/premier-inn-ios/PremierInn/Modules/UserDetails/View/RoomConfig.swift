//
//  RoomConfig.swift
//  PremierInn
//
//  Created by Clint Mengolli on 16/04/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

struct RoomConfig {
    private enum Constants {
        static let roomTitlePrefix = PILocalizedString("genericRoomTitle") + " "
        static let footerHeight: CGFloat = 10
        static let lastRoomFooterHeight = CGFloat.leastNormalMagnitude
    }

    private let totalRooms: Int
    let isBookerStaying: Bool
    let user: User?
    let roomIndex: Int
    let shouldShowRoomInfo: Bool

    init(
        user: User?,
        roomIndex: Int,
        totalRooms: Int,
        isBookerStaying: Bool,
        shouldShowRoomInfo: Bool
    ) {
        self.user = user
        self.roomIndex = roomIndex
        self.totalRooms = totalRooms
        self.isBookerStaying = isBookerStaying
        self.shouldShowRoomInfo = shouldShowRoomInfo
    }

    private var isFirstRoom: Bool {
        roomIndex == 0
    }

    private var isLastRoom: Bool {
        roomIndex == totalRooms - 1
    }

    var roomTitle: String {
        Constants.roomTitlePrefix + String(roomIndex + 1)
    }

    var footerHeight: CGFloat {
        isLastRoom ? Constants.lastRoomFooterHeight : Constants.footerHeight
    }

    var shouldSummarise: Bool {
        isBookerStaying && isFirstRoom
    }

    var isEmailRequired: Bool {
        isBookerStaying && isFirstRoom
    }
}
