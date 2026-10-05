//
//  RoomTypeInformation.swift
//  SimpleNetwork
//
//  Created by Georgios Aikaterinakis on 21/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation

public struct RoomTypeInformation: Codable {
    public let roomTypeCodes: [String]
    public let roomCategory: String
    public let roomLabel: String
    let roomDescription: String
    let roomImage: String

    enum CodingKeys: String, CodingKey {
        case roomTypeCodes = "roomTypeCode"
        case roomCategory
        case roomLabel
        case roomDescription
        case roomImage
    }
}
