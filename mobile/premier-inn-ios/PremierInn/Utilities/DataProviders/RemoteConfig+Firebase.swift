//
//  RemoteConfig+Firebase.swift
//  PremierInn
//
//  Created by Freddie Parks on 26/06/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import SimpleNetwork
import FirebaseRemoteConfig

class FireBaseRemoteConfig {
    private static let instance = FireBaseRemoteConfig()

    private var remoteConfiguration = RemoteConfig.remoteConfig() {
        didSet {
            let remoteConfigSettings = RemoteConfigSettings()

            #if DEV
            remoteConfigSettings.minimumFetchInterval = 1
            #else
            remoteConfigSettings.minimumFetchInterval = 1800
            #endif

            remoteConfiguration.configSettings = remoteConfigSettings

            remoteConfiguration.setDefaults([
                FirebaseConstants.remoteValueKeys.unsupportedVersions: FirebaseConstants.remoteValueDefaults
                    .unsupportedVersions as NSArray,
                FirebaseConstants.remoteValueKeys.minimumSupportedVersion: FirebaseConstants.remoteValueDefaults
                .minimumSupportedVersion as NSString,
                FirebaseConstants.remoteValueKeys.forceUpdateTitle: FirebaseConstants.remoteValueDefaults
                .forceUpdateTitle as NSString,
                FirebaseConstants.remoteValueKeys.forceUpdateDescription: FirebaseConstants.remoteValueDefaults
                .forceUpdateDescription as NSString,
                FirebaseConstants.remoteValueKeys.cmsStrings: FirebaseConstants.remoteValueDefaults
                .cmsStrings as NSDictionary,
                FirebaseConstants.remoteValueKeys.testPush: FirebaseConstants.remoteValueDefaults.testPush as NSString,
                FirebaseConstants.remoteValueKeys.versioning: FirebaseConstants.remoteValueDefaults
                .versioning as NSDictionary,
                FirebaseConstants.remoteValueKeys.notificationsMessageFeature: FirebaseConstants.remoteValueDefaults
                .notificationsMessageFeature as NSString,
                FirebaseConstants.remoteValueKeys.notificationsMessage: FirebaseConstants.remoteValueDefaults
                .notificationsMessage as NSDictionary,
                FirebaseConstants.remoteValueKeys.myAccountLinks: FirebaseConstants.remoteValueDefaults
                .myAccountLinks as NSArray,
                FirebaseConstants.remoteValueKeys.rateContent: FirebaseConstants.remoteValueDefaults.rateContent as NSArray,
                FirebaseConstants.remoteValueKeys.coronavirusBannerMessage: FirebaseConstants.remoteValueDefaults
                .coronavirusBannerMessage as NSString,
                FirebaseConstants.remoteValueKeys.passwordRegexs:
                    FirebaseConstants.remoteValueDefaults.passwordRegex as NSDictionary,
                FirebaseConstants.remoteValueKeys.twinRoomInfo:
                    FirebaseConstants.remoteValueDefaults.twinRoomInfo as NSArray,
                FirebaseConstants.remoteValueKeys.employeeQuestionsOperaFeature:
                    FirebaseConstants.remoteValueDefaults.employeeQuestionsOperaFeature as NSString,
                FirebaseConstants.remoteValueKeys.featurePayPal:
                    FirebaseConstants.remoteValueDefaults.featurePayPal as NSString,
                FirebaseConstants.remoteValueKeys.featureUsePaypalInitiatePayment:
                    FirebaseConstants.remoteValueDefaults.featureUsePaypalInitiatePayment as NSString,
                FirebaseConstants.remoteValueKeys.featureApplePay: FirebaseConstants.remoteValueDefaults
                .applePayFeature as NSString,
                FirebaseConstants.remoteValueKeys.featureUseOperaRestProdEndpoint:
                    FirebaseConstants.remoteValueDefaults.featureUseOperaRestProdEndpoint as NSString,
                FirebaseConstants.remoteValueKeys.featureUseOperaRestLowerEnvironmentEndpoint:
                    FirebaseConstants.remoteValueDefaults.featureUseOperaRestLowerEnvironmentEndpoint as NSString,
                FirebaseConstants.remoteValueKeys.featureUseSnowdropOperaProdEndpoint:
                    FirebaseConstants.remoteValueDefaults.featureUseSnowdropOperaProdEndpoint as NSString,
                FirebaseConstants.remoteValueKeys.featureUseSnowdropOperaLowerEnvEndpoint:
                    FirebaseConstants.remoteValueDefaults.featureUseSnowdropOperaLowerEnvEndpoint as NSString,
                FirebaseConstants.remoteValueKeys.allowEmployeeOfferFeature:
                    FirebaseConstants.remoteValueDefaults.allowEmployeeOfferFeature as NSString,
                FirebaseConstants.remoteValueKeys.featureAllowEciLco:
                    FirebaseConstants.remoteValueDefaults.featureAllowEciLco as NSString,
                FirebaseConstants.remoteValueKeys.featureAppleWalletPass:
                    FirebaseConstants.remoteValueDefaults.featureAppleWalletPass as NSString,
                FirebaseConstants.remoteValueKeys.featureShowDashboard: FirebaseConstants.remoteValueDefaults
                .featureShowDashboard as NSString,
                FirebaseConstants.remoteValueKeys.featureShouldShowDashboardEcommerceContent:
                    FirebaseConstants.remoteValueDefaults.featureShouldShowDashboardEcommerceContent as NSString,
                FirebaseConstants.remoteValueKeys.featureAddNewCard:
                    FirebaseConstants.remoteValueDefaults.featureAddNewCard as NSString,
                FirebaseConstants.remoteValueKeys.featureDigitalKeys:
                    FirebaseConstants.remoteValueDefaults.featureDigitalKeys as NSString,
                FirebaseConstants.remoteValueKeys.featureAppIncentive:
                    FirebaseConstants.remoteValueDefaults.featureAppIncentive as NSString,
                FirebaseConstants.remoteValueKeys.featurePIBACPEnabled:
                    FirebaseConstants.remoteValueDefaults.featurePIBACPEnabled as NSString
            ])
        }
    }
}

extension FireBaseRemoteConfig: PIRemoteConfig {
    static var sharedInstance: PIRemoteConfig {
        self.instance
    }

    var cmsStrings: [String: String] {
        guard let cmsString = remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.cmsStrings)
              .stringValue else { return [:] }
        guard let cmsStringsDict: [String: String] = objectFromJSONString(
            for: FirebaseConstants.remoteValueKeys.cmsStrings,
            in: [FirebaseConstants.remoteValueKeys.cmsStrings: cmsString]
        ) else { return [:] }

        return cmsStringsDict
    }

    var rateContent: [PIDictionary] {
        guard let rateContentString = remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.rateContent)
              .stringValue else { return [] }
        guard let rateContentDics: [PIDictionary] = objectFromJSONString(
            for: FirebaseConstants.remoteValueKeys.rateContent,
            in: [FirebaseConstants.remoteValueKeys.rateContent: rateContentString]
        ) else { return [] }

        return rateContentDics
    }

    var myAccountLinks: [PIDictionary] {
        guard let myAccountLinksString = remoteConfiguration
              .configValue(forKey: FirebaseConstants.remoteValueKeys.myAccountLinks).stringValue else { return [] }
        guard let myAccountLinksDics: [PIDictionary] = objectFromJSONString(
            for: FirebaseConstants.remoteValueKeys.myAccountLinks,
            in: [FirebaseConstants.remoteValueKeys.myAccountLinks: myAccountLinksString]
        ) else { return [] }

        return myAccountLinksDics
    }

    var kioskHotels: [PIDictionary]? {
        guard let kioskHotelsString = remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.kioskHotels)
              .stringValue else { return [] }
        guard let kioskHotelsDics: [PIDictionary] = objectFromJSONString(
            for: FirebaseConstants.remoteValueKeys.kioskHotels,
            in: [FirebaseConstants.remoteValueKeys.kioskHotels: kioskHotelsString]
        ) else { return [] }
        return kioskHotelsDics
    }

    var snpWifiHotels: [PIDictionary]? {
        guard let snpWifiHotelsString = remoteConfiguration
              .configValue(forKey: FirebaseConstants.remoteValueKeys.snpWifiSites).stringValue else { return [] }
        guard let snpWifiHotelsDics: [PIDictionary] = objectFromJSONString(
            for: FirebaseConstants.remoteValueKeys.snpWifiSites,
            in: [FirebaseConstants.remoteValueKeys.snpWifiSites: snpWifiHotelsString]
        ) else { return [] }
        return snpWifiHotelsDics
    }

    var passwordRegexs: [String: String] {
        guard let regexsString = remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.passwordRegexs)
              .stringValue else { return [:] }
        guard let regexsStringDict: [String: String] = objectFromJSONString(
            for: FirebaseConstants.remoteValueKeys.passwordRegexs,
            in: [FirebaseConstants.remoteValueKeys.passwordRegexs: regexsString]
        ) else { return [:] }

        return regexsStringDict
    }

    var twinRoomInfo: [PIDictionary] {
        guard let regexsString = remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.twinRoomInfo)
              .stringValue else { return [PIDictionary]() }

        guard let regexsStringDict: [PIDictionary] = objectFromJSONString(
            for: FirebaseConstants.remoteValueKeys.twinRoomInfo,
            in: [FirebaseConstants.remoteValueKeys.twinRoomInfo: regexsString]
        ) else { return [PIDictionary]() }

        return regexsStringDict
    }

    var notificationsMessage: NotificationsMessage? {
        guard let nmString = remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.notificationsMessage)
              .stringValue else { return nil}
        guard let nmDict: [String: Any] = objectFromJSONString(
            for: FirebaseConstants.remoteValueKeys.notificationsMessage,
            in: [FirebaseConstants.remoteValueKeys.notificationsMessage: nmString]
        ) else { return nil }
        guard let title = nmDict["notificationTitle"] as? String, let body = nmDict["notificationBody"] as? String,
              let ctaTitle = nmDict["notificationCTA"] as? String,
              let notificationId = nmDict["notificationID"] as? Int else { return nil }

        return NotificationsMessage(title: title, message: body, ctaTitle: ctaTitle, id: notificationId)
    }

    var forceUpdateTitle: String {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.forceUpdateTitle).stringValue ?? ""
    }

    var forceUpdateDescription: String {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.forceUpdateDescription).stringValue ?? ""
    }

    var appNeedsToBeUpdated: Bool {
        guard let minimumSupportedVersionString = remoteConfiguration
              .configValue(forKey: FirebaseConstants.remoteValueKeys.minimumSupportedVersion).stringValue
        else { return false }
        guard let infoDictionary = Bundle.main.infoDictionary,
              let versionText = infoDictionary["CFBundleShortVersionString"] as? String else {
            return false
        }

        let appVersion = Version(string: versionText)
        let minimumSupportedVersion = Version(string: minimumSupportedVersionString)
        let isUnsupportedVersion: Bool = {
            guard let unsupportedVersionsString = remoteConfiguration
                  .configValue(forKey: FirebaseConstants.remoteValueKeys.unsupportedVersions).stringValue
            else { return false }
            guard let unsupportedVersions: [String] = objectFromJSONString(
                for: FirebaseConstants.remoteValueKeys.unsupportedVersions,
                in: [FirebaseConstants.remoteValueKeys.unsupportedVersions: unsupportedVersionsString]
            ) else { return false }

            return unsupportedVersions.first(where: { Version(string: $0) == appVersion }) != nil
        }()

        return appVersion < minimumSupportedVersion || isUnsupportedVersion
    }

    var forceTestPushValue: String? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.testPush).stringValue
    }

    var versioningDict: PIDictionary? {
        guard let versioningString = remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.versioning)
              .stringValue else { return [:] }
        guard let versioningStringDict: [String: String] = objectFromJSONString(
            for: FirebaseConstants.remoteValueKeys.versioning,
            in: [FirebaseConstants.remoteValueKeys.versioning: versioningString]
        ) else { return [:] }

        return versioningStringDict
    }

    var notificationsMessageFeature: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.notificationsMessageFeature).boolValue
    }

    func fetchRemoteConfig(completion: @escaping (_ success: Bool) -> Void) {
        var fetchTime = UserDefaults.standard.integer(forKey: FirebaseConstants.localStorageKeys.fetchExpirationDuration)

        remoteConfiguration = RemoteConfig.remoteConfig()
        if UserDefaults.standard.bool(forKey: FirebaseConstants.localStorageKeys.configStale) {
            fetchTime = 0
        }

        #if DEV
        fetchTime = 0
        #endif

        print("\n\n####\nFetching config with time: \(fetchTime)\n####\n\n")

        remoteConfiguration.fetch(withExpirationDuration: TimeInterval(fetchTime)) { status, error in
            guard error == nil else {
                UserDefaults.standard.set(
                    FirebaseConstants.fetchTimeRetry,
                    forKey: FirebaseConstants.localStorageKeys.fetchExpirationDuration
                )
                completion(false)
                return
            }
            if status == .success {
                RemoteConfig.remoteConfig().activate(completion: { _, _ in })
                UserDefaults.standard.set(false, forKey: FirebaseConstants.localStorageKeys.configStale)
                completion(true)

                UserDefaults.standard.set(
                    FirebaseConstants.fetchTime,
                    forKey: FirebaseConstants.localStorageKeys.fetchExpirationDuration
                )
            } else {
                completion(false)
            }
        }
    }

    var coronavirusMessagingSRPAndHDP: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.coronavirusMessagingSRPAndHDP).boolValue
    }

    var coronavirusMessagingHomepage: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.coronavirusMessagingHomepage).boolValue
    }

    var coronavirusBannerMessage: String {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.coronavirusBannerMessage)
            .stringValue ?? PILocalizedString("coronavirusBannerMessaging")
    }

    var bartDown: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.bartDown).boolValue
    }

    var shouldOperaRedirectToWeb: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.shouldOperaRedirectToWeb).boolValue
    }

    var shouldOperaShowFallBackForBB: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.shouldOperaShowFallBackForBB).boolValue
    }

    var shouldShowAmendBanner: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.shouldShowAmendBanner).boolValue
    }
    var orderAndPay: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.orderAndPay).boolValue
    }

    var getKey: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.getKey).boolValue
    }

    var confirmationPollingMessagesConfig: PIDictionary? {
        guard let confirmationPollingMessagesConfigString = remoteConfiguration
              .configValue(forKey: FirebaseConstants.remoteValueKeys.confirmationPollingMessagesConfig).stringValue
        else { return nil }

        guard let confirmationPollingMessagesConfig: PIDictionary = objectFromJSONString(
            for: FirebaseConstants.remoteValueKeys.confirmationPollingMessagesConfig,
            in: [FirebaseConstants.remoteValueKeys
            .confirmationPollingMessagesConfig: confirmationPollingMessagesConfigString]
        ) else { return nil }

        return confirmationPollingMessagesConfig
    }

    var confirmationPollingDelay: Int {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.confirmationPollingDelay).numberValue
            .intValue
    }

    var confirmationPollingInterval: Int {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.confirmationPollingInterval).numberValue
            .intValue
    }

    var employeeQuestionsOperaFeature: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.employeeQuestionsOperaFeature).boolValue
    }

    var featurePayPal: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featurePayPal).boolValue
    }

    var featureUsePaypalInitiatePayment: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featureUsePaypalInitiatePayment).boolValue
    }

    var featureApplePay: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featureApplePay).boolValue
    }

    var featureUseOperaRestProdEndpoint: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featureUseOperaRestProdEndpoint).boolValue
    }

    var featureUseOperaRestLowerEnvironmentEndpoint: Bool? {
        remoteConfiguration
            .configValue(forKey: FirebaseConstants.remoteValueKeys.featureUseOperaRestLowerEnvironmentEndpoint).boolValue
    }

    var featureUseSnowdropOperaLowerEnvEndpoint: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featureUseSnowdropOperaLowerEnvEndpoint)
            .boolValue
    }

    var featureUseSnowdropOperaProdEndpoint: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featureUseSnowdropOperaProdEndpoint)
            .boolValue
    }

    var allowEmployeeOfferFeature: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.allowEmployeeOfferFeature).boolValue
    }

    var featureDeeplinkSRP: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featureDeeplinkSRP).boolValue
    }

    var featureDeeplinkHDP: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featureDeeplinkHDP).boolValue
    }

    var featureDeeplinkCIOL: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featureDeeplinkCIOL).boolValue
    }

    var featureDeeplinkHomepage: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featureDeeplinkHomepage).boolValue
    }

    /// CIOL - this feature flag will enable the posibility to start check in
    /// Controls wheter the user can start check-in online via CTAs/deeplink/push
    var featureCIOL: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featureCIOL).boolValue
    }

    var featureCIOLUpsells: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featureCIOLUpsells).boolValue
    }

    var featureAllowEciLco: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featureAllowEciLco).boolValue
    }

    var featureAppleWalletPass: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featureAppleWalletPass).boolValue
    }

    var featureDigitalKeys: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featureDigitalKeys).boolValue
    }

    var featureShowDashboard: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featureShowDashboard).boolValue
    }

    var featureShouldShowDashboardEcommerceContent: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featureShouldShowDashboardEcommerceContent)
            .boolValue
    }

    var featureAddNewCard: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featureAddNewCard).boolValue
    }

    var featureDonations: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featureDonations).boolValue
    }

    var featureAppIncentive: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featureAppIncentive).boolValue
    }

    var featureHDPDiscountCode: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featureHDPDiscountCode).boolValue
    }

    var hdpDiscountCodeAllowedBrands: [String] {
        let key = FirebaseConstants.remoteValueKeys.hdpDiscountCodeAllowedBrands

        guard let allowedBrandsString = remoteConfiguration.configValue(forKey: key).stringValue,
              let allowedBrandsDict: PIDictionary = objectFromJSONString(for: key, in: [key: allowedBrandsString])
        else {
            return []
        }

        let allowedBrands = allowedBrandsDict["allowedBrands"] as? [String] ?? []

        return allowedBrands
    }

    var featureContentsquareUnmask: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featureContentsquareUnmask).boolValue
    }

    var featureThirdPartyPrepaid: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featureThirdPartyPrepaid).boolValue
    }

    var featurePIBACPEnabled: Bool? {
        remoteConfiguration.configValue(forKey: FirebaseConstants.remoteValueKeys.featurePIBACPEnabled).boolValue
    }
}

extension FireBaseRemoteConfig {
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
}
