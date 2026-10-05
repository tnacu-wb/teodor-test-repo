//
//  MessagingFlag+Mock.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 23/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

#if DEV

import SimpleNetwork

extension MessagingFlag {
    static var mock: Self {
        guard let flag = MessagingFlag(
            dictionary: [
            "flagText": "New Hotel",
            "flagColor": "#000000"
            ]) else {
            fatalError("Failed to create MessagingFlag.mock")
        }

        return flag
    }
}

#endif
