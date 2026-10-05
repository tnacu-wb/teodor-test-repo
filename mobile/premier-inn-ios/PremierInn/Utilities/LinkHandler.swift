//
//  LinkHandler.swift
//  PremierInn
//
//  Created by Freddie Parks on 22/08/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation

private enum URLConfig {
    enum keys {
        static let pageName = "pageName"
        static let searchTerm = "searchModel.searchTerm"
        static let placeId = "PLACEID"
        static let arrivalDay = "ARRdd"
        static let arrivalMonth = "ARRmm"
        static let arrivalYear = "ARRyyyy"
        static let nights = "NIGHTS"
        static let rooms = "ROOMS"
        static let adultsPrefix = "ADULT"
        static let childrenPrefix = "CHILD"
        static let cotPrefix = "COT"
        static let roomTypePrefix = "INTTYP"
        static let arrivalDate = "arrivalDate"
        static let resNo = "resNo"
        static let lastName = "lastName"
        static let cid = "CID"
        static let slug = "slug"
        static let promoType = "promoType"
        static let promoCode = "promoCode"
        static let appIncentivePromo = "appIncentive"
        static let freeBreakfastPromo = "PREBF"
    }

    enum paths {
        static let slug = "/hotels/"
        static let ciol = "/check-in-online.html"
        static let homepageGb = "/gb/en/home.html"
        static let homepageDe = "/de/de/home.html"
    }

    enum pageNames {
        static let checkInOnline = "check-in-online"
        static let hotelDetailsPage = "hotel-details-page"
        static let searchResultsPage = "search-results-page"
    }
}

enum PushNotificationConfig {
    enum Keys {
        static let pushType = "type"
        static let pushCID = "CID"
        static let deeplinkUrl = "url"
        static let title = "title"
        static let message = "message"
        static let button = "button"
        static let hotelCode = "hotelCode"
        static let hotelBrand = "hotelBrand"
        static let arrivalDate = "arrivalDate"
        static let nightsCount = "nights"
        static let locationTitle = "location_title"
        static let latitude = "lat"
        static let longitude = "long"
        static let reservationNumber = "reservationNumber"
        static let trackingCode = "trackingCode"
    }

    static func analyticsKey(for configKey: String) -> String? {
        switch configKey {
        case PushNotificationConfig.Keys.pushType:
            return PIAnalytics.Keys.pushType
        case PushNotificationConfig.Keys.pushCID:
            return PIAnalytics.Keys.sCampaign
        case PushNotificationConfig.Keys.title: // locationTitle was getting mixed for this key 🫠
            return nil
        case PushNotificationConfig.Keys.hotelCode:
            return PIAnalytics.Keys.hotelCode
        case PushNotificationConfig.Keys.hotelBrand:
            return PIAnalytics.Keys.hotelBrand
        case PushNotificationConfig.Keys.arrivalDate:
            return PIAnalytics.Keys.arrivalDate
        case PushNotificationConfig.Keys.nightsCount:
            return PIAnalytics.Keys.nightsCount
        case PushNotificationConfig.Keys.locationTitle:
            return PIAnalytics.Keys.locationTitle
        case PushNotificationConfig.Keys.latitude:
            return PIAnalytics.Keys.latitude
        case PushNotificationConfig.Keys.longitude:
            return PIAnalytics.Keys.longitude
        default:
            return nil
        }
    }
}

enum AppShortcut: Equatable {
    static func == (lhs: AppShortcut, rhs: AppShortcut) -> Bool {
        switch (lhs, rhs) {
        case (.hotelNearMe, .hotelNearMe):
            return true
        case (.search(let lhsCriteria, let lhsSearchTerm), .search(
            criteria: let rhsCriteria,
            searchTerm: let rhsSearchTerm
        )):
            return lhsCriteria == rhsCriteria && lhsSearchTerm == rhsSearchTerm
        case (
            .searchPlaceId(let lhsCriteria, let lhsSearchTerm, let lhsPlaceId),
            .searchPlaceId(criteria: let rhsCriteria, searchTerm: let rhsSearchTerm, placeId: let rhsPlaceId)
        ):
            return lhsCriteria == rhsCriteria && lhsSearchTerm == rhsSearchTerm && lhsPlaceId == rhsPlaceId
        case (
            .hotelDetails(let lhsHotelCode, let lhsHotelBrand, let lhsCriteria),
            .hotelDetails(hotelCode: let rhsHotelCode, hotelBrand: let rhsHotelBrand, criteria: let rhsCriteria)
        ):
            return lhsHotelCode == rhsHotelCode && lhsHotelBrand == rhsHotelBrand && lhsCriteria == rhsCriteria
        case (
            .hotelsNearLocation(let lhsSuggestion, let lhsCriteria),
            .hotelsNearLocation(suggestion: let rhsSuggestion, criteria: let rhsCriteria)
        ):
            return lhsSuggestion == rhsSuggestion && lhsCriteria == rhsCriteria
        case (.reservationDetails(let lhsIdentifier), .reservationDetails(identifier: let rhsIdentifier)):
            return lhsIdentifier == rhsIdentifier
        case (.landingScreen, .landingScreen):
            return true
        case (.employeeRates, .employeeRates):
            return true
        case (.banner, .banner):
            return true
        case (
            .ciol(let lhsArrivalDate, let lhsReservationNumber, let lhsLastName),
            .ciol(arrivalDate: let rhsArrivalDate, reservationNumber: let rhsReservationNumber, lastName: let rhsLastName)
        ):
            return lhsArrivalDate == rhsArrivalDate && lhsReservationNumber == rhsReservationNumber && lhsLastName ==
                rhsLastName
        case (.promotion, .promotion):
            return true
        default:
            return false
        }
    }

	case hotelNearMe
	case search(criteria: Criteria?, searchTerm: String?)
    /// Used for Deeplinking to search for hotels near a location by its PLACEID
    case searchPlaceId(criteria: Criteria?, searchTerm: String?, placeId: String?)
    case hotelDetails(hotelCode: String, hotelBrand: HotelBrand, criteria: Criteria?)
    case hotelDetailsBySlug(slug: String, criteria: Criteria?)
    case hotelsNearLocation(suggestion: PISuggestion, criteria: Criteria?)
	case reservationDetails(identifier: String)
	case landingScreen
    case employeeRates
    case banner(message: NotificationsMessage)
    case ciol(arrivalDate: String?, reservationNumber: String?, lastName: String?)
    case ciolPush(reservationNumber: String)
    case promotion
    case freeBreakfast
}

enum CustomURLIdentifier: String {
    case reservationDetails
    case employeeRates

    init?(url: URL) {
        guard let host = url.host else { return nil }
        self.init(rawValue: host)
    }
}

extension URLComponents {
    var pageName: String? { queryItems?.first(where: { $0.name == URLConfig.keys.pageName })?.value }

    // HDP
    var isHDP: Bool { pageName?.contains(URLConfig.pageNames.hotelDetailsPage) == true }

    var hotelCode: String? {
        guard let cid = queryItems?.first(where: { $0.name.lowercased() == "cid" || $0.name.lowercased() == "innid" })
            else { return nil }
        guard let hotelCode = cid.value?.replacingOccurrences(of: "GLBC_", with: "") else { return nil }
        guard let range = hotelCode.ranges(ofRegex: "[A-Z]{6}", caseSensitive: true).last else { return nil }

        return String(describing: (hotelCode as NSString).substring(with: range))
    }

    var slug: String? {
        let slug = slugQueryItem ?? path
        guard let range = slug.range(of: URLConfig.paths.slug) else { return nil }

        return String(slug[range.lowerBound...])
    }

    var slugQueryItem: String? { queryItems?.first(where: { $0.name == URLConfig.keys.slug })?.value }

    var criteria: Criteria? {
        var searchCriteria = Criteria()

        guard let day = queryItems?.first(where: { $0.name == URLConfig.keys.arrivalDay })?.value else { return nil }
        guard let month = queryItems?.first(where: { $0.name == URLConfig.keys.arrivalMonth })?.value else { return nil }
        guard let year = queryItems?.first(where: { $0.name == URLConfig.keys.arrivalYear })?.value else { return nil }

        var dateComponents = DateComponents()
        dateComponents.day = Int(day)
        dateComponents.month = Int(month)
        dateComponents.year = Int(year)

        guard let arrivalDate = Calendar.current.date(from: dateComponents) else { return nil }
        searchCriteria.arrivalDate = arrivalDate >= Date() ? arrivalDate : Date()

        guard let nightsString = queryItems?.first(where: { $0.name == URLConfig.keys.nights })?.value,
              let nights = Int(nightsString) else { return nil }
        searchCriteria.nights = nights

        guard let searchRooms = rooms, searchRooms.isNotEmpty else { return nil }
        searchCriteria.rooms = searchRooms

        return searchCriteria
    }

    var rooms: [Room]? {
        guard let roomsCount = queryItems?.first(where: { $0.name == URLConfig.keys.rooms })?.value else { return nil }
        guard let count = Int(roomsCount) else { return nil }

        var rooms: [Room] = []

        for index in 1...count {
            guard let adultsString = queryItems?.first(where: { $0.name == "\(URLConfig.keys.adultsPrefix)\(index)" })?
                  .value,
                  let adults = Int(adultsString) else { continue }
            guard let childrenString = queryItems?.first(where: { $0.name == "\(URLConfig.keys.childrenPrefix)\(index)" })?
                  .value, let children = Int(childrenString) else { continue }
            guard let cotString = queryItems?.first(where: { $0.name == "\(URLConfig.keys.cotPrefix)\(index)" })?.value
                else { continue }
            guard let roomTypeString = queryItems?.first(where: { $0.name == "\(URLConfig.keys.roomTypePrefix)\(index)" })?
                  .value, let roomType = RoomType(rawValue: roomTypeString) else { continue }

            let room = Room()
            room.adults = adults
            room.children = children
            room.cotRequired = cotString == "1" ? true : false
            room.type = roomType

            rooms.append(room)
        }

        return rooms
    }

    // SRP
    var isSRP: Bool { pageName?.contains(URLConfig.pageNames.searchResultsPage) == true }
    var searchTerm: String? { queryItems?.first(where: { $0.name == URLConfig.keys.searchTerm })?.value }
    var placeId: String? { queryItems?.first(where: { $0.name == URLConfig.keys.placeId })?.value }

    // CIOL
    var isCiol: Bool { path.contains(URLConfig.paths.ciol) || pageName?.contains(URLConfig.pageNames.checkInOnline) == true }
    var arrivalDate: String? { queryItems?.first(where: { $0.name == URLConfig.keys.arrivalDate })?.value }
    var resNo: String? { queryItems?.first(where: { $0.name == URLConfig.keys.resNo })?.value }
    var lastName: String? { queryItems?.first(where: { $0.name == URLConfig.keys.lastName })?.value }

    // Promotion
    var isPromotion: Bool {
        queryItems?.first(where: { $0.name == URLConfig.keys.promoType })?.value == URLConfig.keys.appIncentivePromo
    }

    var isHomepage: Bool {
        path == URLConfig.paths.homepageGb || path == URLConfig.paths.homepageDe
    }

    var isFreeBreakfast: Bool {
        queryItems?.first(where: { $0.name == URLConfig.keys.promoCode })?.value == URLConfig.keys.freeBreakfastPromo
    }

	var shortcutAction: AppShortcut? {
        if let searchTerm {
            guard SettingsManager.sharedInstance.featureDeeplinkSRP else { return nil }
            if let placeId = placeId {
                return .searchPlaceId(criteria: criteria, searchTerm: searchTerm, placeId: placeId)
            } else {
                return .search(criteria: criteria, searchTerm: searchTerm)
            }
		}

        if SettingsManager.sharedInstance.featureDeeplinkCIOL, isCiol, let arrivalDate = arrivalDate, let resNo = resNo {
            return .ciol(arrivalDate: arrivalDate, reservationNumber: resNo, lastName: lastName)
        }

		if let hotelCode = hotelCode {
            guard SettingsManager.sharedInstance.featureDeeplinkHDP else { return nil }
            // TODO: Hotel brand support in universal linking for Opera
            return .hotelDetails(hotelCode: hotelCode, hotelBrand: HotelBrand.premierInn, criteria: criteria)
        } else if let slug = slug {
            guard SettingsManager.sharedInstance.featureDeeplinkHDP else { return nil }
            return .hotelDetailsBySlug(slug: slug, criteria: criteria)
        }

        if isPromotion {
            return .promotion
        }

        if SettingsManager.sharedInstance.featureDeeplinkHomepage, isHomepage {
            return .landingScreen
        }

        if isFreeBreakfast {
            return .freeBreakfast
        }

		return nil
    }

    // campaign tracking
    var campaignIdentifier: String? {
        guard let result = queryItems?.first(where: { $0.name.lowercased() == "cid" }) else { return nil }

        return result.value
    }

    var mckv: String? {
        guard let result = queryItems?.first(where: { $0.name.lowercased() == "mckv" }) else { return nil }

        return result.value
    }

    var exactTargetRecipientIdentifier: String? {
        guard let result = queryItems?.first(where: { $0.name.lowercased() == "et_rid" }) else { return nil }

        return result.value
    }
}

extension Notification.Name {
    static let appShortcutDidChange = NSNotification.Name(rawValue: "AppShortcutDidChange")
}

class LinkHandler {
    static var adobeTrackingCode: String?

    static let sharedInstance = LinkHandler()

    private let notificationCenter: NotificationCenter

    init(notificationCenter: NotificationCenter = .default) {
        self.notificationCenter = notificationCenter
    }

    static func appShortcut(for url: URL, referrer: String? = nil) -> AppShortcut? {
        // ADBMobile.trackAdobeDeepLink(url)

        // Internal links (ie: app extension)
        if let shortcut = internalShortcut(url: url) {
            return shortcut
        }

        // Deep links (ie: www.premierinn.com)
        if let shortcut = deepLinkShortcut(url: url, referrer: referrer) {
            return shortcut
        }

        return nil
    }

    var activeAppShortcut: AppShortcut? {
        didSet {
            var userInfo: [AnyHashable: Any] = [:]

            if let trackingCode = LinkHandler.adobeTrackingCode {
                userInfo[PushNotificationConfig.Keys.trackingCode] = trackingCode
            }
            DispatchGroupManager.sharedInstance.appShortcutsDispatchGroup.notify(queue: .main) {
                self.notificationCenter.post(
                    name: .appShortcutDidChange,
                    object: self.activeAppShortcut,
                    userInfo: userInfo
                )
            }
        }
    }

    private static func internalShortcut(url: URL) -> AppShortcut? {
        switch CustomURLIdentifier(url: url) {
        case .reservationDetails?:
            let sharedUserDefaults = UserDefaults(suiteName: AppExtensionConstants.CurrentReservation.groupContainerName)

            if let dict = sharedUserDefaults?.dictionary(forKey: AppExtensionConstants.CurrentReservation.currentHotelKey),
               let identifier = dict[AppExtensionConstants.CurrentReservation.identifier] as? String {
                return AppShortcut.reservationDetails(identifier: identifier)
            }

			return AppShortcut.landingScreen

        case .employeeRates?:
            return SettingsManager.sharedInstance.allowEmployeeOfferFeature == true ? AppShortcut.employeeRates : nil
        case .none:
            return nil
        }
    }

    private static func deepLinkShortcut(url: URL, referrer: String?) -> AppShortcut? {
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else { return nil }

        AnalyticsManager.shared.campaignAttribution = CampaignAttribution(
            fullURLString: url.absoluteString,
            referrerURLString: referrer,
            cid: components.campaignIdentifier,
            mckv: components.mckv,
            etRid: components.exactTargetRecipientIdentifier
        )

		return components.shortcutAction
    }

    private static func setAdobeTrackingCode(_ code: String?) {
        if let code,
           code.isNotEmpty {
            adobeTrackingCode = code
        }
    }

    static func handleCustomPushPayload(userInfo: PIDictionary) -> AppShortcut {
        AnalyticsManager.shared.campaignAttribution = CampaignAttribution(
            fullURLString: nil,
            referrerURLString: nil,
            cid: userInfo[PushNotificationConfig.Keys.pushCID] as? String,
            mckv: nil,
            etRid: nil
        )

        setAdobeTrackingCode(userInfo[PushNotificationConfig.Keys.trackingCode] as? String)

        guard let value = userInfo[PushNotificationConfig.Keys.pushType] as? String,
              let type = PushNotificationContent(rawValue: value) else {
            return .landingScreen
        }

        switch type {
        case .banner:
            guard let title = userInfo[PushNotificationConfig.Keys.title] as? String,
                  let message = userInfo[PushNotificationConfig.Keys.message] as? String,
                  let button = userInfo[PushNotificationConfig.Keys.button] as? String else {
                return .landingScreen
            }

            let longMessage = LinkHandler.fullMessageFromParts(firstPart: message, userInfo: userInfo)

            // set id to nil so it doesn't affect the history of Firebase messages
            let notificationsMessage = NotificationsMessage(title: title, message: longMessage, ctaTitle: button, id: nil)

            return .banner(message: notificationsMessage)
        case .hotelDetails:
            guard let hotelCode = userInfo[PushNotificationConfig.Keys.hotelCode] as? String,
                  let hotelBrand = userInfo[PushNotificationConfig.Keys.hotelBrand] as? String,
                  let brand = HotelBrand(rawValue: hotelBrand) else {
                return .landingScreen
            }

            let criteria = Criteria(userInfo: userInfo)

            return .hotelDetails(hotelCode: hotelCode, hotelBrand: brand, criteria: criteria)
        case .locationSearch:
            guard let locationTitle = userInfo[PushNotificationConfig.Keys.locationTitle] as? String else {
                return .landingScreen
            }

            var suggestion: PISuggestion?
            if let latString = userInfo[PushNotificationConfig.Keys.latitude] as? String,
               let longString = userInfo[PushNotificationConfig.Keys.longitude] as? String,
               let lat = Double(latString),
               let long = Double(longString) {
                suggestion = PISuggestion(dictionary: ["name": locationTitle, "lat": lat, "long": long])
            } else if let topDestination = SettingsManager.sharedInstance.topDestinations
                .first(where: { $0.title == locationTitle }) as? PISuggestion {
                suggestion = topDestination
            } else {
                return .landingScreen
            }

            guard let suggestion = suggestion else { return .landingScreen}
            let criteria = Criteria(userInfo: userInfo)

            return .hotelsNearLocation(suggestion: suggestion, criteria: criteria)

        case .ciolReady, .leaveEasy:
            guard let reservationNumber = userInfo[PushNotificationConfig.Keys.reservationNumber] as? String else {
                return .landingScreen
            }

            return .ciolPush(reservationNumber: reservationNumber)
        }
    }

    private static func fullMessageFromParts(firstPart: String, userInfo: PIDictionary) -> String {
        // "message" is required and then we may have "message2", "message3", etc
        var longMessage = firstPart
        var index = 2
        while let messagePart = userInfo[PushNotificationConfig.Keys.message + String(index)] as? String {
            longMessage += "\n\n" + messagePart
            index += 1
        }

        return longMessage
    }

    static func pushNotificationAnalytics(for dictionary: PIDictionary) -> PIDictionary {
        PIDictionary(uniqueKeysWithValues: dictionary.compactMap { (key, value) in
            if let analyticsKey = PushNotificationConfig.analyticsKey(for: key) {
                return (analyticsKey, value)
            }

            return nil
        })
    }
}

private extension Criteria {
    init?(userInfo: PIDictionary) {
        guard let arrivalDateString = userInfo[PushNotificationConfig.Keys.arrivalDate] as? String,
              let nightsString = userInfo[PushNotificationConfig.Keys.nightsCount] as? String,
              let arrivalDate = arrivalDateString.dateValue,
              let nights = Int(nightsString) else {
            return nil
        }

        self = Criteria()
        self.arrivalDate = arrivalDate
        self.nights = nights
    }
}
