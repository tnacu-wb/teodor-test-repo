//
//  Favourite.swift
//  PremierInn
//
//  Created by Marcello Mascia on 15/08/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Foundation

private enum FavouriteError: LocalizedError {
    case missingIdentifier

	var errorDescription: String? { String(describing: self)	}
}

public struct Favourite: DictionaryInitialisable {
    public let identifier: String
    public var dictionary: PIDictionary {
        ["identifier": identifier]
    }

    public init(dictionary: PIDictionary) throws {
        guard let identifier = dictionary["identifier"] as? String else { throw FavouriteError.missingIdentifier }

        self.identifier = identifier
    }

    public static func == (lhs: Favourite, rhs: Favourite) -> Bool {
        lhs.identifier == rhs.identifier
    }
}
