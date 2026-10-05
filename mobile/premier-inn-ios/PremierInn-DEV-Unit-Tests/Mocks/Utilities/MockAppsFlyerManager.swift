//
//  MockAppsFlyerManager.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit
@testable import PremierInn

final class MockAppsFlyerManager: AppsFlyerManager {

    private(set) var receivedDelegate: UIApplicationDelegate?
    override func setUp(delegate: AppDelegate?) {
        receivedDelegate = delegate
    }

    private(set) var receivedCustomerId: String?
    override func integrate(adobeCustomerId: String?) {
        receivedCustomerId = adobeCustomerId
    }
}
