//
//  AdobeCampaignManagerTests.swift
//  PremierInn
//
//  Created by Santa Gurung on 23/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Testing
import Foundation
@testable import PremierInn

final class AdobeCampaignManagerTests {

    var sut: AdobeCampaignManager!

    @Test func tokenConvertsToHexString() {
        // GIVEN
        sut = AdobeCampaignManager.shared
        let tokenData = Data([ 0x74, 0x0f, 0x4d, 0x08, 0x7a, 0x5e, 0x9a, 0x1b, 0x2c, 0x3d, 0x4e, 0x5f, 0x60, 0x71, 0x82, 0x93 ])
        sut.apnsToken = tokenData

        // WHEN
        let hexString = sut.apnsTokenString

        // THEN
        #expect(hexString == "740f4d087a5e9a1b2c3d4e5f60718293")
    }
}
