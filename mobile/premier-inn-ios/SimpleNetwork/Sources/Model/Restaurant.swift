//
//  Restaurant.swift
//  PremierInn
//
//  Created by Marcello Mascia on 18/04/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation

private enum RestaurantError: LocalizedError {
    case missingRestaurantDictionary
    case missingRestaurantName

	var errorDescription: String? { String(describing: self)	}
}

public struct Restaurant {
    public let name: String
    public let description: String?
    public let imageURL: URL?
    public let menus: [RestaurantMenuItem]?

    init(dictionary: PIDictionary?) throws {
        guard let dictionary = dictionary else { throw RestaurantError.missingRestaurantDictionary }
        guard let name = dictionary["name"] as? String else { throw RestaurantError.missingRestaurantName }

        self.name = name
        self.description = dictionary["description"] as? String

        if let imageRef = dictionary["image"] as? String {
           self.imageURL = Constants.imagesBaseURL.appendingPathComponent(imageRef)
        } else {
            self.imageURL = nil
        }

        if let menusDic = dictionary["menus"] as? [PIDictionary] {
            do {
                let data = try JSONSerialization.data(withJSONObject: menusDic, options: .prettyPrinted)
                let decoder = JSONDecoder()
                self.menus = try decoder.decode([RestaurantMenuItem].self, from: data)
            } catch {
                self.menus = nil
            }
        } else {
            self.menus = nil
        }
    }
}
