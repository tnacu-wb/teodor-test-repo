//
//  PushPayloadParser.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 11/05/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import Foundation

enum PushPayloadParser {
    static func extract(from payload: [AnyHashable: Any]) -> PIDictionary {
        let filteredPayload = payload.filter { dict in
            !dict.key.description.starts(with: "google.") &&
            !dict.key.description.starts(with: "gcm.") &&
            !dict.key.description.starts(with: "aps")
        }

        var userInfo = PIDictionary()

        filteredPayload.forEach { key, value in
            if let key = key as? String,
               let value = value as? String {
                userInfo[key] = value
            }
        }
        return userInfo
    }
}
