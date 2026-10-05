//
//  Constants.swift
//  PremierInn
//
//  Created by Marcello Mascia on 20/06/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import CoreLocation
import UIKit
import SimpleCalendar
import SimpleNetwork

typealias PIDictionary = [String: Any]

extension Notification.Name {
	static let guestsDidChange = NSNotification.Name(rawValue: "GuestsDidChange")
    static let businessQuestionsDidChange = NSNotification.Name(rawValue: "BusinessQuestionsDidChange")
    static let creditCardDidChange = NSNotification.Name(rawValue: "CreditCardDidChange")
	static let webserviceConfigurationDidChange = Notification.Name(rawValue: "WebserviceConfigurationDidChange")
    static let multiVariantTestsDidLoad = Notification.Name(rawValue: "ABTestsDidLoad")
    static let staysDidChange = Notification.Name(rawValue: "StaysDidChange")
    static let staysWillChange = Notification.Name(rawValue: "staysWillChange")
    static let digitalKeyFlowDidClose = Notification.Name(rawValue: "digitalKeyFlowDidClose")
	static let refreshReservationsList = Notification.Name("refreshReservationsList")
}

enum Constants {
    /* 🛡--GDPR Stuff--🛡 */
    static let horribleGDPRBannerRowCellTag: String = "GDPRInformationBannerRowCell"
    static let disgustingGDPRBannerHeight: CGFloat = 44
    /*=-=-=-=-=-=-=-=-=-=*/

    enum ImportantAnnouncements {
        static let lastImportantAnnouncementMessageReadIdKey = "lastImportantAnnouncementMessageID"
    }
    static let homepageSubchannel = "apps"
    static let premierInnBaseURL = URL(string: PILocalizedString("premierInnLocalizedBaseURL"))
    static let imageBaseUrl: URL? = URL(string: "https://www.premierinn.com")
    static let searchResultsMapListDragToPopAmount: CGFloat = -100
	static let smallMapHeight: CGFloat = 110
    static let optionsViewHeight: CGFloat = 32
    static let cardCellSize = CGSize(width: 294, height: 174)
    static let regularCellHeight: CGFloat = 390
    static let fullyBookedCellHeight: CGFloat = 165
	static let mapOverviewReasonableDistanceMeters: Double = 12874
	static let mapOverviewHotelsToShowMaximum: Int = 12
    static let homePageImageTag = "homePageImageTag"
    static let hotelDetailUserActivityType = "com.whitbread.PremierInn.hotelDetails"
    static let faqUrl = URL(string: PILocalizedString("urlFAQ"))
    static let digitalKeyFaqUrl = URL(string: PILocalizedString("digitalKeyFaqUrl"))
    static let contactUsUrl = URL(string: PILocalizedString("urlContactUs"))
    static let germanFeedbackUrl = URL(string: "https://secure2.premierinn.com/de/de/feedback-formular/feedback.html")
    static let termsAndConditionsUrl = URL(string: PILocalizedString("urlT&C"))
    static let privacyPolicyUrl = URL(string: PILocalizedString("urlPrivacyPolicy"))
    static let allergyInformationUrl = URL(string: PILocalizedString("urlAllergy"))
    static let disabledAccessUrl = URL(string: PILocalizedString("urlDisableAccess"))
    static let restEasyStoriesUrl = URL(string: PILocalizedString("urlRestEasyStories"))
    static let creditCardImagesBaseUrl = "https://secure2.premierinn.com"
    static let creditCardImagesUrl = "https://secure2.premierinn.com/content/dam/global/booking/"
    static let threeDeeSecureCallbackURLString = "https://www.premierinn.com/booking/3ds/complete"
    static let creditCardImagesFormat = ".png"
    static let storedEmailAddressKey = "userEmailAddress"
	static let bookerSuffix = "Booker"
    static let hasAcceptedGDPRChanges = "ShowGDPRInterstitialOnLaunch"
    static let dismissedCoronavirusMessaging = "DismissedCoronavirusMessaging"
    static let hasMadeAppBooking = "hasMadeAppBooking"
    static let isAppIncentiveEnabled = "isAppIncentiveEnabled"
    static let hasSeenAppIncentive = "hasSeenAppIncentive"
    static let isHDPDiscountCodeSwitchEnabled = "isHDPDiscountCodeSwitchEnabled"
    static let pinningBypassKey = "noPin"
    static let calendarDebugKey = "calendarDebug"
    static let multivariantDebugKey = "multivariantDebug"
    static let cityTaxTestingKey = "cityTaxTesting"
	static let debugButtonOverlayKey = "debugButtonOverlay"
	static let webserviceConfigurationKey = "webserviceConfiguration"
    static let employeeRatesKey = "employeeRates"
    static let googleMapsURL = URL(string: "comgooglemaps://")!
    static let remoteSuggestionsMinCharacters = 3
    static let hotelDetailsAccessoryButtonsTopLimit: CGFloat = 22
    static let mapPadding: CGFloat = 40
    static let mapSinglePointRegionDistance: CLLocationDistance = 1000
    static let mapRectPointSideLength: Double = 0.1
    static let adobeExpectedMapVariant = "map"
	static let cardStyleMapInsets = UIEdgeInsets(top: 30, left: 30, bottom: 30, right: 30)
    static let appStoreUrl = URL(string: "itms-apps://itunes.apple.com/en/app/id602110169")
    static let aboutBusinessBookerUrl = URL(string: PILocalizedString("urlBB"))

    static let bbRulesHardCoded = Restrictions(
        maxRooms: 1,
        maxArrivalDateCount: 364,
        maxNights: 14,
        maxRoomsAmend: 1,
        channel: .BB
    )
    static let leisureRulesHardCoded = Restrictions(
        maxRooms: 4,
        maxArrivalDateCount: 364,
        maxNights: 9,
        maxRoomsAmend: 4,
        channel: .PI
    )
    static let leisureEmployeeOfferRulesHardCoded = Restrictions(
        maxRooms: 2,
        maxArrivalDateCount: 364,
        maxNights: 9,
        maxRoomsAmend: 2,
        channel: .EMPLOYEE
    )
    static let minRoomNumber = 1
    static let paypalUserCancelledErrorCode = 1
    static let calendarPrompt = "CalendarPrompt"
    static let mvtIdentifier = "apps_global"
    // TODO: AB Test to remove when completed - Sticky Extras CTA
    static let stickyExtrasCTAIdentifier = "sticky_extras_cta"
    static let urgencyMessagingIdentifier = "urgency_banner"

    // This is so that the backend service has enough time to create the user before we try login 🥱
    static let loginDelay = 4.0

    static let maxPhoneNumberRoomNumber = 9
    static let groupWebFormUrl = URL(string: PILocalizedString("urlGroupWebForm"))

    static let germanyTextInSuggestion = "germany"

    // Digital Key
    static let geofenceSize: Double = 200

    enum ZaploxConstants {
        static let brandName = "com.zaplox.zdk.premier_inn"
        static let url = "https://zap-zdk1.zaplox.com/"
    }

    static var navigationMaxTitleLength: Int {
        // Horrible hack, we know...
        // Returning arbitrary text length given screen width
        let screenWidth = UIScreen.main.bounds.size.width

        switch screenWidth {
        case 321...375: // 4.7-inch
            return 25

        case 376...CGFloat.greatestFiniteMagnitude: // 5.5-inch
            return 35

        default:
            return 15
        }
    }
    static let hotelDetailMaxTitleLength = 75
    static let numberOfRatesShowed = 3
    static let minimumCharactersForPostCodeLookup = 3
    static let remoteSearchDelay: TimeInterval = 0.7
    static let loadMoreHotelsTimeout: TimeInterval = 10
    static let searchRequestTimeout: TimeInterval = 6
    static let accessibleContactCenterEmailAddress = "pi.accessible@premierinn.com"

    enum PhoneNumbers {
        static let nationalRateFallBack = "+443330038101"
    }

    enum HTML {
        static let termsAndConditions = String(
            format: "<a href='%@' style='text-decoration: none; color: rgb(81,30,98)'>%@</a>",
            Constants.termsAndConditionsUrl?.absoluteString ?? "",
            PILocalizedString("termsAndConditionLinkTitle", comment: "Terms and condition link title")
        )
        static let privacyPolicy = String(
            format: "<a href='%@' style='text-decoration: none; color: rgb(81,30,98)'>%@</a>",
            Constants.privacyPolicyUrl?.absoluteString ?? "",
            PILocalizedString(
                "termsAndConditionPrivacyPolicyLinkTitle",
                comment: "Terms and condition privacy policy link title"
            )
        )
    }

    enum CMS {
		static let topDestinationsKey = "topDestinations"
		static let businessCardQuestionsKey = "businessCardQuestions"
        static let salutations = [
            PILocalizedString("Mr"),
            PILocalizedString("Mrs"),
            PILocalizedString("Ms"),
            PILocalizedString("Miss"),
            "Master",
            "Dr",
            "Lord",
            "Lady",
            "Sir",
            "Col",
            "Prof",
            "Rev"
        ]
        static let highlightedRoomInformationStrings = [
            PILocalizedString("wifiHighlitedTitle1", comment: "Room information wifi first highlighted title"),
            PILocalizedString("wifiHighlitedTitle2", comment: "Room information wifi second highlighted title")
        ]
        static let topDestinationsEN: [PIDictionary] = [
            ["name": "London", "lat": 51.512238, "long": -0.1059152],
            ["name": "Edinburgh", "lat": 55.950691, "long": -3.192125],
            ["name": "York", "lat": 53.96206, "long": -1.07888],
            ["name": "Leeds", "lat": 53.80106, "long": -1.54704],
            ["name": "Nottingham", "lat": 52.95446, "long": -1.15655],
            ["name": "Belfast", "lat": 54.5961604433425, "long": -5.93014955520629],
            ["name": "Newcastle", "lat": 54.97323, "long": -1.617623],
            ["name": "Glasgow", "lat": 55.86425, "long": -4.25048],
            ["name": "Manchester", "lat": 53.47901, "long": -2.24648]
        ]

        static let topDestinationsDE: [PIDictionary] = [
            ["name": "Berlin", "lat": 52.53806, "long": 13.36018],
            ["name": "Hamburg", "lat": 53.55968, "long": 10.01364],
            ["name": "Frankfurt", "lat": 50.11449, "long": 8.65784],
            ["name": "Köln", "lat": 50.93596, "long": 6.96663],
            ["name": "Dresden", "lat": 51.05182, "long": 13.74369],
            ["name": "Lübeck", "lat": 53.86510, "long": 10.68858],
            ["name": "Düsseldorf", "lat": 51.22299, "long": 6.80811],
            ["name": "Munich", "lat": 48.13754, "long": 11.58022],
            ["name": "Leipzig", "lat": 51.34124, "long": 12.37783],
            ["name": "Heidelberg", "lat": 49.40078, "long": 8.67263],
            ["name": "Stuttgart", "lat": 48.77626, "long": 9.18161],
            ["name": "Nürnberg", "lat": 49.45576, "long": 11.07667]
        ]
    }

    enum PaymentCardCVVLength {
        static let common = 3
        static let amex = 4
    }

    enum MealAllowance {
        static let min: Double = 1
        static let max: Double = 100
    }

    enum Regex {
        static let simplePrice = "[£$€]\\s*\\d+([.,]{1})?\\d+" + "|" + "\\d+([.,]{1})?\\d+\\s*[£$€]"
        static let passwordRequirements = "(?=^.{8,}$)(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s)[0-9a-zA-Z!@#$%^&*()]*$"
        // swiftlint:disable:next line_length
        static let ukPostcode = /^(([gG][iI][rR] {0,}0[aA]{2})|((([a-pr-uwyzA-PR-UWYZ][a-hk-yA-HK-Y]?[0-9][0-9]?)|(([a-pr-uwyzA-PR-UWYZ][0-9][a-hjkstuwA-HJKSTUW])|([a-pr-uwyzA-PR-UWYZ][a-hk-yA-HK-Y][0-9][abehmnprv-yABEHMNPRV-Y]))) {0,}[0-9][abd-hjlnp-uw-zABD-HJLNP-UW-Z]{2}))$/
        static let germanPostcode = /^\d+$/
    }

    enum Config {
		static let hotelRecentOpeningInterval: TimeInterval = 60 * 60 * 24 * 30 *
		    3 // seconds * minutes * hours * days * months
        static let flexAmendCutoffHour = 13
        static let hotelIsNewUnitType = NSCalendar.Unit.month
        static let hotelIsNewUnitValue = 6
        static let maxNumberOfAdults = 2
        static let maxNumberOfChildren = 2
        static let maxNumberOfInfants = 1
        static let defaultSearchCoordinate = CLLocationCoordinate2D(
            latitude: 51.512238,
            longitude: -0.1059152
        ) // Central London
        static let maxRecentSuggestionsInsideShortcutItems = 3
        static let paymentRetriesBeforeAllowQuit = 2
    }

    // static let workaroundCountryCodes = ["AE": "UAE"]

    enum PICalendarSettings {
        public static let fonts = SimpleCalendarSettings.Fonts(
            weekdays: .BodySmall_Semibold(),
            days: .BodySmall(),
            footer: .BodySmall(),
            month: .Heading2_Semibold()
        )

        static let colors = SimpleCalendarSettings.Colors(
            weekday: .TintD1,
            weekend: .sea,
            selectable: .TintD1,
            highlighted: .white,
            highlightedBackground: .greyPurple,
            selected: .BasePurple,
            notSelectable: .TintL2,
            footer: .slateGrey,
            month: .TintD1,
            today: .TintL2
        )
    }

    static let pkPassTypeIdentifier = "pass.com.whitbread.booking"
    static let shownBBIntroKey = "didShowBBIntro"
    static let shownBartIsDownKey = "didShowBartIsDown"

    enum DinnerAllowanceLocations {
        static let greaterLondonCounty = "greater-london".lowercased()
        static let irelandCountry = "republic-of-ireland".lowercased()
        static let irelandCountryDE = "republik-irland".lowercased()
        static let germanyCountry = "germany".lowercased()
        static let germanyCountryDE = "deutschland".lowercased()
    }

    enum Dashboard {
        static let defaultRefreshInterval: TimeInterval = 1800
    }

    static var osVersionSupportsApplePay: Bool {
        guard #available(iOS 16, *) else { return false }
        return true
    }

    enum CardTypeConfigMapper: String {
        case AC
        case AM
        case AT
        case BD
        case DI
        case EL
        case MA
        case DL
        case MD
        case VI
        case AX
        case DN
        case MC
        case VS
        case PE
        case PI

        var cardImage: String {
            switch self {
            case .AC, .MC:
                return "Mastercard.jpg"
            case .AM, .AX:
                return "AX.jpg"
            case .AT, .PI:
                return "Business_Account.jpg"
            case .BD, .PE:
                return "Business_Account_Euro.jpg"
            case .DI, .DN:
                return "dinersclub.jpg"
            case .EL:
                return "Electron_white_v.jpg"
            case .MA:
                return "maestro.jpg"
            case .DL:
                return "Visa_Debit.jpg"
            case .MD:
                return "MD.jpg"
            case .VI, .VS:
                return "VC.jpg"
            }
        }

        var cardName: String {
            switch self {
            case .AC, .MC:
                return "Mastercard Credit"
            case .AM, .AX:
                return "American Express"
            case .AT, .PI:
                return "Business Account"
            case .BD, .PE:
                return "InnBusiness Pay"
            case .DI, .DN:
                return "Diners Club"
            case .EL:
                return "Electron"
            case .MA:
                return "Maestro"
            case .DL:
                return "Visa Debit"
            case .MD:
                return "Mastercard Debit"
            case .VI, .VS:
                return "Visa Credit"
            }
        }

        var completeUrl: URL? {
            URL(string: Constants.creditCardImagesUrl + cardImage)
        }
    }

    enum RoomClass {
        static let standardRoom = "ST"
        static let standardFamilyRoom = "SF"
        static let standardRoomWithAView = "SV"
        static let standardRoomWithSeaView = "SS"
        static let standardRoomWithCityView = "SC"
        static let premierPlusRoom = "PP"
        static let premierPlusSuite = "SU"
        static let premierPlusRoomWithAView = "PV"
        static let premierPlusRoomWithSeaView = "PS"
        static let premierPlusRoomWithCityView = "PC"
        static let standardExtraRoom = "SE"
        static let biggerRoom = "BG"
        static let pseudoRoom = "PSE"
    }
}

enum ImageTag: String {
    case hubStandardRoom = "hub-standard-room"
    case hubBiggerRoom = "hub-bigger-room"
    case hubAccessibleRoom = "hub-accessible-room"
}

enum PIError {
    static let domain = "com.premierinn"

    enum Code {
        static let locationUnavailable = 1000

        static let maxRangeReached = 1100
        static let maxRangeUnavailable = 1101
        static let minRangeReached = 1102
        static let minRangeUnavailable = 1103
        static let maxNightsRangeReached = 1104
        static let moreThanOneYear = 1105
        static let maxNumberOfAdultsRangeReached = 1106

        static let maxRoomsNumberReached = 1200
        static let minRoomsNumberReached = 1201

        static let configDataCouldNotBeRead = 1300

        static let unexpectedResponse = 1400
    }
}
