//
//  HomepageAppsContent.swift
//  SimpleNetwork
//
//  Created by Santa Gurung on 12/02/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

public struct HomepageAppsContent: Decodable {
    public let logo: String?
    public let heading: String?
    public let destinationCards: [AppsDestinationCard]?
    public let contentCards: [AppsContentCard]?
    public let promoCards: [AppsCard]?
    public let notification: NotificationBanner?
}

public struct AppsCard: Decodable {
    public let imagePath: String
    public let imageTag: String?
    public let title: String
    public let subtitle: String?
    public let linkPath: String?
    public let openLinkInApp: Bool?
    public let order: Int
    public let trackingId: String?
}

public struct AppsDestinationCard: Decodable {
    public let imagePath: String
    public let imageTag: String?
    public let title: String
    public let subtitle: String?
    public let linkPath: String?
    public let openLinkInApp: Bool?
    public let order: Int
    public let trackingId: String?
    public let latitude: Double?
    public let longitude: Double?

    enum CodingKeys: String, CodingKey {
        case imagePath, imageTag, title, subtitle, linkPath, openLinkInApp, order, trackingId, latitude, longitude
    }

    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        imagePath = try container.decode(String.self, forKey: .imagePath)
        imageTag = try container.decodeIfPresent(String.self, forKey: .imageTag)
        title = try container.decode(String.self, forKey: .title)
        subtitle = try container.decodeIfPresent(String.self, forKey: .subtitle)
        linkPath = try container.decodeIfPresent(String.self, forKey: .linkPath)
        openLinkInApp = try container.decodeIfPresent(Bool.self, forKey: .openLinkInApp)
        order = try container.decode(Int.self, forKey: .order)
        trackingId = try container.decodeIfPresent(String.self, forKey: .trackingId)

        // Decode latitude - GraphQL returns String, convert to Double
        if let latitudeString = try? container.decodeIfPresent(String.self, forKey: .latitude) {
            latitude = Double(latitudeString)
        } else {
            latitude = nil
        }

        // Decode longitude - GraphQL returns String, convert to Double
        if let longitudeString = try? container.decodeIfPresent(String.self, forKey: .longitude) {
            longitude = Double(longitudeString)
        } else {
            longitude = nil
        }
    }
}

public struct AppsContentCard: Decodable {
    public let imagePath: String
    public let imageTag: String?
    public let title: String?
    public let subtitle: String?
    public let linkPath: String?
    public let openLinkInApp: Bool?
    public let order: Int
    public let trackingId: String?
}

public struct NotificationBanner: Decodable {
    public let type: String
    public let title: String?
    public let message: String
    public let linkLabel: String?
    public let linkPath: String?
    public let openLinkInApp: Bool?
    public let dismissible: Bool?
}
