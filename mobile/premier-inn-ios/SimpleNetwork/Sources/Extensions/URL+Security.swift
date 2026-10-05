//
//  URL+Security.swift
//  SimpleNetwork
//
//  Created by Emil Vaklinov 31/03/2026
//  Copyright © 2026 Whitbread. All rights reserved.

import Foundation

extension URL {
    /// Creates a URL from a string, automatically converting HTTP to HTTPS for security
    /// - Parameter string: The URL string (may contain http:// or https://)
    /// - Returns: A URL with HTTPS protocol enforced, or nil if the string is invalid
    static func secureURL(from string: String) -> URL? {
        let httpsString = string.replacingOccurrences(of: "http://", with: "https://")
        return URL(string: httpsString)
    }
}

extension String {
    /// Converts any HTTP protocol to HTTPS in the string
    /// - Returns: String with HTTP replaced by HTTPS
    func enforcingHTTPS() -> String {
        self.replacingOccurrences(of: "http://", with: "https://")
    }
}
