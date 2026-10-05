//
//  RecentSearchesManager.swift
//  PremierInn
//
//  Created by Marcello Mascia on 20/06/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import CoreLocation
import Foundation
import MapKit
import SimpleNetwork

class RecentSearch: NSObject, Suggestion, MKAnnotation {
	let type: SuggestionType
    let title: String?
    let subtitle: String?
    let icon: String?
	let latitude: CLLocationDegrees?
	let longitude: CLLocationDegrees?
    let isHotel: Bool
    let identifier: String?
    let brand: HotelBrand?
    let criteria: Criteria?
    let searchDate: Date?

	var rangeOfSearchTerm: NSRange?

    var iconName: String? {
        if let icon = icon {
            return icon
        }

        return "mapPin"
    }

    var coordinate: CLLocationCoordinate2D {
        if let latitude = latitude, let longitude = longitude {
            return CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
        }

        return CLLocationCoordinate2D.zero
    }

    init?(dictionary: PIDictionary, criteria: Criteria?) {
        guard let title = dictionary["title"] as? String else { return nil }

        self.title = title
        self.subtitle = dictionary["subtitle"] as? String
        self.icon = dictionary["icon"] as? String
		self.latitude = dictionary["latitude"] as? Double
		self.longitude = dictionary["longitude"] as? Double
        self.isHotel = dictionary["isHotel"] as? Bool ?? false
        self.identifier = dictionary["identifier"] as? String
		self.type = {
			guard let rawValue = dictionary["type"] as? String else { return .location }

			return SuggestionType(rawValue: rawValue) ?? .location
		}()

        self.brand = {
            guard let brand = dictionary["brand"] as? String else { return nil }

            return HotelBrand(rawValue: brand)
        }()
        self.criteria = criteria
        self.searchDate = dictionary["searchDate"] as? Date
    }
}

class RecentSuggestionsManager {
    private let dataSource: UserDefaults
    private let recentSearchesUserDefaultsKey = "recentSearches"
    private var maximumSize: Int

    init(dataSource: UserDefaults, maximumSize size: Int = 10) {
        self.dataSource = dataSource
        self.maximumSize = size
    }

    var results: [Suggestion] {
        guard let dictionaries = dataSource.array(forKey: recentSearchesUserDefaultsKey) as? [PIDictionary]
            else { return [] }

        return dictionaries.compactMap { RecentSearch(dictionary: $0, criteria: $0["criteria"] as? Criteria) }.reversed()
    }

    func add(_ suggestion: Suggestion) {
        var object = PIDictionary()
        object["latitude"] = suggestion.coordinate.latitude as AnyObject?
        object["longitude"] = suggestion.coordinate.longitude as AnyObject?
        object["isHotel"] = suggestion.isHotel as AnyObject?
        object["identifier"] = suggestion.identifier as AnyObject?
        object["title"] = suggestion.title as AnyObject?
        object["icon"] = suggestion.iconName as AnyObject?
        object["subtitle"] = suggestion.subtitle as AnyObject?
		object["type"] = suggestion.type.rawValue
        object["brand"] = suggestion.brand?.rawValue ?? ""

        if let dictionaries = dataSource.array(forKey: recentSearchesUserDefaultsKey) as? [PIDictionary] {
            var tempArray = dictionaries

            if let existingIndex = tempArray.firstIndex(where: { $0["title"] as? String == suggestion.title }) {
                tempArray.remove(at: existingIndex)
            }

            tempArray.append(object)

            if tempArray.count > maximumSize {
                tempArray.removeFirst()
            }

            dataSource.set(tempArray, forKey: recentSearchesUserDefaultsKey)
        } else {
            dataSource.set([object], forKey: recentSearchesUserDefaultsKey)
        }
    }

    func reset(userDefaults: UserDefaults) {
        userDefaults.removeObject(forKey: recentSearchesUserDefaultsKey)
        userDefaults.synchronize()
    }
}

extension RecentSuggestionsManager {
    var recentSearchesShortcutItems: [UIApplicationShortcutItem] {
        let items = results.prefix(Constants.Config.maxRecentSuggestionsInsideShortcutItems)
            .compactMap { suggestion -> UIApplicationShortcutItem? in
            guard let title = suggestion.title else { return nil }

            var icon = UIApplicationShortcutIcon(type: .search)

            var userInfo: PIDictionary? = [
                "latitude": suggestion.coordinate.latitude,
                "longitude": suggestion.coordinate.longitude
            ]
            if suggestion.isHotel, let hotelCode = suggestion.identifier, let hotelBrand = suggestion.brand?.rawValue {
                userInfo = ["hotelCode": hotelCode, "hotelBrand": hotelBrand]
                icon = UIApplicationShortcutIcon(templateImageName: "hotel")
            }

            let shortcut = UIMutableApplicationShortcutItem(
                type: ShortcutIdentifier.hotelsNearLocation.type,
                localizedTitle: title,
                localizedSubtitle: suggestion.subtitle,
                icon: icon,
                userInfo: userInfo as? [String: NSSecureCoding]
            )

            return shortcut
        }

        return items
    }
}

class RecentSearchesManager {
    private let dataSource: UserDefaults
    private let recentSearchesUserDefaultsKey = "recentSearchesWithCriteria"
    private var maximumSize: Int

    var searchesComponentType: SearchesComponentType = .recent

    init(dataSource: UserDefaults, maximumSize size: Int = 10) {
        self.dataSource = dataSource
        self.maximumSize = size
    }

    var selectedResults: [RecentSearch] {
        searchesComponentType == .recent ? recentResults : allResults
    }

    var hasRecentSearches: Bool {
        recentResults.isNotEmpty
    }

    private var allResults: [RecentSearch] {
        guard let dictionaries = dataSource.array(forKey: recentSearchesUserDefaultsKey) as? [PIDictionary]
            else { return [] }

        return Array(dictionaries.compactMap { RecentSearch(
            dictionary: $0,
            criteria: criteria(with: $0["criteria"] as? PIDictionary)
        ) }.reversed().prefix(2))
    }

    private var recentResults: [RecentSearch] {
        // filter recent search data
        if let resultsArray = dataSource.array(forKey: recentSearchesUserDefaultsKey) as? [PIDictionary] {
            let filteredResults = resultsArray.filter { recentSearch in
                guard let criteria = criteria(with: recentSearch["criteria"] as? PIDictionary) else { return true }
                if let searchDate = recentSearch["searchDate"] as? Date {
                    if Date().timeIntervalSince(searchDate) > (secondsInDay * 7) { return false }
                }
                return criteria.arrivalDate < Date() && !criteria.arrivalDate.isToday ? false : true
            }

            return Array(filteredResults.compactMap { RecentSearch(
                dictionary: $0,
                criteria: criteria(with: $0["criteria"] as? PIDictionary)
            ) }.reversed().prefix(2))
        }

        return []
    }

    func recentSearch(at index: Int) -> RecentSearch? {
        selectedResults[safe: index]
    }

    func add(_ suggestion: Suggestion, criteria: Criteria) {
        var object = PIDictionary()
        object["latitude"] = suggestion.coordinate.latitude as AnyObject?
        object["longitude"] = suggestion.coordinate.longitude as AnyObject?
        object["isHotel"] = suggestion.isHotel as AnyObject?
        object["identifier"] = suggestion.identifier as AnyObject?
        object["title"] = suggestion.title as AnyObject?
        object["icon"] = suggestion.iconName as AnyObject?
        object["subtitle"] = suggestion.subtitle as AnyObject?
        object["type"] = suggestion.type.rawValue
        object["brand"] = suggestion.brand?.rawValue ?? ""
        object["criteria"] = criteria.dictionary
        object["searchDate"] = Date()

        if let dictionaries = dataSource.array(forKey: recentSearchesUserDefaultsKey) as? [PIDictionary] {
            var tempArray = dictionaries

            if let existingIndex = tempArray
               .firstIndex(where: {
                $0["title"] as? String == suggestion.title && self
                .criteria(with: $0["criteria"] as? PIDictionary) == criteria }) {
                tempArray.remove(at: existingIndex)
            }

            tempArray.append(object)

            // remove the maximumSize and filter the results for the required size?
            if tempArray.count > maximumSize {
                tempArray.removeFirst()
            }

            dataSource.set(tempArray, forKey: recentSearchesUserDefaultsKey)
        } else {
            dataSource.set([object], forKey: recentSearchesUserDefaultsKey)
        }
    }

    func remove(searchWith hotelName: String, criteria: Criteria) {
        if let dictionaries = dataSource.array(forKey: recentSearchesUserDefaultsKey) as? [PIDictionary] {
            var tempArray = dictionaries

            tempArray.removeAll(where: {
                guard let title = $0["title"] as? String,
                      let storedCriteria = self.criteria(with: $0["criteria"] as? PIDictionary) else { return false }
                return title == hotelName && storedCriteria == criteria
            })

            if tempArray.count == dictionaries.count, let firstRecent = tempArray.last,
               let storedCriteria = self.criteria(with: firstRecent["criteria"] as? PIDictionary),
               let date = firstRecent["searchDate"] as? Date {
                // 30 minutes?
                if storedCriteria == criteria && Date().timeIntervalSince(date) < (30 * 60) {
                    tempArray.removeLast()
                }
            }

            dataSource.set(tempArray, forKey: recentSearchesUserDefaultsKey)
        }
    }

    func reset(userDefaults: UserDefaults) {
        userDefaults.removeObject(forKey: recentSearchesUserDefaultsKey)
        userDefaults.synchronize()
    }

    private func criteria(with dictionary: PIDictionary?) -> Criteria? {
        guard let dictionary = dictionary else { return nil }

        var criteria = Criteria()

        criteria.arrivalDate = dictionary["arrivalDate"] as? Date ?? Date()
        criteria.nights = dictionary["nights"] as? Int ?? 1
        criteria.rooms = (dictionary["rooms"] as? [PIDictionary])?.map { Room(dictionary: $0) } ?? [Room()]

        return criteria
    }
}

extension Criteria {
    var dictionary: PIDictionary {
        var dictionary: PIDictionary = [:]

        dictionary["arrivalDate"] = arrivalDate
        dictionary["nights"] = nights
        dictionary["rooms"] = rooms.map { room in
            let roomDict: [String: Any] = [
                "type": room.type.rawValue,
                "children": room.children,
                "adults": room.adults,
                "cot": room.cotRequired
            ]
            return roomDict
        }

        return dictionary
    }
}
