//
//  Bundle+Extension.swift
//  SimpleNetwork
//
//  Created by Rodrigues, Seymour (Contractor) on 20/01/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

private final class BundleToken { }

extension Bundle {
    static var simpleNetworkResources: Bundle {
        #if SWIFT_PACKAGE
        .module
        #else
        Bundle(for: BundleToken.self)
        #endif
    }
}
