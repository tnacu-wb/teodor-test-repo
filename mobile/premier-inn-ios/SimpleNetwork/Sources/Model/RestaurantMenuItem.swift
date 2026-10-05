//
//  RestaurantMenuItem.swift
//  PremierInn
//
//  Created by Freddie Parks on 13/04/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation

public struct RestaurantMenuItem: Codable {
    let name: String?
    let description: String?

    public let disclaimer: String?
    public let path: String?
}

extension RestaurantMenuItem: FoodSectionContent {
    public var title: String {
        name ?? ""
    }

    public var body: String {
        description?.htmlStripped() ?? ""
    }
}
