//
//  Hotel.swift
//  PremierInn
//
//  Created by Freddie Parks on 25/07/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Foundation
import Contacts
import CoreLocation
import MapKit

public struct CNPAuthorisation: Codable {
    public let dinnerAvailable: Bool?
    public let dinnerAvailableNonBa: Bool?
    public let alcoholAllowed: Bool?
    public let carParkAvailable: Bool?
    public let otherChargesAvailable: Bool?
    public let wiFiAvailable: Bool?
    // If we decide to use breakfastsAvailable we will need to implement the relevant protocols on UpsellItem
    // public let breakfastsAvailable: [UpsellItem]?
}

public protocol FoodSectionContent {
	var title: String { get }
	var body: String { get }
}

public enum HotelBrand: String, Codable, CaseIterable {
	case premierInn = "PI"
    case premierInnGermany = "PID"
	case hub = "HUB"
    case zip = "ZIP"

    var categoryKeys: String {
        switch self {
        case .premierInn:
            return "hotelInfo.disclaimer.PI"
        case .premierInnGermany:
            return "hotelInfo.disclaimer.PID"
        case .hub:
            return "hotelInfo.disclaimer.HUB"
        case .zip:
            return "hotelInfo.disclaimer.ZIP"
        }
    }
}

private enum HotelError: LocalizedError {
    case missingHotelCode

	var errorDescription: String? { String(describing: self)	}
}

public struct HotelImage {
    public let image: URL
    var tags: [String] = []
}

public enum PaymentProvider: String {
    case cccp = "3CP"
}

public class Hotel: NSObject, MKAnnotation {
    // MARK: - Properties

    // Immutable properties
    public let code: String
    public let coordinate: CLLocationCoordinate2D
    public let name: String
	public let brand: HotelBrand
    public let images: [HotelImage]
    public let address: Address?
    public let county: String?
    public var tripAdvisorDetails: TripAdvisorDetails?
    public let messagingFlag: MessagingFlag?
    public let webURL: URL?
    public let hotelRoomConfiguration: HotelRoomConfiguration?
    public let offersPremierPlus: Bool
    public let offersBusinessRooms: Bool
    public let announcement: Announcement?
    public var pmsSource: BookingSourcePMS = .bart
    public let lowestRoomRateCost: Cost?
    public var cityTaxForLeisure: Bool?
    public var cityTaxForBusiness: Bool?
    public var disclaimer: String?
    // Variables properties
    public private(set) var rates: [Rate]
    public private(set) var notes: [Note]?
    public private(set) var hotelDescription: String?
    public private(set) var directions: String?
    public private(set) var acceptedCreditCards: [CardType]?
    public private(set) var parkingDescription: String?
    public private(set) var restaurant: Restaurant?
    public private(set) var facilities: [Facility]?
    public private(set) var prepaymentAllowed: Bool
    public private(set) var available: Bool
    public private(set) var limitedAvailability: Bool
    public private(set) var phoneNumber: String?
    public private(set) var nationalPhoneNumber: String?
    public private(set) var email: String?
    public private(set) var paymentProvider: PaymentProvider?

    // Computed properties
    public var title: String? { name }
    public var distance: Double
    public var parkings: [ParkingType] {
        guard let facilities = facilities else { return [] }

        return facilities.compactMap { ParkingType(rawValue: $0.code) }
    }

    public var isPremierPlus: Bool {
        guard let facilities = facilities else { return false }

        return facilities.contains(where: { $0.code == "PRR"})
    }

    public var cheapestRate: Rate? {
        guard !rates.isEmpty else { return nil }

        let sortedRates = rates.sorted(by: { $0.totalCost < $1.totalCost})

        return sortedRates.first
    }

    public var distanceUnit: LengthFormatter.Unit {
        LanguageManager.supportedLanguage == .english ? .mile : .kilometer
    }

    public func getDistanceString(formatter: LengthFormatter) -> String? {
        distance > 0
        ? formatter.string(fromValue: distance, unit: distanceUnit)
        : nil
    }

    // Tagged Images
    public var primaryImages: [URL] {
        images.compactMap { $0.image }
    }
    public var roomImages: [URL] {
        images(hotelImages: images, withTags: Constants.ImageTags.room)
    }
    public var primaryImagesWithTags: [(url: URL, tags: [String])] {
        images.compactMap { ($0.image, $0.tags) }
    }
    public var restaurantImages: [URL] {
        images(hotelImages: images, withTags: Constants.ImageTags.restaurant)
    }
    public var parkingImages: [URL] {
        images(hotelImages: images, withTags: Constants.ImageTags.parking)
    }
    public var extrasImages: [URL] {
        let sortedImages = images.sorted(by: {
            $0.tags.contains("coffee-shop") == true && $1.tags.contains("coffee-shop") == false
        }).sorted(by: {
            $0.tags.contains("breakfast") == true && $1.tags.contains("breakfast") == false
        })
        return images(hotelImages: sortedImages, withTags: Constants.ImageTags.breakfast)
    }

    public var accessibleLoweredBathroomImages: [URL] {
        let filteredImages = images
            .filter({ $0.tags.contains("accessible-lowered-bathroom") || $0.tags.contains("accessible-wet-room") })

        let sortedImages = filteredImages.sorted(by: {
            $0.tags.contains("accessible-lowered-bathroom") == true && $1.tags
                .contains("accessible-lowered-bathroom") == false
        })
        return images(hotelImages: sortedImages, withTags: Constants.ImageTags.accessibleBathroom)
    }

    public var promotionRate: Rate? {
        rates.first(where: { $0.promotionCode?.isEmpty == false })
    }

    public var cnpAuthorisation: CNPAuthorisation?

    public var ancillaryCloseout: [AncillaryCloseOutItem]?

    // MARK: - Init

    public init(dictionary: PIDictionary) throws {
        let infoDict: PIDictionary
        let availabilityDict: PIDictionary?
        let hotelCode = Hotel.hotelCode(dictionary: dictionary)

        if let dict = dictionary["hotelInfo"] as? PIDictionary {
            infoDict = dict
            availabilityDict = dictionary
        } else {
            infoDict = dictionary
            availabilityDict = nil
        }

        guard let code = hotelCode else { throw HotelError.missingHotelCode }

        self.code = code
        self.brand = Hotel.brand(rawString: Hotel.hotelBrandString(dictionary: dictionary))
        self.address = try? Address(dictionary: infoDict["address"] as? PIDictionary)
        self.county = infoDict["county"] as? String ?? ""
        self.name = infoDict["name"] as? String ?? ""
        self.coordinate = Hotel.coordinate(dictionary: infoDict["map"] as? PIDictionary)
        self.webURL = Hotel.webURL(rawString: (infoDict["links"] as? PIDictionary)?["detailsPage"] as? String)
        self.images = Hotel.images(dictionaries: infoDict["images"] as? [PIDictionary])

        if let tripAdvisorDetails = infoDict["tripAdvisorDetails"] as? PIDictionary {
            self.tripAdvisorDetails = TripAdvisorDetails(dictionary: tripAdvisorDetails)
        }

        self.messagingFlag = MessagingFlag(dictionary: infoDict["messagingFlag"] as? PIDictionary)
        self.facilities = Hotel.facilities(dictionaries: infoDict["facilities"] as? [PIDictionary])
        self.offersPremierPlus = (self.facilities ?? []).contains(where: { $0.code == "PRR" })
        self.offersBusinessRooms = (self.facilities ?? []).contains(where: { $0.code == "STE" })
        self.lowestRoomRateCost = dictionary["lowestRoomRate"] as? Cost
        self.pmsSource = dictionary["pmsSource"] as? BookingSourcePMS ?? .bart

        // Variable properties
        self.hotelDescription = (infoDict["hotelDescription"] as? String)?.htmlStripped()
        self.directions = (infoDict["hotelDirections"] as? String)?.htmlStripped()
        self.acceptedCreditCards = Hotel
            .acceptedCreditCards(dictionaries: infoDict["acceptedCreditCards"] as? [PIDictionary])
        self.parkingDescription = (infoDict["parkingDescription"] as? String)?.htmlStripped()
        self.restaurant = try? Restaurant(dictionary: infoDict["restaurant"] as? PIDictionary)
        self.prepaymentAllowed = infoDict["prepaymentAllowed"] as? Bool ?? false

        // Availability stuff
        self.rates = Hotel.rates(dictionaries: availabilityDict?["ratePlans"] as? [PIDictionary])
        self
            .limitedAvailability = availabilityDict?["limitedAvailability"] as? Bool ??
            (infoDict["limitedAvailability"] as? Bool ?? false)
        self.notes = Hotel.notes(dictionaries: infoDict["notes"] as? [PIDictionary])
        self.distance = availabilityDict?["distance"] as? Double ?? 0
        self.available = availabilityDict?["available"] as? Bool ?? false
        self.disclaimer = Hotel.disclaimerForBrand(input: infoDict["disclaimer"] as? PIDictionary, brand: self.brand)

        if let contactDetails = dictionary["contactDetails"] as? PIDictionary {
            self.phoneNumber = contactDetails["phone"] as? String
            self.nationalPhoneNumber = contactDetails["hotelNationalPhone"] as? String
            self.email = contactDetails["email"] as? String
        }

        self.hotelRoomConfiguration = Hotel.hotelRoomConfiguration(with: infoDict["hotelRoomConfiguration"] as? PIDictionary)
        self.announcement = Hotel.announcement(with: infoDict["announcement"] as? PIDictionary)
        self.ancillaryCloseout = Hotel.ancillaryCloseOutItems(dictionary: infoDict["ancillaryCloseout"] as? PIDictionary)
    }

    public func getLowestCost() -> Cost? {
        var cost: Cost?
        // lowestRoomRateCost only comes from graphQL
        if let lowestCost = lowestRoomRateCost {
            cost = lowestCost
        } else if let lowestCost = cheapestRate?.totalCost { // for REST calls
            cost = lowestCost
        }
        return cost
    }

    // MARK: - Update Methods

    public func update(with info: PIDictionary?) {
        guard let info = info else { return }

        hotelDescription = (info["hotelDescription"] as? String)?.htmlStripped()
        directions = (info["hotelDirections"] as? String)?.htmlStripped()
        acceptedCreditCards = Hotel.acceptedCreditCards(dictionaries: info["acceptedCreditCards"] as? [PIDictionary])
        parkingDescription = info["parkingDescription"] as? String
        restaurant = try? Restaurant(dictionary: info["restaurant"] as? PIDictionary)
        facilities = Hotel.facilities(dictionaries: info["facilities"] as? [PIDictionary])
    }

    public func update(notes: [Note]?) {
        self.notes = notes
    }

    public func update(rates: [Rate]) {
        self.rates = rates.filter { $0.classification != nil }
    }

    public func update(available: Bool) {
        self.available = available
    }

    public func update(limitedAvailability: Bool) {
        self.limitedAvailability = limitedAvailability
    }

    public func update(prepaymentAllowed: Bool) {
        self.prepaymentAllowed = prepaymentAllowed
    }

    public func update(cityTaxResponse: CityTaxResponse?) {
        guard let response = cityTaxResponse else { return }
        cityTaxForLeisure = response.cityTaxForLeisure
        cityTaxForBusiness = response.cityTaxForBusiness
    }

    public func update(paymentProvider: PaymentProvider?) {
        self.paymentProvider = paymentProvider
    }

    // MARK: - Helpers Methods

    public func rate(for classification: String) -> Rate? {
        rates.first { $0.classification == classification }
    }

	private class func webURL(rawString: String?) -> URL? {
		guard let hotelDetailsPage = rawString else { return nil }

		return URL(string: Constants.hotelResourcesBaseAddress + hotelDetailsPage + ".html")
	}

    class func disclaimerForBrand(input: PIDictionary?, brand: HotelBrand?) -> String? {
        guard let input, let brand else { return nil }
        return input[brand.categoryKeys] as? String
    }

	private func images(hotelImages: [HotelImage], withTags tags: [String]) -> [URL] {
        hotelImages.compactMap {
            for tag in tags where $0.tags.contains(tag) {
                return $0.image
            }
            return nil
        }
    }

    public var foodOptionContentSections: [FoodSectionContent]? {
        if let rate = rates.first, let foodUpsells = rate.foodUpsells, !foodUpsells.isEmpty {
            return foodUpsells
        } else if let restaurant = restaurant, let menus = restaurant.menus {
            return menus
        }
        return nil
    }
}

// MARK: - Utilities

extension Hotel {
    class func coordinate(dictionary: PIDictionary?) -> CLLocationCoordinate2D {
        guard let dict = dictionary, let latitude = dict["latitude"] as? CLLocationDegrees,
              let longitude = dict["longitude"] as? CLLocationDegrees else { return CLLocationCoordinate2D(
                  latitude: 0,
                  longitude: 0
              ) }

        return CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
    }

    class func brand(rawString: String?) -> HotelBrand {
        guard let rawString = rawString else { return .premierInn }

        return HotelBrand(rawValue: rawString) ?? .premierInn
    }

    class func images(dictionaries: [PIDictionary]?) -> [HotelImage] {
        guard let imageDictionaries = dictionaries else { return [] }

        return imageDictionaries.compactMap { dict in
            guard let fileRef = dict["fileReference"] as? String else { return nil }

            // Handle both full URLs and relative paths from backend
            let imageURL: URL
            if fileRef.hasPrefix("http://") || fileRef.hasPrefix("https://") {
                // Backend returned a full URL - ensure it's HTTPS
                guard let url = URL.secureURL(from: fileRef) else { return nil }
                imageURL = url
            } else {
                // Backend returned a relative path - append to base URL
                imageURL = Constants.imagesBaseURL.appendingPathComponent(fileRef)
            }

            if let tags = dict["tags"] as? [String] {
                return HotelImage(image: imageURL, tags: tags)
            } else {
                return HotelImage(image: imageURL, tags: [])
            }
        }
    }

    class func facilities(dictionaries: [PIDictionary]?) -> [Facility]? {
        guard let facilitiesDict = dictionaries else { return nil }

        return facilitiesDict.compactMap { Facility(dictionary: $0) }
    }

    class func rates(dictionaries: [PIDictionary]?) -> [Rate] {
        guard let rateDictionaries = dictionaries else { return [] }

        return rateDictionaries.map { Rate(dictionary: $0) }.filter { $0.classification != nil }
    }

    class func notes(dictionaries: [PIDictionary]?) -> [Note]? {
        guard let notesDict = dictionaries else { return nil }

        return notesDict.compactMap { Note(dictionary: $0) }.sorted(by: { $0.priority < $1.priority})
    }

    class func acceptedCreditCards(dictionaries: [PIDictionary]?) -> [CardType]? {
        guard let dictionaries = dictionaries else { return nil }

        return dictionaries.compactMap { CardType(dictionary: $0) }
    }

    class func hotelCode(dictionary: PIDictionary) -> String? {
        if let value: String = dictionary.value(forKeys: ["hotelCode", "code"]) {
            return value
        }

        if let dict = dictionary["hotelInfo"] as? PIDictionary, let value = dict["code"] as? String {
            return value
        }

        return nil
    }

    class func hotelBrandString(dictionary: PIDictionary) -> String? {
        if let value: String = dictionary.value(forKeys: ["hotelBrand", "brand"]) {
            return value
        }

        if let dict = dictionary["hotelInfo"] as? PIDictionary, let value = dict["brand"] as? String {
            return value
        }

        return nil
    }
}

extension Hotel {
    private var favouritesManager: SimpleStorageManager<Favourite> {
        SimpleStorageManager<Favourite>(dataSource: UserDefaults.standard)
    }

    private var favouriteObject: Favourite? {
        try? Favourite(dictionary: ["identifier": code])
    }

    public func toggleFavourite() -> Bool {
        guard let favouriteObject = favouriteObject else { return false }

        try? favouritesManager.toggle(favouriteObject)

        return isFavourite
    }

    public var isFavourite: Bool {
        guard let favouriteObject = favouriteObject else { return false }

        return favouritesManager.items.contains(favouriteObject)
    }

    public var placeMark: MKPlacemark? {
        guard let addressDictionary = address?.postalAddressDictionary else { return nil }

        return MKPlacemark(coordinate: coordinate, addressDictionary: addressDictionary)
    }
}

public extension Hotel {
    func indexOfRoomImage(with tag: String) -> Int? {
        let roomHotelImages: [HotelImage] = images.compactMap {
            for tag in Constants.ImageTags.room where $0.tags.contains(tag) {
                return $0
            }
            return nil
        }

        for (index, image) in roomHotelImages.enumerated() where image.tags.contains(tag) {
            return index
        }

        return nil
    }
}
