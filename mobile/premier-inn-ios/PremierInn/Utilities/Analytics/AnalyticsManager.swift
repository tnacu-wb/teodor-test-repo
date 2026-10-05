//
//  AnalyticsManager.swift
//  PremierInn
//
//  Created by Marcello Mascia on 09/06/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import FirebaseAnalytics
import FirebaseCrashlytics
import CoreLocation
import SimpleNetwork
import AEPCore
import AEPAnalytics
import AEPIdentity
import AEPAssurance
import AEPLifecycle
import AEPTarget
import Dynatrace
import ContentsquareModule

protocol AnalyticsType {
    // Properties
    var campaignAttribution: CampaignAttribution? { get set }

    // Lifecycle
    func setup(completion: @escaping (_ visitorId: String?) -> Void)
    func start()
    func pause()

    // Track
    func trackAction(_ action: String, userInfo: PIDictionary?)
    func track(error: Error, name: String)
	func track(error: Error, name: String, extraData: [String: Any]?)
    func track(errorName: String)
    func trackState(_ state: String, data: PIDictionary)
    func trackDebugAction(_ action: String, userInfo: PIDictionary?)
    func log(event: String, parameters: [String: Any]?)
    func analyticsProperties(stateType: String) -> PIDictionary
}

class AnalyticsManager: AnalyticsType {
    // MARK: - Singleton

    static let shared = AnalyticsManager()

    // MARK: - Properties

    var userID: String { UserSessionManager.sharedInstance.currentUser?.customerAccountId ?? "" }
    var time: String { Date().analyticsTimeFormat }

    var campaignAttribution: CampaignAttribution?

    // MARK: - Lifecycle

    func setup(completion: @escaping (_ visitorId: String?) -> Void) {
        // Adobe
        let extensions = [
            Lifecycle.self,
            Analytics.self,
            Target.self,
            Identity.self,
            Assurance.self
        ]

        MobileCore.registerExtensions(extensions, {
            MobileCore.configureWith(appId: AnalyticsConstants.appId)

#if DEV
            MobileCore.setLogLevel(.error)
#endif

            Identity.getExperienceCloudId { visitorId, _ in
                completion(visitorId)
            }
        })
    }

    func start() {
        MobileCore.lifecycleStart(additionalContextData: nil)
    }

    func pause() {
        MobileCore.lifecyclePause()
    }

    // MARK: - Track

    func trackAction(_ action: String, userInfo: PIDictionary?) {
        MobileCore.track(action: action, data: userInfo?.trackingDictionary)

        Analytics.logEvent(action, parameters: userInfo)

        // we are not tracking these outside of Adobe
    }

    func track(error: Error, name: String) {
		track(error: error, name: name, extraData: nil)
    }

	func track(error: Error, name: String, extraData: [String: Any]?) {
		// Adobe
		MobileCore.track(action: name, data: [PIAnalytics.Action.error: name])

		// DT
		DTXAction.reportError(withName: name, error: error)

		let error = error as NSError
		var userInfo = error.userInfo
		userInfo[PIAnalytics.CustomUserInfoParameters.errorName] = name

		if let extraData = extraData {
			userInfo.merge(extraData) { _, new in new }
		}

		let errorWithCustomParams = NSError(
		    domain: error.domain,
		    code: error.code,
		    userInfo: userInfo
		)

		// Firebase Crashlytics
		Crashlytics.crashlytics().record(error: errorWithCustomParams)
	}

    func track(errorName: String) {
        // Adobe
        MobileCore.track(action: errorName, data: [PIAnalytics.Action.error: errorName])

        // AppDynamics
        let error = NSError(
            domain: errorName,
            code: PIAnalytics.ErrorCode.customErrorCode,
            userInfo: [NSLocalizedDescriptionKey: errorName]
        )

        // Firebase Crashlytics
        Crashlytics.crashlytics().record(error: error)
    }

    func trackState(_ state: String, data: PIDictionary) {
        let trackingDictionary = data.trackingDictionary

        MobileCore.track(state: state, data: trackingDictionary)

        let csqVariables = contentSquareVariables(data: trackingDictionary)
        Contentsquare.send(screenViewWithName: state, cvars: csqVariables)
    }

    func trackDebugAction(_ action: String, userInfo: PIDictionary?) {
        Analytics.logEvent(action, parameters: userInfo)
    }

    func log(event: String, parameters: [String: Any]?) {
        Analytics.logEvent(event, parameters: parameters)
    }

    func analyticsProperties(stateType: String) -> PIDictionary {
        let userSession: LoggedInAnalytic = UserSessionManager.sharedInstance.currentUser != nil ? .loggedIn : .notLoggedIn
        var dictionary: PIDictionary = [
            PIAnalytics.Keys.environment: AnalyticsConstants.environment,
            PIAnalytics.Keys.userLogin: userSession.rawValue,
            PIAnalytics.Keys.timeZone: TimeZone.current.description,
            PIAnalytics.Keys.language: Locale.current.language.languageCode?.identifier ?? "n/a",
            PIAnalytics.Keys.screenType: stateType,
            PIAnalytics.Keys.userID: userID,
            PIAnalytics.Keys.time: time
        ]

        if let user = UserSessionManager.sharedInstance.currentUser, let companyId = user.companyId,
           let accessLevel = user.accessLevel?.rawValue {
            dictionary[PIAnalytics.Keys.companyID] = companyId
            dictionary[PIAnalytics.Keys.businessUserLevel] = accessLevel
        }

        return dictionary
    }
}

extension AnalyticsManager {
    static func getPackagesErrorParams(bookingDetails: BookingDetails, bookingFlow: String? = nil) -> [String: NSObject] {
        var parameters: [String: NSObject] = [:]
        parameters["mode"] = bookingDetails.bookingMode.rawValue as NSObject
        parameters["hotelCode"] = (bookingDetails.hotel?.code ?? "") as NSObject
        parameters["brand"] = (bookingDetails.hotel?.brand.rawValue ?? "") as NSObject
        parameters["bookingFlowId"] = (bookingFlow ?? "") as NSObject
        parameters["rate"] = (bookingDetails.rate?.classification ?? "") as NSObject
        if #available(iOS 16, *) {
            parameters["lang"] = (Locale.current.language.languageCode?.identifier ?? "") as NSObject
        } else {
            parameters["lang"] = (Locale.current.languageCode ?? "") as NSObject
        }
        return parameters
    }
}

extension AnalyticsManager {
    func trackDTMetric(double: Double, actionName: String, actionKey: String) {
        let trackDouble = DTXAction.enter(withName: actionName)
        trackDouble?.reportValue(withName: actionKey, doubleValue: double)
        trackDouble?.leave()
    }
}

// MARK: - ContentSquare

extension AnalyticsManager {
    func trackCSQTransaction(bookingReference: String, totalCost: Cost) {
        let bookingTransaction = CustomerTransaction(
            id: bookingReference,
            value: totalCost.amount.floatValue,
            currency: totalCost.currencyCode
        )
        Contentsquare.send(transaction: bookingTransaction)
    }

    private func contentSquareVariables(data: [String: String]?) -> [CustomVar] {
        let csqData: [String: String]? = filterCSQKeys(data: data)

        let csqVariables: [CustomVar] = csqData?.compactMap { element in
            guard let csqKey = ContentSquareKey(rawValue: element.key) else { return nil }
            let index = csqKey.index

            return CustomVar(index: UInt32(index), name: element.key, value: element.value)
        } ?? []

        return csqVariables
    }

    private func filterCSQKeys(data: [String: String]?) -> [String: String]? {
        data?.filter { element in
            let csqKeys = ContentSquareKey.allCases

            guard let csqKey = ContentSquareKey(rawValue: element.key),
                  csqKeys.contains(csqKey) else { return false }

            return true
        }
    }
}
