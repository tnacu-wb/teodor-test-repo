//
//  File.swift
//  PremierInn UnitTests
//
//  Created by Filippo Minelle on 23/06/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import Foundation
@testable import PremierInn
@testable import SimpleNetwork

final class MockAnalyticsManager: AnalyticsType, AnalyticsPromotionsTrackable {

    enum MockDictDataKeyValues {
        static let someAnalyticsKey = "someAnalyticsKey"
        static let someAnalyticsValue = "someAnalyticsValue"
    }

    static let mockAnalyticsDict: PIDictionary = [
        MockDictDataKeyValues.someAnalyticsKey: MockDictDataKeyValues.someAnalyticsValue
    ]

    // MARK: - Properties

    private(set) var states = [String]()

    private(set) var events = [String]()

    private(set) var actions = [String]()

    private(set) var userInfos = [[String: String]]()

    private(set) var dictionaries = [[String: String]]()

    private(set) var parameters = [[String: Any]]()

    var campaignAttribution: CampaignAttribution?

    // MARK: - Lifecycle

    private(set) var setupCompletion: ((String?) -> Void)?

    func setup(completion: @escaping (String?) -> Void) {
        setupCompletion = completion
    }

    func start() {
    }

    func pause() {
    }
    
    // MARK: - Track

    func trackAction(_ action: String, userInfo: PIDictionary?) {
        actions.append(action)
        userInfos.append(userInfo?.trackingDictionary ?? [:])
    }

    func track(error: Error, name: String) {

    }

    func track(errorName: String) {
    }

	func track(error: any Error, name: String, extraData: [String : Any]?) {
		
	}
    func trackState(_ state: String, data: PIDictionary) {

        states.append(state)
        dictionaries.append(data.trackingDictionary ?? [:])
    }

    func trackDebugAction(_ action: String, userInfo: PIDictionary?) {
    }

    func log(event: String, parameters: [String: Any]?) {

        events.append(event)
        if let logParameters = parameters { self.parameters.append(logParameters) }
    }

    func analyticsProperties(stateType: String) -> PIDictionary {

        return [:]
    }
}

// MARK: - AnalyticsPromotionsTrackable

extension MockAnalyticsManager {
    func getPromotionsAnalyticsDict(with bookingDetails: SimpleNetwork.BookingDetails?) -> PremierInn.PIDictionary? {
        Self.mockAnalyticsDict
    }
}
