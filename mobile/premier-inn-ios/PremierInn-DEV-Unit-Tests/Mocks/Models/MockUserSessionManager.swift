//
//  MockUserSessionManager.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 16/02/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import SimpleNetwork

struct MockUserSessionManager: UserSessionManagerProtocol {
    var currentUser: User? { User.emptyGuest() }
}
