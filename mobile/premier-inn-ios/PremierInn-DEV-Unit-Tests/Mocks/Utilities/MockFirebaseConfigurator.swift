//
//  MockFirebaseConfigurator.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 12/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import Firebase
@testable import PremierInn

struct MockFirebaseConfigurator: FirebaseAppConfigurable {

    static var receivedOptions: FirebaseOptions?
    static var callCount = 0

    static func configure(options: FirebaseOptions?) {
        callCount += 1
        receivedOptions = options
    }

    static func reset() {
        callCount = 0
        receivedOptions = nil
    }
}
