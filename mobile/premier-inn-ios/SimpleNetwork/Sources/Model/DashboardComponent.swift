//
//  DashboardComponent.swift
//  SimpleNetwork
//
//  Created by Georgios Aikaterinakis on 09/12/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
import CoreLocation

public enum DashboardComponentType: String, Codable {
    case upcomingBooking = "UPCOMING_BOOKING"
    case frequentlyBooked = "FREQUENT_BOOKINGS"
    case recentSearches = "RECENT_SEARCHES"
    case pastSearches = "PAST_SEARCHES"
}

public enum DashboardComponent: Decodable {
    case upcomingBooking(_ upcomingBooking: UpcomingBooking)
    case frequentlyBooked(_ frequentlyBooked: FrequentlyBooked)
    case recentSearches(_ recentSearches: RecentSearchesComponent)
    case pastSearches(_ pastSearches: PastSearchesComponent)

    private enum CodingKeys: String, CodingKey {
        case `type`
        case `content`
    }

    public init(from decoder: Decoder) throws {
        let map = try decoder.container(keyedBy: CodingKeys.self)
        let dataType = try map.decode(DashboardComponentType.self, forKey: .type)
        switch dataType {
        case .upcomingBooking:
            self = .upcomingBooking(try map.decode(UpcomingBooking.self, forKey: .content))
        case .frequentlyBooked:
            self = .frequentlyBooked(try map.decode(FrequentlyBooked.self, forKey: .content))
        case .recentSearches:
            self = .recentSearches(RecentSearchesComponent(type: dataType.rawValue))
        case .pastSearches:
            self = .pastSearches(PastSearchesComponent(type: dataType.rawValue))
        }
    }
}

public struct RecentSearchesComponent: Codable {
    public let type: String?
}

public struct PastSearchesComponent: Codable {
    public let type: String?
}

public struct UpcomingBooking {
    public let hotelImage: String?
    public let hotelName: String?
    public let confirmationNumber: String
    public let arrivalDateString: String
    public let departureDateString: String
    public let guests: Int
    public let map: CLLocationCoordinate2D
    public let rooms: [UpcomingBookingRoom]   // TODO: make Room Codable
    public let actions: [UpcomingBookingAction]?
    public let address: Address?
    public let checkedIn: Bool
    public let arrivalDate: Date?
    public let departureDate: Date?

    public init(
        hotelImage: String?,
        hotelName: String?,
        confirmationNumber: String,
        arrivalDateString: String,
        departureDateString: String,
        guests: Int,
        map: CLLocationCoordinate2D,
        rooms: [UpcomingBookingRoom],
        actions: [UpcomingBookingAction]?,
        address: Address?,
        checkedIn: Bool,
        arrivalDate: Date?,
        departureDate: Date?
    ) {
        self.hotelImage = hotelImage
        self.hotelName = hotelName
        self.confirmationNumber = confirmationNumber
        self.arrivalDateString = arrivalDateString
        self.departureDateString = departureDateString
        self.guests = guests
        self.map = map
        self.rooms = rooms
        self.actions = actions
        self.address = address
        self.checkedIn = checkedIn
        self.arrivalDate = arrivalDate
        self.departureDate = departureDate
    }
}

public struct UpcomingBookingRoom: Codable {
    public let type: String

    public init(type: String) {
        self.type = type
    }
}

public struct UpcomingBookingAction: Codable {
    public let type: String
    public let title: String

    public init(type: String, title: String) {
        self.type = type
        self.title = title
    }
}

extension UpcomingBooking: Decodable {
    enum CodingKeys: String, CodingKey {
        case hotelImage
        case hotelName
        case confirmationNumber
        case arrivalDate
        case departureDate
        case guests
        case map
        case rooms
        case actions
        case address
        case checkedIn
    }

    enum MapKeys: String, CodingKey {
        case latitude
        case longitude
    }

    public init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: CodingKeys.self)

        hotelImage = try? values.decode(String.self, forKey: .hotelImage)
        hotelName = try? values.decode(String.self, forKey: .hotelName)
        confirmationNumber = try values.decode(String.self, forKey: .confirmationNumber)
        arrivalDateString = try values.decode(String.self, forKey: .arrivalDate)
        departureDateString = try values.decode(String.self, forKey: .departureDate)
        guests = try values.decode(Int.self, forKey: .guests)

        arrivalDate = DateFormatter.parameterFormatter.date(from: arrivalDateString)
        departureDate = DateFormatter.parameterFormatter.date(from: departureDateString)

        let mapValues = try values.nestedContainer(keyedBy: MapKeys.self, forKey: .map)
        let latitude = try? mapValues.decode(CLLocationDegrees.self, forKey: .latitude)
        let longitude = try? mapValues.decode(CLLocationDegrees.self, forKey: .longitude)
        map = CLLocationCoordinate2D(latitude: latitude ?? 0, longitude: longitude ?? 0)

        rooms = try values.decode([UpcomingBookingRoom].self, forKey: .rooms)
        actions = try values.decode([UpcomingBookingAction].self, forKey: .actions)
        address = try? values.decode(Address.self, forKey: .address)
        checkedIn = try values.decode(Bool.self, forKey: .checkedIn)
    }
}

extension UpcomingBooking: Encodable {
    public func encode(to encoder: Encoder) throws {}
}
