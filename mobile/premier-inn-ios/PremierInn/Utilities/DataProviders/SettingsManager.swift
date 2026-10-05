//
//  SettingsManager.swift
//  PremierInn
//
//  Created by Marcello Mascia on 12/05/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit
import Foundation
import MapKit
import SimpleNetwork

struct Version {
    let major: Int
    let minor: Int
    let patch: Int
}

struct QRKioskHotels {
    let hotelCode: String
}

struct SNPWifiHotels {
    let hotelCode: String
}

extension Version {
    init(string: String) {
        let components = string.split(separator: ".")

        self.major = components.indices.contains(0) ? Int(components[0]) ?? 1 : 1
        self.minor = components.indices.contains(1) ? Int(components[1]) ?? 0 : 0
        self.patch = components.indices.contains(2) ? Int(components[2]) ?? 0 : 0
    }
}

extension Version: Comparable {
    static func < (lhs: Version, rhs: Version) -> Bool {
        lhs.major > rhs
            .major ? false :
            (lhs.major < rhs
            .major ? true :
            (lhs.minor > rhs.minor ? false : (lhs.minor < rhs.minor ? true : (lhs.patch < rhs.patch ? true : false))))
    }
}

extension String {
    static let autoLoginKey = "autoLogin"
}

struct RateContent: Codable {
    let classification: String
    let name: String?
    let description: String?
    let bookingTermsMessage: String?
}

struct NotificationsMessage {
    let title: String
    let message: String
    let ctaTitle: String
    let id: Int?
}

struct MyAccountLink {
    let ctaTitle: String
    let url: URL
    let isActive: Bool

    init?(dictionary: PIDictionary) {
        guard let ctaTitle = dictionary["ctaTitle"] as? String else { return nil }
        guard let urlString = dictionary["url"] as? String, let url = URL(string: urlString) else { return nil }
        guard let isActive = dictionary["isActive"] as? Bool else { return nil }

        self.ctaTitle = ctaTitle
        self.url = url
        self.isActive = isActive
    }
}

struct ConfirmationPollingMessagesConfig {
    let messages: [ConfirmationPollingMessage]

    var maxDurationOfPolling: Int {
        messages.reduce(0, { $0 + $1.seconds })
    }
}

struct ConfirmationPollingMessage {
    let order: Int
    let seconds: Int
    let message: String

    init?(dictionary: PIDictionary) {
        guard let order = dictionary["order"] as? Int else { return nil }
        guard let seconds = dictionary["seconds"] as? Int else { return nil }
        guard let message = dictionary["message"] as? String else { return nil }

        self.order = order
        self.seconds = seconds
        self.message = message
    }
}

struct SiteWidePromotionContent {
    let title: String?
    let subtitle: String?
    let promotionCode: String?
	let urlString: String?

	public init(
	    title: String?,
	    subtitle: String?,
	    promotionCode: String?,
	    urlString: String?
	) {
		self.title = title
		self.subtitle = subtitle
		self.promotionCode = promotionCode
		self.urlString = urlString
	}
}

protocol PIRemoteConfig {
    static var sharedInstance: PIRemoteConfig { get }

    var cmsStrings: [String: String] { get }
    var rateContent: [PIDictionary] { get }
    var forceUpdateTitle: String { get }
    var forceUpdateDescription: String { get }
    var appNeedsToBeUpdated: Bool { get }
    var forceTestPushValue: String? { get }
    var notificationsMessage: NotificationsMessage? { get }
    var versioningDict: PIDictionary? { get }
    var notificationsMessageFeature: Bool? { get }
    var myAccountLinks: [PIDictionary] { get }
    var coronavirusMessagingSRPAndHDP: Bool? { get }
    var coronavirusMessagingHomepage: Bool? { get }
    var coronavirusBannerMessage: String { get }
    var passwordRegexs: [String: String] { get }
    var twinRoomInfo: [PIDictionary] { get }
    var bartDown: Bool? { get }
    var shouldOperaRedirectToWeb: Bool? { get }
    var shouldOperaShowFallBackForBB: Bool? { get }
    var orderAndPay: Bool? { get }
    var getKey: Bool? { get }
    var shouldShowAmendBanner: Bool? { get }
    var employeeQuestionsOperaFeature: Bool? { get }
    var allowEmployeeOfferFeature: Bool? { get }
    var featureShouldShowDashboardEcommerceContent: Bool? { get }

    // endpoints migration
    var featureUseOperaRestProdEndpoint: Bool? { get }
    var featureUseOperaRestLowerEnvironmentEndpoint: Bool? { get }
    var featureUseSnowdropOperaLowerEnvEndpoint: Bool? { get }
    var featureUseSnowdropOperaProdEndpoint: Bool? { get }

    // confirmation/basket polling
    var confirmationPollingMessagesConfig: PIDictionary? { get }
    var confirmationPollingDelay: Int { get }
    var confirmationPollingInterval: Int { get }

    // PayPal
    var featurePayPal: Bool? { get }
    var featureUsePaypalInitiatePayment: Bool? { get }

    // ApplePay
    var featureApplePay: Bool? { get }

    // QR Kiosk Hotel
    var kioskHotels: [PIDictionary]? { get }

    var snpWifiHotels: [PIDictionary]? { get }

    // Deeplinking
    var featureDeeplinkSRP: Bool? { get }
    var featureDeeplinkHDP: Bool? { get }
    var featureDeeplinkHomepage: Bool? { get }
    /// This will control only the redirection to find my booking upon tapping on a ciol deeeplink
    var featureDeeplinkCIOL: Bool? { get }

    /// CIOL - this feature flag will enable the posibility to start check in
    /// Controls wheter the user can start check-in online via CTAs/deeplink/push
    var featureCIOL: Bool? { get }

    var featureCIOLUpsells: Bool? { get }

    // Early check-in, Late check-out
    var featureAllowEciLco: Bool? { get }

    var featureAppleWalletPass: Bool? { get }

    // Dashboard
    var featureShowDashboard: Bool? { get }

    var featureAddNewCard: Bool? { get }
    var featureDonations: Bool? { get }
    var featureAppIncentive: Bool? { get }

    var featureContentsquareUnmask: Bool? { get }

    var featureDigitalKeys: Bool? { get }

    var featureHDPDiscountCode: Bool? { get }
    var hdpDiscountCodeAllowedBrands: [String] { get }

    var featureThirdPartyPrepaid: Bool? { get }
    var featurePIBACPEnabled: Bool? { get }

    func fetchRemoteConfig(completion: @escaping (_ success: Bool) -> Void)
}

enum Feature {
    case SyphonFuel
    case NotificationsMessage

    var isActive: Bool {
        switch self {
        case .NotificationsMessage:
            return SettingsManager.sharedInstance.piRemoteConfig.notificationsMessageFeature ?? false
        default:
            return false
        }
    }
}

class SettingsManager {
	static let sharedInstance = SettingsManager()

    // MARK: - Keys

    private enum Keys {
        static let freeBreakfastPromoCode = "PREBF"
    }

    var piRemoteConfig: PIRemoteConfig = FireBaseRemoteConfig.sharedInstance
    var forceTestPushValue: String? {
        piRemoteConfig.forceTestPushValue
    }

    private(set) var fetchedRoomTypesContent: [RoomTypeInformation]?
    private(set) var fetchedRatesContent: [RateInformation]?

    var supportedInterfaceOrientations: UIInterfaceOrientationMask = .portrait

    public var restrictionsArray: [Restrictions] = [Restrictions]()

    public var activeRules: Restrictions {
        // Probably worth refactoring the UserSessionManager to just care about the channel and not the individual flags, for now this works out what channel the user is.
        var channel: Channel {
            if UserSessionManager.sharedInstance.currentUser?.isBusiness == true {
                return .BB
            } else if SettingsManager.sharedInstance.allowEmployeeOfferFeature == true && BookingDetails.sharedInstance
                        .employeeRatesEnabled {
                return .EMPLOYEE
            } else {
                return .PI
            }
        }

        // Find the restriction for the users channel, if its not found then get the correct hardcoded rule
        return rulesForChannel(channel: channel)
    }

    // This is used as a standalone in Amend when we don't want to use the rules based on the logged in state
    public func rulesForChannel(channel: Channel) -> Restrictions {
        restrictionsArray.first(where: { $0.channel == channel }) ?? channel.hardCodedRule
    }


    private let settingsDictionary = Bundle.main.object(forInfoDictionaryKey: "piSettings") as? NSDictionary

    // MARK: - Remote content
    var notificationsMessage: NotificationsMessage? {
        piRemoteConfig.notificationsMessage
    }
    var cmsStrings: [String: String] {
        piRemoteConfig.cmsStrings
    }
    var topDestinations: [Suggestion] {
        switch LanguageManager.supportedLanguage {
        case .english:
            return Constants.CMS.topDestinationsEN.compactMap { PISuggestion(dictionary: $0) }
        case .german:
            return Constants.CMS.topDestinationsDE.compactMap { PISuggestion(dictionary: $0) }
        }
    }
    var myAccountLinks: [MyAccountLink] {
        piRemoteConfig.myAccountLinks.compactMap { MyAccountLink(dictionary: $0) }
    }

    var passwordRegexs: [String: String] {
        piRemoteConfig.passwordRegexs
    }

    var twinRoomInfo: [PIDictionary] {
        piRemoteConfig.twinRoomInfo
    }

    // MARK: - Coronavirus 😷
    var coronavirusMessagingSRPAndHDP: Bool {
        piRemoteConfig.coronavirusMessagingSRPAndHDP ?? false
    }
    var coronavirusMessagingHomepage: Bool {
        piRemoteConfig.coronavirusMessagingHomepage ?? false
    }
    var coronavirusBannerMessage: String {
        piRemoteConfig.coronavirusBannerMessage
    }

    var bartDown: Bool {
        piRemoteConfig.bartDown ?? false
    }

    var shouldOperaRedirectToWeb: Bool {
        piRemoteConfig.shouldOperaRedirectToWeb ?? false
    }

    var shouldOperaShowFallBackForBB: Bool {
        piRemoteConfig.shouldOperaShowFallBackForBB ?? false
    }

    var shouldRedirectForAmendOpera: Bool {
        piRemoteConfig.shouldShowAmendBanner ?? false
    }

    var orderAndPay: Bool {
        piRemoteConfig.orderAndPay ?? false
    }

    var getKey: Bool {
        piRemoteConfig.getKey ?? false
    }

    var featurePayPal: Bool {
        piRemoteConfig.featurePayPal ?? true
    }

    var featureUsePaypalInitiatePayment: Bool {
        piRemoteConfig.featureUsePaypalInitiatePayment ?? true
    }

    var featureApplePay: Bool {
        piRemoteConfig.featureApplePay ?? true
    }

    var roomTypesContent: [RoomTypeInformation]? {
        get {
            fetchedRoomTypesContent
        }
        set {
            fetchedRoomTypesContent = newValue
        }
    }

    var ratesContent: [RateInformation]? {
        get {
            fetchedRatesContent
        }
        set {
            fetchedRatesContent = newValue
        }
    }

    var siteWidePromotionContent: SiteWidePromotionContent?

    var confirmationPollingMessagesConfig: ConfirmationPollingMessagesConfig {
        let messages = piRemoteConfig.confirmationPollingMessagesConfig?["messages"] as? [PIDictionary] ?? []
        let sortedMessages = messages.compactMap({ ConfirmationPollingMessage(dictionary: $0) })
            .sorted(by: { $0.order < $1.order })
        let config = ConfirmationPollingMessagesConfig(messages: sortedMessages)

        return config
    }

    var employeeQuestionsOperaFeature: Bool {
        piRemoteConfig.employeeQuestionsOperaFeature ?? true
    }

    var supportedKioskHotel: [QRKioskHotels]? {
        piRemoteConfig.kioskHotels?.compactMap {
            guard let hotelCode = $0["hotelCode"] as? String else { return nil }
            return QRKioskHotels(hotelCode: hotelCode)
        }
    }

    var snpWifiEnabledHotels: [SNPWifiHotels]? {
        piRemoteConfig.snpWifiHotels?.compactMap {
            guard let hotelCode = $0["hotelCode"] as? String else { return nil }
            return SNPWifiHotels(hotelCode: hotelCode)
        }
    }

    var hdpDiscountCodeAllowedHotelBrands: [HotelBrand] {
        piRemoteConfig.hdpDiscountCodeAllowedBrands.compactMap {
            HotelBrand(rawValue: $0)
        }
    }

    var featureUseOperaRestProdEndpoint: Bool? {
        piRemoteConfig.featureUseOperaRestProdEndpoint ?? false
    }

    var featureUseOperaRestLowerEnvironmentEndpoint: Bool? {
        piRemoteConfig.featureUseOperaRestLowerEnvironmentEndpoint ?? false
    }

    var featureUseSnowdropOperaLowerEnvEndpoint: Bool? {
        piRemoteConfig.featureUseSnowdropOperaLowerEnvEndpoint ?? false
    }

    var featureUseSnowdropOperaProdEndpoint: Bool? {
        piRemoteConfig.featureUseSnowdropOperaProdEndpoint ?? false
    }

    var allowEmployeeOfferFeature: Bool? {
        piRemoteConfig.allowEmployeeOfferFeature ?? false
    }

    var featureDeeplinkSRP: Bool {
        piRemoteConfig.featureDeeplinkSRP ?? false
    }

    var featureCIOL: Bool {
        piRemoteConfig.featureCIOL ?? false
    }

    var featureCIOLUpsells: Bool {
        piRemoteConfig.featureCIOLUpsells ?? false
    }

    var featureDeeplinkHDP: Bool {
        piRemoteConfig.featureDeeplinkHDP ?? false
    }

    var featureDeeplinkHomepage: Bool {
        piRemoteConfig.featureDeeplinkHomepage ?? false
    }

    var featureDeeplinkCIOL: Bool {
        piRemoteConfig.featureDeeplinkCIOL ?? false
    }

    // Early check-in, Late check-out
    var featureAllowEciLco: Bool {
        piRemoteConfig.featureAllowEciLco ?? false
    }

    var featureAppleWalletPass: Bool {
        piRemoteConfig.featureAppleWalletPass ?? true
    }

    var featureDigitalKeys: Bool {
        piRemoteConfig.featureDigitalKeys ?? false
    }

    var featureShowDashboard: Bool {
        piRemoteConfig.featureShowDashboard ?? true
    }

    var featureShouldShowDashboardEcommerceContent: Bool {
        piRemoteConfig.featureShouldShowDashboardEcommerceContent ?? false
    }

    var featureAddNewCard: Bool {
        piRemoteConfig.featureAddNewCard ?? true
    }

    var featureDonations: Bool {
        piRemoteConfig.featureDonations ?? false
    }

    var featureAppIncentive: Bool {
        piRemoteConfig.featureAppIncentive ?? true
    }

    var featureHDPDiscountCode: Bool {
        piRemoteConfig.featureHDPDiscountCode ?? false
    }

    var featureContentsquareUnmask: Bool {
        piRemoteConfig.featureContentsquareUnmask ?? false
    }

    var featureThirdPartyPrepaid: Bool {
        piRemoteConfig.featureThirdPartyPrepaid ?? false
    }

    var featurePIBACPEnabled: Bool {
        piRemoteConfig.featurePIBACPEnabled ?? false
    }
    // MARK: - Local settings

    var isCalendarPromptShown: Bool {
        get {
            UserDefaults.standard.bool(forKey: Constants.calendarPrompt)
        }
        set {
            UserDefaults.standard.setValue(newValue, forKey: Constants.calendarPrompt)
        }
    }

    private var isDiscountCodeAllowedForCurrentEmployeeState: Bool {
        allowEmployeeOfferFeature != true || BookingDetails.sharedInstance.employeeRatesEnabled == false
    }

    lazy var showVersionNumber: Bool = {
        if let dictionary = settingsDictionary,
           let value = dictionary["showVersionNumber"] as? Bool {
            return value
        }

        return false
    }()

	lazy var showDebugMenu: Bool = {
		if let dictionary = settingsDictionary, let value = dictionary["showDebugMenu"] as? Bool {
			return value
		}

		return false
	}()

    lazy var sharedMapView: MKMapView = {
        let mapView = MKMapView()
        mapView.mapType = .standard

        return mapView
    }()

    static var shouldPinCertificates: Bool {
        get {
            #if DEV
                return UserDefaults.standard.bool(forKey: Constants.pinningBypassKey)
            #else
                return true
            #endif
        }
        set {
            #if DEV
                RequestsManager.shouldPinCertificates = newValue
                UserDefaults.standard.set(newValue, forKey: Constants.pinningBypassKey)
            #endif
        }
    }

    var shouldShowNewABTestVariantForDevelopmentTesting: Bool {
        get {
            #if DEV
                return UserDefaults.standard.bool(forKey: Constants.multivariantDebugKey)
            #else
                return false
            #endif
        }
        set {
            #if DEV
                UserDefaults.standard.set(newValue, forKey: Constants.multivariantDebugKey)
            #endif
        }
    }

	var shouldShowDebugButtonOverlay: Bool {
		get {
			#if DEV
				if UserDefaults.standard.value(forKey: Constants.debugButtonOverlayKey) == nil {
					return true
				}

				return UserDefaults.standard.bool(forKey: Constants.debugButtonOverlayKey)
			#else
				return false
			#endif
		}
		set {
			#if DEV
				UserDefaults.standard.set(newValue, forKey: Constants.debugButtonOverlayKey)

				NotificationCenter.default.post(name: .webserviceConfigurationDidChange, object: nil)
			#endif
		}
	}

    var enableEmployeeRates: Bool {
        get {
            UserDefaults.standard.bool(forKey: Constants.employeeRatesKey)
        }
        set {
            UserDefaults.standard.set(newValue, forKey: Constants.employeeRatesKey)
            BookingDetails.sharedInstance.employeeRatesEnabled = newValue
            BookingDetails.sharedInstance.criteria.rooms = [BookingDetails.sharedInstance.criteria.rooms[0]]
            NotificationCenter.default.post(name: .userDidChange, object: nil)
        }
    }

    var hasMadeAppBooking: Bool {
        get {
            UserDefaults.standard.bool(forKey: Constants.hasMadeAppBooking)
        }
        set {
            UserDefaults.standard.set(newValue, forKey: Constants.hasMadeAppBooking)
        }
    }

    var isAppIncentiveEnabled: Bool {
        get {
            UserDefaults.standard.bool(forKey: Constants.isAppIncentiveEnabled)
        }
        set {
            UserDefaults.standard.set(newValue, forKey: Constants.isAppIncentiveEnabled)
        }
    }

    var isFreeBreakfastEnabled: Bool = false

    var appIncentivePromoCode: String {
        PILocalizedString("appIncentivePromoCode")
    }

    var hasSeenAppIncentive: Bool {
        get {
            UserDefaults.standard.bool(forKey: Constants.hasSeenAppIncentive)
        }
        set {
            UserDefaults.standard.set(newValue, forKey: Constants.hasSeenAppIncentive)
        }
    }

    var isAppIncentiveAvailable: Bool {
        guard featureAppIncentive,
              isAppIncentiveEnabled,
              hasMadeAppBooking == false,
              appIncentivePromoCode.isNotEmpty
        else {
            BookingDetails.sharedInstance.appIncentivePromoCode = nil
            return false
        }

        BookingDetails.sharedInstance.appIncentivePromoCode = appIncentivePromoCode

        return true
    }

    var isFreeBreakfastPromotionAvailable: Bool {
        guard isFreeBreakfastEnabled else {
            BookingDetails.sharedInstance.freeBreakfastPromoCode = nil
            return false
        }

        BookingDetails.sharedInstance.freeBreakfastPromoCode = Keys.freeBreakfastPromoCode
        return true
    }

    /// - Hide cell if brand not present in the list of brands we set on Firebase.
    /// - Priority of promotions: App Incentive > Free Breakfast > Site-Wide > User entered discount.
    /// - Due to backend limitations, the business has decided to hide cell for business booker.
    ///
    /// **TICKETS**:
    /// - [CTECH-4557](https://whitbreadis.atlassian.net/browse/CTECH-4557)
    /// - [CTECH-6701](https://whitbreadis.atlassian.net/browse/CTECH-6701)
    /// - [CTECH-7171](https://whitbreadis.atlassian.net/browse/CTECH-7171)
    ///
    /// (Requirements updated so often this comment has trust issues 😅)
    func shouldShowHdpDiscountCodeCell(for brand: HotelBrand) -> Bool {
        hdpDiscountCodeAllowedHotelBrands.contains(brand) &&
        BookingDetails.sharedInstance.bookingMode != .business &&
        featureHDPDiscountCode &&
        isDiscountCodeAllowedForCurrentEmployeeState &&
        !isAppIncentiveAvailable &&
        !isFreeBreakfastEnabled &&
        siteWidePromotionContent?.promotionCode == nil
    }

    var currentRouterType: RouterType {
        get {
#if DEV
            guard let value = UserDefaults.standard.string(forKey: Constants.webserviceConfigurationKey)
                else { return .uatGraphQL }

            return RouterType(rawValue: value) ?? .uatGraphQL
#else
            return .production
#endif
        }
        set {
#if DEV
            UserDefaults.standard.set(newValue.rawValue, forKey: Constants.webserviceConfigurationKey)

            NotificationCenter.default.post(name: .webserviceConfigurationDidChange, object: nil)
#endif
        }
    }

    var forceUpdateTitle: String {
        piRemoteConfig.forceUpdateTitle
    }

    var forceUpdateDescription: String {
        piRemoteConfig.forceUpdateDescription
    }

    var shouldAttemptAutoLogin: Bool {
        get {
            UserDefaults.standard.bool(forKey: .autoLoginKey)
        }
        set {
            UserDefaults.standard.set(newValue, forKey: .autoLoginKey)
        }
    }

    var versioning: Versioning? {
        guard let versioningDict = piRemoteConfig.versioningDict else { return nil }

        return Set<WebserviceVersion>(versioningDict.keys.compactMap {
            guard let action = WebserviceAction(rawValue: $0) else { return nil }
            guard let version = versioningDict[$0] as? String else { return nil }
            return WebserviceVersion(webserviceAction: action, version: version)
        })
    }

    // MARK: Functions

    func rateContent(for classification: String) -> RateContent? {
        let decoder = JSONDecoder()
        let mappedContent: [RateContent] = piRemoteConfig.rateContent.compactMap {
            guard let data = try? JSONSerialization.data(withJSONObject: $0, options: .prettyPrinted) else { return nil }

            return try? decoder.decode(RateContent.self, from: data)
        }
        return mappedContent.first(where: { $0.classification == classification })
    }

    private func objectFromJSONString<T>(for key: String, in dictionary: PIDictionary) -> T? {
        if let jsonString = dictionary[key] as? String, let data = jsonString.data(using: .utf8) {
            do {
                let array = try JSONSerialization.jsonObject(with: data, options: .allowFragments) as? T
                return array
            } catch {
                return nil
            }
        }

        return nil
    }

    func fetchRemoteConfig(completion: @escaping () -> Void) {
        piRemoteConfig.fetchRemoteConfig { _ in
            if self.piRemoteConfig.appNeedsToBeUpdated {
                guard let topViewController = UIApplication.topViewController() else { return }

                let forceUpdateController = ForceUpdateRouter.build()
                forceUpdateController.modalPresentationStyle = .fullScreen

                topViewController.present(forceUpdateController, animated: true)
            }

            completion()
        }
    }

    func rateContent(for rateClassification: String, and hotelBrand: HotelBrand? = .premierInn) -> RateInformation? {
        let rateContent = ratesContent?.first(where: { $0.rateClassification == rateClassification })

        return rateContent
    }

    func roomLabelFor(lettingType: String) -> String {
        guard let roomTypeContent = roomTypesContent?
              .first(where: { $0.roomTypeCodes.contains(lettingType) }) as? RoomTypeInformation else {
            return PILocalizedString("hotelDetailsStandardRoomCapitalised")
        }
        return roomTypeContent.roomLabel
    }

    func roomsSectionTitle(
        for lettingType: String?,
        roomClass: String? = nil,
        and hotelBrand: HotelBrand = .premierInn
    ) -> String {
        // GraphQL only
        guard let lettingType,
              let roomClass,
              roomClass != "ST",
              let roomTypeContent = roomTypesContent?.first(where: { $0.roomTypeCodes.contains(lettingType) }) else {
            return PILocalizedString("hotelDetailsStandardRoomCapitalised")
        }

        return roomTypeContent.roomLabel
    }

    func roomsTitleForAmend(
        for lettingType: String?,
        roomClass: String? = nil,
        and hotelBrand: HotelBrand = .premierInn
    ) -> String? {
        // GraphQL only as roomTypeCodes will not match for Bart
        guard let lettingType,
              let roomClass = roomClass,
              roomClass != "ST",
              let roomTypeContent = roomTypesContent?.first(where: { $0.roomTypeCodes.contains(lettingType) }) else {
            // For the bart flow or standard room types return nil
            return nil
        }

        return roomTypeContent.roomLabel
    }

    func cleanupStoredData() {
        LocalReservationManager.shared.cleanupLocalCheckInSessions()
    }

    func setUpBusinessRules() {
        let requestManager = RequestsManager()

        requestManager.getRestrictions { restrictionsArray, _ in
            guard let restrictionsArray else { return }
            SettingsManager.sharedInstance.restrictionsArray = restrictionsArray
        }
    }

    var isBartBannerDismissible: Bool {
        PILocalizedString("bartDowntimeModalCloseButtonTitle").isNotEmpty
    }
}

extension Channel {
    var hardCodedRule: Restrictions {
        switch self {
        case .PI:
            return Constants.leisureRulesHardCoded
        case .BB:
            return Constants.bbRulesHardCoded
        case .EMPLOYEE:
            return Constants.leisureEmployeeOfferRulesHardCoded
        }
    }
}
