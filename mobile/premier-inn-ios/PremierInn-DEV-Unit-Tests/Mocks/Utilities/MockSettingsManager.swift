//
//  MockSettingsManager.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
@testable import PremierInn

final class MockSettingsManager: SettingsManager {

    private(set) var mockEnableEmployeeRates: Bool = false
    override var enableEmployeeRates: Bool {
        get { mockEnableEmployeeRates }
        set { mockEnableEmployeeRates = newValue }
    }

    private(set) var cleanupCallCount = 0
    override func cleanupStoredData() {
        cleanupCallCount += 1
    }

    private(set) var mockFeatureThirdPartyPrepaid: Bool = false
    override var featureThirdPartyPrepaid: Bool {
        get { mockFeatureThirdPartyPrepaid }
        set { mockFeatureThirdPartyPrepaid = newValue }
    }
}
