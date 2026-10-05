//
//  MVTests.swift
//  PremierInn
//
//  Created by Freddie Parks on 24/01/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation

extension MVTest {
    static let appsGlobal = MVTest(
        tag: "flag",
        identifier: Constants.mvtIdentifier,
        completionIdentifier: Constants.mvtIdentifier,
        defaultValue: "default"
    )
    static let stickyExtrasCTA = MVTest(
        tag: "flag",
        identifier: Constants.stickyExtrasCTAIdentifier,
        completionIdentifier: Constants.stickyExtrasCTAIdentifier,
        defaultValue: "control"
    )
    static let urgencyMessaging = MVTest(
        tag: "flag",
        identifier: Constants.urgencyMessagingIdentifier,
        completionIdentifier: Constants.urgencyMessagingIdentifier,
        defaultValue: "control"
    )
}

enum MVTConfig {
    // Remove tests from this array to stop testing...
    static let tests: [MVTest] = [
        MVTest.appsGlobal,
        MVTest.stickyExtrasCTA,
        MVTest.urgencyMessaging
    ]
}
