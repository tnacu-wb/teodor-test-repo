//
//  HeaderInformation.swift
//  SimpleNetwork
//
//  Created by Georgios Aikaterinakis on 28/07/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

public struct HeaderInformation: Codable {
    let content: Content
    let form: Form
    let datePicker: DatePicker
    let results: Results
    let config: Config
}

public struct Content: Codable {
    let global: Global
    let menu: Menu
}

public struct Global: Codable {
    let addRoom: String
    let done: String
    let room: String
    let roomLabel: String
    let single: String
    let double: String
    let twin: String
    let accessible: String
    let family: String
    let adult: String
    let adults: String
    let child: String
    let children: String
    let night: String
    let rooms: String
    let adultsLabel: String
    let childrenLabel: String
    let today: String
    let tomorrow: String
}

public struct Menu: Codable {
    let mobileMenuButton: String
    let language: String
    let business: String
    let languageButton: String
    let tick: String
    let logIn: String
    let discoverPI: String
    let findBooking: String
    let bookHotel: String
    let guestAccount: String?
    let changeLogs: String?
    let agentMemo: String?
}

public struct Form: Codable {
    enum CodingKeys: String, CodingKey {
        case childrenHelperText
        case adultsHelperText
        case includeCot
        case cotLimit
        case removeRoom
        case checkout
        case roomType
        case whereEmailLandingPage
        case whereString = "where"
    }

    let childrenHelperText: String
    let adultsHelperText: String
    let includeCot: String
    let cotLimit: String?
    let removeRoom: String
    let checkout: String
    let roomType: String
    let whereEmailLandingPage: String
    let whereString: String // key
}

public struct DatePicker: Codable {
    let reset: String
}

public struct Results: Codable {
    let notifications: Notifications
}

public struct Notifications: Codable {
    let groupBookingHeader: String
    let groupBookingMessage: String
}

public struct Config: Codable {
    let roomCodes: RoomCodes
}

public struct RoomCodes: Codable {
    let double: String
    let family: String
    let accessible: String
    let single: String
    let twin: String
}
