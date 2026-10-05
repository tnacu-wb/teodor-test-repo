//
//  MockAdobeCampaignManager.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
@testable import PremierInn

final class MockAdobeCampaignManager: AdobeCampaignManager {

    private(set) var setupCallCount = 0

    override func setup() {
        setupCallCount += 1
    }
}
