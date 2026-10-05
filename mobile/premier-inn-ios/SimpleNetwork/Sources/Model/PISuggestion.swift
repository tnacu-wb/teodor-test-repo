//
//  PISuggestion.swift
//  PremierInn
//
//  Created by Marcello Mascia on 07/09/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import CoreLocation
import Foundation
import MapKit

private struct Geometry {
    let type: String?
    let coordinates: CLLocationCoordinate2D

    init?(dict: PIDictionary?) {
        guard let dict = dict else { return nil }
        guard let coordinatesArray = dict["coordinates"] as? [Double] else { return nil }
        guard let latitude = coordinatesArray.last else { return nil }
        guard let longitude = coordinatesArray.first else { return nil }

        self.type = dict["type"] as? String
        self.coordinates = CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
    }
}

public protocol Venue: MKAnnotation {}

public protocol Suggestion: Venue {
    var title: String? { get }
    var subtitle: String? { get }
    var iconName: String? { get }
    var rangeOfSearchTerm: NSRange? { get }
    var coordinate: CLLocationCoordinate2D { get }
    var isHotel: Bool { get }
    var identifier: String? { get }
    var type: SuggestionType { get }
    var brand: HotelBrand? { get }
}

extension Suggestion {
    var location: String? {
        switch type {
        case .location:
            return "\(coordinate.latitude),\(coordinate.longitude)"
        case .place:
            return identifier
        }
    }

    var locationFormat: String {
        switch type {
        case .location:
            return "LATLONG"
        case .place:
            return "PLACEID"
        }
    }
}

public enum SuggestionType: String {
    case location
    case place
}

public class PISuggestion: NSObject, Suggestion {
    public var aCustomTitle: String
    public var rangeOfSearchTerm: NSRange?
    public var isHotel: Bool
    public var identifier: String?
    public let type: SuggestionType
    public var brand: HotelBrand?

    private var aCustomSubtitle: String?
    private var aCustomCoordinate = kCLLocationCoordinate2DInvalid

    public var title: String? {
        aCustomTitle
    }
    public var subtitle: String? {
        aCustomSubtitle
    }
    public var iconName: String? {
        if isHotel {
            return brand == .hub ? "hub" : "hotel"
        }

        return "mapPin"
    }
    public var coordinate: CLLocationCoordinate2D {
        get {
            aCustomCoordinate
        }
        set {
            aCustomCoordinate = newValue
        }
    }

    public init(dictionary: PIDictionary) {
        self.aCustomTitle = dictionary["name"] as? String ?? ""
        self.isHotel = dictionary["isHotel"] as? Bool ?? false
        self.identifier = dictionary["hotelId"] as? String
        self.type = .location

        if let brand = dictionary["brand"] as? String {
            self.brand = HotelBrand(rawValue: brand)
        }

        if let latitude = dictionary["lat"] as? Double, let longitude = dictionary["long"] as? Double {
            self.aCustomCoordinate = CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
        } else if let location = dictionary["location"] as? PIDictionary, let latitude = location["latitude"] as? Double,
                  let longitude = location["longitude"] as? Double {
            self.aCustomCoordinate = CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
        }
    }

    public init?(propertyDictionary dictionary: PIDictionary) {
        guard let geometry = Geometry(dict: dictionary["geometry"] as? PIDictionary) else { return nil }
        guard let hotelCode = dictionary["code"] as? String else { return nil }

        self.aCustomTitle = dictionary["suggestion"] as? String ?? ""
        self.isHotel = true
        self.identifier = hotelCode
        self.aCustomCoordinate = geometry.coordinates
        self.type = .location

        if let brand = dictionary["brand"] as? String {
            self.brand = HotelBrand(rawValue: brand)
        }
    }

    public init?(placeDictionary dictionary: PIDictionary) {
        self.aCustomTitle = dictionary["suggestion"] as? String ?? ""
        self.isHotel = false
        self.identifier = dictionary["placeId"] as? String
        self.brand = nil
        self.aCustomCoordinate = kCLLocationCoordinate2DInvalid
        self.type = .place
    }

    public init(coordinate: CLLocationCoordinate2D) {
        self.aCustomTitle = NSLocalizedString("userCoordinateName", comment: "User's coordinate name")
        self.aCustomCoordinate = coordinate
        self.isHotel = false
        self.type = .location
    }

	public init(title: String) {
		self.aCustomTitle = title
		self.isHotel = false
		self.aCustomCoordinate = kCLLocationCoordinate2DInvalid
        self.type = .location
	}

	public static func == (lhs: PISuggestion, rhs: PISuggestion) -> Bool {
		lhs.aCustomTitle == rhs.aCustomTitle
	}
}

extension PISuggestion: Comparable {
	public static func < (lhs: PISuggestion, rhs: PISuggestion) -> Bool {
		guard let lhsRange = lhs.rangeOfSearchTerm, let rhsRange = rhs.rangeOfSearchTerm else {
			return lhs.aCustomTitle < rhs.aCustomTitle
		}

		guard lhsRange.location != rhsRange.location else {
			return lhs.aCustomTitle < rhs.aCustomTitle
		}

		return lhsRange.location < rhsRange.location
	}
}

public extension PISuggestion {
    convenience init(hotel: Hotel) {
        self.init(coordinate: hotel.coordinate)

        self.aCustomTitle = hotel.title ?? ""
        self.isHotel = true
        self.identifier = hotel.code
        self.brand = hotel.brand
    }
}
