//
//  KeyedUnarchiverClassMapper.swift
//  SimpleNetwork
//
//  Created by Rodrigues, Seymour (Contractor) on 20/01/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation
import Auth0

/// Registers a class-name mapping required to decode Auth0 credentials.
///
/// Auth0 archives credentials using the legacy Objective‑C class name
/// `"A0Credentials"`. When using Swift Package Manager, this name is not
/// automatically bridged to `Auth0.Credentials` during unarchiving.
///
/// Registering this mapping ensures `NSKeyedUnarchiver` can correctly
/// decode persisted credentials and prevents runtime decoding failures.
enum Auth0KeyedUnarchiverMapping {
    static func register() {
        NSKeyedUnarchiver.setClass(Credentials.self, forClassName: "A0Credentials")
    }
}
