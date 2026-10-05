//
//  TripAdvisorDetails.swift
//  SimpleNetwork
//
//  Created by Georgios Aikaterinakis on 12/11/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import Foundation

public struct Award {
    public var awardType: String
    public var year: Int
    public var imageURL: URL?

    init(dictionary: PIDictionary) {
        self.awardType = dictionary["awardType"] as? String ?? ""
        self.year = dictionary["year"] as? Int ?? 0
        self.imageURL = {
            guard let string = dictionary["image"] as? String else { return nil }
            return URL.secureURL(from: string)
        }()
    }
}

public struct SubRating {
    public var localisedName: String
    public var value: Double
    public var ratingImageURL: URL?

    init(dictionary: PIDictionary) {
        self.localisedName = dictionary["localisedName"] as? String ?? ""
        self.value = dictionary["value"] as? Double ?? 0.0
        self.ratingImageURL = {
            guard let string = dictionary["ratingImageUrl"] as? String else { return nil }
            return URL.secureURL(from: string)
        }()
    }
}

public struct TripAdvisorDetails {
    public var rating: Double
    public var numberOfReviews: Int
    public var awards: [Award]
    public var subRatings: [SubRating]

    public init(dictionary: PIDictionary) {
        self.rating = dictionary["rating"] as? Double ?? 5  // if in doubt...
        self.numberOfReviews = dictionary["numberOfReviews"] as? Int ?? 0
        self.awards = {
            guard let dictionaries = dictionary["awards"] as? [PIDictionary] else { return [] }

            return dictionaries.compactMap { Award(dictionary: $0) }
        }()
        self.subRatings = {
            guard let dictionaries = dictionary["subRatings"] as? [PIDictionary] else { return [] }

            return dictionaries.compactMap { SubRating(dictionary: $0) }
        }()
    }
}
